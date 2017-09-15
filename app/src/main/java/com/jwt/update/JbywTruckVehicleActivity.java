package com.jwt.update;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TruckCheckBean;
import com.jwt.bean.TruckVehicleBean;
import com.jwt.dao.MessageDao;
import com.jwt.pojo.TruckDriverBean;
import com.jwt.thread.CommQueryThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.WebQueryResult;

public class JbywTruckVehicleActivity extends AppCompatActivity implements
		OnClickListener {

	private static final int REQCODE_CHECK_TRUCK = 10;
	public static final String INTENT_VEH = "truck_veh";
	private static final int REQCODE_CHECK_DRV = 11;
	private static final String RESULT_TRUCK_CHECK = "";
	private Spinner spHpzl, spHpqz;
	protected TextView truckInfo;
	private EditText edHphm;
	private Button btnQueryTruck, btnCheckTruck, btnCheckDrv;
	private Context self;
	private TruckCheckBean truckCheckBean = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jbyw_truck_vehicle);
		setTitle("重型车查询登记");
		self = this;
		spHpzl = (Spinner) findViewById(R.id.spin_hpzl);
		spHpqz = (Spinner) findViewById(R.id.spin_hpqz);
		truckInfo = (TextView) findViewById(R.id.truck_info);
		edHphm = (EditText) findViewById(R.id.edit_hphm);
		btnQueryTruck = (Button) findViewById(R.id.btn_query_veh);
		btnCheckTruck = (Button) findViewById(R.id.btn_check_veh);
		btnCheckDrv = (Button) findViewById(R.id.btn_check_drv);
		btnQueryTruck.setOnClickListener(this);
		btnCheckTruck.setOnClickListener(this);
		btnCheckDrv.setOnClickListener(this);
		List<KeyValueBean> kvs = new ArrayList<KeyValueBean>();
		kvs.add(new KeyValueBean("01", "大型汽车"));
		GlobalMethod.changeAdapter(spHpzl, kvs, this);
		GlobalMethod.changeAdapter(spHpqz, GlobalData.hpqlList, this);
		GlobalMethod.changeSpinnerSelect(spHpqz, "苏", GlobalConstant.VALUE);
		edHphm.setText("F");
		edHphm.setSelection(1);
		edHphm.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				truckCheckBean = null;
				showTruckInfo();
			}
		});
	}

	public String createText(String title, String nr, boolean isNeed) {
		String s = title + "：" + GlobalMethod.ifNull(nr);
		if (isNeed && TextUtils.isEmpty(nr)) {
			s = "<font color='red'>" + s + "</font>";
		}
		s = "&nbsp;&nbsp;&nbsp;&nbsp;" + s + "<br>";
		return s;
	}

	private void showTruckInfo() {
		if (truckCheckBean != null) {
			TruckVehicleBean truck = truckCheckBean.getTruck();
			String info = "车辆登记情况：<br>";
			info += createText(
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;登记车主",
					truck.getSyr(), false);
			info += createText(
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆类型 ",
					GlobalMethod.getStringFromKVListByKey(GlobalData.jtfsList,  truck.getCllx()), false);
			info += createText("实际车主姓名 ", truck.getSjsyr(), true);
			info += createText("实主证件号码 ", truck.getSjsfzh(), true);
			info += createText("实主联系地址 ", truck.getSjlxdz(), true);
			info += createText("实主手机号码 ", truck.getSjsjhm(), true);
			String sjfwdw = "";
			if (!TextUtils.isEmpty(truck.getFwdwdm())) {
				KeyValueBean kv = MessageDao.queryQymcByBy(truck.getFwdwdm(),GlobalMethod.getBoxStore(self));
				sjfwdw = kv != null ? kv.getValue() : "";
			}
			info += createText("车辆服务单位 ", sjfwdw, true);
			TruckDriverBean[] drvs = truckCheckBean.getDrvs();
			if (drvs != null && drvs.length > 0) {
				for (int i = 0; i < drvs.length; i++) {
					TruckDriverBean d = drvs[i];
					info += "第" + (i + 1) + "个驾驶员登记信息：<br>";
					info += createText(
							"姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名 ",
							d.getXm(), false);
					info += createText("档案编号 ", d.getDabh(), false);
					info += createText("驾驶证号 ", d.getJszh(), false);
					info += createText("发证机关 ", d.getFzjg(), false);
					info += createText("手机号码 ", d.getSjhm(), false);
					info += createText("联系地址 ", d.getLxdz(), false);
					info += createText("准驾车型 ", d.getZjcx(), false);
				}
			} else {
				info += "未登记驾驶员<br>";
			}
			truckInfo.setText(Html.fromHtml(info));
		} else {
			truckInfo.setText("无登记情况");
		}
	}

	private void queryCheck() {
		String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
				GlobalConstant.KEY);
		Editable hp = edHphm.getText();
		String hpqz = GlobalMethod.getKeyFromSpinnerSelected(spHpqz,
				GlobalConstant.VALUE);
		String hphm = (hpqz + hp).toUpperCase();
		truckInfo.setText("");
		GlobalMethod.setEnable(true, btnCheckTruck);
		if (hphm.length() != 7) {
			GlobalMethod.showErrorDialog("号牌号码不正确", self);
			return;
		}
		CommQueryThread thread = new CommQueryThread(
				CommQueryThread.QUERY_TRUCK_CHECK,
				new String[] { hpzl, hphm }, self);
		thread.doStart();
	}

	@Override
	public void onClick(View v) {
		if (v == btnCheckDrv) {
			if (truckCheckBean == null || truckCheckBean.getTruck() == null) {
				GlobalMethod.showErrorDialog("必须要有车辆信息，才能登记驾驶员", self);
				return;
			}
			Intent intent = new Intent(JbywTruckVehicleActivity.this,
					JbywTruckDriverActivity.class);
			intent.putExtra(INTENT_VEH, truckCheckBean.getTruck());
			startActivityForResult(intent, REQCODE_CHECK_DRV);
		} else if (v == btnQueryTruck) {
			queryCheck();
		} else if (v == btnCheckTruck) {
			String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
					GlobalConstant.KEY);
			Editable hp = edHphm.getText();
			String hpqz = GlobalMethod.getKeyFromSpinnerSelected(spHpqz,
					GlobalConstant.VALUE);
			String hphm = (hpqz + hp).toUpperCase();
			Intent intent = new Intent(self, JbywTruckCheckActivity.class);
			if (hphm.length() != 7) {
				GlobalMethod.showErrorDialog("号牌号码不正确", self);
				return;
			}
			intent.putExtra("hphm", hphm);
			intent.putExtra("hpzl", hpzl);
			if (truckCheckBean != null && truckCheckBean.getTruck() != null) {
				intent.putExtra(INTENT_VEH, truckCheckBean.getTruck());
			}
			startActivityForResult(intent, REQCODE_CHECK_TRUCK);
		}
	}

	static class TruckQueryHandler extends Handler {

		private final WeakReference<JbywTruckVehicleActivity> myActivity;

		public TruckQueryHandler(JbywTruckVehicleActivity activity) {
			myActivity = new WeakReference<JbywTruckVehicleActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			JbywTruckVehicleActivity ac = myActivity.get();
			if (ac != null) {
				ac.queryTruckHandler(msg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void queryTruckHandler(Message msg) {
		Bundle b = msg.getData();
		WebQueryResult<TruckCheckBean> re = (WebQueryResult<TruckCheckBean>) b
				.getSerializable(RESULT_TRUCK_CHECK);
		String err = GlobalMethod.getErrorMessageFromWeb(re);
		if (TextUtils.isEmpty(err)) {
			truckCheckBean = re.getResult();
			showTruckInfo();
			return;
		}
		// else {
		// GlobalMethod.showErrorDialog(err, self);
		// }
		truckInfo.setText("车辆登记情况：\n无相关登记信息");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQCODE_CHECK_TRUCK
					|| requestCode == REQCODE_CHECK_DRV) {
				queryCheck();
			}
		}
	}

}
