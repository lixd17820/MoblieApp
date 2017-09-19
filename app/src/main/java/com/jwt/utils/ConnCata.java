package com.jwt.utils;

public enum ConnCata {
	 OUTSIDECONN("线路一", 0), INSIDECONN("线路二", 1);

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
