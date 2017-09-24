package com.jwt.update;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jwt.adapter.MainMenuAdapter;
import com.jwt.adapter.MainMenuAdapter.MenuClickListener;
import com.jwt.bean.MenuGridBean;
import com.jwt.bean.MenuOptionBean;
import com.jwt.event.MenuPosEvent;
import com.jwt.pojo.Bjbd;
import com.jwt.pojo.Bjbd_;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.MenuParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity {

    private MainMenuAdapter menusAdapter;
    private List<MenuGridBean> menuLists;
    private List<MenuOptionBean> menuOptionList;
    private Context self;
    private MainReferService mrService;
    private long bjwd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = this;
        EventBus.getDefault().register(this);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        String mjxx = GlobalMethod.getSavedInfo(self, "mjxx");
        if (TextUtils.isEmpty(mjxx)) {
            Intent intent = new Intent(self, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        if (GlobalData.grxx == null) {
            GlobalData.grxx = GlobalMethod.getSavedMjInfo(self);
        }
        setUpViews();
        //测试数据
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.READ_PHONE_STATE)
                .request();

    }


    protected void setUpViews() {
        bjwd = GlobalMethod.getBoxStore(self).boxFor(Bjbd.class).query()
                .notEqual(Bjbd_.ydbj, 1).build().count();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gridView1);
        //noinspection ConstantConditions
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        menuLists = MenuParser.parseMenuXml(self);
        // 根据权限过滤菜单
        //menuLists = MenuParser.filterMenuByQx(menuLists, GlobalData.grxx.get("YHLX"));
        menuOptionList = menuLists.get(0).getOptions();
        menusAdapter = new MainMenuAdapter(menuClickListener, 3);
        recyclerView.setAdapter(menusAdapter);
        for (MenuOptionBean opt : menuOptionList) {
            if ("com.jwt.update.JbywBjbdListActivity".equals(opt.getClassName())) {
                opt.setBadge(bjwd > 0);
            }
        }
        menusAdapter.setMenus(menuOptionList);

    }

    private MenuClickListener menuClickListener = new MenuClickListener() {
        @Override
        public void onNoteClick(int position) {
            MenuOptionBean m = menuOptionList.get(position);
            //m.setBadge(false);
            //menusAdapter.notifyDataSetChanged();
            if (!TextUtils.isEmpty(m.getPck())
                    && !TextUtils.isEmpty(m.getClassName())) {
                String dn = m.getDataName();
                String data = m.getData();
                if (TextUtils.equals(dn, "out")) {
                    Intent intent = new Intent(m.getClassName()); //广播内容
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    Log.e("MainActivity", m.getPck() + "/" + m.getClassName());
                    intent.setComponent(new ComponentName(m.getPck(), m
                            .getClassName()));
                    if (!TextUtils.isEmpty(dn)
                            && !TextUtils.isEmpty(data)) {
                        intent.putExtra(m.getDataName(), m.getData());
                    }
                    intent.putExtra("title", m.getMenuName());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_zhcx: {
                Intent intent = new Intent(self, ZhcxMainActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.menu_config: {
                Intent intent = new Intent(self, ConfigMainActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.menu_scan: {
                IntentIntegrator integrator = new IntentIntegrator(this);
//                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
//                integrator.setPrompt("Scan a barcode");
//                integrator.setCameraId(0);  // Use a specific camera of the device
//                integrator.setBeepEnabled(false);
//                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void menuBackEvent(MenuPosEvent event) {
        int pos = event.getPos();
        Log.e("Main activity", "return " + pos);
        boolean isBadge = event.isBadge();
        MenuOptionBean m = menuOptionList.get(pos);
        m.setBadge(isBadge);
        menusAdapter.notifyItemChanged(pos);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        Log.e("MainActivity", "permiss ok");
        GlobalData.serialNumber = GlobalMethod.getSerial(self);
        // 运行服务
        if (!MenuParser.checkServerRunning(self, "com.jwt.update",
                "com.jwt.update.MainReferService")) {
            startService(new Intent(this, MainReferService.class));
        }
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this).title("系统确认")
                .content("是否确定退出系统?")
                .positiveText("退出").negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        GlobalData.loginStatus = 1;
                        mrService.logoutJwt();
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private DialogInterface.OnClickListener exitSystem = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            GlobalData.loginStatus = 1;
            mrService.logoutJwt();
            finish();
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConn);
        GlobalData.isBadger = true;
        Log.e("MainActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalData.isBadger = false;
        Intent serviceIntent = new Intent(this, MainReferService.class);
        bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
        Log.e("MainActivity", "onResume");
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
}

