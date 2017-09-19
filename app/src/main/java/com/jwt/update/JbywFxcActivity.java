package com.jwt.update;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jwt.adapter.ImageListAdapter;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.FxczfDao;
import com.jwt.dao.WfddDao;
import com.jwt.pojo.VioFxcFileBean;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintJdsTools;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JbywFxcActivity extends AppCompatActivity {

    private final int CAMER_REQUEST = 1110;
    private final int REQCODE_WFDD = 1111;
    private final int REQCODE_WFXW = 1112;

    //private ArrayList<VioFxcFileBean> zpList;
    private Context self;
    private Spinner spHpzl, spHpqz;
    private EditText edWfdd, edWfxw, edHphm, edWfsj;
    private KeyValueBean kvWfdd;

    private boolean isSaveText = false, isSaveFile = false;
    private VioFxczfBean fxczf;
    private String tzsbh;
    private KeyValueBean printerInfo;
    private BlueToothPrint btp = null;

    private static final int READONLY = 100;
    private static final int ADD_NEW = 101;

    private int operMod = ADD_NEW;

    private static final String STATE_KV_WFDD = "kvWfdd";
    private static final String STATE_IS_SAVE_FILE_BOL = "isSaveFile";
    private static final String STATE_IS_SAVE_TEXT_BOL = "isSaveText";
    private static final String STATE_TZSBH = "tzsbh";
    private static final String STATE_FXCZF_BEAN = "fxczf";
    private static final String STATE_OPER_MOD = "operMod";

    private ImageListAdapter adapter;
    private String photoName = "";

    @SuppressWarnings("unchecked")
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the previously serialized current dropdown position.
        Log.e("AcdTakePhotoActivity", "onRestoreInstanceState");
//        if (savedInstanceState.containsKey(STATE_ZP_LIST)) {
//            zpList = (ArrayList<VioFxcFileBean>) savedInstanceState
//                    .getSerializable(STATE_ZP_LIST);
//        }
        if (savedInstanceState.containsKey(STATE_TZSBH)) {
            tzsbh = savedInstanceState.getString(STATE_TZSBH);
        }

        if (savedInstanceState.containsKey(STATE_IS_SAVE_TEXT_BOL)) {
            isSaveText = savedInstanceState.getBoolean(STATE_IS_SAVE_TEXT_BOL);
        }

        if (savedInstanceState.containsKey(STATE_IS_SAVE_FILE_BOL)) {
            isSaveFile = savedInstanceState.getBoolean(STATE_IS_SAVE_FILE_BOL);
        }

        if (savedInstanceState.containsKey(STATE_KV_WFDD)) {
            kvWfdd = (KeyValueBean) savedInstanceState
                    .getSerializable(STATE_KV_WFDD);
        }

        if (savedInstanceState.containsKey(STATE_FXCZF_BEAN)) {
            fxczf = (VioFxczfBean) savedInstanceState
                    .getSerializable(STATE_FXCZF_BEAN);
        }

        if (savedInstanceState.containsKey(STATE_OPER_MOD)) {
            operMod = savedInstanceState.getInt(STATE_OPER_MOD);
        }

        findViewById(R.id.unless_linear).requestFocus();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putString(STATE_TZSBH, tzsbh);
        outState.putBoolean(STATE_IS_SAVE_FILE_BOL, isSaveFile);
        outState.putBoolean(STATE_IS_SAVE_TEXT_BOL, isSaveText);
        outState.putInt(STATE_OPER_MOD, operMod);
        if (kvWfdd != null)
            outState.putSerializable(STATE_KV_WFDD, kvWfdd);
        if (fxczf != null) {
            outState.putSerializable(STATE_FXCZF_BEAN, fxczf);
        }
//        if (zpList != null)
//            outState.putSerializable(STATE_ZP_LIST, zpList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jbyw_fxc);
        self = this;
        if (!GlobalData.isInitLoadData) {
            GlobalData.initGlobalData(GlobalMethod.getBoxStore(self));
            GlobalData.serialNumber = GlobalMethod.getSerial(self);
            //GlobalMethod.readParam(self);
        }
        spHpzl = (Spinner) findViewById(R.id.spin_hpzl);
        spHpqz = (Spinner) findViewById(R.id.spin_hpqz);
        edWfdd = (EditText) findViewById(R.id.edit_wfdd);
        edWfdd.setKeyListener(null);
        edWfxw = (EditText) findViewById(R.id.edit_wfxw);
        edHphm = (EditText) findViewById(R.id.edit_hphm);
        edWfsj = (EditText) findViewById(R.id.edit_wfsj);
        edWfsj.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(new Date()));
        edWfsj.setKeyListener(null);
        GlobalMethod.changeAdapter(spHpzl, GlobalData.hpzlList, this);
        GlobalMethod.changeAdapter(spHpqz, GlobalData.hpqlList, this);
        spHpzl.setSelection(
                GlobalMethod.getPositionByKey(GlobalData.hpzlList, "02"), true);
        spHpqz.setSelection(
                GlobalMethod.getPositionByKey(GlobalData.hpqlList, "320000"),
                true);

        edHphm.setText("F");
        kvWfdd = new KeyValueBean("", "");
        //zpList = new ArrayList<VioFxcFileBean>();
        findViewById(R.id.but_wfsj).setOnClickListener(butClick);
        findViewById(R.id.but_wfdd).setOnClickListener(butClick);
        findViewById(R.id.but_wfxw).setOnClickListener(butClick);
        // 检测
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            GlobalMethod.showDialogWithListener("错误信息", "本模块需要SD存储卡,请加载后继续!",
                    "确定", dc, self);
            return;
        }
        Log.e("jbywFxc", "test");
        tzsbh = FxczfDao.getTodayFxczfId(GlobalMethod.getBoxStore(self));
        String[] ar = FxczfDao.getLastWfdd(GlobalMethod.getBoxStore(self));
        if (ar != null && WfddDao.isWfddOk(ar[0], GlobalMethod.getBoxStore(self))) {
            kvWfdd = new KeyValueBean(ar[0], ar[1]);
            edWfdd.setText(ar[1]);
            edWfxw.setText(ar[2]);
        }
        //初始化图片列表
        RecyclerView gridView = (RecyclerView) findViewById(R.id.gridView1);
        gridView.setHasFixedSize(true);
        gridView.setLayoutManager(new GridLayoutManager(this, 2));
        gridView.setAdapter(adapter = new ImageListAdapter(imgClick, new ArrayList<String>()));
        // 设置打印的名字，打印时在数据库中取
        String pn = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME);
        String pd = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS);
        if (!TextUtils.isEmpty(pn) && !TextUtils.isEmpty(pd))
            printerInfo = new KeyValueBean(pn, pd);

        // String printerName = !TextUtils.isEmpty(printerInfo.getValue()) ?
        // printerInfo
        // .getKey() : "无打印机";
        setTitle("非现场执法");
        // setTitle("非现场编号: " + tzsbh + " 打印机：" + printerName);
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
    public void onBackPressed() {
        // super.onBackPressed();
        if (isSaveFile || operMod == READONLY) {
            Intent i = new Intent();
            i.putExtra("operMod", operMod);
            setResult(RESULT_OK, i);
            finish();
        } else {
            // 本决定书没有保存
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("系统提醒")
                    .setMessage("本次处罚还没有保存，是否确定退出！")
                    .setNeutralButton("返回",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                }
                            }
                    )
                    .setNegativeButton("退出",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    finish();
                                }
                            }
                    ).create().show();
        }
    }

    private View.OnClickListener butClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == findViewById(R.id.but_wfsj)) {
                GlobalMethod.changeTime(edWfsj, self);
            } else if (v == findViewById(R.id.but_wfdd)) {
                // 查询违法地点
                Intent intent = new Intent(self, ConfigWfddActivity.class);
                startActivityForResult(intent, REQCODE_WFDD);
            } else if (v == findViewById(R.id.but_wfxw)) {
                Intent intent = new Intent(self, ConfigWfdmActivity.class);
                intent.putExtra("comefrom", 1);
                startActivityForResult(intent, REQCODE_WFXW);
            }

        }
    };

    private void delImage() {
        int pos = adapter.getSelectIndex();
        if (pos < 0) {
            GlobalMethod.showErrorDialog("请选择一张照片", this);
            return;
        }
        List<String> imgList = adapter.getList();
        imgList.remove(pos);
        adapter.setImageList(imgList);
        adapter.notifyDataSetChanged();
    }

    private DialogInterface.OnClickListener delImageDialog = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            delImage();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fxczf_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.menu_upload);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (operMod == READONLY) {
            GlobalMethod.showErrorDialog("不能在此模式下操作", self);
            return true;
        }
        switch (item.getItemId()) {
            // 打开照相机
            case R.id.open_camare: {
                if (isSaveFile) {
                    GlobalMethod.showErrorDialog("记录已保存，不能再拍照了！", self);
                    return true;
                }
                if (TextUtils.isEmpty(edWfdd.getText())
                        || TextUtils.isEmpty(edWfsj.getText())) {
                    GlobalMethod.showErrorDialog("违法时间和违法地点是必填项", self);
                    return true;
                }
                startTakePhoto();
            }
            return true;
            case R.id.print_file: {
                // 打印文本，已保存打印保存
                if (!isSaveText && !isSaveFile) {
                    VioFxczfBean temp = getFxcFromView();
                    String err = vaidatePic(temp, false);
                    if (!TextUtils.isEmpty(err)) {
                        GlobalMethod.showErrorDialog(err, self);
                        return true;
                    }
                    fxczf = temp;
                    isSaveText = true;
                }
                printFxcTzs();
            }
            return true;
            case R.id.save_file: {
                if (isSaveFile) {
                    GlobalMethod.showErrorDialog("已保存,无需重复保存", self);
                    return true;
                }
                VioFxczfBean temp = getFxcFromView();
                String err = vaidatePic(temp, true);
                if (!TextUtils.isEmpty(err)) {
                    GlobalMethod.showErrorDialog(err, self);
                    return true;
                }
                fxczf = temp;
                saveFxcIntoDb();
                //QueryVehHandler qvHandler = new QueryVehHandler(this);
                //QueryVehThread thread = new QueryVehThread(qvHandler,
                //       fxczf.getHpzl(), fxczf.getHphm());
                //thread.start();
            }
            return true;
            case R.id.menu_del_image:
                if (isSaveFile) {
                    GlobalMethod.showErrorDialog("记录已保存，不能删除图片", self);
                    return true;
                }
                GlobalMethod.showDialogTwoListener("系统提示", "是否删除图片，该操作将无法恢复", "删除",
                        "取消", delImageDialog, self);
                return true;
            case R.id.menu_show_image:
                showSelectImage();
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

    private void saveFxcIntoDb() {
        long id = FxczfDao.insertFxczfDb(fxczf, GlobalMethod.getBoxStore(self));
        if (id > 0) {
            isSaveFile = true;
        }
        GlobalMethod.showDialog("系统提示", id > 0 ? "非现场保存成功" : "非现场保存失败",
                "确定", self);
    }

    private VioFxczfBean getFxcFromView() {
        VioFxczfBean temp = new VioFxczfBean();
        if (kvWfdd != null && !TextUtils.isEmpty(kvWfdd.getKey())
                && kvWfdd.getKey().length() == 18) {
            String key = kvWfdd.getKey();
            temp.setXzqh(key.substring(0, 6));
            temp.setWfdd(key.substring(6, 11));
            temp.setLddm(key.substring(11, 15));
            temp.setDdms(key.substring(15, 18));
            temp.setWfdz(edWfdd.getText().toString());
        }
        Editable hphm = edHphm.getText();
        temp.setHpzl(GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                GlobalData.hpzlList, GlobalConstant.KEY));
        String hpqz = spHpqz.getSelectedItem() == null ? "" : spHpqz
                .getSelectedItem().toString();
        String hp = hpqz + hphm.toString().toUpperCase();
        temp.setHphm(hp);
        temp.setWfxw(edWfxw.getText().toString());
        temp.setWfsj(edWfsj.getText().toString());
        temp.setTzsh(tzsbh);
        temp.setPhotos(adapter.getItemCount() + "");
        temp.setCjjg(GlobalData.grxx.get(GlobalConstant.KSBMBH));
        temp.setFzjg(hp.length() > 2 ? hp.substring(0, 2) : "");
        temp.setTzrq(temp.getWfsj());
        temp.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
        temp.setSbbh(GlobalData.serialNumber);
        temp.setScbj("0");
        List<String> pics = adapter.getList();
        if (pics != null && !pics.isEmpty())
            temp.setPics(GlobalMethod.join(pics, ","));
        return temp;
    }

    private String checkHphm(String hpzl, String hphm) {
        if (TextUtils.isEmpty(hphm) || hphm.length() < 6)
            return "号牌号码长度不够";
        String hm = hphm.substring(1, hphm.length());
        if (TextUtils.equals(hpzl, "51") || TextUtils.equals(hpzl, "52")) {
            if (hphm.length() != 8)
                return "新能源车的号牌长度为八位";
        } else {
            if ((TextUtils.equals(hpzl, "15") || TextUtils.equals(hpzl, "16") || TextUtils.equals(hpzl, "23"))) {
                if (hm.length() != 5)
                    return "教练车、挂车号牌为六位，无需汉字";
            } else if (hphm.length() != 7) {
                return "普通号牌号码长度应该为七位";
            }
        }
        if (!GlobalMethod.isNumberOrAZ(hm)) {
            return "号牌包含非法字符";
        }

        if (hphm.toUpperCase().indexOf("I") >= 0 || hphm.toUpperCase().indexOf("O") >= 0) {
            return "号牌中不能包含字母O或者I，可能为数字零或者一";
        }

        return null;
    }

    private String vaidatePic(VioFxczfBean temp, boolean isCheckImage) {
        String wfdd = temp.getXzqh() + temp.getWfdd() + temp.getLddm()
                + temp.getDdms();
        if (TextUtils.isEmpty(wfdd) || TextUtils.isEmpty(temp.getWfdz())
                || wfdd.length() != 18) {
            return "违法地点不能为空!";
        }
        String err = checkHphm(temp.getHpzl(), temp.getHphm());
        if (!TextUtils.isEmpty(err)) {
            return err;
        }
        if (TextUtils.isEmpty(temp.getWfxw())) {
            return "违法行为不能为空";
        }
        if (!TextUtils.equals("10393", temp.getWfxw()) && !TextUtils.equals("13446", temp.getWfxw())) {
            return "违法行为目前只允许10393和13446";
        }
        //WfdmBean wf = ViolationDAO.queryWfxwByWfdm(temp.getWfxw(),
        //        getContentResolver());
        //if (wf == null)
        //    return "违法代码错误!";
        // 查询无此号码或不在有效期内

        // 先前不要过滤,在这里过滤最好了,可以有提示
        //if (!ViolationDAO.isYxWfdm(wf)) {
        //   return "不是有效代码,不可以处罚!";
        //}

        boolean isSer = WfddDao.checkIsSeriousStreet(wfdd, GlobalMethod.getBoxStore(self));

        if (isSer) {
            if (TextUtils.equals(temp.getWfxw(), "10393"))
                return "严管路段不允许使用10393代码";
        } else {
            if (TextUtils.equals("13446", temp.getWfxw()))
                return "不是严管路段，请不要使用13446代码！";
        }
        int photos = Integer.valueOf(temp.getPhotos());
        if (isCheckImage && (photos < 2 || photos > 3)) {
            return "图片需2或3张";
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("JbywImageView", "onActivityResult");
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMER_REQUEST) {
                camerResult();
            } else if (requestCode == REQCODE_WFDD) {
                Bundle b = data.getExtras();
                kvWfdd = new KeyValueBean(b.getString("wfddDm"),
                        b.getString("wfddMc"));
                edWfdd.setText(kvWfdd.getValue());
            } else if (requestCode == REQCODE_WFXW) {
                String wfxw = data.getStringExtra("wfxw");
                if (!TextUtils.isEmpty(wfxw))
                    edWfxw.setText(wfxw);
            }
        }

    }

    DialogInterface.OnClickListener dc = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };


    private void startTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = GlobalMethod.createImageFile(JbywFxcActivity.this,
                    false);
            if (photoFile != null) {
                photoName = photoFile.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoURI = FileProvider.getUriForFile(self, "com.jwt.update.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                Log.e("fxc photo", photoFile.getAbsolutePath());
                startActivityForResult(takePictureIntent, CAMER_REQUEST);
            }
        }
    }

    private void camerResult() {
        if (TextUtils.isEmpty(photoName))
            return;
        File image = new File(photoName);
        Log.e("mCurrentPhotoPath", photoName);
        if (!image.exists()) {
            Toast.makeText(self, "照片拍摄失败", Toast.LENGTH_LONG).show();
            return;
        }
        File dir = image.getParentFile();
        String fn = image.getName();
        dir = new File(dir, "small");
        if (!dir.exists())
            dir.mkdirs();
        File smallF = new File(dir, fn);
        String text = "拍摄时间："
                + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        text += " 拍摄地点：" + edWfdd.getText();
        Bitmap smallImage = GlobalMethod.compressBitmap(photoName, 800, text);
        if (smallImage == null) {
            Toast.makeText(self, "照片压缩失败", Toast.LENGTH_LONG).show();
            return;
        }
        boolean isSave = GlobalMethod.savePicIntoFile(smallImage, smallF);
        if (!isSave) {
            Toast.makeText(self, "照片保存失败", Toast.LENGTH_LONG).show();
            return;
        }
        List<String> smallPhotoList = adapter.getList();
        smallPhotoList.add(smallF.getAbsolutePath());
        adapter.setImageList(smallPhotoList);
        adapter.notifyDataSetChanged();
        showImageActivity(smallF.getAbsolutePath());

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
