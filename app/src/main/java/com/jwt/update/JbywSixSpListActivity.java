package com.jwt.update;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.jwt.activity.ActionBarSelectListActivity;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TTViolation;
import com.jwt.bean.TwoColTwoSelectBean;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

public class JbywSixSpListActivity extends ActionBarSelectListActivity
		implements View.OnClickListener {

	private static final int REQ_SP = 100;

	private Context self;

	private Button btnQuerySix, btnSp, btnExit;

	private Spinner spinDwqx;

	private List<TTViolation> sixSpList;

	private String[] spDws;

	private List<KeyValueBean> dwList;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.jbyw_sixsp_list);
		btnQuerySix = (Button) findViewById(R.id.btn_left);
		btnQuerySix.setText("查未审批");
		btnSp = (Button) findViewById(R.id.btn_center);
		btnSp.setText("审批");
		btnExit = (Button) findViewById(R.id.btn_right);
		btnExit.setText("退出");
		spinDwqx = (Spinner) findViewById(R.id.spin_six_dw);
		btnQuerySix.setOnClickListener(this);
		btnSp.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		initView();
		dwList = new ArrayList<KeyValueBean>();
		CheckSpQxThread cthread = new CheckSpQxThread(checkQxHandler);
		cthread.start();
		referListView();
		setTitle(getIntent().getStringExtra("title"));
	}

	private void createSp() {
		if (spDws != null && spDws.length > 0) {
			dwList.clear();
			for (String k : spDws) {
				dwList.add(new KeyValueBean(k, GlobalConstant.fxjgMap.get(k)));
			}
			GlobalMethod.changeAdapter(spinDwqx, dwList, (Activity) self);
		}
	}

	private void referListView() {
		if (sixSpList == null)
			sixSpList = new ArrayList<TTViolation>();
		beanList.clear();
		if (!sixSpList.isEmpty()) {
			for (TTViolation vio : sixSpList) {
				String text1 = vio.getJdsbh() + "|" + vio.getWfxw();
				String text2 = vio.getWfsj() + "|" + vio.getZqmj();
				boolean isSc = !TextUtils.equals(vio.getScbj(), "9");
				beanList.add(new TwoColTwoSelectBean(text1, text2, isSc, false));
			}
		}
		getCommAdapter().notifyDataSetChanged();
		selectedIndex = -1;
	}

	@Override
	public void onClick(View v) {
		if (v == btnQuerySix) {
			String dw = GlobalMethod.getKeyFromSpinnerSelected(spinDwqx,
					GlobalConstant.KEY);
			if (TextUtils.isEmpty(dw)) {
				Toast.makeText(self, "获取单位代码失败", Toast.LENGTH_LONG).show();
				return;
			}
			sixSpList.clear();
			beanList.clear();
			QueryUnspThread qThread = new QueryUnspThread(uploadHandler, dw);
			qThread.doStart();
		} else if (v == btnSp) {
			if (selectedIndex < 0) {
				Toast.makeText(self, "未选择任何记录", Toast.LENGTH_LONG).show();
				return;
			}
			TTViolation vio = sixSpList.get(selectedIndex);
			if (vio == null) {
				Toast.makeText(self, "选择记录出现错误，请重新获取", Toast.LENGTH_LONG)
						.show();
				return;
			}
			if (!TextUtils.equals(vio.getScbj(), "9")) {
				Toast.makeText(self, "该记录状态不处于可审批状态，请重新获取", Toast.LENGTH_LONG)
						.show();
				return;
			}
			Intent intent = new Intent(self, JbywSixSpActivity.class);
			intent.putExtra("vio", vio);
			startActivityForResult(intent, REQ_SP);
		} else if (v == btnExit) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_SP) {
			if (resultCode == RESULT_OK) {
				ZapcReturn re = (ZapcReturn) data.getSerializableExtra("spjg");
				if (re != null) {
					GlobalMethod.showDialog("系统提示", re.getScms(), "确定", self);
					if (TextUtils.equals(re.getCgbj(), "8")) {
						String jdsbh = re.getPcbh()[0];
						for (int i = 0; i < sixSpList.size(); i++) {
							if (TextUtils.equals(jdsbh, sixSpList.get(i)
									.getJdsbh())) {
								sixSpList.get(i).setScbj("8");
								break;
							}
						}
						referListView();
					}
				} else {
					GlobalMethod.showErrorDialog("审批出现未错误", self);
				}
			}
		}
	}

	Handler checkQxHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			String qx = b.getString("qx");
			if (TextUtils.isEmpty(qx)) {
				GlobalMethod.showDialogWithListener("系统提示", "对不起，您无权使用该模块！",
						"确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}, self);
			} else {
				spDws = qx.split(",");
				createSp();
			}
		}
	};

	class CheckSpQxThread extends Thread {
		private Handler mHandler;

		public CheckSpQxThread(Handler handler) {
			this.mHandler = handler;
		}

		@Override
		public void run() {
			String qxs = "";
			RestfulDao dao = RestfulDaoFactory.getDao();
			String jh = GlobalData.grxx.get(GlobalConstant.YHBH);
			WebQueryResult<ZapcReturn> rs = dao.checkSixSp(jh.substring(2));
			String err = GlobalMethod.getErrorMessageFromWeb(rs);
			if (TextUtils.isEmpty(err)) {
				ZapcReturn zcre = rs.getResult();
				if (zcre != null) {
					String cgbj = zcre.getCgbj();
					// if("1")
					String[] qx = zcre.getPcbh();
					if (qx != null && qx.length > 0) {
						qxs = qx[0];
					}
				}
			}
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("qx", qxs);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

	}

	class QueryUnspThread extends Thread {

		private Handler mHandler;
		private String dw;

		public QueryUnspThread(Handler handler, String dw) {
			this.mHandler = handler;
			this.dw = dw;
		}

		/**
		 * 启动线程
		 */
		public void doStart() {
			// 显示进度对话框
			progressDialog = new ProgressDialog(self);
			progressDialog.setTitle("提示");
			progressDialog.setMessage("系统正在查询请稍等...");
			progressDialog.setCancelable(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
			this.start();
		}

		/**
		 * 线程运行，上传盘查，成功后发送信号给进度条
		 */
		@Override
		public void run() {
			RestfulDao dao = RestfulDaoFactory.getDao();
			WebQueryResult<List<TTViolation>> rs = dao.querySixSpList(dw, "9");
			String error = GlobalMethod.getErrorMessageFromWeb(rs);
			if (TextUtils.isEmpty(error)) {
				List<TTViolation> vios = rs.getResult();
				if (vios != null && !vios.isEmpty()) {
					sixSpList = vios;
					error = "共查询到" + vios.size() + "条记录";
				} else {
					error = "未查询到记录";
				}
			}
			if (progressDialog.isShowing())
				progressDialog.dismiss();
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("error", error);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}

	}

	Handler uploadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			String totalError = b.getString("error");
			GlobalMethod.showDialog("系统提示", totalError, "确定", self);
			referListView();
		}
	};

}
