package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MjJobBean implements Serializable {
	private String[] job;

	public String[] getJob() {
		return job;
	}

	public void setJob(String[] job) {
		this.job = job;
	}

}
