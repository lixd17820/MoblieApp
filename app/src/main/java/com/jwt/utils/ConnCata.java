package com.jwt.utils;

public enum ConnCata {
	JWTCONN("移动2G", 0), OUTSIDECONN("电信3G", 1), INSIDECONN("公安三所", 2), OFFCONN(
			"离线模式", 3), UNKNOW("未设定", 4);

	private String name;
	private int index;

	private ConnCata(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public static ConnCata getValByIndex(int _index) {
		ConnCata[] ar = ConnCata.values();
		for (ConnCata c : ar) {
			if (c.getIndex() == _index)
				return c;
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return index + "";
	}

}
