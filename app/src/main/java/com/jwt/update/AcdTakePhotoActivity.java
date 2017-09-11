package com.jwt.update;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.AcdSimpleDao;
import com.jwt.dao.WsglDAO;
import com.jwt.jbyw.AcdPhotoBean;
import com.jwt.pojo.THmb;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;

@SuppressLint("NewApi")
public class AcdTakePhotoActivity extends AppCompatActivity {
    private static final int FIND_SGDD = 0;

    private static final int CAMER_REQUEST = 1024;

    protected static final int MENU_DEL_IMG = 2;

    public static final String ACD_PHOTO_BEAN = "acdPhoto";

    public static final int REQ_SELECT_PIC = 3;

    private Button btnChgSgsj, btnChgSgdd;
    private Button btnChgSgrq, btnShowPhoto;
    private EditText edSgsj;
    private ImageView mImageView;
    private Spinner spinImage, spinSgdd;

    private SimpleDateFormat sdf;
    private Context self;
    private Activity activity;
    private List<String> bigPhotoList, smallPhotoList;
    private THmb dqbmb;
    private AcdPhotoBean acdPhoto = null;
    private ProgressDialog progressDialog;
    private int photoIndex = -1;
    private int operMod;
    private boolean isSave;
    private Bitmap currentSmallImage;
    private KeyValueBean kvSgdd = null;
    private List<KeyValueBean> textList;

    private static final String STATE_BIG_LIST = "big_list";
    private static final String STATE_TEXT_LIST = "text_list";
    private static final String STATE_SMALL_LIST = "small_list";
    private static final String STATE_PHOTO_INDEX = "photo_index";
    private static final String STATE_OPER_MOD_INT = "operMod";
    private static final String STATE_IS_SAVE_BOL = "isSave";
    private static final String STATE_DQBMB = "dqbmb";
    private static final String STATE_ACD_PHOTO = "acdPhoto";
    private static final String STATE_KV_SGDD = "kvSgdd";

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        Log.e("AcdTakePhotoActivity", "onRestoreInstanceState");
        if (savedInstanceState.containsKey(STATE_SMALL_LIST)) {
            smallPhotoList = savedInstanceState
                    .getStringArrayList(STATE_SMALL_LIST);
        }

        if (savedInstanceState.containsKey(STATE_BIG_LIST)) {
            bigPhotoList = savedInstanceState
                    .getStringArrayList(STATE_BIG_LIST);
        }

        if (savedInstanceState.containsKey(STATE_PHOTO_INDEX)) {
            photoIndex = savedInstanceState.getInt(STATE_PHOTO_INDEX);
        }

        if (savedInstanceState.containsKey(STATE_OPER_MOD_INT)) {
            operMod = savedInstanceState.getInt(STATE_OPER_MOD_INT);
        }

        if (savedInstanceState.containsKey(STATE_IS_SAVE_BOL)) {
            isSave = savedInstanceState.getBoolean(STATE_IS_SAVE_BOL);
        }

        if (savedInstanceState.containsKey(STATE_DQBMB)) {
            dqbmb = (THmb) savedInstanceState.getSerializable(STATE_DQBMB);
        }

