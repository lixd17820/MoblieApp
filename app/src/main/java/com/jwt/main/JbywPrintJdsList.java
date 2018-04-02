package com.jwt.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.activity.ActionBarListActivity;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.adapter.TwoLineSelectAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TwoLineSelectBean;
import com.jwt.dao.ViolationDAO;
import com.jwt.event.UploadEvent;
import com.jwt.event.VioUploadEvent;
import com.jwt.pojo.VioViolation;
import com.jwt.pojo.VioViolation_;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintJdsTools;
import com.jwt.thread.CommUploadThread;
import com.jwt.thread.UploadVioThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.objectbox.query.QueryBuilder;

public class JbywPrintJdsList extends ActionBarListActivity {

    private KeyValueBean printerInfo;
    private BlueToothPrint btp = null;
    public static final int REQUEST_ENABLE_BT = 2;
    private static final int MENU_PREVIEW = 10;
    private static final int MENU_PRINT = 11;
    private static final int MENU_UPLOAD = 12;
    private static final int MENU_DETAIL = 13;
    // private int printState = BlueToothPrint.BLUETOOTH_NONE;
    // 在单选列表中显示的对象
    private List<TwoLineSelectBean> strList = null;
    private List<VioViolation> puList;
    // private TextView title;
    private String title;

