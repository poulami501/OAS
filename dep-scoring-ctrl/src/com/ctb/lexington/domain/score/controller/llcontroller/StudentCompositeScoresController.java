package com.ctb.lexington.domain.score.controller.llcontroller;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.ctb.lexington.db.data.ContextData;
import com.ctb.lexington.db.data.CurriculumData;
import com.ctb.lexington.db.data.StsTotalStudentScoreData;
import com.ctb.lexington.db.data.StsTotalStudentScoreDetail;
import com.ctb.lexington.db.data.StudentPredictedScoresData;
import com.ctb.lexington.db.data.CurriculumData.Composite;
import com.ctb.lexington.db.irsdata.irslldata.IrsLLCompositeFactData;
import com.ctb.lexington.db.mapper.llmapper.IrsLLCompositeFactMapper;

public class StudentCompositeScoresController {
	  private StsTotalStudentScoreData totalData;
	    private CurriculumData currData;
	    private ContextData context;
	    private IrsLLCompositeFactMapper mapper;

	    public StudentCompositeScoresController(Connection conn, StsTotalStudentScoreData totalData, CurriculumData currData, ContextData context) {
	        this.totalData = totalData;
	        this.currData = currData;
	        this.context = context;
	        mapper = new IrsLLCompositeFactMapper(conn);
	    }

	    public void run() throws SQLException {
	    	IrsLLCompositeFactData [] facts = getCompositeFactBeans();
	        for(int i=0;i<facts.length;i++) {
	        	IrsLLCompositeFactData newFact = facts[i];
	            mapper.delete(newFact);
	            if(new Long(1).equals(context.getCurrentResultId()))  {
	                mapper.insert(newFact);
	            }
	        }
	    }
	    
	    public IrsLLCompositeFactData [] getCompositeFactBeans() {
	        if(totalData.size() > 0) {
	            Composite [] composites = currData.getComposites();
	            ArrayList facts = new ArrayList();
	            for(int i=0;i<composites.length;i++) {
	               StsTotalStudentScoreDetail total = totalData.get(composites[i].getCompositeName());
	              // && ("T".equals(total.getValidScore()) || "Y".equals(total.getValidScore())|| "F".equals(total.getValidScore()) || "N".equals(total.getValidScore()))
	               if(total != null )  {
	            	   IrsLLCompositeFactData newFact = new IrsLLCompositeFactData();
	                   newFact.setAssessmentid(context.getAssessmentId());
	                   newFact.setCompositeid(composites[i].getCompositeId());
	                   newFact.setCurrentResultid(context.getCurrentResultId());
	                   newFact.setGradeid(context.getGradeId());
	                  // newFact.setNormalCurveEquivalent((total.getNormalCurveEquivalent()==null)?null:new Long(total.getNormalCurveEquivalent().longValue()));
	                   newFact.setNrsLevelid(new Long(0));
	                   newFact.setOrgNodeid(context.getOrgNodeId());
	                   newFact.setPointsAttempted(total.getPointsAttempted());
	                   newFact.setPointsObtained(total.getPointsObtained());
	                   newFact.setPointsPossible(composites[i].getCompositePointsPossible());
	                   newFact.setProgramid(context.getProgramId());
	                   //newFact.setRecLevelid((total.getRecommendedLevelId() == null)?new Long(6):total.getRecommendedLevelId());
	                   newFact.setScaleScore((total.getScaleScore()==null)?null:new Long(total.getScaleScore().longValue()));
	                   newFact.setProficencyLevel((total.getProficencyLevel()==null)?null:new Long(total.getProficencyLevel().longValue()));
	                   newFact.setNormalCurveEquivalent((total.getNormalCurveEquivalent()==null)?null:new Long(total.getNormalCurveEquivalent().longValue()));
	                   newFact.setNationalPercentile((total.getNationalPercentile()==null)?null:new Long(total.getNationalPercentile().longValue()));
	                   newFact.setSessionid(context.getSessionId());
	                   newFact.setStudentid(context.getStudentId());
	                   newFact.setTestStartTimestamp(context.getTestStartTimestamp());
	                   newFact.setTestCompletionTimestamp(context.getTestCompletionTimestamp());
	                  /* newFact.setAttr1id(context.getDemographicData().getAttr1Id());
	                   newFact.setAttr2id(context.getDemographicData().getAttr2Id());
	                   newFact.setAttr3id(context.getDemographicData().getAttr3Id());
	                   newFact.setAttr4id(context.getDemographicData().getAttr4Id());
	                   newFact.setAttr5id(context.getDemographicData().getAttr5Id());
	                   newFact.setAttr6id(context.getDemographicData().getAttr6Id());
	                   newFact.setAttr7id(context.getDemographicData().getAttr7Id());
	                   newFact.setAttr8id(context.getDemographicData().getAttr8Id());
	                   newFact.setAttr9id(context.getDemographicData().getAttr9Id());
	                   newFact.setAttr10id(context.getDemographicData().getAttr10Id());
	                   newFact.setAttr11id(context.getDemographicData().getAttr11Id());
	                   newFact.setAttr12id(context.getDemographicData().getAttr12Id());
	                   newFact.setAttr13id(context.getDemographicData().getAttr13Id());
	                   newFact.setAttr14id(context.getDemographicData().getAttr14Id());
	                   newFact.setAttr15id(context.getDemographicData().getAttr15Id());
	                   newFact.setAttr16id(context.getDemographicData().getAttr16Id());*/
	                   if (currData.getContentAreas().length > 0) {
	                        newFact.setFormid(new Long("A".equals(currData.getContentAreas()[0].getSubtestForm())?7:
	                                          "B".equals(currData.getContentAreas()[0].getSubtestForm())?8:
	                                         ("Espa?ol".equals(currData.getContentAreas()[0].getSubtestForm()) 
	                                        		 || "Espanol".equals(currData.getContentAreas()[0].getSubtestForm()) 
	                                        		 || "Espa�ol".equals(currData.getContentAreas()[0].getSubtestForm()))?9:10));
	                        
	                        
	                        newFact.setLevelid(new Long("K".equals(currData.getContentAreas()[0].getSubtestLevel())?16:
	                                          "1".equals(currData.getContentAreas()[0].getSubtestLevel())?17:
	                                          "2-3".equals(currData.getContentAreas()[0].getSubtestLevel())?18:
	                                          "4-5".equals(currData.getContentAreas()[0].getSubtestLevel())?19:
	                                          "6-8".equals(currData.getContentAreas()[0].getSubtestLevel())?20:
	                                          "9-12".equals(currData.getContentAreas()[0].getSubtestLevel())?21:22));
	                   }
	                   
	                   facts.add(newFact);
	               }
	            }
	            return (IrsLLCompositeFactData []) facts.toArray(new IrsLLCompositeFactData[0]);
	        } else {
	            return new IrsLLCompositeFactData[0];
	        }
	    }
}
