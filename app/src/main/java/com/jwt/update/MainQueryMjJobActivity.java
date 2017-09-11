package com.jwt.update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.bean.MjJobBean;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

public class MainQueryMjJobActivity extends ActionBarActivity {

	private EditText editStime, editEtime;
	private Button btnQuery, btnCancel, btnChgStime, btnChgEtime;
	private TextView tvContent;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Context self;
	private ProgressDialog progressDialog;
	private static final String JOB = "job";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_mj_job);
		setTitle(getIntent().getStringExtra("title"));
		self = this;
		editStime = (EditText) findViewById(R.id.edit_stime);
		editEtime = (EditText) findViewById(R.id.edit_etime);
		btnQuery = (Button) findViewById(R.id.btn_query);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnChgStime = (Button) findViewById(R.id.btn_chg_stime);
		btnChgEtime = (Button) findViewById(R.id.btn_chg_etime);

		tvContent = (TextView) findViewById(R.id.tv_content);
		Calendar calendar = Calendar.getInstance();
		Date today = new Date();
		calendar.setTime(today);
		editEtime.setText(sdf.format(today));
		int month = calendar.get(Calendar.MONTH);
		if (month > 1) {
			calendar.set(Calendar.MONTH, month - 1);
		} else {
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
			calendar.set(Calendar.MONTH, 11);
		}
		calendar.set(Calendar.DAY_OF_MONTH, 21);

		editStime.setText(sdf.format(calendar.getTime()));

		btnChgEtime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				changeDate(editEtime, self);
			}
		});
		btnChgStime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				changeDate(editStime, self);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		btnQuery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
				String stime = editStime.getText().toString();
				String etime = editEtime.getText().toString();
				try {
					Date st = sdf.parse(stime);
					Date et = sdf.parse(etime);
					long kd = (et.getTime() - st.getTime()) / 1000;
					long td = 60 * 24 * 60 * 60;
					if (kd > td) {
						GlobalMethod.showErrorDialog("查询时间跨度不能超过60天", self);
					} else {
						QueryMjJobThread thread = new QueryMjJobThread(self,
								handler);
						thread.doStart(jybh, stime, etime);
					}
				} catch (ParseException e) {
					e.printStackTrace();
					GlobalMethod.showErrorDialog("日期格式有错误，请重新录入", self);
				}
			}
		});
	}

	private void changeDate(final EditText edDateTime, Context context) {
		final Calendar calendar = Calendar.getInstance();
		// 读取控件的时间
		Date d = null;
		try {
			d = sdf.parse(edDateTime.getText().toString());
		} catch (Exception e) {
		}
		if (d == null) {
			Toast.makeText(context, "日期格式不正确", Toast.LENGTH_LONG).show();
			calendar.setTime(new Date());
		} else
			calendar.setTime(d);
		new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				edDateTime.setText(sdf.format(calendar.getTime()));
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)).show();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			@SuppressWarnings("unchecked")
			WebQueryResult<MjJobBean> mjJob = (WebQueryResult<MjJobBean>) data
					.getSerializable(JOB);
			if (mjJob != null) {
				String err = GlobalMethod.getErrorMessageFromWeb(mjJob);
				if (TextUtils.isEmpty(err)) {
					// Return data is OK;
					MjJobBean job = mjJob.getResult();
					if (job != null && job.getJob() != null) {
						String s = "";
						String[] as = job.getJob();
						for (String string : as) {
							s += string + "\n";
						}
						tvContent.setText(s);
					} else {
						GlobalMethod.showErrorDialog("没有查到对应的数据！", self);
					}
				} else {
					GlobalMethod.showErrorDialog(err, self);
				}
			} else {
				GlobalMethod.showErrorDialog("网络连接错误，请查看配置！", self);
			}
			super.handleMessage(msg);
		}

	};

	class QueryMjJobThread extends Thread {
		private Context context;
		private Handler mHandler;
		private String jybh;
		private String stime;
		private String etime;

		public QueryMjJobThread(Context context, Handler mHandler) {
			this.context = context;
			this.mHandler = mHandler;
		}

		public void doStart(String jybh, String stime, String etime) {

			this.jybh = jybh;
			this.stime = stime;
			this.etime = etime;
			progressDialog = ProgressDialog.show(context, "提示",
					context.getString(R.string.progress_wait), true);
			progressDialog.setCancelable(true);
			start();
		}

		@Override
		public void run() {
			WebQueryResult<MjJobBean> mjJob = RestfulDaoFactory.getDao()
					.queryMjJob(jybh, stime, etime);
			Message msg = mHandler.obtainMessage();
			Bundle data = new Bundle();
			data.putSerializable(JOB, mjJob);
			msg.setData(data);
			if (progressDialog.isShowing())
				progressDialog.dismiss();
			mHandler.sendMessage(msg);
		}
	}

}
