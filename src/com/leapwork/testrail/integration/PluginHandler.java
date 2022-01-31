package com.leapwork.testrail.integration;

import com.google.gson.*;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.io.*;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public final class PluginHandler {

	private static PluginHandler pluginHandler = null;

	private static Logger logger = null;

	private PluginHandler(String loggerName) {
		logger = Logger.getLogger(loggerName);
	}

	public static PluginHandler getInstance(String loggerName) {
		if (pluginHandler == null)
			pluginHandler = new PluginHandler(loggerName);

		return pluginHandler;
	}

	public int getTimeDelay(String rawTimeDelay) {
		int timeDelay = 5; // 5 seconds - default
		try {
			if (!rawTimeDelay.isEmpty() || !"".equals(rawTimeDelay))
				timeDelay = Integer.parseInt(rawTimeDelay);
			else
				throw new NumberFormatException(Messages.EMPTY_TIME_DELAY);
		} catch (NumberFormatException e) {
			timeDelay = 5;
			logger.warning(String.format("$1%s%2$s%3$s", e.getMessage(), Messages.NEW_LINE,
					String.format(Messages.SET_TIME_DELAY_TO_DEFAULT_VALUE, timeDelay)));
		} finally {
			return timeDelay;
		}
	}

	public int getDoneStatusAs(String rawDoneStatusAs) {
		int doneStatus = 4; // default value RETEST

		try {

			if (!rawDoneStatusAs.isEmpty() || !"".equals(rawDoneStatusAs)) {
				doneStatus = Integer.parseInt(rawDoneStatusAs);

				if (doneStatus < 1 || doneStatus > 5 || doneStatus == 3)
					throw new NumberFormatException(
							String.format(Messages.UNSUPPORTED_STATUS_VALUE_WARNING, doneStatus));

			} else
				throw new NumberFormatException(Messages.EMPTY_DONE_STATUS);
		} catch (NumberFormatException e) {
			logger.warning(String.format("%1$s%2$s%3$s", e.getMessage(), Messages.NEW_LINE,
					Messages.SET_DONE_STATUS_TO_DEFAULT_VALUE));
			doneStatus = 4; // SET_DEFAULT VALUE
		} finally {
			return doneStatus;
		}
	}

	public void checkEnvironmentsAndStepsQuantity(Schedule schedule, ArrayList<Test> tests) throws Exception {
		int totalSteps = 0;

		totalSteps = tests.size();
		int envs = schedule.getCountRunItems();
		if (totalSteps != envs) {
			for (Test test : tests) {
				test.addComment(
						String.format(Messages.STEPS_AND_ENVIRONMENTS_QUANTITY_ARE_NOT_EQUAL, totalSteps, envs));
				logger.warning(String.format(Messages.STEPS_AND_ENVIRONMENTS_QUANTITY_ARE_NOT_EQUAL, totalSteps, envs));
				throw new Exception(Messages.CASE_FLOW_MISMATCH);
			}
		}
	}

	public ArrayList<Test> getTestRailTests(String testRailRunId, APIClient testRailAPIClient) throws Exception {

		ArrayList<Test> test = new ArrayList<>();

		try {
			JsonElement jsonElements = testRailAPIClient
					.sendGet(String.format(Messages.GET_TESTRAIL_TESTS_GET, testRailRunId));

			// for own prem testrail
			if (jsonElements.isJsonArray()) {

				JsonArray jsonTests = jsonElements.getAsJsonArray();
				test = addTestForJsonElement(jsonTests, testRailAPIClient);

			}

			// for cloud testrail
			else if (jsonElements.isJsonObject()) {
				JsonArray jsonTests = jsonElements.getAsJsonObject().get("tests").getAsJsonArray();

				test = addTestForJsonElement(jsonTests, testRailAPIClient);
			}

			else {
				String errorMessage = String.format(Messages.ERROR_MESSAGE_TESTS);
				throw new Exception(errorMessage);
			}

		}

		catch (UnknownHostException e) {
			String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO, e.getMessage());
			throw new Exception(connectionErrorMessage);
		} catch (Exception e) {
			throw new Exception(e);
		}

		return test;
	}

	public ArrayList<Test> addTestForJsonElement(JsonArray jsonTests, APIClient testRailAPIClient) {
		ArrayList<Test> tests = new ArrayList<>();

		for (JsonElement jsonElement : jsonTests) {
			JsonObject jsonTest = jsonElement.getAsJsonObject();
			tests.add(new Test(jsonTest.get("id").getAsInt(), jsonTest.get("case_id").getAsInt(),
					jsonTest.get("title").getAsString(), testRailAPIClient.getTestRailAddress(),
					Utils.defaultIntegerIfNull(jsonTest.get("assignedto_id"), null)));
		}
		return tests;

	}

	public Schedule detectSchedule(String leapworkHost, String scheduleId, String accesskey) throws Exception {

		Schedule schedule = null;

		String scheduleListUri = String.format(Messages.GET_SPECIFIC_SCHEDULE_URI, leapworkHost, scheduleId);

		try {

			try {

				AsyncHttpClient client = new AsyncHttpClient();
				Response response = client.prepareGet(scheduleListUri).setHeader("AccessKey", accesskey).execute()
						.get();
				client = null;

				switch (response.getStatusCode()) {
				case 200:

					JsonParser parser = new JsonParser();
					JsonObject jsonSchedule = parser.parse(response.getResponseBody()).getAsJsonObject();
					String Id = Utils.defaultStringIfNull(jsonSchedule.get("Id"), "null Id");
					String Title = Utils.defaultStringIfNull(jsonSchedule.get("Title"), "null Title");
					int environmentsQuantity = 0;
					ArrayList<JsonElement> jsonEleList = new ArrayList<>();
					JsonArray jsonRunListSteps = jsonSchedule.get("RunListSteps").getAsJsonArray();
					for (JsonElement runListSteps : jsonRunListSteps) {

						JsonElement stepId = runListSteps.getAsJsonObject().get("RunListStepId");
						if (!(stepId == null))
							jsonEleList.add(stepId);

					}

					environmentsQuantity = jsonEleList.size();

					if (Id.contentEquals("null Id"))
						throw new Exception(Messages.NO_SUCH_SCHEDULE);

					schedule = new Schedule(Id, Title, environmentsQuantity);
					logger.info(String.format(Messages.SCHEDULE_DETECTED, Title, Id));

					break;

				case 404:
					String errorMessage404 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), scheduleId);
					errorMessage404 += String.format("%1$s%2$s", Messages.NEW_LINE, Messages.NO_SUCH_SCHEDULE);
					throw new Exception(errorMessage404);

				case 445:
					String errorMessage445 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), scheduleId);
					errorMessage445 += String.format("\n%1$s", Messages.LICENSE_EXPIRED);
					throw new Exception(errorMessage445);

				case 500:
					String errorMessage500 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), scheduleId);
					errorMessage500 += String.format("%1$s%2$s", Messages.NEW_LINE,
							Messages.CONTROLLER_RESPONDED_WITH_ERRORS);
					throw new Exception(errorMessage500);

				default:
					String errorMessage = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), scheduleId);
					throw new Exception(errorMessage);
				}

			} catch (ConnectException | UnknownHostException e) {
				String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO, e.getMessage());
				throw new Exception(connectionErrorMessage);
			} catch (InterruptedException e) {
				String interruptedExceptionMessage = String.format(Messages.INTERRUPTED_EXCEPTION, e.getMessage());
				throw new Exception(interruptedExceptionMessage);
			} catch (ExecutionException e) {
				if (e.getCause() instanceof ConnectException || e.getCause() instanceof UnknownHostException) {
					String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO,
							e.getCause().getMessage());
					throw new Exception(connectionErrorMessage);
				} else {
					String executionExceptionMessage = String.format(Messages.EXECUTION_EXCEPTION, e.getMessage());
					throw new Exception(executionExceptionMessage);
				}
			} catch (IOException e) {
				String ioExceptionMessage = String.format(Messages.IO_EXCEPTION, e.getMessage());
				throw new Exception(ioExceptionMessage);
			}
		} catch (Exception e) {
			logger.severe(Messages.SCHEDULE_TITLE_OR_ID_ARE_NOT_GOT);
			logger.severe(e.getMessage());
			logger.severe(Messages.PLEASE_CONTACT_SUPPORT);
			throw new Exception(String.format("%1$s%2$s%3$s", Messages.SCHEDULE_TITLE_OR_ID_ARE_NOT_GOT,
					Messages.NEW_LINE, e.getMessage()));
		}

		return schedule;
	}

	public RUN_RESULT runSchedule(String leapworkHost, Schedule schedule, String accesskey) throws Exception {
		RUN_RESULT isSuccessfullyRun = RUN_RESULT.RUN_FAIL;

		String uri = String.format(Messages.RUN_SCHEDULE_URI, leapworkHost, schedule.getScheduleId());
		AsyncHttpClient client = new AsyncHttpClient();
		try {
			try {

				Response response = client.preparePut(uri).setHeader("AccessKey", accesskey).setBody("").execute()
						.get();
				switch (response.getStatusCode()) {
				case 200:
					isSuccessfullyRun = RUN_RESULT.RUN_SUCCESS;
					JsonParser parser = new JsonParser();
					JsonObject jsonData = parser.parse(response.getResponseBody()).getAsJsonObject();
					String leapRunId = jsonData.get("RunId").getAsString();
					schedule.setLeapRunId(leapRunId);
					String successMessage = String.format(Messages.SCHEDULE_RUN_SUCCESS, schedule.getScheduleTitle(),
							schedule.getScheduleId(), leapRunId);
					logger.info(successMessage);
					break;

				case 404:
					String errorMessage404 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getScheduleId());
					errorMessage404 += String.format("%1$s%2$s", Messages.NEW_LINE,
							String.format(Messages.NO_SUCH_SCHEDULE_WAS_FOUND, schedule.getScheduleTitle(),
									schedule.getScheduleId()));
					throw new Exception(errorMessage404);

				case 444:
					String errorMessage444 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getScheduleId());
					errorMessage444 += String.format("%1$s%2$s", Messages.NEW_LINE, String.format(
							Messages.SCHEDULE_HAS_NO_CASES, schedule.getScheduleTitle(), schedule.getScheduleId()));
					throw new Exception(errorMessage444);

				case 445:
					String errorMessage445 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getScheduleId());
					errorMessage445 += String.format("\n%1$s", Messages.LICENSE_EXPIRED);
					throw new InterruptedException(errorMessage445);

				case 448:
					String errorMessage448 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getScheduleId());
					errorMessage448 += String.format("\n%1$s", String.format(Messages.CACHE_TIMEOUT_EXCEPTION,
							schedule.getScheduleTitle(), schedule.getScheduleId()));
					isSuccessfullyRun = RUN_RESULT.RUN_REPEAT;
					logger.warning(errorMessage448);
					break;

				case 500:
					String errorMessage500 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getScheduleId());
					errorMessage500 += String.format("%1$s%2$s", Messages.NEW_LINE, String.format(
							Messages.SCHEDULE_IS_RUNNING_NOW, schedule.getScheduleTitle(), schedule.getScheduleId()));
					throw new Exception(errorMessage500);

				default:
					String errorMessage = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getScheduleId());
					throw new Exception(errorMessage);
				}
			} catch (ConnectException | UnknownHostException e) {
				String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO_BUT_WAIT, e.getMessage());
				logger.warning(connectionErrorMessage);
				return RUN_RESULT.RUN_REPEAT;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof ConnectException || e.getCause() instanceof UnknownHostException
						|| e.getCause() instanceof NoRouteToHostException) {
					String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO_BUT_WAIT,
							e.getCause().getMessage());
					logger.warning(connectionErrorMessage);
					return RUN_RESULT.RUN_REPEAT;
				} else {
					String executionExceptionMessage = String.format(Messages.EXECUTION_EXCEPTION, e.getMessage());
					throw new Exception(executionExceptionMessage);
				}
			} catch (IOException e) {
				String ioExceptionMessage = String.format(Messages.IO_EXCEPTION, e.getMessage());
				throw new Exception(ioExceptionMessage);
			} catch (Exception e) {
				throw e;
			}
		} catch (InterruptedException e) {
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			String errorMessage = String.format(Messages.SCHEDULE_RUN_FAILURE, schedule.getScheduleTitle(),
					schedule.getScheduleId());
			logger.severe(errorMessage);
			logger.severe(e.getMessage());
			logger.severe(Messages.PLEASE_CONTACT_SUPPORT);
			throw new Exception(String.format("%1$s%2$s%3$s", errorMessage, Messages.NEW_LINE, e.getMessage()));
		}
		client = null;
		return isSuccessfullyRun;

	}

	public boolean getScheduleState(String leapworkHost, Schedule schedule, Integer doneStatusValue, String accesskey)
			throws Exception {
		boolean isScheduleStillRunning = true;

		String uri = String.format(Messages.GET_SCHEDULE_STATE_URI, leapworkHost, schedule.getLeapRunId());

		try {

			try {
				AsyncHttpClient client = new AsyncHttpClient();
				Response response = client.prepareGet(uri).setHeader("AccessKey", accesskey).execute().get();
				JsonParser parser = new JsonParser();

				switch (response.getStatusCode()) {
				case 200:

					JsonObject jsonState = parser.parse(response.getResponseBody()).getAsJsonObject();

					if (isScheduleStillRunning(jsonState))
						isScheduleStillRunning = true;
					else {
						isScheduleStillRunning = false;

						String runItemsUri = String.format(Messages.GET_RUN_ITEMS_URI, leapworkHost,
								schedule.getLeapRunId());
						Response runItemIdsJson = client.prepareGet(runItemsUri).setHeader("AccessKey", accesskey)
								.execute().get();

						JsonObject runItemIdsJsonObj = parser.parse(runItemIdsJson.getResponseBody()).getAsJsonObject();
						JsonArray runItemIdsJsonArray = runItemIdsJsonObj.get("RunItemIds").getAsJsonArray();
						schedule.setCountRunItems(runItemIdsJsonArray.size());
						// get data for each runItem
						for (JsonElement runItemId : runItemIdsJsonArray) {

							String strRunItemId = runItemId.getAsString();

							String caseTitle = "";
							String caseId = "";
							String statusStr = "";
							String elapsed = "";
							String uriRunItemIdInfo = String.format(Messages.GET_RUNITEMIDINFO_URI, leapworkHost,
									strRunItemId);
							Response runItemIdResp = client.prepareGet(uriRunItemIdInfo)
									.setHeader("AccessKey", accesskey).execute().get();

							// get data for flow in RunItem
							if (runItemIdResp.getStatusCode() == 200) {
								JsonObject runItemIdJsonObj = parser.parse(runItemIdResp.getResponseBody())
										.getAsJsonObject();
								JsonObject flowInfoJsonObj = runItemIdJsonObj.get("FlowInfo").getAsJsonObject();

								caseTitle = flowInfoJsonObj.get("FlowTitle").getAsString();
								caseId = flowInfoJsonObj.get("FlowId").getAsString();
								statusStr = runItemIdJsonObj.get("Status").getAsString();
								elapsed = runItemIdJsonObj.get("Elapsed").getAsString();
							}

							else {
								String errorMessage = String.format(Messages.ERROR_CODE_MESSAGE,
										response.getStatusCode(), response.getStatusText(), uriRunItemIdInfo);
								throw new Exception(errorMessage);
							}

							String uriKeyFrameInfo = String.format(Messages.GET_KEYFRAMES_URI, leapworkHost,
									strRunItemId);
							Response keyFrameResp = client.prepareGet(uriKeyFrameInfo).setHeader("AccessKey", accesskey)
									.execute().get();
							String keyFrames = String.format("CaseTitle: %1$s%2$s", caseTitle, Messages.NEW_LINE);

							// get KeyFrames for each RunItem
							if (keyFrameResp.getStatusCode() == 200) {

								JsonArray keyFrameJsonArr = parser.parse(keyFrameResp.getResponseBody())
										.getAsJsonArray();
								for (JsonElement keyFrame : keyFrameJsonArr) {
									String level = keyFrame.getAsJsonObject().get("Level").getAsString();
									String timeStamp = keyFrame.getAsJsonObject().get("Timestamp").getAsJsonObject()
											.get("Value").getAsString();
									String logMessage = keyFrame.getAsJsonObject().get("LogMessage").getAsString();
									JsonElement keyframeBlockTitle = keyFrame.getAsJsonObject().get("BlockTitle");
									String stacktrace = "";
									if (!level.contentEquals("") && !level.contentEquals("Trace")) {
										if (keyframeBlockTitle != null) {

											stacktrace = String.format(Messages.CASE_KEYFRAME_FORMAT_WITHBLOCKTITLE,
													timeStamp, keyframeBlockTitle.getAsString(), logMessage);
										} else {
											stacktrace = String.format(Messages.CASE_KEYFRAME_FORMAT, timeStamp,
													logMessage);
										}
										keyFrames += stacktrace;
										keyFrames += Messages.NEW_LINE;
									}
								}

							}

							else {
								String errorMessage = String.format(Messages.ERROR_CODE_MESSAGE,
										response.getStatusCode(), response.getStatusText(), uriKeyFrameInfo);
							}

							logger.info(keyFrames);
							schedule.getCases()
									.add(new Case(caseId, caseTitle,
											parseStringStatusToInteger(statusStr, doneStatusValue),
											parseExecutionTimeToSeconds(elapsed), keyFrames));
						}
					}

					break;

				case 404:
					String errorMessage404 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getLeapRunId());
					errorMessage404 += String.format("%1$s%2$s", Messages.NEW_LINE,
							String.format(Messages.NO_SUCH_SCHEDULE_WAS_FOUND, schedule.getScheduleTitle(),
									schedule.getScheduleId()));
					throw new Exception(errorMessage404);

				case 445:
					String errorMessage445 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getLeapRunId());
					errorMessage445 += String.format("\n%1$s", Messages.LICENSE_EXPIRED);
					throw new InterruptedException(errorMessage445);

				case 448:
					String errorMessage448 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getLeapRunId());
					errorMessage448 += String.format("\n%1$s", String.format(Messages.CACHE_TIMEOUT_EXCEPTION,
							schedule.getScheduleTitle(), schedule.getScheduleId()));
					isScheduleStillRunning = true;
					logger.warning(errorMessage448);
					break;

				case 500:
					String errorMessage500 = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getLeapRunId());
					errorMessage500 += String.format("%1$s%2$s", Messages.NEW_LINE,
							Messages.CONTROLLER_RESPONDED_WITH_ERRORS);
					throw new Exception(errorMessage500);

				default:
					String errorMessage = String.format(Messages.ERROR_CODE_MESSAGE, response.getStatusCode(),
							response.getStatusText(), schedule.getLeapRunId());
					throw new Exception(errorMessage);
				}
				parser = null;
			} catch (NoRouteToHostException e) {
				String connectionLostErrorMessage = String.format(Messages.CONNECTION_LOST, e.getCause().getMessage());
				logger.warning(connectionLostErrorMessage);
				return true;
			} catch (ConnectException | UnknownHostException e) {
				String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO_BUT_WAIT, e.getMessage());
				logger.warning(connectionErrorMessage);
				return true;
			} catch (ExecutionException e) {
				if (e.getCause() instanceof ConnectException || e.getCause() instanceof UnknownHostException) {
					String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO_BUT_WAIT,
							e.getCause().getMessage());
					logger.warning(connectionErrorMessage);
					return true;
				} else if (e.getCause() instanceof NoRouteToHostException) {
					String connectionLostErrorMessage = String.format(Messages.CONNECTION_LOST,
							e.getCause().getMessage());
					logger.warning(connectionLostErrorMessage);
					return true;
				} else {
					String executionExceptionMessage = String.format(Messages.EXECUTION_EXCEPTION, e.getMessage());
					throw new Exception(executionExceptionMessage);
				}

			} catch (IOException e) {
				String ioExceptionMessage = String.format(Messages.IO_EXCEPTION, e.getMessage());
				throw new Exception(ioExceptionMessage);
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			String errorMessage = String.format(Messages.SCHEDULE_STATE_FAILURE, schedule.getScheduleTitle(),
					schedule.getScheduleId());
			logger.severe(errorMessage);
			logger.severe(e.getMessage());
			logger.severe(Messages.PLEASE_CONTACT_SUPPORT);
			throw new Exception(String.format("%1$s%2$s%3$s", errorMessage, Messages.NEW_LINE, e.getMessage()));
		}

		return isScheduleStillRunning;

	}

	public void setTestRailTestResults(String testRailRunId, APIClient testRailAPIClient, Schedule schedule,
			ArrayList<Test> testRailTests) throws Exception {

		try {

			//////// MAPPING SCHEDULE CASES AND TEST RESULTS

			for (Test test : testRailTests) {
				boolean testfound = false;
				for (Case aCase : schedule.getCases()) {
					if (aCase.getCaseName().trim().equalsIgnoreCase(test.getTestTitle().trim())
							&& !aCase.isResultAlreadySet()) {
						if (!test.isTestFilled()) {
							test.addComment(aCase.getKeyFramesLogs());
							test.setElapsed(convertSecondsToTime(aCase.getSeconds()));
							test.setStatusId(aCase.getCaseStatus());
							test.setTestFilled(true);
							aCase.setResultAlreadySet(true);
							testfound = true;
							break;
						}

					}

				}
				if (!testfound)
					test.addComment(Messages.CASE_DIDNOT_MATCHED_WITH_FLOW);

			}

			testRailAPIClient.sendPost(String.format(Messages.SET_TESTRAIL_TESTS_RESULTS_POST, testRailRunId),
					new TestCollection(testRailTests));

		} catch (UnknownHostException e) {
			String connectionErrorMessage = String.format(Messages.COULD_NOT_CONNECT_TO, e.getMessage());
			throw new Exception(connectionErrorMessage);
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	private boolean isScheduleStillRunning(JsonObject jsonState) {

		JsonElement statusOfRunId = jsonState.get("Status");
		if (statusOfRunId != null) {
			String status = statusOfRunId.getAsString();
			if (status.contentEquals("Executing") || status.contentEquals("Queued"))
				return true;
			else
				return false;
		}

		else
			return true;

	}

	private Integer parseExecutionTimeToSeconds(String rawExecutionTime) {
		String ExecutionTotalTime[] = rawExecutionTime.split(":|\\.");

		return Integer.parseInt(ExecutionTotalTime[0]) * 60 * 60 + // hours
				Integer.parseInt(ExecutionTotalTime[1]) * 60 + // minutes
				Integer.parseInt(ExecutionTotalTime[2]); // seconds
	}

	private String defaultElapsedIfNull(JsonElement rawElapsed) {
		if (rawElapsed != null)
			return rawElapsed.getAsString();
		else
			return "00:00:00.0000000";

	}

	private String convertSecondsToTime(int totalSeconds) {
		Integer hours = totalSeconds / 3600;
		Integer minutes = (totalSeconds - (hours * 3600)) / 60;
		Integer seconds = totalSeconds - hours * 3600 - minutes * 60;

		String resultString = "";

		if (seconds > 0) {
			if (hours != 0)
				resultString += String.format("%1$dh", hours);
			if (minutes != 0) {
				if (resultString.length() > 0)
					resultString += String.format(" %1$dm", minutes);
				else
					resultString += String.format("%1$dm", minutes);
			}
			if (resultString.length() > 0)
				resultString += String.format(" %1$ds", seconds);
			else
				resultString += String.format("%1$ds", seconds);
		} else
			resultString = "1s";

		return resultString;
	}

	private Integer parseStringStatusToInteger(String statuses, Integer doneSatus) {

		Integer setStatusId = Test.Status.BLOCKED;

		switch (statuses) {
		case "Passed":
			setStatusId = Test.Status.PASSED;
			break;
		case "Failed":
			setStatusId = Test.Status.FAILED;
			break;
		case "Done":
			setStatusId = doneSatus;
			break;
		case "Cancelled":
			setStatusId = Test.Status.RETEST;
			break;
		case "Error":
			setStatusId = Test.Status.RETEST;
			break;
		default:
			setStatusId = Test.Status.RETEST;
			break;
		}
		return setStatusId;

	}
}
