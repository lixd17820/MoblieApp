package com.jwt.main;

import java.util.ArrayList;
import java.util.List;

import com.jwt.activity.ActionBarListActivity;
import com.jwt.adapter.WsglListAdapter;
import com.jwt.dao.WsglDAO;
import com.jwt.event.DownSpeedEvent;
import com.jwt.pojo.THmb;
import com.jwt.thread.WsglThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ConfigWsglActivity extends ActionBarListActivity {

    // private ArrayList<KeyValueBean> kvs;
    // private Spinner spWslb;
    // private int maxWssl = 50;
    // private Context self;
    private List<THmb> jdsList;
    private Activity self;
    // private boolean isOldJwt;

    private String zqmj;

    // 0 警示卡
    // 1 简易处罚决定书
    // 2 行政处罚决定书
    // 3 强制措施凭证
    // 4 撤销决定书
    // 5 转递通知书
    // 9 其他
    // 6 违法处理通知书
    // 7 违停告知单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.comm_no_button_list);
        zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);
        if (zqmj == null) {

        }
        EventBus.getDefault().register(self);
        setTitle("文书领用查看");
        jdsList = new ArrayList<THmb>();
        changeList();
    }

    private void changeList() {
        jdsList.clear();
        jdsList.addAll(WsglDAO
                .getJdsListByHdzl("1", zqmj, GlobalMethod.getBoxStore(this)));
        jdsList.addAll(WsglDAO
                .getJdsListByHdzl("3", zqmj, GlobalMethod.getBoxStore(this)));
        jdsList.addAll(WsglDAO
                .getJdsListByHdzl("9", zqmj, GlobalMethod.getBoxStore(this)));
        WsglListAdapter wsAdapter = new WsglListAdapter(
                ConfigWsglActivity.this, jdsList);
        getListView().setAdapter(wsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_menu, menu);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(DownSpeedEvent event) {
        if (event.getTotal() == event.getStep()) {
            GlobalMethod.showDialog(event.getTitle(),"","", self);
            changeList();
            return;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_update: {
                new WsglThread(self, zqmj).start();
            }
            return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(self);
        // if (progressDialog != null && progressDialog.isShowing())
        // progressDialog.dismiss();
    }

}
