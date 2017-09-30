package com.jwt.update;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;

import com.jwt.adapter.ImageListAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.AcdSimpleDao;
import com.jwt.dao.WsglDAO;
import com.jwt.event.CommEvent;
import com.jwt.pojo.AcdPhotoBean;
import com.jwt.pojo.THmb;
import com.jwt.thread.AcdUploadPhotoThread;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressLint("NewApi")
public class AcdTakePhotoActivity extends AppCompatActivity {
    private static final int FIND_SGDD = 0;

    private static final int CAMER_REQUEST = 1024;

    protected static final int MENU_DEL_IMG = 2;

    public static final String ACD_PHOTO_BEAN = "acdPhoto";

    public static final int REQ_SELECT_PIC = 3;

    private Button btnChgSgsj, btnChgSgdd;
    private Button btnChgSgrq;
    private EditText edSgsj;
    private TextView tvSgbh;
    private Spinner spinSgdd;

    private SimpleDateFormat sdf;
    private Context self;
    private Activity activity;
    private THmb dqbmb = null;
    private int operMod;
    private KeyValueBean kvSgdd = null;
    private AcdPhotoBean acdPhoto = null;

    private static final String STATE_PHOTO_NAME = "photo_name";
    private static final String STATE_OPER_MOD_INT = "operMod";
    private static final String STATE_DQBMB = "dqbmb";
    private static final String STATE_ACD_PHOTO = "acdPhoto";
    private static final String STATE_KV_SGDD = "kvSgdd";


    private RecyclerView gridView;
    private ImageListAdapter adapter;
    private String photoName = "";


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        Log.e("AcdTakePhotoActivity", "onRestoreInstanceState");
        if (savedInstanceState.containsKey(STATE_ACD_PHOTO)) {
            acdPhoto = (AcdPhotoBean) savedInstanceState.getSerializable(STATE_ACD_PHOTO);
        }
        if (savedInstanceState.containsKey(STATE_PHOTO_NAME)) {
            photoName = savedInstanceState.getString(STATE_PHOTO_NAME);
        }
        if (savedInstanceState.containsKey(STATE_OPER_MOD_INT)) {
            operMod = savedInstanceState.getInt(STATE_OPER_MOD_INT);
        }

        if (savedInstanceState.containsKey(STATE_DQBMB)) {
            dqbmb = (THmb) savedInstanceState.getSerializable(STATE_DQBMB);
        }

