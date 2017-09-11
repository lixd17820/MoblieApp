package com.jwt.update;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.jwt.dao.WfdmDao;
import com.jwt.dao.WsglDAO;
import com.jwt.jbyw.VerifyData;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

public class VioQwjgActivity extends ViolationActivity {
	private static final String TAG = "VioQwjgActivity";

	private Context self;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "VIOLATION ON CREATE");
		// 常量赋值
		self = this;

		zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);
		if (TextUtils.isEmpty(zqmj))
			return;

		jdsbh = WsglDAO.getCurrentJdsbh(GlobalConstant.JYCFJDS, zqmj, GlobalMethod.getBoxStore(self));
		if (jdsbh == null) {
			GlobalMethod.showDialogWithListener("提示信息",
					"没有相应的处罚编号，请到文书管理中获取编号", "确定", exitSystem, self);
			return;
		}

		if (WsglDAO.hmNotEqDw(jdsbh)) {
			GlobalMethod.showDialogWithListener("提示信息",
					"当前文书编号与处罚机关不符，请上交文书后重新获取", "确定", exitSystem, self);
			return;
		}

		// 设置监听
		setTitle(getActivityTitle());
		textJdsbh.setText("决定书编号："+ jdsbh.getDqhm());
		//
		initViolation();
        cfzl = "1";
        wslb = "1";
        violation.setCfzl(cfzl);
        violation.setWslb(wslb);
		violation.setJkfs("0");
		violation.setJkbj("9");
		violation.setFkje(0);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_qzcs);
		rl.removeAllViewsInLayout();
		RelativeLayout r2 = (RelativeLayout) findViewById(R.id.layout_jycx);
		r2.removeAllViewsInLayout();
		((RelativeLayout) findViewById(R.id.layout_jkfs)).setVisibility(View.GONE);
	}

	@Override
	protected String getViolationTitle() {
		return "轻微警告";
	}

	@Override
	protected String saveAndCheckVio() {
		getViolationFromView(violation);
		String err = VerifyData.verifyCommVio(violation,self);
		if (!TextUtils.isEmpty(err))
			return err;
		// 缴款方式
		String wfxwdm = edWfxw.getText().toString().trim();
		VioWfdmCode wfxwBean = WfdmDao.queryWfxwByWfdm(wfxwdm, GlobalMethod.getBoxStore(self));
		if (wfxwBean == null) {
			return "错误的违法代码!";
		}
		if (!WfdmDao.isYxWfdm(wfxwBean)) {
			return "违法代码不在有效期内!";
		}
		if (TextUtils.equals(wfxwBean.getJgbj(), "0"))
			return "此违法行为不能用于警告";
		violation.setWfxw(wfxwBean.getWfxw());
		err = VerifyData.verifyQwjgVio(violation);
		return err;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case (R.id.save_quite):
			return menuSaveViolation();
		case (R.id.print_preview):
			// 预览打印
			return menuPreviewViolation();

		case (R.id.pre_print):
			// 单据已保存，打印决定书
			return menuPrintViolation();
		case R.id.con_vio: 
			if (violation != null && isViolationSaved)
				showConVio(violation);
			else
				GlobalMethod.showToast("请保存当前决定书", self);
			return true;
		case R.id.sys_config:
			Intent intent = new Intent(self, ConfigParamSetting.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.punish_menu_qwjg, menu);
		return true;
	}

	protected String showWfdmDetail(VioWfdmCode w) {
		String s = w.getWfxw() + ": " + w.getWfms();
		// 简易程序将显示是否可罚款或警告
		s += ", 罚款" + w.getFkjeDut() + "元, 记" + w.getWfjfs() + "分";
		s += "| 罚款 " + (TextUtils.equals(w.getFkbj(), "1") ? "是" : "否");
		s += "| 警告 " + (TextUtils.equals(w.getJgbj(), "1") ? "是" : "否");
		s += "| " + (WfdmDao.isYxWfdm(w) ? "有效代码" : "无效代码");
		return s;
	}

}
