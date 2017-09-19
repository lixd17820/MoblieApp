package com.jwt.update;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.adapter.ImageListAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.FxczfDao;
import com.jwt.dao.WfddDao;
import com.jwt.pojo.VioFxcFileBean;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintJdsTools;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class JbywFxcShowActivity extends AppCompatActivity {

    private Context self;
    private TextView tvHpzl, tvHphm, tvWfdd, tvWfsj, tvWfxw, tvScbj;
    private VioFxczfBean fxczf;
    private KeyValueBean printerInfo;
    private BlueToothPrint btp = null;
    private ImageListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jbyw_fxc_show);
        self = this;
        if (!GlobalData.isInitLoadData) {
            GlobalData.initGlobalData(GlobalMethod.getBoxStore(self));
            GlobalData.serialNumber = GlobalMethod.getSerial(self);
            //GlobalMethod.readParam(self);
        }
        tvHpzl = (TextView) findViewById(R.id.tv_hpzl);
        tvHphm = (TextView) findViewById(R.id.tv_hphm);
        tvWfdd = (TextView) findViewById(R.id.tv_wfdd);
        tvWfxw = (TextView) findViewById(R.id.tv_wfxw);
        tvScbj = (TextView) findViewById(R.id.tv_scbj);
        tvWfsj = (TextView) findViewById(R.id.tv_wfsj);
        fxczf = (VioFxczfBean) getIntent().getSerializableExtra("fxc");
        if(fxczf == null)
            return;
        List<String> images = new ArrayList<>();
        if(!TextUtils.isEmpty(fxczf.getPics())) {
            String[] temp = fxczf.getPics().split(",");
           images = Arrays.asList(temp);
        }
        tvHpzl.setText("号牌种类：" + GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList, fxczf.getHpzl()));
        tvHphm.setText("号牌号码：" + fxczf.getHphm());
        tvWfdd.setText("违法地点：" + fxczf.getWfdz());
        tvWfsj.setText("违法时间：" + fxczf.getWfsj());
        tvWfxw.setText("违法代码：" + fxczf.getWfxw());
        tvScbj.setText("是否上传：" + ("0".equals(fxczf.getScbj()) ? "未上传" : "已上传"));
        //初始化图片列表
        RecyclerView gridView = (RecyclerView) findViewById(R.id.gridView1);
        gridView.setHasFixedSize(true);
        gridView.setLayoutManager(new GridLayoutManager(this, 2));
        gridView.setAdapter(adapter = new ImageListAdapter(imgClick, images));
        // 设置打印的名字，打印时在数据库中取
        String pn = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME);
        String pd = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS);
        if (!TextUtils.isEmpty(pn) && !TextUtils.isEmpty(pd))
            printerInfo = new KeyValueBean(pn, pd);

        setTitle("非现场执法");
        Log.e("AcdTakePhotoActivity", "onCreate");

    }

    ImageListAdapter.ImageClickListener imgClick = new ImageListAdapter.ImageClickListener() {
        @Override
        public void onClick(int position) {
            adapter.setSelectIndex(position);
            adapter.notifyDataSetChanged();
        }
    };

    private void showImageActivity(String file) {
        Intent intent = new Intent(self, ShowImageActivity.class);
        intent.putExtra("image", file);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e("JbywImageView", "onStop");
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fxczf_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.open_camare);
        menu.removeItem(R.id.save_file);
        menu.removeItem(R.id.menu_del_image);
        menu.removeItem(R.id.menu_upload);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.print_file: {

                printFxcTzs();
            }
            return true;

            case R.id.menu_show_image: {
                showSelectImage();
            }
            return true;
            default:
                break;
        }
        return false;
    }

    private void showSelectImage() {
        int index = adapter.getSelectIndex();
        if (index < 0) {
            GlobalMethod.showErrorDialog("请选择一张图片", this);
            return;
        }
        String file = adapter.getImg(index);
        showImageActivity(file);
    }


    private void printFxcTzs() {
        if (TextUtils.isEmpty(printerInfo.getValue())) {
            GlobalMethod.showDialog("错误信息", "没有配置默认打印机!", "返回", self);
            return;
        }
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return;
        }

        if (!TextUtils.isEmpty(printerInfo.getValue()) && btp == null) {
            btp = new BlueToothPrint(printerInfo.getValue());
        }

        if (btp == null)
            return;
        if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
            // 没有建立蓝牙串口流
            int errorStaus = btp.createSocket(btAdapter);
            if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
                GlobalMethod.showErrorDialog(
                        btp.getBluetoothCodeMs(errorStaus), self);
                return;
            }
        }
        List<JdsPrintBean> content = PrintJdsTools.getPrintFxczfContent(fxczf);
        int status = btp.printJdsByBluetooth(content);
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btp != null) {
            btp.closeConn();
        }
    }
}
