package com.jwt.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KeyValueBean implements Serializable {
	private String key;
	private String value;

	public KeyValueBean() {
	}

	public KeyValueBean(String _key, String _value) {
		this.key = _key;
		this.value = _value;
	}

	@Override
	public String toString() {
		return value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
