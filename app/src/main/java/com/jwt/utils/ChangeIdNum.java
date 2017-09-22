package com.jwt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChangeIdNum {

	static int[] wi = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
			8, 4, 2, 1 };
	static char[] ai = new char[] { '1', '0', 'X', '9', '8', '7', '6', '5',
			'4', '3', '2' };
	static String[] unit = new String[] { "", "s", "b", "q" };

	static Map<String, String> bigChina = new HashMap<String, String>();
	static Map<String, String> bigNum = new HashMap<String, String>();
	static Map<String, String> sf = new HashMap<String, String>();
	static {
		bigChina.put("0", "零");
		bigChina.put("1", "壹");
		bigChina.put("2", "贰");
		bigChina.put("3", "叁");
		bigChina.put("4", "肆");
		bigChina.put("5", "伍");
		bigChina.put("6", "陆");
		bigChina.put("7", "柒");
		bigChina.put("8", "捌");
		bigChina.put("9", "玖");
		bigChina.put("s", "拾");
		bigChina.put("b", "佰");
		bigChina.put("q", "仟");
		bigChina.put("w", "万");
		bigChina.put("y", "亿");
		bigNum.put("0", "〇");
		bigNum.put("1", "一");
		bigNum.put("2", "二");
		bigNum.put("3", "三");
		bigNum.put("4", "四");
		bigNum.put("5", "五");
		bigNum.put("6", "六");
		bigNum.put("7", "七");
		bigNum.put("8", "八");
		bigNum.put("9", "九");
		bigNum.put("s", "十");
		bigNum.put("b", "百");
		bigNum.put("q", "千");
		bigNum.put("w", "万");
		bigNum.put("y", "亿");
	}

	static {
		sf.put("藏", "西藏");
		sf.put("川", "四川");
		sf.put("鄂", "湖北");
		sf.put("甘", "甘肃");
		sf.put("赣", "江西");
		sf.put("桂", "广西");
		sf.put("贵", "贵州");
		sf.put("黑", "黑龙江");
		sf.put("沪", "上海");
		sf.put("吉", "吉林");
		sf.put("冀", "河北");
		sf.put("津", "天津");
		sf.put("晋", "山西");
		sf.put("京", "北京");
		sf.put("辽", "辽宁");
		sf.put("鲁", "山东");
		sf.put("蒙", "内蒙古自治区");
		sf.put("闽", "福建");
		sf.put("宁", "宁夏");
		sf.put("青", "青海");
		sf.put("琼", "海南");
		sf.put("陕", "陕西");
		sf.put("苏", "江苏");
		sf.put("皖", "安徽");
		sf.put("湘", "湖南");
		sf.put("新", "新疆");
		sf.put("渝", "重庆");
		sf.put("豫", "河南");
		sf.put("粤", "广东");
		sf.put("云", "云南");
		sf.put("浙", "浙江");
	}

	public static String changeId(String id) {
		if (id.length() == 15) {
			id = id.substring(0, 6) + "19" + id.substring(6);

			int sum = 0;
			for (int i = 0; i < id.length(); i++) {
				sum += (id.charAt(i) - 48) * wi[i];
			}
			return id + ai[(sum % 11)];
		}
		return id;
	}

	public static StringBuilder changNumUpper(int je) {
		String sje = String.valueOf(je);
		return changNumUpper(sje);
	}

	public static StringBuilder changNumUpper(String sje) {
		StringBuilder sb = new StringBuilder();
		if ("0".equals(sje))
			return sb.append("0");
		int gs = sje.length();
		for (int i = 0; i < sje.length(); i++) {
			if (sje.charAt(i) - 48 == 0) {
				if ((gs - i - 1 != 4))
					sb.append(sje.charAt(i));
			} else
				sb.append(sje.charAt(i)).append(unit[(gs - i - 1) % 4]);
			if (gs - i - 1 == 4)
				sb.append("w");
			if (gs - i - 1 == 8)
				sb.append("y");
		}
		while (sb.lastIndexOf("0") == sb.length() - 1)
			sb.deleteCharAt(sb.length() - 1);

		while (sb.indexOf("00") > -1)
			sb.deleteCharAt(sb.indexOf("00"));

		while (sb.indexOf("0w") > -1)
			sb.deleteCharAt(sb.indexOf("0w"));

		while (sb.indexOf("0y") > -1)
			sb.deleteCharAt(sb.indexOf("0y"));

		while (sb.indexOf("1s") == 0)
			sb.deleteCharAt(0);

		return sb;
	}

	public static String changToChina(String small) {
		StringBuilder sb = changNumUpper(small);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < sb.length(); i++) {
			result.append(bigChina.get(String.valueOf(sb.charAt(i))));
		}
		return result.toString();
	}

	public static String changToChina(int small) {
		return changToChina(String.valueOf(small));
	}

	public static String changToBignum(String small) {
		StringBuilder sb = changNumUpper(small);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < sb.length(); i++) {
			result.append(bigNum.get(String.valueOf(sb.charAt(i))));
		}
		return result.toString();
	}

	public static String changToBignum(int small) {
		return changToBignum(String.valueOf(small));
	}

	public static String readYear(String year) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < year.length(); i++) {
			result.append(bigNum.get(String.valueOf(year.charAt(i))));
		}
		return result.toString();
	}

	public static String readYear(int year) {
		return readYear(String.valueOf(year));
	}

	public static String changDateToChina(String strDate, int style) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Date wfsj = new Date();
		try {
			wfsj = sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.setTime(wfsj);
		StringBuilder wfsjcnB = new StringBuilder().append(
				readYear(cal.get(Calendar.YEAR))).append("年").append(
				changToBignum(cal.get(Calendar.MONTH) + 1)).append("月").append(
				changToBignum(cal.get(Calendar.DAY_OF_MONTH))).append("日");
		if (style == 0) {
			wfsjcnB.append(changToBignum(cal.get(Calendar.HOUR_OF_DAY)))
					.append("时")
					.append(changToBignum(cal.get(Calendar.MINUTE)))
					.append("分");
		}
		return wfsjcnB.toString();
	}

	public static String changDateToChina(Date date, int style) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		StringBuilder wfsjcnB = new StringBuilder().append(
				readYear(cal.get(Calendar.YEAR))).append("年").append(
				changToBignum(cal.get(Calendar.MONTH) + 1)).append("月").append(
				changToBignum(cal.get(Calendar.DAY_OF_MONTH))).append("日");
		if (style == 0) {
			wfsjcnB.append(changToBignum(cal.get(Calendar.HOUR_OF_DAY)))
					.append("时")
					.append(changToBignum(cal.get(Calendar.MINUTE)))
					.append("分");
		}
		return wfsjcnB.toString();
	}

	public static String BigDate(Date date, boolean isTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		if (isTime)
			sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
		return sdf.format(date);
	}

	public static String getSF(String sx) {
		return sf.get(sx);
	}

}
