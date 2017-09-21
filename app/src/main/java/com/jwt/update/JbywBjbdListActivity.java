package com.jwt.update;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.adapter.BjbdAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.event.MenuPosEvent;
import com.jwt.pojo.Bjbd;
import com.jwt.pojo.Bjbd_;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.ParserJson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.objectbox.Box;
import me.leolin.shortcutbadger.ShortcutBadger;

public class JbywBjbdListActivity extends AppCompatActivity {

    private static final int CONFIG_BJ = 100;
    private Button btnSendMes;
    private EditText editMessage;
    private BjbdAdapter adapter;
    private RecyclerView mRecycleView;
    private Box<Bjbd> bjbdBox;
    private Activity self;

    private List<Bjbd> bjbdList = new ArrayList<Bjbd>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private MainReferService mrService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jbyw_bjbd_list);
        setTitle("报警信息");
        self = this;
        EventBus.getDefault().register(this);
        int position = getIntent().getIntExtra("position", 0);
        EventBus.getDefault().post(new MenuPosEvent(position));
        btnSendMes = (Button) findViewById(R.id.button_send);
        editMessage = (EditText) findViewById(R.id.edit_message);
        btnSendMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bmbh = GlobalData.grxx.get(GlobalConstant.YBMBH);
                if (TextUtils.isEmpty(bmbh) || bmbh.length() < 6)
                    return;
                String mes = editMessage.getText().toString();
                if (!TextUtils.isEmpty(mes)) {
                    editMessage.setText("");
                    Bjbd bjbd = new Bjbd();
                    bjbd.setType("0");
                    bjbd.setBjyy(mes);
                    bjbd.setDdsj(sdf.format(new Date()));
                    String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
                    bjbd.setSender(jybh);
                    String message = ParserJson.objToJson(bjbd).toString();
                    Log.e("Send mes", message);
                    mrService.publish(message, "info." + bmbh.substring(0, 6));
                }
            }
        });
        bjbdBox = ((App) getApplication()).getBoxStore().boxFor(Bjbd.class);
        long count = bjbdBox.count();
        if (count > 100) {
            bjbdList = bjbdBox.query().build().find(0, count - 100);
            bjbdBox.remove(bjbdList);
        }
        List<Bjbd> bjlist = bjbdBox.getAll();
        for (Bjbd bj : bjlist) {
            bj.setYdbj(1);
        }
        bjbdBox.put(bjlist);
        bjbdList = bjbdBox.getAll();
        adapter = new BjbdAdapter(clickListener);
        mRecycleView = (RecyclerView) findViewById(R.id.rec_view_bjbd);
        //noinspection ConstantConditions
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(adapter);
        adapter.setBjbds(bjbdList);
        if (adapter.getItemCount() > 1)
            mRecycleView.smoothScrollToPosition(adapter.getItemCount() - 1);
        ShortcutBadger.removeCount(this);
        GlobalData.isBadger = false;
        GlobalSystemParam.syncBjzl();
        Intent serviceIntent = new Intent(this, MainReferService.class);
        bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    public BjbdAdapter.BjbdClickListener clickListener = new BjbdAdapter.BjbdClickListener() {

        @Override
        public void onBjbdItemClick(int position) {
            Bjbd bjbd = adapter.getBjbd(position);
            bjbd.setYdbj(1);
            bjbdBox.put(bjbd);
            adapter.notifyItemChanged(position);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(serviceConn);
        List<Bjbd> bjlist = bjbdBox.getAll();
        for (Bjbd bj : bjlist) {
            bj.setYdbj(1);
        }
        bjbdBox.put(bjlist);

    }

    // 主线程调用
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusMain(Bjbd bjbd) {
        Log.i("TAG", "MAIN:" + bjbd.getBjyy() + " Thread=" + Thread.currentThread().getId());
        if (!isRecBjbd(bjbd))
            return;
        bjbdList.add(bjbd);
        adapter.notifyItemInserted(bjbdList.size() - 1);
        mRecycleView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    private boolean isRecBjbd(Bjbd bjbd) {
        if ("1".equals(bjbd.getType()) &&
                (!GlobalSystemParam.isReciveBj ||
                        !GlobalSystemParam.bjzlNames.contains(bjbd.getBjyy())))
            return false;
        //这是群发信息
        if ("0".equals(bjbd.getType()) && !GlobalSystemParam.isReciveText)
            return false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bjbd_config_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_bj_config: {
                Intent intent = new Intent(self, ConfigParamSetting.class);
                intent.putExtra("data", "2");
                startActivityForResult(intent, CONFIG_BJ);
            }
            return true;
//            case R.id.menu_open_conn: {
//                String s = "已连接，无需重复连接";
//                if (!mrService.isMqttConn()) {
//                    mrService.doClientConnection();
//                    s = "正在后台连接，请在点连接状态查询";
//                }
//                new MaterialDialog.Builder(this)
//                        .title("系统提示")
//                        .content(s)
//                        .positiveText("知道了")
//                        .show();
//            }
//            return true;
            case R.id.menu_conn_status: {
                String status = "目前报警连接状态：" + (mrService.isMqttConn() ? "连接成功" : "无连接");
                new MaterialDialog.Builder(this)
                        .title("系统提示")
                        .content(status)
                        .positiveText("知道了")
                        .show();
            }
            return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == CONFIG_BJ) {
        //    mrService.subscribeTopic();
        //}
    }

    private ServiceConnection serviceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mrService = ((MainReferService.DownLoadServiceBinder) service)
                    .getService();

        }
    };
