package com.leapwork.testrail.integration;

import java.util.ArrayList;

public final class Schedule {
	private String scheduleTitle;

	private String scheduleId;

	private ArrayList<Case> cases;

	private Integer environmentsQuantity;

	private String runId;

	private int runItemsCount;

	public Schedule(String scheduleId, String title, int environmentsQuantity) {
		cases = new ArrayList<Case>();
		this.scheduleId = scheduleId;
		this.scheduleTitle = title;
		this.environmentsQuantity = environmentsQuantity;

	}

	public String getScheduleTitle() {
		return scheduleTitle;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public ArrayList<Case> getCases() {
		return cases;
	}

	public Integer getEnvironmentsQuantity() {
		return environmentsQuantity;
	}

	public void setLeapRunId(String runId) {

		this.runId = runId;
	}

	public String getLeapRunId() {
		return runId;
	}

	public void setCountRunItems(int sizeOfRunitems) {

		runItemsCount = sizeOfRunitems;
	}

	public int getCountRunItems() {
		return runItemsCount;
	}
}
