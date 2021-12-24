package com.leapwork.testrail.integration;

public final class Messages {

	public static final String NEW_LINE = System.lineSeparator();

	public static final String SCHEDULE_DETECTED = "Schedule %1$s[%2$s] successfully detected";
	public static final String SCHEDULE_RUN_SUCCESS = "Schedule %1$s[%2$s] Launched Successfully and runId = %3$s";

	public static final String SCHEDULE_TITLE_OR_ID_ARE_NOT_GOT = "Tried to get schedule title or id";
	public static final String SCHEDULE_RUN_FAILURE = "Failed to run %1$s[%2$s]";
	public static final String SCHEDULE_STATE_FAILURE = "Tried to get %1$s[%2$s] state";
	public static final String NO_SUCH_SCHEDULE = "No such schedule This may occur if try to run schedule that controller does not have. It can be deleted. Or you simply have forgotten to select schedules after changing controller address;";
	public static final String NO_SUCH_SCHEDULE_WAS_FOUND = "Could not find %1$s[%2$s] schedule It was likely deleted";
	public static final String SCHEDULE_HAS_NO_CASES = "Schedule %1$s[%2$s] has no cases Add them in your leapwork studio and try again";
	public static final String SCHEDULE_IS_RUNNING_NOW = "Schedule %1$s[%2$s] is already running or queued now Try to run it again when it's finished or you can try stop it, then run it";

	public static final String CASE_FLOW_MISMATCH = "Values of cases and Flows doesn't match";
	public static final String CASE_INFORMATION = "Case: %1$s | Status: %2$s | Elapsed: %3$s";
	public static final String CASE_KEYFRAME_FORMAT = "%1$s - %2$s";
	public static final String CASE_KEYFRAME_FORMAT_WITHBLOCKTITLE = "%1$s - %2$s - %3$s";

	public static final String GET_SPECIFIC_SCHEDULE_URI = "%1$s/api/v4/schedules/%2$s";
	public static final String GET_RUN_ITEMS_URI = "%1$s/api/v4/run/%2$s/runItemIds";
	public static final String GET_RUNITEMIDINFO_URI = "%1$s/api/v4/runItems/%2$s";
	public static final String RUN_SCHEDULE_URI = "%1$s/api/v4/schedules/%2$s/runNow";
	public static final String GET_SCHEDULE_STATE_URI = "%1$s/api/v4/run/%2$s/status";
	public static final String GET_KEYFRAMES_URI = "%1$s/api/v4/runitems/%2$s/keyframes";

	public static final String PLUGIN_SUCCESSFUL_FINISH = "leapwork for TestRail  plugin  successfully finished";
	public static final String PLUGIN_ERROR_FINISH = "leapwork for TestRail plugin finished with errors";

	public static final String CONTROLLER_RESPONDED_WITH_ERRORS = "Controller responded with errors Please check controller logs and try again If does not help, try to restart controller.";
	public static final String PLEASE_CONTACT_SUPPORT = "If nothing helps, please contact support https://leapwork.com/support and provide the next information:\n1.Plugin Logs\n2.leapwork and plugin version\n3.Controller logs from the moment you've run the plugin.\n4.Assets without videos if possible.\nYou can find them {Path to leapwork}/leapwork/Assets\nThank you"
			.replace("\n", NEW_LINE);

	public static final String GET_TESTRAIL_TESTS_GET = "get_tests/%1$s";
	public static final String SET_TESTRAIL_TESTS_RESULTS_POST = "add_results/%1$s";
	public static final String GET_TESTRAIL_TEST_GET = "%1$s/index.php?/tests/view/%2$s";

	public static final String LOG_FILE_NAME = "leapworkIntegrationLogsRunId%1$s.txt";
	public static final String COMMENT_FORMAT = "Status: %1$s\n%2$s".replace("\n", NEW_LINE);

	public static final String UNSUPPORTED_CASE_TYPE = "Case %1$s has unsupported type It should be step type";
	public static final String UNSUPPORTED_STATUS_VALUE_WARNING = "Unsupported status value %1$d.";
	public static final String EMPTY_TIME_DELAY = "Time delay value is empty";
	public static final String EMPTY_DONE_STATUS = "Done status value is empty";
	public static final String SET_DONE_STATUS_TO_DEFAULT_VALUE = "Set done status to default value RETEST";
	public static final String SET_TIME_DELAY_TO_DEFAULT_VALUE = "Set time delay to default value %1$d";
	public static final String THIS_STEP_WAS_NOT_RUN = "This step was not even run due to:\n1.leapwork schedule run cancellation.\n2.Error in connection to TestRail or leapwork. Check Testrail URL, login, password and leapwork controller: its URL and if it is run in services.\n 3.You've selected schedule that does not have this case.\n4.Schedule environments quantity is less than steps in TestRail case.\n5.You have more than one case with the same name in a run."
			.replace("\n", NEW_LINE);

	public static final String CANNOT_SET_ANY_RESULTS_AS_TESTS_ARE_NOT_GOT = "Cannot set test error results because tests are not got";
	public static final String ADD_CASE_ID_INFORMATION_TO_COMMENT = "TestRail case Id: %1$d\n"
			.replace("\n", NEW_LINE);
	public static final String STEPS_AND_ENVIRONMENTS_QUANTITY_ARE_NOT_EQUAL = "Test steps quantity and schedule environments quantity are not equal.\n Steps: %1$d\n Environments: %2$d\n Check steps quantity in your TestRail case and environments quantity in leapwork schedule"
			.replace("\n", NEW_LINE);

	public static final String ERROR_CODE_MESSAGE = "Code: %1$s Status: %2$s while trying to access %3$s";
	public static final String COULD_NOT_CONNECT_TO = "Could not connect to %1$s Check it and try again ";
	public static final String COULD_NOT_CONNECT_TO_BUT_WAIT = "Could not connect to %1$s Check connection The plugin is waiting for connection reestablishment ";
	public static final String CONNECTION_LOST = "Connection to controller is lost: %1$s Check connection The plugin is waiting for connection reestablishment";
	public static final String INTERRUPTED_EXCEPTION = "Interrupted exception: %1$s";
	public static final String EXECUTION_EXCEPTION = "Execution exception: %1$s";
	public static final String IO_EXCEPTION = "I/O exception: %1$s";
	public static final String EXCEPTION = "Exception: %1$s";
	public static final String CACHE_TIMEOUT_EXCEPTION = "Cache time out exception has occurred Don't worry This schedule %1$s[%2$s] will be run again later";

	public static final String LICENSE_EXPIRED = "Your leapwork license has expired. Please contact support https://leapwork.com/support";

	public static final String SCHEDULE_IS_STILL_RUNNING = "Schedule %1$s[%2$s] is still running";
}
