package com.jwt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import com.jwt.bean.MenuGridBean;
import com.jwt.bean.MenuOptionBean;

public class MenuParser {

	public static List<MenuGridBean> parseMenuXml(Context context) {
		try {
			InputStream myInput = context.getAssets().open("menu_option.xml");
			InputStreamReader bfr = new InputStreamReader(myInput);
			List<MenuGridBean> grids = parseMenuXml(bfr);
			bfr.close();
			return grids;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<MenuGridBean> parseMenuXml(InputStreamReader xml) {
		// Map<String, Integer> map = getMenuImgContent();
		List<MenuGridBean> menuGrid = new ArrayList<MenuGridBean>();
		MenuGridBean mb = new MenuGridBean();
		XmlPullParser parser = Xml.newPullParser();
		try {
			boolean isContinue = true;
			parser.setInput(xml);
			int event = parser.getEventType();// 产生第一个事件
			while (event != XmlPullParser.END_DOCUMENT) {
				String curTag = parser.getName();
				switch (event) {
				// 判断当前事件是否是文档开始事件
				case XmlPullParser.START_DOCUMENT:
					break;
				// 判断当前事件是否是标签元素开始事件
				case XmlPullParser.START_TAG: {
					if ("grid".equals(curTag)) {
						mb = new MenuGridBean();
						mb.setId(Integer.valueOf(parser.getAttributeValue(null,
								"gid")));
						String img = parser.getAttributeValue(null, "img");
						mb.setImg(img);
						mb.setGridName(parser.getAttributeValue(null, "name"));
					} else if ("menu".equals(curTag)) {
						MenuOptionBean menu = new MenuOptionBean();
						menu.setId(Integer.valueOf(parser.getAttributeValue(
								null, "id")));
						menu.setMenuName(parser.getAttributeValue(null, "name"));
						String img = parser.getAttributeValue(null, "img");
						menu.setImg(img);
						menu.setQx(Integer.valueOf(parser.getAttributeValue(
								null, "qx")));
						menu.setPck(parser.getAttributeValue(null, "pck"));
						menu.setClassName(parser.getAttributeValue(null,
								"cname"));
						menu.setDataName(parser
								.getAttributeValue(null, "dname"));
						menu.setData(parser.getAttributeValue(null, "data"));
						menu.setCatalog(parser.getAttributeValue(null,
								"catalog"));
						String badge = parser.getAttributeValue(null,"badge");
						menu.setBadge("1".equals(badge));
						mb.getOptions().add(menu);
					}
				}
					break;
				// 判断当前事件是否是标签元素结束事件
				case XmlPullParser.END_TAG:
					if ("grid".equals(curTag)) {
						menuGrid.add(mb);
					}
					break;
				}
				if (!isContinue)
					break;
				event = parser.next();// 进入下一个元素并触发相应事件
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return menuGrid;
	}

	public static boolean checkServerRunning(Context context, String packName,
			String serverName) {
		boolean isRunning = false;
		// 检测服务是否在运行
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = activityManager.getRunningServices(100);
		for (RunningServiceInfo ri : list) {
			if (TextUtils.equals(ri.service.getPackageName(), packName)
					&& TextUtils.equals(ri.service.getClassName(), serverName)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 根据权限决定菜单的显示
	 * 
	 * @param list
	 * @param sqx
	 * @return
	 */
	public static List<MenuGridBean> filterMenuByQx(List<MenuGridBean> list,
			String sqx) {
		int qx = Integer.valueOf(sqx.trim());
		List<MenuGridBean> result = new ArrayList<MenuGridBean>();
		for (MenuGridBean menuGridBean : list) {
			if (menuGridBean.getId() == 0) {
				MenuGridBean mb = new MenuGridBean();
				mb.setGridName(menuGridBean.getGridName());
				mb.setId(menuGridBean.getId());
				mb.setImg(menuGridBean.getImg());
				ArrayList<MenuOptionBean> options = new ArrayList<MenuOptionBean>();
				for (MenuOptionBean op : menuGridBean.getOptions()) {
					if (isQx(op, qx))
						options.add(op);
				}
				mb.setOptions(options);
				result.add(mb);
			} else {
				result.add(menuGridBean);
			}
		}
		return result;
	}

	private static boolean isQx(MenuOptionBean op, int qx) {
		int pw = GlobalMethod.power(2, op.getId());
		int i = pw & qx;
		return (i == pw);
	}

}
