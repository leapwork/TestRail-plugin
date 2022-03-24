package com.leapwork.testrail.integration;


public class Case {
	private String caseId;
	private String caseName;
	private Integer caseStatus;
	private Integer seconds;
	private String keyFramesLogs;
	private boolean isResultAlreadySet;

	public Case(String caseId, String caseTitle, Integer caseStatus, Integer seconds, String keyFramesLogs) {
		this.caseId = caseId;
		this.caseName = caseTitle;
		this.caseStatus = caseStatus;
		this.seconds = seconds;
		this.keyFramesLogs = keyFramesLogs;
		this.isResultAlreadySet = false;
	}

	public String getCaseName() {
		return caseName;
	}

	public Integer getCaseStatus() {
		return caseStatus;
	}

	public Integer getSeconds() {
		return seconds;
	}

	public String getKeyFramesLogs() {
		return keyFramesLogs;
	}

	public boolean isResultAlreadySet() {
		return isResultAlreadySet;
	}

	public String getCaseId() {

		return caseId;
	}

	public void setResultAlreadySet(boolean resultAlreadySet) {
		isResultAlreadySet = resultAlreadySet;
	}

}
