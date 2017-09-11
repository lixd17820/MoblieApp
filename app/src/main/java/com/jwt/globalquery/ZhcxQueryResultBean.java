package com.jwt.globalquery;

import java.io.Serializable;

/**
 * 用于返回查询结果的实体类
 */
public class ZhcxQueryResultBean implements Serializable {

	private static final long serialVersionUID = -7938282987536323621L;

	private String field;
	private String comment;
	private String value;

	public ZhcxQueryResultBean() {
	}

	public ZhcxQueryResultBean(String _field, String _comment, String _value) {
		this.field = _field;
		this.comment = _comment;
		this.value = _value;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
