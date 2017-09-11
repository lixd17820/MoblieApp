package com.jwt.bean;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MenuGridBean implements Serializable {

	private int id;
	private String gridName;
	private ArrayList<MenuOptionBean> options;
	private String img;

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGridName() {
		return gridName;
	}

	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	public ArrayList<MenuOptionBean> getOptions() {
		if(options == null){
			options = new ArrayList<MenuOptionBean>();
		}
		return options;
	}

	public void setOptions(ArrayList<MenuOptionBean> options) {
		this.options = options;
	}

	

}
