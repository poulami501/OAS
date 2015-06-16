package com.ctb.db;

public interface AuditTrailSQL {
	
	String UPDATE_DATAEXPORT_INTERMIDEATE_JOB_STATUS = " UPDATE data_export SET LAST_UPDATE_STATUS = ?, LAST_UPDATE_TIME = SYSDATE, MESSAGE = ? WHERE EXPORT_ID = ?  ";
	String UPDATE_DATAEXPORT_JOB_STATUS             = " UPDATE data_export SET STATUS = ?, LAST_UPDATE_STATUS = ?, LAST_UPDATE_TIME = SYSDATE, MESSAGE = ? WHERE EXPORT_ID = ?  ";
	String UPDATE_TEST_ROSTER = "UPDATE TEST_ROSTER SET STUDENT_EXPORTED = 'T' WHERE TEST_ROSTER_ID IN( <<ROSTER_ID_LIST>> )" ;
	String HAS_ENGRADE_CUSTOMER_CONFIGURATION = "SELECT DECODE(COUNT(1), 0 , 'F', 'T')  FROM CUSTOMER C, CUSTOMER_CONFIGURATION CC WHERE C.CUSTOMER_ID = CC.CUSTOMER_ID  AND C.CUSTOMER_ID = ?  AND CC.CUSTOMER_CONFIGURATION_NAME = ?";

}
