package com.jwt.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TTViolation;
import com.jwt.dao.WfdmDao;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import java.util.ArrayList;
import java.util.List;

public class JbywSixSpActivity extends AppCompatActivity implements View.OnClickListener {

	private TextView vioDetail;
	private Spinner spinSpyj;
	private EditText editSpnr;
	private Button btnSp, btnExit;
	private TTViolation vio;
	private List<KeyValueBean> spyjSel;
	private Activity self;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jbyw_sixsp_detail);
		self = this;
		vio = (TTViolation) getIntent().getSerializableExtra("vio");
		if (vio == null)
			finish();

		vioDetail = (TextView) findViewById(R.id.text_vio_detail);
		spinSpyj = (Spinner) findViewById(R.id.spin_spyj);
		editSpnr = (EditText) findViewById(R.id.edit_spnr);
		btnSp = (Button) findViewById(R.id.btn_sp);
		btnExit = (Button) findViewById(R.id.btn_exit);
		btnSp.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		showVioDetail();
		spyjSel = new ArrayList<KeyValueBean>();
		spyjSel.add(new KeyValueBean("0", "不同意上传"));
		spyjSel.add(new KeyValueBean("1", "同意上传"));
		GlobalMethod.changeAdapter(spinSpyj, spyjSel, this);
		hideIM(editSpnr);

	}

	private void showVioDetail() {
		String s = "";
		s += "决定书编号: " + vio.getJdsbh() + "\n";
		s += "当事人: " + vio.getDsr() + "\n";
		s += "驾驶证号: " + vio.getJszh() + "\n";
		s += "档案编号: " + vio.getDabh() + "\n";
		s += "准驾车型: " + vio.getZjcx() + "\n";
		s += "号牌种类: "
				+ GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList,
						vio.getHpzl()) + "\n";
		s += "号牌号码: " + vio.getHphm() + "\n";
		s += "交通方式: "
				+ GlobalMethod.getStringFromKVListByKey(GlobalData.jtfsList,
						vio.getJtfs()) + "\n";
		s += "违法时间: " + vio.getWfsj() + "\n";
		s += "违法地点: " + vio.getWfdz() + "\n";
		s += "违法代码: " + vio.getWfxw() + "\n";
		s += "违法行为: "
				+ WfdmDao.queryWfxwByWfdm(vio.getWfxw(),
						GlobalMethod.getBoxStore(self)).getWfnr() + "\n";
		s += "罚款金额: " + vio.getFkje() + "元\n";
		s += "违法记分: " + vio.getWfjfs() + "分\n";
		s += "缴款方式: "
				+ GlobalMethod.getStringFromKVListByKey(GlobalData.jkfsList,
						vio.getJkfs()) + "\n";
		s += "值勤民警: " + vio.getZqmj() + "\n";
		s += "处理机关: "
				+ GlobalConstant.fxjgMap.get(vio.getFxjg().substring(0, 6))
				+ "\n";

		vioDetail.setText(s);
	}

	@Override
	public void onClick(View v) {
		if (v == btnSp) {
			final String spnr = editSpnr.getText().toString();
			if (TextUtils.isEmpty(spnr)) {
				editSpnr.setError("审批意见不能为空");
				return;
			}
			final String spjg = GlobalMethod.getKeyFromSpinnerSelected(
					spinSpyj, GlobalConstant.KEY);
			final String jdsbh = vio.getJdsbh();
			GlobalMethod.showDialogTwoListener("系统确认", "是否确定提交您的审批意见，此操作不能撤销？",
					"确定", "取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							SpThread thread = new SpThread(spHandler, jdsbh,
									spjg, spnr);
							thread.doStart();
						}
					}, self);

		} else if (v == btnExit) {
			finish();
		}
	}

	private void hideIM(View edt) {
		try {
			InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			IBinder windowToken = edt.getWindowToken();
			if (windowToken != null) {
				// always de-activate IM
				im.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {
			Log.e("HideInputMethod", "failed:" + e.getMessage());
		}
	}

	Handler spHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) b
					.getSerializable("re");
			String error = GlobalMethod.getErrorMessageFromWeb(re);
			Intent i = new Intent();
			if (TextUtils.isEmpty(error) && re.getResult() != null
					&& !TextUtils.equals(re.getResult().getCgbj(), "0")) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("spjg", re.getResult());
				i.putExtras(bundle);
				setResult(RESULT_OK, i);
				finish();
			} else {
				setResult(RESULT_CANCELED);
			}
		}
	};

	class SpThread extends Thread {
		private String jdsbh;
		private String spyj;
		private String spnr;
		private Handler mHandler;

		public SpThread(Handler mHandler, String jdsbh, String spyj, String spnr) {
			this.jdsbh = jdsbh;
			this.spyj = spyj;
			this.spnr = spnr;
			this.mHandler = mHandler;
		}

		public void doStart() {
			progressDialog = new ProgressDialog(self);
			progressDialog.setTitle("提示");
			progressDialog.setMessage("系统正在提交...");
			progressDialog.setCancelable(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			this.start();
		}

		@Override
		public void run() {
			String jh = GlobalData.grxx.get(GlobalConstant.YHBH);
			RestfulDao dao = RestfulDaoFactory.getDao();
			WebQueryResult<ZapcReturn> re = dao.submitSixSp(jdsbh, spyj, spnr,
					jh);
			if (progressDialog.isShowing())
				progressDialog.dismiss();
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putSerializable("re", re);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

	}
}
