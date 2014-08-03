package org.whut.strings;

public class UrlStrings {

	public static final String BASE_URL = "http://www.cseicms.com/inspectManagement";
	
	public static final String SECURITY_CHECK= BASE_URL+"/j_spring_cas_security_check";
	public static final String GET_CURRENT_USER = BASE_URL+"/rs/inspectUser/currentUser";
	public static final String GET_CONFIG_FILE_LIST = BASE_URL+"/rs/inspectTable/getList";
	public static final String GET_ROLE_TABLES = BASE_URL+"/rs/roleTablesConfiguration/downloadRoleTable";
	public static final String GET_INSPECT_TABLES =BASE_URL+"/rs/configuration/downloadInspectTable";
	public static final String SEND_LOCATION = BASE_URL+"/rs/inspectLocate/receiveInspectLocateInfo";
	public static final String GET_TASK_LIST = BASE_URL+"/rs/inspectTask/userLastTask";
	public static final String UPLOAD_FILE = BASE_URL+"/rs/inspectResult/upload";
}
