package com.ctb.lexington.util;

import com.ctb.lexington.data.TestAdminVO;

/**
 * TestAdminStartDateAscendingComparator
 *
 * Copyright CTB/McGraw-Hill, 2005
 * CONFIDENTIAL
 *
 * @author <a href="mailto:Jonathan_Becker@ctb.com">Jon Becker</a>
 * @version
 */
public class TestAdminStartDateAscendingComparator extends TestAdminBaseComparator
{
	protected int compareTestAdmins(TestAdminVO admin1_, TestAdminVO admin2_){
		int result = compareStartDate(admin1_, admin2_);
		if(result == 0)
			result = compareTestAdminName(admin1_, admin2_);
		return result;
	}
}