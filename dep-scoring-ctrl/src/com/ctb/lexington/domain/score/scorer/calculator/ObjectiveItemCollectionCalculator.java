package com.ctb.lexington.domain.score.scorer.calculator;

import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.ctb.lexington.db.data.ObjectiveData;
import com.ctb.lexington.db.mapper.ObjectiveMapper;
import com.ctb.lexington.db.utils.DatabaseHelper;
import com.ctb.lexington.domain.score.event.Objective;
import com.ctb.lexington.domain.score.event.SubtestItemCollectionEvent;
import com.ctb.lexington.domain.score.event.SubtestObjectiveCollectionEvent;
import com.ctb.lexington.domain.score.event.common.Channel;
import com.ctb.lexington.domain.score.scorer.Scorer;
import com.ctb.lexington.util.SafeHashMap;
import com.ctb.lexington.util.SimpleCache;

/**
 * @todo HACK: remove buildObjectiveContext and rework the data structures
 */
public class ObjectiveItemCollectionCalculator extends Calculator {
    private final class ObjectiveMap extends SafeHashMap {
        public ObjectiveMap() {
            super(Long.class, Objective.class);
        }
    }

    private final Map objectiveContextMap = new ObjectiveMap();
    // TODO: HACK: without having to rework buildObjectiveContext
    private final Map cachedObjectives = new ObjectiveMap();
    private SubtestItemCollectionEvent sicEvent;

    /**
     * @param channel
     * @param scorer
     */
    public ObjectiveItemCollectionCalculator(Channel channel, Scorer scorer) {
        super(channel, scorer);
        channel.subscribe(this, SubtestItemCollectionEvent.class);
    }

    public void onEvent(SubtestItemCollectionEvent event) {
        this.sicEvent = event;
        Collection objectives;
        SubtestObjectiveCollectionEvent subtestObjectivesEvent = new SubtestObjectiveCollectionEvent(
                event.getTestRosterId(), event.getItemSetId());
        Connection oasConnection = null;
        try {
            oasConnection = scorer.getOASConnection();
            String cacheKey = event.getProductId().toString() + "::" + event.getItemSetId().toString();
            objectives = (Collection) SimpleCache.checkCache("ObjectivesBySubtestIdAndProductId", cacheKey, "scoringUser");
            if(objectives == null) {
	            objectives = new ObjectiveMapper(oasConnection).findObjectivesBySubtestIdAndProductId(
	                    DatabaseHelper.asLong(event.getItemSetId()), DatabaseHelper.asLong(event.getProductId()));
	            SimpleCache.cacheResult("ObjectivesBySubtestIdAndProductId", cacheKey, objectives, "scoringUser");
            }
            buildObjectiveContext(objectives);
            for (Iterator iter = objectives.iterator(); iter.hasNext();) {
                ObjectiveData record = (ObjectiveData) iter.next();

                if (isPrimaryObjective(record))
                    subtestObjectivesEvent.addPrimaryReportingLevelObjective(record.getItemId(),
                            asObjective(record));
                else if (isSecondaryObjective(record))
                    subtestObjectivesEvent.addSecondaryReportingLevelObjective(record.getItemId(),
                            asObjective(record));
                else
                    throw new IllegalStateException("Neither primary nor secondary: " + record);
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            try {
                scorer.close(true, oasConnection);
            } catch (Exception e) {
                handleException(e);
            }
        }
        channel.send(subtestObjectivesEvent);
    }

    private void buildObjectiveContext(final Collection objectives) {
        for (final Iterator it = objectives.iterator(); it.hasNext();) {
            final ObjectiveData objectiveData = (ObjectiveData) it.next();
            final Objective objective = getObjective(objectiveData);
            final String itemId = objectiveData.getItemId();

            objective.incNumberOfItems();
            objective.incPointsPossible(sicEvent.getMaxPoints(itemId).intValue());

            objectiveContextMap.put(objectiveData.getItemSetId(), objective);
        }
    }

    /**
     * @todo Why return a new objective instead of storing it? Weird --bko
     */
    private Objective getObjective(final ObjectiveData data) {
        final Long itemSetId = data.getItemSetId();

        if (objectiveContextMap.containsKey(itemSetId))
            return (Objective) objectiveContextMap.get(itemSetId);
        else
            return new Objective(itemSetId, data.getItemSetName());
    }

    private Objective getCachedObjective(final Long itemSetId, final String itemSetName) {
        if (!cachedObjectives.containsKey(itemSetId))
            cachedObjectives.put(itemSetId, new Objective(itemSetId, itemSetName));

        return (Objective) cachedObjectives.get(itemSetId);
    }

    private Objective asObjective(final ObjectiveData record) {
        final Objective objective = getCachedObjective(record.getItemSetId(), record
                .getItemSetName());
        // TODO: HACK: objectiveContextMap must be prepopulated else NPE
        final Objective contextObjective = (Objective) objectiveContextMap.get(record
                .getItemSetId());
        objective.setNumberOfItems(contextObjective.getNumberOfItems());
        objective.setPointsPossible(contextObjective.getPointsPossible());
        return objective;
    }

    private static boolean isPrimaryObjective(ObjectiveData record) {
        return ObjectiveData.PRIMARY.equals(record.getReportingLevel());
    }

    private static boolean isSecondaryObjective(ObjectiveData record) {
        return ObjectiveData.SECONDARY.equals(record.getReportingLevel());
    }

    private void handleException(Exception e) {
        throw new RuntimeException(e);
    }
}