        if (savedInstanceState.containsKey(STATE_ACD_PHOTO)) {
            acdPhoto = (AcdPhotoBean) savedInstanceState
                    .getSerializable(STATE_ACD_PHOTO);
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
        outState.putStringArrayList(STATE_SMALL_LIST,
                (ArrayList<String>) smallPhotoList);
        outState.putStringArrayList(STATE_BIG_LIST,
                (ArrayList<String>) bigPhotoList);
        outState.putInt(STATE_PHOTO_INDEX, photoIndex);
        outState.putSerializable(STATE_DQBMB, dqbmb);
        outState.putInt(STATE_OPER_MOD_INT, operMod);
        outState.putBoolean(STATE_IS_SAVE_BOL, isSave);
        if (acdPhoto != null)
            outState.putSerializable(STATE_ACD_PHOTO, acdPhoto);
        if (kvSgdd != null) {
            outState.putSerializable(STATE_KV_SGDD, kvSgdd);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acd_take_photo);
        Log.e("AcdTakePhotoActivity", "onCreate");
        self = this;
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

        operMod = getIntent().getIntExtra(AcdSimpleDao.OPER_MOD,
                AcdSimpleDao.ACD_MOD_NEW);
        acdPhoto = (AcdPhotoBean) getIntent().getSerializableExtra(
                AcdSimpleDao.PHOTO_BEAN);

        btnChgSgsj = (Button) findViewById(R.id.btn_chg_sgsj);
        btnChgSgrq = (Button) findViewById(R.id.btn_chg_sgrq);
        btnChgSgdd = (Button) findViewById(R.id.btn_chg_sgdd);
        btnShowPhoto = (Button) findViewById(R.id.btn_show_image);
        edSgsj = (EditText) findViewById(R.id.edit_acd_sgsj);
        edSgsj.setKeyListener(null);
        spinSgdd = (Spinner) findViewById(R.id.spin_sgdd);
        spinImage = (Spinner) findViewById(R.id.spin_images);
        mImageView = (ImageView) findViewById(R.id.imageView1);
        setTitle("事故图片采集");
        // 初始化照片文件列表
        if (operMod == AcdSimpleDao.ACD_MOD_NEW) {
            edSgsj.setText(sdf.format(new Date()));
            smallPhotoList = new ArrayList<String>();
            isSave = false;
            dqbmb = WsglDAO.getCurrentJdsbh(GlobalConstant.ACDSIMPLEWS, zqmj,
                    GlobalMethod.getBoxStore(self));
            if (dqbmb == null) {
                GlobalMethod.showDialogWithListener("系统提示",
                        "未获取简易事故处理编号，请在文书管理中获取！", "确定", finishView, self);
                return;
            }
            ((TextView) findViewById(R.id.tv_sgbh)).setText("事故编号："
                    + dqbmb.getDqhm());
        } else if ((operMod == AcdSimpleDao.ACD_MOD_SHOW || operMod == AcdSimpleDao.ACD_MOD_MODITY) && acdPhoto != null) {
            edSgsj.setText(acdPhoto.getSgsj());
            kvSgdd = new KeyValueBean(acdPhoto.getSgdddm(), acdPhoto.getSgdd());
            GlobalMethod.changeAdapter(spinSgdd, kvSgdd, activity, 0);
            ((TextView) findViewById(R.id.tv_sgbh)).setText("事故编号："
                    + acdPhoto.getSgbh());
            if (operMod == AcdSimpleDao.ACD_MOD_SHOW) {
                //查看模式
                String[] s = acdPhoto.getPhoto().split(",");
                smallPhotoList = Arrays.asList(s);
                isSave = true;
            } else {
                smallPhotoList = new ArrayList<String>();
                isSave = false;
            }
        }
        edSgsj.setKeyListener(null);
        GlobalMethod.changeAdapter(spinImage, textList, (Activity) self);
        //

        bigPhotoList = new ArrayList<String>();
        btnChgSgsj.setOnClickListener(clListener);
        btnChgSgrq.setOnClickListener(clListener);
        btnChgSgdd.setOnClickListener(clListener);
        btnShowPhoto.setOnClickListener(clListener);
        spinImage.setOnItemSelectedListener(sl);
        showSmallImage();
        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (photoIndex > -1 && smallPhotoList != null && !smallPhotoList.isEmpty()
                        && smallPhotoList.size() > photoIndex) {
                    showImageActivity(smallPhotoList.get(photoIndex));
                }
            }
        });
    }

    private void showImageActivity(String file) {
        Intent intent = new Intent(self, ShowImageActivity.class);
        intent.putExtra("image", file);
        startActivity(intent);
    }

    OnSpinnerItemSelected sl = new OnSpinnerItemSelected() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (photoIndex != position) {
                photoIndex = position;
                threadReferImageView();
            }
        }
    };

    /**
     * 多线程刷新图片
     */
    private void threadReferImageView() {
        Handler handler = new ChangeImageHandler(AcdTakePhotoActivity.this);
        ChangeImageThread thread = new ChangeImageThread(handler);
        thread.start();
    }

    class ChangeImageThread extends Thread {
        private Handler mHandler;

        public ChangeImageThread(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            if (photoIndex > -1 && smallPhotoList != null
                    && !smallPhotoList.isEmpty()) {
                String file = smallPhotoList.get(photoIndex);
                File smallFn = new File(file);
                if (smallFn.exists()) {
                    currentSmallImage = GlobalMethod.getImageFromFile(file);
                }
            }
            boolean isOk = currentSmallImage != null;
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("isOk", isOk);
            msg.setData(b);
            mHandler.sendMessage(msg);
        }

    }

    static class ChangeImageHandler extends Handler {

        private final WeakReference<AcdTakePhotoActivity> myActivity;

        public ChangeImageHandler(AcdTakePhotoActivity activity) {
            myActivity = new WeakReference<AcdTakePhotoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AcdTakePhotoActivity ac = myActivity.get();
            if (ac != null) {
                Bundle data = msg.getData();
                if (data != null && data.getBoolean("isOk"))
                    ac.showSmallImage();
            }
        }
    }

    private void showSmallText() {
        if (smallPhotoList == null)
            smallPhotoList = new ArrayList<String>();
        if (textList == null)
            textList = new ArrayList<KeyValueBean>();
        textList.clear();
        for (int i = 0; i < smallPhotoList.size(); i++) {
            textList.add(new KeyValueBean(String.valueOf(i), "第" + (i + 1) + "张图片"));
        }
        GlobalMethod.changeAdapter(spinImage, textList, (Activity) self);
        GlobalMethod.changeSpinnerSelect(spinImage, String.valueOf(photoIndex), GlobalConstant.KEY, true);
    }

    private void showSmallImage() {
        if (currentSmallImage != null)
            mImageView.setImageBitmap(currentSmallImage);
        spinImage.requestFocus();
    }

    private View.OnClickListener clListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // 是否保存已做了判断，后续无需处理
            if (v == btnShowPhoto) {
                threadReferImageView();
            } else if (v == btnChgSgsj) {
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
        progressDialog = new ProgressDialog(self);
        int maxStep = acd.getPhoto().split(",").length + 1;
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在上传事故信息...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxStep * 25);
        progressDialog.show();
        //AcdUploadPhotoThread thread = new AcdUploadPhotoThread(upHandler, acd);
        //thread.doStart();
    }

    private Handler upHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            int step = 0;
            switch (what) {
                case GlobalConstant.WHAT_ERR:
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Bundle data = msg.getData();
                    String err = data.getString("err");
                    GlobalMethod.showErrorDialog(err, self);
                    break;
                case GlobalConstant.WHAT_RECODE_OK:
                    step = msg.arg1;
                    progressDialog.setProgress(step);
                    break;
                case GlobalConstant.WHAT_PHOTO_OK:
                    step = msg.arg1;
                    progressDialog.setProgress(step);
                    progressDialog.setMessage("正在上传第" + (step / 25) + "张图片");
                    break;
                case GlobalConstant.WHAT_ALL_OK:
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (msg.getData() != null) {
                        long recID = msg.getData().getLong("xtbh");
                        int acdID = msg.getData().getInt("acdID");
                        int r = AcdSimpleDao.updateAcdPhotoRecode(
                                recID, acdID, GlobalMethod.getBoxStore(self));
                        if (r > 0)
                            acdPhoto.setXtbh(String.valueOf(recID));
                        r = AcdSimpleDao.updateAcdPhotoRecodeScbj(
                                acdID, GlobalMethod.getBoxStore(self));
                        if (r > 0)
                            acdPhoto.setScbj(1);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private void delImage() {
        int pos = spinImage.getSelectedItemPosition();
        if (pos < 0 || bigPhotoList == null || bigPhotoList.size() <= pos)
            return;
        File bf = new File(bigPhotoList.get(pos));
        bf.delete();
        bigPhotoList.remove(pos);
        File mf = new File(smallPhotoList.get(pos));
        mf.delete();
        smallPhotoList.remove(pos);
        photoIndex = bigPhotoList.size() - 1;
        if (bigPhotoList.isEmpty()) {
            mImageView.setImageBitmap(null);
            mImageView.destroyDrawingCache();
        } else
            threadReferImageView();
        showSmallText();
    }

    private void delImageListener() {
        if (isSave) {
            GlobalMethod.showErrorDialog("记录已保存，不能删除图片", self);
            return;
        }
        int pos = spinImage.getSelectedItemPosition();
        if (pos < 0 || bigPhotoList == null || bigPhotoList.size() <= pos)
            return;
        GlobalMethod.showDialogTwoListener("系统提示", "是否删除图片，该操作将无法恢复", "删除",
                "取消", delImageDialog, self);
    }

    private void savePhoto() {
        // 需要对数据进行验证
        String err = checkData();
        if (!TextUtils.isEmpty(err)) {
            GlobalMethod.showErrorDialog(err, self);
            return;
        }
        if (!isSave) {
            if (acdPhoto == null)
                acdPhoto = new AcdPhotoBean();
            acdPhoto.setPhoto(GlobalMethod.join(smallPhotoList, ","));
            acdPhoto.setSgsj(edSgsj.getText().toString());
            acdPhoto.setSgdd(GlobalMethod.getKeyFromSpinnerSelected(spinSgdd,
                    GlobalConstant.VALUE));
            acdPhoto.setSgdddm(GlobalMethod.getKeyFromSpinnerSelected(spinSgdd,
                    GlobalConstant.KEY));
            if (operMod == AcdSimpleDao.ACD_MOD_NEW)
                acdPhoto.setSgbh(dqbmb.getDqhm());
            long id = AcdSimpleDao
                    .addAcdPhoto(acdPhoto, GlobalMethod.getBoxStore(self));
            if (id > 0) {
                if (operMod == AcdSimpleDao.ACD_MOD_NEW) {
                    WsglDAO.saveHmbAddOne(dqbmb, GlobalMethod.getBoxStore(self));
                } else if (operMod == AcdSimpleDao.ACD_MOD_MODITY) {
                    AcdSimpleDao.delAcdPhotoRecode(acdPhoto, GlobalMethod.getBoxStore(self));
                }
                isSave = true;
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
        if (TextUtils.isEmpty(edSgsj.getText())) {
            return "事故时间不能为空";
        } else if (spinSgdd.getSelectedItemPosition() < 0) {
            return "事故地点不能为空";
        } else if (smallPhotoList == null || smallPhotoList.isEmpty()) {
            return "图片不能为空";
        } else if (smallPhotoList.size() < GlobalConstant.MIN_ACD_PHOTO_COUNT) {
            return "图片不能少于" + GlobalConstant.MIN_ACD_PHOTO_COUNT + "张";
        }
        return null;
    }

    private void startTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = GlobalMethod.createImageFile(activity, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                bigPhotoList.add(photoFile.getAbsolutePath());
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
        Log.e("AcdTakePhotoActivity", "onResume");
        showSmallText();
        Log.e("AcdTakePhotoActivity", "" + GlobalSystemParam.isPreviewPhoto
                + "/" + photoIndex);
        if (GlobalSystemParam.isPreviewPhoto && photoIndex > -1)
            threadReferImageView();
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
        } else {
            if (requestCode == CAMER_REQUEST) {
                while (bigPhotoList.size() > smallPhotoList.size()) {
                    bigPhotoList.remove(bigPhotoList.size() - 1);
                }
            }
        }
    }

    private void cameraActivityResult() {
        if (bigPhotoList == null || bigPhotoList.isEmpty())
            return;
        String mCurrentPhotoPath = bigPhotoList.get(bigPhotoList.size() - 1);
        File image = new File(mCurrentPhotoPath);
        if (!image.exists()) {
            Toast.makeText(self, "照片拍摄失败", Toast.LENGTH_LONG).show();
            bigPhotoList.remove(bigPhotoList.size() - 1);
            return;
        }
        // 大于最大张数，删除每一张
        if (bigPhotoList.size() > GlobalConstant.MAX_ACD_PHOTO_COUNT) {
            bigPhotoList.remove(0);
            smallPhotoList.remove(0);
        }
        File dir = image.getParentFile();
        String fn = image.getName();
        dir = new File(dir, "small");
        if (!dir.exists())
            dir.mkdirs();
        File smallF = new File(dir, fn);
        String text = "拍摄时间："
                + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        Bitmap smallImage = GlobalMethod.compressBitmap(mCurrentPhotoPath, 800,
                text);
        if (smallImage == null) {
            Toast.makeText(self, "照片压缩失败", Toast.LENGTH_LONG).show();
            bigPhotoList.remove(bigPhotoList.size() - 1);
            return;
        }
        boolean isSave = GlobalMethod.savePicIntoFile(smallImage, smallF);
        if (!isSave) {
            Toast.makeText(self, "照片保存失败", Toast.LENGTH_LONG).show();
            bigPhotoList.remove(bigPhotoList.size() - 1);
            return;
        }
        smallPhotoList.add(smallF.getAbsolutePath());
        photoIndex = smallPhotoList.size() - 1;
        // GlobalMethod.showPicFileDialog(smallImage, self, null);
        showImageActivity(smallF.getAbsolutePath());
    }

    @Override
    public void onBackPressed() {
        if (isSave) {
            setResult(RESULT_OK);
            super.onBackPressed();
        } else {
            GlobalMethod.showDialogTwoListener("系统提示", "记录还没有保存，是否退出", "退出",
                    "取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelSave();
                        }
                    }, self
            );
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

    private DialogInterface.OnClickListener delImageDialog = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            delImage();
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isOffConn = GlobalData.connCata == ConnCata.OFFCONN
                || GlobalData.connCata == ConnCata.UNKNOW;
        boolean noMod = operMod == AcdSimpleDao.ACD_MOD_SHOW;
        if (noMod) {
            menu.removeItem(R.id.menu_upload);
            menu.removeItem(R.id.open_camare);
            menu.removeItem(R.id.save_file);
            menu.removeItem(R.id.menu_del_image);
        } else {
            if (isOffConn)
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
        switch (item.getItemId()) {
            case R.id.save_file:
                if (isSave) {
                    GlobalMethod.showErrorDialog("记录已保存不能更改", self);
                    return true;
                }
                if (bigPhotoList.size() >= GlobalConstant.MIN_ACD_PHOTO_COUNT) {
                    savePhoto();
                } else {
                    GlobalMethod.showErrorDialog("图片至少需要"
                            + GlobalConstant.MIN_ACD_PHOTO_COUNT + "张", self);
                }
                return true;
            case R.id.open_camare:
                if (bigPhotoList.size() >= GlobalConstant.MAX_ACD_PHOTO_COUNT) {
                    GlobalMethod.showDialog("系统提示", "照片已达到"
                            + GlobalConstant.MAX_ACD_PHOTO_COUNT
                            + "张，请删除一张后继续拍", "确定", self);
                } else {
                    startTakePhoto();
                }
                return true;
            case R.id.menu_upload:
                if (isSave && acdPhoto != null && acdPhoto.getScbj() != 1) {
                    uploadAcd(acdPhoto);
                }
            case R.id.menu_del_image:
                delImageListener();
                return true;
        }
        return false;
    }

}