        if (savedInstanceState.containsKey(STATE_KV_SGDD)) {
            kvSgdd = (KeyValueBean) savedInstanceState
                    .getSerializable(STATE_KV_SGDD);
            GlobalMethod.changeAdapter(spinSgdd, kvSgdd, activity, 0);
        }
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putSerializable(STATE_DQBMB, dqbmb);
        outState.putInt(STATE_OPER_MOD_INT, operMod);
        outState.putString(STATE_PHOTO_NAME, photoName);
        if (kvSgdd != null) {
            outState.putSerializable(STATE_KV_SGDD, kvSgdd);
        }
        if (acdPhoto != null)
            outState.putSerializable(STATE_ACD_PHOTO, acdPhoto);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acd_take_photo);
        Log.e("AcdTakePhotoActivity", "onCreate");
        self = this;
        EventBus.getDefault().register(this);
        activity = AcdTakePhotoActivity.this;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (!GlobalData.isInitLoadData) {
            GlobalData.initGlobalData(GlobalMethod.getBoxStore(self));
            GlobalData.serialNumber = GlobalMethod.getSerial(self);
            //GlobalMethod.readParam(self);
        }
        String zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);
        if (TextUtils.isEmpty(zqmj))
            return;
        btnChgSgsj = (Button) findViewById(R.id.btn_chg_sgsj);
        btnChgSgrq = (Button) findViewById(R.id.btn_chg_sgrq);
        btnChgSgdd = (Button) findViewById(R.id.btn_chg_sgdd);
        edSgsj = (EditText) findViewById(R.id.edit_acd_sgsj);
        edSgsj.setKeyListener(null);
        spinSgdd = (Spinner) findViewById(R.id.spin_sgdd);
        tvSgbh = (TextView) findViewById(R.id.tv_sgbh);
        setTitle("事故图片采集");
        operMod = getIntent().getIntExtra(AcdSimpleDao.OPER_MOD,
                AcdSimpleDao.ACD_MOD_NEW);
        Serializable temp = getIntent().getSerializableExtra(
                AcdSimpleDao.PHOTO_BEAN);
        if (temp != null)
            acdPhoto = (AcdPhotoBean) temp;

        List<String> photoList = new ArrayList<String>();
        // 初始化照片文件列表
        if (operMod == AcdSimpleDao.ACD_MOD_NEW && acdPhoto == null) {
            edSgsj.setText(sdf.format(new Date()));
            dqbmb = WsglDAO.getCurrentJdsbh(GlobalConstant.ACDSIMPLEWS, zqmj,
                    GlobalMethod.getBoxStore(self));
            if (dqbmb == null) {
                GlobalMethod.showDialogWithListener("系统提示",
                        "未获取简易事故处理编号，请在文书管理中获取！", "确定", finishView, self);
                return;
            }
            tvSgbh.setText(dqbmb.getDqhm());
        } else if (acdPhoto != null) {
            photoList = getAcdPhoto(acdPhoto);
        }
        edSgsj.setKeyListener(null);
        btnChgSgsj.setOnClickListener(clListener);
        btnChgSgrq.setOnClickListener(clListener);
        btnChgSgdd.setOnClickListener(clListener);

        gridView = (RecyclerView) findViewById(R.id.gridView1);
        gridView.setHasFixedSize(true);
        gridView.setLayoutManager(new GridLayoutManager(this, 2));
        gridView.setAdapter(adapter = new ImageListAdapter(click, photoList));

    }

    private List<String> getAcdPhoto(AcdPhotoBean acdPhoto) {
        List<String> photoList = new ArrayList<>();
        edSgsj.setText(acdPhoto.getSgsj());
        kvSgdd = new KeyValueBean(acdPhoto.getSgdddm(), acdPhoto.getSgdd());
        GlobalMethod.changeAdapter(spinSgdd, kvSgdd, activity, 0);
        tvSgbh.setText(acdPhoto.getSgbh());
        String s = acdPhoto.getPhoto();
        if (!TextUtils.isEmpty(s)) {
            photoList = Arrays.asList(s.split(","));
        }
        return photoList;
    }

    ImageListAdapter.ImageClickListener click = new ImageListAdapter.ImageClickListener() {
        @Override
        public void onClick(int position) {
            adapter.setSelectIndex(position);
            adapter.notifyDataSetChanged();
        }
    };


    private View.OnClickListener clListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // 是否保存已做了判断，后续无需处理
            if (v == btnChgSgsj) {
                GlobalMethod.changeTime(edSgsj, self);
            } else if (v == btnChgSgrq) {
                GlobalMethod.changeDate(edSgsj, self);
            } else if (v == btnChgSgdd) {
                Intent intent = new Intent(self, ConfigWfddActivity.class);
                startActivityForResult(intent, FIND_SGDD);
            }

        }
    };

    protected void uploadAcd(AcdPhotoBean acd) {
        AcdUploadPhotoThread thread = new AcdUploadPhotoThread(acd, self);
        thread.doStart();
    }

    private void delImage() {
        GlobalMethod.showDialogWithListener("系统提示", "是否确定删除记录", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delImageListener();
            }
        }, self);
    }

    private void delImageListener() {
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

    private void savePhoto() {
        // 需要对数据进行验证
        String err = checkData();
        if (!TextUtils.isEmpty(err)) {
            GlobalMethod.showErrorDialog(err, self);
            return;
        }
        if (acdPhoto == null)
            acdPhoto = new AcdPhotoBean();
        if (acdPhoto.getId() == 0) {
            List<String> photoList = adapter.getList();
            String photos = GlobalMethod.join(photoList, ",");
            Log.e("acdtakephoto", photos);
            acdPhoto.setPhoto(photos);
            acdPhoto.setSgsj(edSgsj.getText().toString());
            acdPhoto.setSgdd(GlobalMethod.getKeyFromSpinnerSelected(spinSgdd,
                    GlobalConstant.VALUE));
            acdPhoto.setSgdddm(GlobalMethod.getKeyFromSpinnerSelected(spinSgdd,
                    GlobalConstant.KEY));
            String sgbh = tvSgbh.getText().toString();
            acdPhoto.setSgbh(sgbh);
            long id = AcdSimpleDao
                    .addAcdPhoto(acdPhoto, GlobalMethod.getBoxStore(self));
            Log.e("acdtakephoto", "return id：" + id);
            if (id > 0) {
                if (dqbmb != null) {
                    WsglDAO.saveHmbAddOne(dqbmb, GlobalMethod.getBoxStore(self));
                }
                GlobalMethod.showDialog("系统提示", acdPhoto.getSgbh() + "保存成功",
                        "确定", self);
            } else {
                GlobalMethod.showErrorDialog("保存记录出现错误，请重试或与管理员联系", self);
            }
        } else {
            GlobalMethod.showErrorDialog("记录已保存，无需重复保存！", self);
        }
    }

    private String checkData() {
        int imgCount = adapter.getItemCount();
        if (TextUtils.isEmpty(edSgsj.getText())) {
            return "事故时间不能为空";
        } else if (spinSgdd.getSelectedItemPosition() < 0) {
            return "事故地点不能为空";
        } else if (imgCount < GlobalConstant.MIN_ACD_PHOTO_COUNT) {
            return "图片不能少于" + GlobalConstant.MIN_ACD_PHOTO_COUNT + "张";
        } else if (imgCount > GlobalConstant.MAX_ACD_PHOTO_COUNT) {
            return "图片不能多于" + GlobalConstant.MAX_ACD_PHOTO_COUNT + "张";
        }
        return null;
    }

    private void startTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = GlobalMethod.createImageFile(activity, false);
            if (photoFile != null) {
                photoName = photoFile.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoURI = FileProvider.getUriForFile(self, "com.jwt.update.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                startActivityForResult(takePictureIntent, CAMER_REQUEST);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        Log.e("AcdTakePhotoActivity", "onStop");
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("AcdTakePhotoActivity", "onActivityResult " + requestCode + "/"
                + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == FIND_SGDD) {
                Bundle b = data.getExtras();
                kvSgdd = new KeyValueBean(b.getString("wfddDm"),
                        b.getString("wfddMc"));
                GlobalMethod.changeAdapter(spinSgdd, kvSgdd, activity, 0);
            } else if (requestCode == CAMER_REQUEST) {
                cameraActivityResult();
            } else if (requestCode == REQ_SELECT_PIC) {
                Bundle b = data.getExtras();
                String picFile = b.getString("pic_file");
                if (picFile == null)
                    return;
            }
        }
    }

    private void cameraActivityResult() {
        if (TextUtils.isEmpty(photoName))
            return;
        File image = new File(photoName);
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
        Bitmap smallImage = GlobalMethod.compressBitmap(image.getAbsolutePath(), 800,
                text);
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
        if (GlobalSystemParam.isPreviewPhoto)
            GlobalMethod.showImageActivity(smallF.getAbsolutePath(), self);

    }

    @Override
    public void onBackPressed() {
        if (acdPhoto == null || acdPhoto.getId() == 0) {
            GlobalMethod.showDialogTwoListener("系统提示", "记录还没有保存，是否退出", "退出",
                    "取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelSave();
                        }
                    }, self
            );
        } else {
            setResult(RESULT_OK);
            super.onBackPressed();
        }

    }

    private void cancelSave() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private DialogInterface.OnClickListener finishView = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean noMod = operMod == AcdSimpleDao.ACD_MOD_SHOW;
        if (noMod) {
            menu.removeItem(R.id.menu_upload);
            menu.removeItem(R.id.open_camare);
            menu.removeItem(R.id.save_file);
            menu.removeItem(R.id.menu_del_image);
        } else {
            menu.removeItem(R.id.menu_upload);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acd_take_photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isSave = acdPhoto != null && acdPhoto.getId() > 0;
        switch (item.getItemId()) {
            case R.id.save_file:
                if (isSave) {
                    GlobalMethod.showErrorDialog("记录已保存无需重复保存", self);
                    return true;
                }
                savePhoto();
                return true;
            case R.id.open_camare:
                if (!isSave)
                    startTakePhoto();
                else
                    GlobalMethod.showErrorDialog("记录已保存不能拍照", self);
                return true;
            case R.id.menu_upload:
                if (isSave && acdPhoto != null && acdPhoto.getScbj() != 1) {
                    uploadAcd(acdPhoto);
                }
                return true;
            case R.id.menu_del_image:
                if (!isSave)
                    delImage();
                else
                    GlobalMethod.showErrorDialog("记录已保存不能删除照片", self);
                return true;
            case R.id.menu_show_image:
                showSelectImage();
                return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadEvent(CommEvent event) {
        if (event.getStatus() == -1) {
            GlobalMethod.showErrorDialog(event.getMessage(), self);
        } else if (event.getStatus() == 200) {
            GlobalMethod.showDialog("系统提示", "事故图片上传成功", "知道了", self);
        }
    }

    private void showSelectImage() {
        List<String> list = adapter.getList();
        if (list == null || list.isEmpty()) {
            GlobalMethod.showErrorDialog("没有图片", this);
            return;
        }
        GlobalMethod.showImageActivity(GlobalMethod.join(list, ","), self);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
