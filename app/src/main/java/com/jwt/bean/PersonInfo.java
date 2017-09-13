package com.jwt.bean;

import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;

public class PersonInfo {
	String name;
	String sex;
	String nation;
	String birthday;
	String address;
	String idNum;
	String authority;
	String validStart;
	String validEnd;
	String sexCode;
	String nationCode;
	String birthday2;
	String validDate2;
	Bitmap photo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIdNum() {
		return idNum;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getValidStart() {
		return validStart;
	}

	public void setValidStart(String validStart) {
		this.validStart = validStart;
	}

	public String getValidEnd() {
		return validEnd;
	}

	public void setValidEnd(String validEnd) {
		this.validEnd = validEnd;
	}

	public String getSexCode() {
		return sexCode;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public String getNationCode() {
		return nationCode;
	}

	public void setNationCode(String nationCode) {
		this.nationCode = nationCode;
	}

	public String getBirthday2() {
		return birthday2;
	}

	public void setBirthday2(String birthday2) {
		this.birthday2 = birthday2;
	}

	public String getValidDate2() {
		return validDate2;
	}

	public void setValidDate2(String validDate2) {
		this.validDate2 = validDate2;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}

	public PersonInfo() {
		Empty();
	}

	public void Empty() {
		name = "";
		sex = "";
		nation = "";
		birthday = "";
		address = "";
		idNum = "";
		authority = "";
		validStart = "";
		validEnd = "";
		sexCode = "";
		nationCode = "";
		birthday2 = "";
		validDate2 = "";
		photo = null;
	}

	public static PersonInfo Parse(byte[] src, int offset) {
		PersonInfo pi = new PersonInfo();
		try {
			pi.name = new String(src, offset + 0, 30, "UTF-16LE");
			pi.sexCode = new String(src, offset + 30, 2, "UTF-16LE");
			pi.nationCode = new String(src, offset + 32, 4, "UTF-16LE");
			pi.birthday = new String(src, offset + 36, 16, "UTF-16LE");
			pi.address = new String(src, offset + 52, 70, "UTF-16LE");
			pi.idNum = new String(src, offset + 122, 36, "UTF-16LE");
			pi.authority = new String(src, offset + 158, 30, "UTF-16LE");
			pi.validStart = new String(src, offset + 188, 16, "UTF-16LE");
			pi.validEnd = new String(src, offset + 204, 16, "UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (pi.sexCode.equals("1")) {
			pi.sex = new String("男");
		} else if (pi.sexCode.equals("2")) {
			pi.sex = new String("女");
		} else {
			pi.sex = new String();
		}
		pi.nation = getNation(Integer.parseInt(pi.nationCode));

		pi.name = pi.name.replace(" ", "");
		pi.address = pi.address.trim();
		pi.authority = pi.authority.trim();
		pi.validEnd = pi.validEnd.trim();

		pi.birthday2 = ConvertDate(pi.birthday, 1);
		pi.validDate2 = ConvertDate(pi.validStart, 2) + "-"
				+ ConvertDate(pi.validEnd, 2);
		return pi;
	}

	static String getNation(int nationCode) {
		String[] nationName = { "", "汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮",
				"布依", "朝鲜", "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎",
				"傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土",
				"达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米",
				"塔吉克", "怒", "乌孜别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔",
				"独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺" };

		if (nationCode > 0 && nationCode < 57)
			return nationName[nationCode];
		else if (98 == nationCode)
			return "外国血统中国籍人士";
		else
			return "其他";

	}

	static String ConvertDate(String date, int mode) {
		String year;
		String month;
		String day;

		if (date.length() == 8) {
			year = date.substring(0, 4);
			month = date.substring(4, 6);
			day = date.substring(6, 8);
			if (mode == 1) {
				return year + "年" + month + "月" + day + "日";
			} else {
				return year + "." + month + "." + day;
			}
		} else if (date.equals("长期")) {
			return date;
		} else {
			return "";
		}
	}
}