//    case R.id.menu_bjzl: {
//        //显示报警的种类
//        String[] allBjzl = new String[GlobalData.bjzlList.size()];
//        List<Integer> sel = new ArrayList<>();
//        for (int i = 0; i < allBjzl.length; i++) {
//            allBjzl[i] = GlobalData.bjzlList.get(i).getValue();
//            if (bjzlArray != null && GlobalMethod.getPositionFromArray(bjzlArray, allBjzl[i]) > -1)
//                sel.add(i);
//        }
//        Integer[] sels = sel.toArray(new Integer[sel.size()]);
//        new MaterialDialog.Builder(this)
//                .title(R.string.bjbd_zl)
//                .items(allBjzl)
//                .itemsCallbackMultiChoice(sels, new MaterialDialog.ListCallbackMultiChoice() {
//                    @Override
//                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
//                        if (text != null && text.length > 0) {
//                            List<String> s = new ArrayList<>();
//                            for (CharSequence i : text) {
//                                s.add(i.toString());
//                            }
//                            GlobalMethod.putSavedInfo(self, "bjzl", GlobalMethod.join(s, ","));
//                            bjzlArray = s.toArray(new String[s.size()]);
//                        } else {
//                            GlobalMethod.putSavedInfo(self, "bjzl", "");
//                            bjzlArray = new String[0];
//                        }
//                        return true;
//                    }
//                })
//                .positiveText(R.string.choose)
//                .show();
//    }
//            return true;
//            case R.id.menu_bjfw: {
//        //显示报警的范围，哪些大队的信息
//        String bjfw = GlobalMethod.getSavedInfo(self, "bjfw");
//        String[] saveBjfw = null;
//        Integer[] sels = null;
//        if (!TextUtils.isEmpty(bjfw)) {
//            saveBjfw = bjfw.split(",");
//            sels = new Integer[saveBjfw.length];
//            for (int i = 0; i < saveBjfw.length; i++) {
//                sels[i] = GlobalMethod.getPositionByKey(GlobalData.glbmList, saveBjfw[i]);
//            }
//        }
//        glbmArray = new String[GlobalData.glbmList.size()];
//        for (int i = 0; i < glbmArray.length; i++) {
//            glbmArray[i] = GlobalData.glbmList.get(i).getValue();
//        }
//        new MaterialDialog.Builder(this)
//                .title(R.string.bjbd_fw)
//                .items(glbmArray)
//                .itemsCallbackMultiChoice(sels, new MaterialDialog.ListCallbackMultiChoice() {
//                    @Override
//                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
//                        if (text != null && text.length > 0) {
//                            List<String> s = new ArrayList<>();
//                            for (Integer i : which) {
//                                s.add(GlobalData.glbmList.get(i).getKey());
//                            }
//                            GlobalMethod.putSavedInfo(self, "bjfw", GlobalMethod.join(s, ","));
//                            glbmArray = s.toArray(new String[s.size()]);
//                        } else {
//                            GlobalMethod.putSavedInfo(self, "bjfw", "");
//                            glbmArray = new String[0];
//                        }
//                        mrService.subscribeTopic();
//                        return true;
//                    }
//                })
//                .positiveText(R.string.choose)
//                .show();
//    }
//            return true;
}
