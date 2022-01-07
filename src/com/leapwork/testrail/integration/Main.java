package com.leapwork.testrail.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

	private static String loggerName = "logger";

	private static Logger logger = Logger.getLogger(loggerName);

	private static PluginHandler pluginHandler = PluginHandler.getInstance(loggerName);

	public static void main(String[] args) throws IOException, APIException {
		
		/*final String testRailRunId = args[0];
		final String testRailAddress = args[1];
		final String testRailLogin = args[2];
		final String testRailPassword = args[3];
		final String leapworkHostURL = args[4];
		final String scheduleId = args[5];
		final String delay = args[6];
		final String doneStatusAs = args[7];
		final String apiAccesskey = args[8];
*/
	
		final String testRailRunId = "18";
		final String testRailAddress ="http://localhost:85";
		final String testRailLogin = "admin@leapwork.com";
		final String testRailPassword = "admin";
		final String leapworkHostURL = "http://localhost:9001";
		final String scheduleId = "a56307fe-f820-44e5-8257-2dd82169a4bb";
		final String delay = "10";
		final String doneStatusAs = "1";
		final String apiAccesskey = "qwertyui";

		//FileHandler logFileHandler = new FileHandler(String.format(Messages.LOG_FILE_NAME, testRailRunId));
		FileHandler logFileHandler = new FileHandler(System.getProperty("user.dir") + testRailRunId);
		logger.addHandler(logFileHandler);
		SimpleFormatter formatter = new SimpleFormatter();
		logFileHandler.setFormatter(formatter);

		//logger.info("Starting plugin, args length = " + args.length);

		logger.info(String.format("Passed parameters:\n%1$s\n%2$s\n%3$s\n%4$s\n%5$s\n%6$s\n%7$s\n%8$s\n",
				String.format("Run Id: %1$s", testRailRunId), String.format("TestRail URL: %1$s", testRailAddress),
				String.format("TestRail User: %1$s", testRailLogin), String.format("Leapwork Controller URL: %1$s", testRailPassword),
				String.format("Schedule Id: %1$s", scheduleId), String.format("Time Delay in seconds: %1$s", delay),
				String.format("Done Status is interpreted as status number: %1$s",doneStatusAs),
				String.format("api accesskey", apiAccesskey).replace("\n", Messages.NEW_LINE)));

		

		APIClient testRailAPIclient = null;
		Schedule schedule = null;
		ArrayList<Test> testRailTests = null;

		try {

			testRailAPIclient = new APIClient(testRailAddress);
			testRailAPIclient.setUser(testRailLogin);
			testRailAPIclient.setPassword(testRailPassword);

			int timeDelay = pluginHandler.getTimeDelay(delay);

			int doneStatus = pluginHandler.getDoneStatusAs(doneStatusAs);

			testRailTests = pluginHandler.getTestRailTests(testRailRunId, testRailAPIclient);
			
			//marking test as Retest
			testRailAPIclient.sendPost(String.format(Messages.SET_TESTRAIL_TESTS_RESULTS_POST, testRailRunId),
					new TestCollection(testRailTests));

			schedule = pluginHandler.detectSchedule(leapworkHostURL, scheduleId, apiAccesskey);

			RUN_RESULT runResult = RUN_RESULT.RUN_REPEAT;

			do {
				runResult = pluginHandler.runSchedule(leapworkHostURL, schedule,apiAccesskey);

				if (runResult.equals(RUN_RESULT.RUN_SUCCESS)) // if schedule was successfully run
				{
					boolean isStillRunning = true;

					do {
						Thread.sleep(timeDelay * 1000); // Time delay
						isStillRunning = pluginHandler.getScheduleState(leapworkHostURL, schedule, doneStatus,
								apiAccesskey);
						logger.info(String.format(Messages.SCHEDULE_IS_STILL_RUNNING, schedule.getScheduleTitle(),
								schedule.getScheduleId()));
					} while (isStillRunning);
				} else if (runResult.equals(RUN_RESULT.RUN_REPEAT)) {
					Thread.sleep(timeDelay * 1000); // Time delay
				} else {
					// In case of error getScheduleState throws exception, so this block is
					// unreachable
				}
			} while (runResult.equals(RUN_RESULT.RUN_REPEAT));

			pluginHandler.checkEnvironmentsAndStepsQuantity(schedule, testRailTests);

			pluginHandler.setTestRailTestResults(testRailRunId, testRailAPIclient, schedule, testRailTests);

			logger.info(Messages.PLUGIN_SUCCESSFUL_FINISH);

		} catch (Exception e) {
			if (testRailTests != null) {
				for (Test test : testRailTests) {
					test.setStatusId(Test.Status.RETEST);
					test.setElapsed("1s");
					test.addComment(e.getMessage());
				}

				testRailAPIclient.sendPost(String.format(Messages.SET_TESTRAIL_TESTS_RESULTS_POST, testRailRunId),
						new TestCollection(testRailTests));
			} else
				logger.severe(Messages.CANNOT_SET_ANY_RESULTS_AS_TESTS_ARE_NOT_GOT);

			logger.severe(Messages.PLUGIN_ERROR_FINISH);
			logger.severe(e.getMessage());
			logger.severe(Messages.PLEASE_CONTACT_SUPPORT);

		} finally {
			logger.removeHandler(logFileHandler);
			logFileHandler.close();
		}
	}

}
