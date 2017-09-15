package com.jwt.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;


import com.jwt.activity.ActionBarSelectListActivity;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.SpringDjItf;
import com.jwt.bean.TwoColTwoSelectBean;
import com.jwt.dao.MessageDao;
import com.jwt.thread.UploadSpringThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class JbywSpringDjListActivity extends ActionBarSelectListActivity
		implements View.OnClickListener {

	private static final int REQ_KCDJ = 100;
	private static final int REQ_WHPDJ = 200;
	protected static final int MENU_DEL_DJ = 301;
	protected static final int MENU_UPLOAD_DJ = 302;

	private Context self;

	private Button btnKcdj, btnWhpdj, btnUpload;

	private Spinner spinDjlx;

	private List<SpringDjItf> springDjList;

	private List<KeyValueBean> djlxList;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		self = this;
		setContentView(R.layout.jbyw_spring_list);
		btnKcdj = (Button) findViewById(R.id.btn_left);
		btnKcdj.setText("客车登录");
		btnWhpdj = (Button) findViewById(R.id.btn_center);
		btnWhpdj.setText("危化车登录");
		btnUpload = (Button) findViewById(R.id.btn_right);
		btnUpload.setText("上传");
		spinDjlx = (Spinner) findViewById(R.id.spin_djlx);
		btnKcdj.setOnClickListener(this);
		btnWhpdj.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		initView();
		djlxList = new ArrayList<KeyValueBean>();
		djlxList.add(new KeyValueBean("0", "大客车登记"));
		djlxList.add(new KeyValueBean("1", "危化品车登记"));
		GlobalMethod.changeAdapter(spinDjlx, djlxList, (Activity) self);
		referListView();
		String title = getIntent().getStringExtra("title");
		setTitle(TextUtils.isEmpty(title) ? "春运车辆登记" : title);

		getListView().setOnCreateContextMenuListener(contextMenuListener);
		spinDjlx.setOnItemSelectedListener(new OnSpinnerItemSelected() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				referListView();
			}

		});


	}

	private OnCreateContextMenuListener contextMenuListener = new OnCreateContextMenuListener() {

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;
			int pos = mi.position;
			if (pos > -1 && springDjList != null && !springDjList.isEmpty()) {
				SpringDjItf dj = springDjList.get(pos);
				menu.add(Menu.NONE, MENU_DEL_DJ, Menu.NONE, "删除该登记");
				if (dj.getScbj() != 1)
					menu.add(Menu.NONE, MENU_UPLOAD_DJ, Menu.NONE, "上传该登录");
			}
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int pos = mi.position;
		if (pos > -1 && springDjList != null && !springDjList.isEmpty()) {
			SpringDjItf dj = springDjList.get(pos);
			switch (item.getItemId()) {
			case MENU_DEL_DJ:
				if (dj.getDjlx() == 0) {
					MessageDao.delKcdjById(dj.getId(),GlobalMethod.getBoxStore(self));
				} else {
					MessageDao.delWhpdjById(dj.getId(),GlobalMethod.getBoxStore(self));
				}
				referListView();
				break;
			case MENU_UPLOAD_DJ:
				uploadDj(dj);
				break;
			default:
				break;
			}
		}
		return false;
	}

	private void uploadDj(SpringDjItf dj) {
		SpringHandler h = new SpringHandler(JbywSpringDjListActivity.this,
				dj.getId(), dj.getDjlx());
		UploadSpringThread thread = new UploadSpringThread(h, dj, self);
		thread.start();
	}

	static class SpringHandler extends Handler {

		private final WeakReference<JbywSpringDjListActivity> myActivity;
		private int djlx;
		private String id;;

		public SpringHandler(JbywSpringDjListActivity activity, String id,
				int djlx) {
			myActivity = new WeakReference<JbywSpringDjListActivity>(activity);
			this.djlx = djlx;
			this.id = id;
		}

		@Override
		public void handleMessage(Message msg) {
			JbywSpringDjListActivity ac = myActivity.get();
			if (ac != null) {
				ac.uploadHandler(msg, id, djlx);
			}
		}
	}

	private void uploadHandler(Message msg, String id, int djlx) {
		Bundle data = msg.getData();
		WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) data
				.getSerializable(UploadSpringThread.UPLOAD_RESULT);
		String err = GlobalMethod.getErrorMessageFromWeb(re);
		if (TextUtils.isEmpty(err)) {
			ZapcReturn zr = re.getResult();
			if (TextUtils.equals(zr.getCgbj(), "1")) {
				MessageDao.updateSpringScbj(id, djlx,GlobalMethod.getBoxStore(self));
				referListView();
			}
			GlobalMethod.showDialog("系统提示", zr.getScms(), "确定", self);
			return;
		}
		GlobalMethod.showErrorDialog(err, self);

	}

	private void updateDjList() {
		String lx = GlobalMethod.getKeyFromSpinnerSelected(spinDjlx,
				GlobalConstant.KEY);
		List<SpringDjItf> list = null;
		if (TextUtils.equals("0", lx))
			list = MessageDao.getAllKcdj(GlobalMethod.getBoxStore(self));
		else
			list = MessageDao.getAllWhpdj(GlobalMethod.getBoxStore(self));
		if (springDjList == null)
			springDjList = new ArrayList<SpringDjItf>();
		springDjList.clear();
		for (SpringDjItf s : list) {
			springDjList.add(s);
		}
	}

	private void referListView() {
		updateDjList();
		beanList.clear();
		if (!springDjList.isEmpty()) {
			for (SpringDjItf dj : springDjList) {
				String text1 = dj.getJcsj() + " | 车号：" + dj.getHphm();
				String text2 = "驾驶员：" + dj.getDsr() + "| 类型："
						+ (dj.getDjlx() == 0 ? "客车" : "危化品")
						+ (dj.getScbj() > 0 ? " | 已上传" : " | 未上传");
				boolean isSc = dj.getScbj() > 0;
				beanList.add(new TwoColTwoSelectBean(text1, text2, isSc, false));
			}
		}
		getCommAdapter().notifyDataSetChanged();
		selectedIndex = -1;
	}

	@Override
	public void onClick(View v) {
		if (v == btnKcdj) {
			Intent intent = new Intent(self, JbywSpringDjMainActivity.class);
			intent.putExtra("lx", 0);
			startActivityForResult(intent, REQ_KCDJ);
		} else if (v == btnWhpdj) {
			Intent intent = new Intent(self, JbywSpringDjMainActivity.class);
			intent.putExtra("lx", 1);
			startActivityForResult(intent, REQ_WHPDJ);
		} else if (v == btnUpload) {
			if (selectedIndex > -1) {
				SpringDjItf dj = springDjList.get(selectedIndex);
				uploadDj(dj);
			} else {
				GlobalMethod.showErrorDialog("请选择一个项目进行上传", self);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_KCDJ) {
				GlobalMethod.changeSpinnerSelect(spinDjlx, "0",
						GlobalConstant.KEY);
			} else if (requestCode == REQ_WHPDJ) {
				GlobalMethod.changeSpinnerSelect(spinDjlx, "1",
						GlobalConstant.KEY);
			}
			referListView();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
