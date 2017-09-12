package com.jwt.printer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;
import android.view.Gravity;

import com.jwt.pojo.AcdSimpleBean;
import com.jwt.pojo.AcdSimpleHumanBean;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;


public class PrintAcdTools {

	public static String getPreviewAcd(AcdSimpleBean acd,
									   List<AcdSimpleHumanBean> humans) {
		ArrayList<JdsPrintBean> jds = getPrintAcdContent(acd, humans);
		StringBuilder sb = new StringBuilder();
		for (JdsPrintBean j : jds) {
			String c = j.getContent().replaceAll("", "&nbsp;");
			sb.append(c).append("<br>");
		}
		return sb.toString();
	}

	public static ArrayList<JdsPrintBean> getPrintAcdContent(AcdSimpleBean acd,
			List<AcdSimpleHumanBean> humans) {
		ArrayList<JdsPrintBean> jds = new ArrayList<JdsPrintBean>();
		SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
		SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdfNoSec = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (acd == null || humans == null || humans.size() == 0)
			return null;
		String fxjgqc = GlobalData.grxx.get(GlobalConstant.BMMC);
		String sj = sdfShort.format(new Date());
		jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(0, 6)));
		jds.add(new JdsPrintBean(Gravity.CENTER, fxjgqc.substring(6)));

		jds.add(new JdsPrintBean(Gravity.CENTER, "道路交通事故认定书（简易程序）"));
		jds.add(new JdsPrintBean(Gravity.CENTER, "第 " + acd.getWsbh() + " 号"));
		try {
			String sjs = sdfFull.format(sdfNoSec.parse(acd.getSgfssj()));
			jds.add(new JdsPrintBean(Gravity.LEFT, "事故时间：" + sjs));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jds.add(new JdsPrintBean(Gravity.LEFT, "天气："
				+ GlobalMethod.getStringFromKVListByKey(GlobalData.arrayAcdTq,
						acd.getTq())));

		jds.add(new JdsPrintBean(Gravity.LEFT, "事故地点：" + acd.getSgdd()));
		for (AcdSimpleHumanBean human : humans) {
			jds.add(new JdsPrintBean(Gravity.LEFT,
					"________________________________"));// 32格
			jds.add(new JdsPrintBean(Gravity.LEFT, "当事人：" + human.getXm()));
			if (!TextUtils.isEmpty(human.getSfzmhm()))
				jds.add(new JdsPrintBean(Gravity.LEFT, "驾驶证或身份证号码："
						+ human.getSfzmhm()));
			if (!TextUtils.isEmpty(human.getDh()))
				jds.add(new JdsPrintBean(Gravity.LEFT, "联系电话：" + human.getDh()));
			if (!TextUtils.isEmpty(human.getJtfs()))
				jds.add(new JdsPrintBean(Gravity.LEFT, "交通方式："
						+ GlobalMethod.getStringFromKVListByKey(
								GlobalData.acdJtfsList, human.getJtfs())));
			if (!TextUtils.isEmpty(human.getHpzl()))
				jds.add(new JdsPrintBean(Gravity.LEFT, "号牌种类："
						+ GlobalMethod.getStringFromKVListByKey(
								GlobalData.hpzlList, human.getHpzl())));
			if (!TextUtils.isEmpty(human.getHphm()))
				jds.add(new JdsPrintBean(Gravity.LEFT, "号牌号码："
						+ human.getHphm()));
			if (!TextUtils.isEmpty(human.getBxpzh()))
				jds.add(new JdsPrintBean(Gravity.LEFT, "保险凭证号："
						+ human.getBxgs() + "-" + human.getBxpzh()));
		}
		jds.add(new JdsPrintBean(Gravity.LEFT,
				"________________________________"));// 32格
		jds.add(new JdsPrintBean(Gravity.LEFT, "交通事故事实及责任："));
		jds.add(new JdsPrintBean(Gravity.LEFT, "    " + acd.getSgss()));
		for (int i = 0; i < humans.size(); i++) {
			jds.add(new JdsPrintBean(Gravity.LEFT, ""));
			jds.add(new JdsPrintBean(Gravity.LEFT, "当事人：___________________"));

		}
		jds.add(new JdsPrintBean(Gravity.LEFT, "交通警察："
				+ GlobalData.grxx.get(GlobalConstant.XM)));
		jds.add(new JdsPrintBean(Gravity.RIGHT, sj));
		if (!TextUtils.isEmpty(acd.getZrtjjg())
				&& TextUtils.equals("1", acd.getJafs())) {
			jds.add(new JdsPrintBean(Gravity.LEFT, "损害赔偿调解结果："));
			jds.add(new JdsPrintBean(Gravity.LEFT, "    " + acd.getZrtjjg()));
			for (int i = 0; i < humans.size(); i++) {
				jds.add(new JdsPrintBean(Gravity.LEFT, ""));
				jds.add(new JdsPrintBean(Gravity.LEFT,
						"当事人：___________________"));
			}
			jds.add(new JdsPrintBean(Gravity.LEFT, "交通警察："
					+ GlobalData.grxx.get(GlobalConstant.XM)));
			jds.add(new JdsPrintBean(Gravity.RIGHT, sj));

		}
		jds.add(new JdsPrintBean(Gravity.LEFT, "有下列情形之一"
				+ "或者调解未达成协议及调解生效后当事人不履行的，" + "当事人可以向人民法院提起民事诉讼："
				+ "（一）当事人对交通事故认定有异议的；" + "（二）当事人拒绝签名的；" + "（三）当事人不同意由交通警察调解的。"));
		jds.add(new JdsPrintBean(Gravity.LEFT, ""));
		return jds;
	}
}
