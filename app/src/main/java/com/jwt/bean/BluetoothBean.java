package com.jwt.bean;

public class BluetoothBean {
	private String name;
	private String address;
	private int status;
	
	public BluetoothBean(String _name,String _address,int _status){
		this.name = _name;
		this.address = _address;
		this.status = _status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
