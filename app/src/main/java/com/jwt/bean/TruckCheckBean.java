package com.jwt.bean;

import com.jwt.bean.TruckVehicleBean;
import com.jwt.pojo.TruckDriverBean;

import java.io.Serializable;


@SuppressWarnings("serial")
public class TruckCheckBean implements Serializable {

	private TruckVehicleBean truck;

	private TruckDriverBean[] drvs;

	public TruckVehicleBean getTruck() {
		return truck;
	}

	public void setTruck(TruckVehicleBean truck) {
		this.truck = truck;
	}

	public TruckDriverBean[] getDrvs() {
		return drvs;
	}

	public void setDrvs(TruckDriverBean[] drvs) {
		this.drvs = drvs;
	}

}