    private List<KeyValueBean> wslbs;
    private Spinner spinWslb;

    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wslbs = createWslbs();
        self = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.jwt_print_jds);
        // 删除过多
        ViolationDAO.delOldViolation(GlobalConstant.MAX_RECORDS,
                GlobalMethod.getBoxStore(self));
        spinWslb = (Spinner) findViewById(R.id.spin_wszl);
        GlobalMethod.changeAdapter(spinWslb, wslbs, (Activity) self);
        String wslb = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                GlobalConstant.KEY);
        puList = getViolationByConds(wslb);
        strList = new ArrayList<TwoLineSelectBean>();
        getList(puList);
        // title = ((TextView) findViewById(R.id.title_left_text));
        title = "决定书-" + puList.size() + "条";
        // TextView t2 = (TextView) findViewById(R.id.title_right_text);
        TwoLineSelectAdapter ad = new TwoLineSelectAdapter(this,
                R.layout.two_line_list_item, strList);
        getListView().setAdapter(ad);
        setTitle(title);

        // 文书打印
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long arg3) {
                // 单选,修改其他为不选
                TwoLineSelectBean c = strList.get(position);
                for(TwoLineSelectBean item: strList)
                    item.setSelect(false);
                c.setSelect(!c.isSelect());
                TwoLineSelectAdapter ad = (TwoLineSelectAdapter) parent
                        .getAdapter();
                ad.notifyDataSetChanged();
            }
        });
        spinWslb.setOnItemSelectedListener(new OnSpinnerItemSelected() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String wslb = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                        GlobalConstant.KEY);
                puList = getViolationByConds(wslb);
                getList(puList);
                ((TwoLineSelectAdapter) getListView().getAdapter())
                        .notifyDataSetChanged();
            }
        });
    }

    public List<VioViolation> getViolationByConds(String wslb) {
        QueryBuilder<VioViolation> query = GlobalMethod.getBoxStore(this).boxFor(VioViolation.class).query();
        if ("1".equals(wslb)) {
            query = query.equal(VioViolation_.wslb, "1").equal(VioViolation_.cfzl, "2");
        } else if ("2".equals(wslb))
            query = query.equal(VioViolation_.wslb, "1").equal(VioViolation_.cfzl, "1");
        else if ("3".equals(wslb))
            query = query.equal(VioViolation_.wslb, "3");
        else if ("6".equals(wslb))
            query = query.equal(VioViolation_.wslb, "6");
        List<VioViolation> list = query.build().find();
        Collections.reverse(list);
        return list;
    }

    private List<KeyValueBean> createWslbs() {
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        list.add(new KeyValueBean("1", "简易处罚"));
        list.add(new KeyValueBean("2", "轻微警告"));
        list.add(new KeyValueBean("3", "强制措施"));
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_vio_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Integer> position = getSelectItem();
        if (position == null || position.isEmpty()) {
            GlobalMethod.showDialog("错误信息", "请选择一条记录!", "返回",
                    JbywPrintJdsList.this);
            return true;
        }
        if(item.getItemId() == R.id.menu_print_jds){
            Integer index = position.get(0);
            final VioViolation vio = puList.get(index);
            printJdsBySelect(vio);
            return true;
        }
        List<String> list = new ArrayList<>();
        if (position.size() == 1) {
            list.add("预览决定书");
            list.add("详细情况");
            VioViolation v = puList.get(position.get(0));
            boolean hasPic = !TextUtils.isEmpty(v.getPicFile());
            boolean isPicUp = v.getPicScbj() == 1;
            boolean isupload = !TextUtils.equals(v.getScbj(), "1");
            if (hasPic) {
                list.add("查看照片");
                if (!isPicUp) {
                    list.add("上传照片");
                    list.add("拍照片");
                }
            } else {
                if (!isPicUp)
                    list.add("拍照片");
            }
            if (isupload)
                list.add("上传决定书");
        }
        list.add("打印决定书");
        list.add("取消选中");
        final List<VioViolation> vios = new ArrayList<>();
        for (Integer i : position) {
            vios.add(puList.get(i));
        }
        final VioViolation vio = vios.get(0);
        new MaterialDialog.Builder(this)
                .items(list.toArray(new String[]{}))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (TextUtils.equals("预览决定书", text.toString())) {
                            ArrayList<JdsPrintBean> jds = PrintJdsTools.getPrintJdsContent(
                                    vio, JbywPrintJdsList.this);
                            Log.e("JbywPrintList", "决定书" + jds.size());
                            Intent intent = new Intent(self, JdsPreviewActivity.class);
                            intent.putExtra("jds", jds);
                            startActivity(intent);
                        } else if (TextUtils.equals("详细情况", text.toString())) {
                            Intent intent2 = new Intent(self, JbywPrintJdsDetailActivity.class);
                            intent2.putExtra("jdsbh", vio.getJdsbh());
                            startActivity(intent2);
                        } else if (TextUtils.equals("上传决定书", text.toString())) {
                            if (TextUtils.equals(vio.getScbj(), "1")) {
                                GlobalMethod.showErrorDialog("文书已上传,无需重复上传", self);
                                return;
                            }
                            UploadVioThread thread = new UploadVioThread(vio, JbywPrintJdsList.this, true);
                            thread.doStart();
                        } else if (TextUtils.equals("打印决定书", text.toString())) {
                            printJdsBySelect(vio);
                        } else if (TextUtils.equals("拍照片", text.toString())) {
                            startTakePhoto();
                        } else if (TextUtils.equals("上传照片", text.toString())) {
                            new CommUploadThread(CommUploadThread.UPLOAD_VIO_PIC, new Object[]{vio}, self)
                                    .doStart();
                        } else if (TextUtils.equals("查看照片", text.toString())) {
                            if (!TextUtils.isEmpty(vio.getPicFile()))
                                GlobalMethod.showImageActivity(vio.getPicFile(), self);
                        } else if (TextUtils.equals("取消选中", text.toString())) {
                            for (int i = 0; i < strList.size(); i++) {
                                TwoLineSelectBean c = strList.get(i);
                                c.setSelect(false);
                            }
                            ((TwoLineSelectAdapter) getListView().getAdapter())
                                    .notifyDataSetChanged();
                        }
                    }
                }).show();
        return super.onOptionsItemSelected(item);
    }


    protected void startTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = GlobalMethod.createImageFile(this, false);
            if (photoFile != null) {
                GlobalData.photoName = photoFile.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoURI = FileProvider.getUriForFile(self, "com.jwt.main.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                Log.e("fxc photo", photoFile.getAbsolutePath());
                startActivityForResult(takePictureIntent, GlobalData.CAMER_REQUEST);
            }
        }
    }

    private void camerResult() {
        if (TextUtils.isEmpty(GlobalData.photoName))
            return;
        File image = new File(GlobalData.photoName);
        Log.e("mCurrentPhotoPath", GlobalData.photoName);
        if (!image.exists()) {
            Toast.makeText(self, "照片拍摄失败", Toast.LENGTH_LONG).show();
            return;
        }
        File dir = image.getParentFile();
        String fn = image.getName();
        dir = new File(dir, "vio");
        if (!dir.exists())
            dir.mkdirs();
        File smallF = new File(dir, fn);
        String text = "拍摄时间："
                + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        Bitmap smallImage = GlobalMethod.compressBitmap(GlobalData.photoName, 800, text);
        if (smallImage == null) {
            Toast.makeText(self, "照片压缩失败", Toast.LENGTH_LONG).show();
            return;
        }
        boolean isSave = GlobalMethod.savePicIntoFile(smallImage, smallF);
        if (!isSave) {
            Toast.makeText(self, "照片保存失败", Toast.LENGTH_LONG).show();
            return;
        }

        List<Integer> position = getSelectItem();
        VioViolation violation = puList.get(position.get(0));
        //showImageActivity(smallF.getAbsolutePath());
        violation.setPicFile(smallF.getAbsolutePath());
        ViolationDAO.saveViolationIntoDB(violation, GlobalMethod.getBoxStore(self));
        Toast.makeText(self, "照片保存成功", Toast.LENGTH_LONG).show();
        String wslb = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                GlobalConstant.KEY);
        puList = getViolationByConds(wslb);
        getList(puList);
        ((TwoLineSelectAdapter) getListView().getAdapter())
                .notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GlobalData.CAMER_REQUEST) {
                camerResult();
            }
        }
    }

    /**
     * 上传处罚决定书的控制操作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadVioHandler(VioUploadEvent event) {
        if (event.scbj == 1) {
            GlobalMethod.showToast("决定书已上传", self);
            ViolationDAO.setVioUploadStatus(event.id, true, GlobalMethod.getBoxStore(self));
            referListView();
        } else {
            GlobalMethod.showErrorDialog(event.message, self);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadPicEvent(UploadEvent event) {
        if (event.status) {
            ViolationDAO.setVioPicUploadStatus(event.id, true, GlobalMethod.getBoxStore(self));
            referListView();
        } else {
            GlobalMethod.showErrorDialog(event.message, self);
        }
    }

    private void referListView() {
        String wslb = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                GlobalConstant.KEY);
        puList = getViolationByConds(wslb);
        getList(puList);
        ((TwoLineSelectAdapter) getListView().getAdapter())
                .notifyDataSetChanged();
    }

    private List<Integer> getSelectItem() {
        //int position = -1;
        int i = 0;
        List<Integer> list = new ArrayList<>();
        while (strList.size() > 0 && i < strList.size()) {
            if (strList.get(i).isSelect()) {
                //    position = i;
                //    break;
                list.add(i);
            }
            i++;
        }
        //if(list.isEmpty())
        //    return null;

        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (btp != null) {
            btp.closeConn();
            btp = null;
        }
    }

    @Override
    protected void onStop() {
        Log.e("vio", "onStop");
        if (btp != null) {
            btp.closeConn();
            btp = null;
        }
        super.onStop();
    }

    private void printJdsBySelect(VioViolation vio) {
        if (btp == null && ((btp = GlobalMethod.getBluetoothPrint(this)) == null)) {
            return;
        }
        int status = btp.printJdsByBluetooth(vio, this);
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    private void getList(List<VioViolation> puList) {
        strList.clear();
        if (puList == null)
            return;
        for (VioViolation v : puList) {
            TwoLineSelectBean ts = new TwoLineSelectBean();
            String text1 = "";
            String text2 = v.getWfsj() + " " + v.getDsr();
            if (Integer.valueOf(v.getWslb()) == 1
                    && TextUtils.equals(v.getCfzl(), "1")) {
                text1 = "警告";
            } else if (Integer.valueOf(v.getWslb()) == 1) {
                text1 = "简易";
            } else if (Integer.valueOf(v.getWslb()) == 3) {
                text1 = "强制";
            } else if (Integer.valueOf(v.getWslb()) == 6) {
                text1 = "通知";
            }
            text1 += " " + v.getJdsbh()
                    + (Integer.valueOf(v.getScbj()) == 0 ? " 数据未上传" : " 数据已上传");
            if (TextUtils.isEmpty(v.getPicFile())) {
                text2 += " 无图片";
            } else {
                text2 += "图片" + (v.getPicScbj() > 0 ? "已上传" : "未上传");
            }
            ts.setText1(text1);
            ts.setText2(text2);
            ts.setSelect(false);
            strList.add(ts);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
