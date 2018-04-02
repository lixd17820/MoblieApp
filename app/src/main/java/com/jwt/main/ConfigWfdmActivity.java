package com.jwt.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.jwt.activity.ActionBarListActivity;
import com.jwt.adapter.TwoLineSelectAdapter;
import com.jwt.bean.TwoLineSelectBean;
import com.jwt.dao.WfdmDao;
import com.jwt.event.DownSpeedEvent;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.pojo.VioWfdmCode_;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ThreadMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class ConfigWfdmActivity extends ActionBarListActivity {

    private static final int MENU_UPDATE_WFDM = 101;
    private EditText dm, fk, fz, hz;
    private Button butSearch, butWfdmDetail, buWfxwOk;
    private List<VioWfdmCode> wfxws = null;
    private List<TwoLineSelectBean> contents = null;
    private Box<VioWfdmCode> box;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jwt_search_wfdm);
        EventBus.getDefault().register(this);
        setTitle("查询违法代码");
        dm = (EditText) findViewById(R.id.Edit_dm);
        fk = (EditText) findViewById(R.id.Edit_fk);
        fz = (EditText) findViewById(R.id.Edit_fz);
        hz = (EditText) findViewById(R.id.Edit_hz);
        butSearch = (Button) findViewById(R.id.Butt_search);
        butWfdmDetail = (Button) findViewById(R.id.Butt_wfdm_detail);
        buWfxwOk = (Button) findViewById(R.id.Butt_wfdm_ok);

        int comefrom = getIntent().getIntExtra("comefrom", 0);
        if (comefrom == 0) {
            //buWfxwOk.setVisibility(Button.INVISIBLE);
            buWfxwOk.setEnabled(false);
        }
        createContents();
        TwoLineSelectAdapter ad = new TwoLineSelectAdapter(this,
                R.layout.two_line_list_item, contents);
        getListView().setAdapter(ad);
        App app = ((App) getApplication());
        box = app.getBoxStore().boxFor(VioWfdmCode.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("更新数据");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        butSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                QueryBuilder<VioWfdmCode> query = box.query();
                // 加入代码条件
                if (!TextUtils.isEmpty(dm.getText())) {
                    query.contains(VioWfdmCode_.wfxw, dm.getText().toString());
                }
                if (!TextUtils.isEmpty(fk.getText()))
                    query.equal(VioWfdmCode_.fkjeDut, Integer.valueOf(fk.getText().toString()));
                if (!TextUtils.isEmpty(fz.getText()))
                    query.equal(VioWfdmCode_.wfjfs, Integer.valueOf(fz.getText().toString()));
                // 加入违法行为汉字描述
                Editable qhz = hz.getText();
                if (!TextUtils.isEmpty(qhz)) {
                    String[] z = qhz.toString().trim().split(" ");
                    if (z.length > 0) {
                        for (String s : z) {
                            query.contains(VioWfdmCode_.wfnr, s);
                        }
                    }
                }
                // 罚款标记
                if (((CheckBox) findViewById(R.id.ChBox_fkbj)).isChecked()) {
                    query.equal(VioWfdmCode_.fkbj, "1");
                }
                // 警告标记
                if (((CheckBox) findViewById(R.id.ChBox_jgbj)).isChecked()) {
                    query.equal(VioWfdmCode_.jgbj, "1");
                }
                // 强制措施
                if (((CheckBox) findViewById(R.id.ChBox_qzcxbj)).isChecked()) {
                    query.notNull(VioWfdmCode_.qzcslx);
                }
                if (((CheckBox) findViewById(R.id.ChBox_yxqx)).isChecked()) {
                    query.greater(VioWfdmCode_.yxqz, new Date()).less(VioWfdmCode_.yxqs, new Date());
                }
                wfxws = query.build().find();
                if (wfxws != null && wfxws.size() > 0) {
                    Toast.makeText(ConfigWfdmActivity.this,
                            "查到" + wfxws.size() + "个符合条件的违法代码",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ConfigWfdmActivity.this, "没找到相应的违法代码",
                            Toast.LENGTH_LONG).show();
                }
                createContents();
                ((TwoLineSelectAdapter) getListView().getAdapter())
                        .notifyDataSetChanged();
            }
        });

        butWfdmDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pos = getSelectItem();
                if (pos > -1) {
                    VioWfdmCode w = wfxws.get(pos);
                    StringBuilder sb = new StringBuilder();
                    sb.append("违法代码：").append(w.getWfxw()).append("\n");
                    sb.append("违法描述：").append(w.getWfms()).append("\n");
                    sb.append("处罚依据：").append(w.getFltw()).append("\n");
                    sb.append("罚款金额：").append(w.getFkjeDut()).append("\n");
                    sb.append("违法记分：").append(w.getWfjfs()).append("\n");
                    sb.append("可否罚款：").append(
                            Integer.valueOf(w.getFkbj()) > 0 ? "可以罚款" : "不可罚款")
                            .append("\n");
                    sb.append("可否警告：").append(
                            Integer.valueOf(w.getJgbj()) > 0 ? "可以警告" : "不可警告")
                            .append("\n");
                    sb.append("强制措施：").append(
                            TextUtils.isEmpty(w.getQzcslx()) ? "无"
                                    : WfdmDao.getQzcslxMs(w.getQzcslx()))
                            .append("\n");
                    sb.append("是否有效：").append(
                            WfdmDao.isYxWfdm(w) ? "有效代码" : "无效代码");
                    GlobalMethod.showDialog("代码详细描述", sb.toString(), "确定",
                            ConfigWfdmActivity.this);
                } else {
                    Toast.makeText(ConfigWfdmActivity.this, "请选择一个违法代码",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        buWfxwOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getSelectItem();
                if (pos > -1) {
                    VioWfdmCode w = wfxws.get(pos);
                    if (!WfdmDao.isYxWfdm(w)) {
                        GlobalMethod.showErrorDialog("不是有效代码,不可以处罚!",
                                ConfigWfdmActivity.this);
                        return;
                    }
                    Intent i = new Intent();
                    i.putExtra("wfxw", w.getWfxw());
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Toast.makeText(ConfigWfdmActivity.this, "请选择一个违法代码",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long arg3) {
                // 单选,修改其他为不选
                for (int i = 0; i < contents.size(); i++) {
                    TwoLineSelectBean c = contents.get(i);
                    if (i == position)
                        c.setSelect(!c.isSelect());
                    else
                        c.setSelect(false);
                }
                TwoLineSelectAdapter ad = (TwoLineSelectAdapter) parent
                        .getAdapter();
                ad.notifyDataSetChanged();
            }
        });

    }

    private int getSelectItem() {
        int position = -1;
        int i = 0;
        while (contents.size() > 0 && i < contents.size()) {
            if (contents.get(i).isSelect()) {
                position = i;
                break;
            }
            i++;
        }
        return position;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // peccDb.close();
        EventBus.getDefault().unregister(this);
    }

    private void createContents() {
        if (contents == null)
            contents = new ArrayList<TwoLineSelectBean>();
        else
            contents.clear();
        if (wfxws != null && wfxws.size() > 0) {
            for (VioWfdmCode w : wfxws) {
                TwoLineSelectBean ts = new TwoLineSelectBean();
                ts.setText1(w.getWfxw() + ":" + w.getWfms());
                ts.setText2(" 罚款" + w.getFkjeDut() + "元记" + w.getWfjfs() + "分");
                contents.add(ts);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_UPDATE_WFDM, Menu.NONE, "更新代码");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_UPDATE_WFDM:
                new UpdateWfdmThread().start();
                break;
            default:
                break;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(DownSpeedEvent event) {
        if (event.getStep() == 0) {
            progressDialog.show();
        }
        if (event.getTotal() <= event.getStep()) {
            progressDialog.dismiss();
            GlobalMethod.showDialog(event.getTitle(), event.getCurrentName(), "知道了", this);
            return;
        }

        progressDialog.setTitle(event.getTitle());
        progressDialog.setMessage(event.getCurrentName());
        progressDialog.setMax(event.getTotal());
        progressDialog.setProgress(event.getStep());
    }

    public class UpdateWfdmThread extends Thread {
        @Override
        public void run() {
            box.removeAll();
            Log.e("savewfdm", "wdm count: " + box.count());
            RestfulDao dao = RestfulDaoFactory.getDao();
            EventBus.getDefault().post(new DownSpeedEvent("下载数据", 2, 0, "下载违法代码..."));
            WebQueryResult<String> dict2 = dao.updateOtherDict("wfdm", "1");
            if (dict2.getStatus() != 200) {
                EventBus.getDefault().post(new DownSpeedEvent("错误提示", 2, 3, "网络连接失败"));
                return;
            }
            if (!TextUtils.isEmpty(dict2.getStMs())) {
                EventBus.getDefault().post(new DownSpeedEvent("错误提示", 2, 3, dict2.getStMs()));
                return;
            }
            EventBus.getDefault().post(new DownSpeedEvent("下载数据", 2, 1, "下载违法代码..."));
            ThreadMethod.saveWfdmInDb(dict2, box);
            EventBus.getDefault().post(new DownSpeedEvent("下载数据", 2, 2, "下载完成"));
            Log.e("savewfdm", "wdm count: " + box.count());
        }
    }

}
