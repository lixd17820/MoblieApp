package com.jwt.update;

import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.jwt.adapter.ImageListAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.RepairDao;
import com.jwt.pojo.RepairBean;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RepairJtssActivity extends AppCompatActivity {

    protected static final int FIND_BXDD = 0;
    protected static final int CAMER_REQUEST = 1;
    private Context self;
    private EditText editBxdd, EditBxnr;
    private Spinner spinBxxm, spinSide;
    private String[] bxItem, bxSide;
    private RepairBean repair;
    private KeyValueBean kvBxdd;
    // -------------------------------------------------------------
    private boolean isSave;
    private ProgressDialog progressDialog;
    private static String TAG = "RepairJtssActivity";

    private ImageListAdapter adapter;
    private String photoName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair_jtss);
        ButterKnife.bind(this);
        bxItem = getResources().getStringArray(R.array.bx_item);
        bxSide = getResources().getStringArray(R.array.bx_side);
        self = this;
        isSave = false;
        editBxdd = (EditText) findViewById(R.id.edit_rep_bxdd);
        EditBxnr = (EditText) findViewById(R.id.edit_rep_bxnr);
        spinBxxm = (Spinner) findViewById(R.id.spin_rep_item);
        spinSide = (Spinner) findViewById(R.id.spin_rep_side);
        RecyclerView gridView = (RecyclerView) findViewById(R.id.gridView1);
        gridView.setHasFixedSize(true);
        gridView.setLayoutManager(new GridLayoutManager(this, 2));
        gridView.setAdapter(adapter = new ImageListAdapter(imgClick, new ArrayList<String>()));
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(self,
                R.layout.spinner_item, bxItem);
        itemAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBxxm.setAdapter(itemAdapter);
        ArrayAdapter<String> sideAdapter = new ArrayAdapter<String>(self,
                R.layout.spinner_item, bxSide);
        sideAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSide.setAdapter(sideAdapter);
        repair = new RepairBean();
        kvBxdd = new KeyValueBean("", "");
        setTitle("交通设施报修");
    }

    ImageListAdapter.ImageClickListener imgClick = new ImageListAdapter.ImageClickListener() {
        @Override
        public void onClick(int position) {
            adapter.setSelectIndex(position);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("RepairActivity", requestCode + "/" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == FIND_BXDD) {
                if (data == null)
                    return;
                Bundle b = data.getExtras();
                if (b != null) {
                    kvBxdd.setKey(b.getString("wfddDm"));
                    kvBxdd.setValue(b.getString("wfddMc"));
                    editBxdd.setText(b.getString("wfddMc"));
                }
            } else if (requestCode == CAMER_REQUEST) {
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
                dir = new File(dir, "repair");
                if (!dir.exists())
                    dir.mkdirs();
                File smallF = new File(dir, fn);
                String text = "拍摄时间："
                        + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
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
            }
        }
    }


    @OnClick(R.id.btn_rep_bxdd)
    public void findBxdd() {
        Intent intent = new Intent(self, ConfigWfddActivity.class);
        startActivityForResult(intent, FIND_BXDD);
    }

    @OnClick(R.id.btn_rep_save)
    public void saveRep() {
        if (repair.getScbj() > 0 || isSave) {
            GlobalMethod.showErrorDialog("该报修已保存或已上传，无需重复保存！", self);
            return;
        }
        String err = checkSaveRepair();
        if (!TextUtils.isEmpty(err)) {
            GlobalMethod.showErrorDialog(err, self);
            return;
        }
        long id = 0L;
        if (repair.getId() != 0) {
            id = RepairDao.updateRepair(repair, GlobalMethod.getBoxStore(self));
        } else {
            List<String> pics = adapter.getList();
            if (pics != null && !pics.isEmpty()) {
                repair.setPic(GlobalMethod.join(pics, ","));
            }
            long res = RepairDao.updateRepair(repair, GlobalMethod.getBoxStore(self));
            id = Long.valueOf(res);
            if (id > 0) {
                isSave = true;
            }
        }
        GlobalMethod.showDialog(getString(R.string.sys_prompt_text),
                id > 0 ? "交通设施报修保存成功!" : "未能保存交通设施报修信息!", "确定", self);
    }

    @OnClick(R.id.btn_show_image)
    public void showRepairImage() {
        int index = adapter.getSelectIndex();
        if (index < 0) {
            GlobalMethod.showErrorDialog("请选择一张图片", this);
            return;
        }
        String file = adapter.getImg(index);
        showImageActivity(file);
    }

    @OnClick(R.id.btn_rep_upload)
    public void uploadRepair() {
        if (!isSave) {
            GlobalMethod.showErrorDialog("请先保存记录才可以上传", self);
            return;
        }
        if (repair.getScbj() > 0) {
            GlobalMethod.showErrorDialog("该报修已经上传，无需重复上传！", self);
            return;
        }
        UploadPicThread thread = new UploadPicThread();
        thread.doStart(uploadPicHander);
    }

    @OnClick(R.id.btn_rep_pic)
    public void takePhoto() {
        if (isSave || repair.getScbj() > 0) {
            GlobalMethod.showErrorDialog("记录已保存或上传，不要拍照了", self);
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = GlobalMethod.createImageFile(RepairJtssActivity.this,
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

    private void showImageActivity(String file) {
        Intent intent = new Intent(self, ShowImageActivity.class);
        intent.putExtra("image", file);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //litMap.recycle();
    }

    private String checkSaveRepair() {
        List<String> images = adapter.getList();
        if (images == null || images.isEmpty())
            return "报修图片不能为空";
        if (TextUtils.isEmpty(kvBxdd.getKey())
                || TextUtils.isEmpty(kvBxdd.getValue())) {
            return "报修地点不能为空";
        }
        repair.setBxdd(editBxdd.getText().toString());
        repair.setXzqh(kvBxdd.getKey().substring(0, 4));
        if (TextUtils.isEmpty(EditBxnr.getText()))
            return "报修内容不能不空";
        repair.setBxnr(EditBxnr.getText().toString());
        repair.setItem((String) spinBxxm.getSelectedItem());
        repair.setBxsj(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date()));
        repair.setScbj(0);
        return null;
    }

    @Override
    public void onBackPressed() {
        if (!isSave) {
            // 没有保存
            GlobalMethod.showDialogTwoListener("系统提示", "记录没有保存，是否退出？",
                    "退出", "返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    }, self
            );
            return;
        }
        if (repair.getScbj() < 1) {
            // 没有上传
            GlobalMethod.showDialogTwoListener("系统提示", "记录没有上传，是否退出？",
                    "退出", "返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }, self
            );
            return;
        }
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }

    class UploadPicThread extends Thread {

        private Handler mHandler;

        /**
         * 启动线程
         */
        public void doStart(Handler mHandler) {
            // 显示进度对话框
            progressDialog = new ProgressDialog(self);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在上传报修记录...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            this.mHandler = mHandler;
            this.start();
        }

        @Override
        public void run() {
            Message m = mHandler.obtainMessage();
            Bundle data = new Bundle();
            data.putInt("catalog", 1);
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<ZapcReturn> re = dao.uploadRepair(repair);
            String err = GlobalMethod.getErrorMessageFromWeb(re);
            if (TextUtils.isEmpty(err)) {
                ZapcReturn z = re.getResult();
                if (z != null) {
                    if (TextUtils.equals(z.getCgbj(), "1")) {
                        // 系统返回记录上传成功
                        repair.setXtbh(re.getResult().getPcbh()[0]);
                        repair.setScbj(1);
                        re = dao.uploadRepPic(Long.valueOf(repair.getXtbh()),
                                repair.getPic(), mHandler);
                        err = GlobalMethod.getErrorMessageFromWeb(re);
                        if (TextUtils.isEmpty(err)) {
                            z = re.getResult();
                            if (z != null) {
                                RepairDao.updateRepair(repair, GlobalMethod.getBoxStore(self));
                            }
                            data.putBoolean("isOk", true);
                            data.putString("msg", z.getScms());
                            data.putSerializable("rep", repair);
                            m.setData(data);
                            mHandler.sendMessage(m);
                            return;
                        } else {
                            data.putSerializable("rep", repair);
                            data.putString("msg", err);
                        }
                    } else
                        data.putString("msg", z.getScms());
                } else {
                    data.putString("msg", "上传失败");
                }
            } else {
                data.putString("msg", err);
            }
            m.setData(data);
            mHandler.sendMessage(m);
        }
    }

    /**
     * 上传图片控制回调
     */
    private Handler uploadPicHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (data != null) {
                int catalog = data.getInt("catalog");
                if (catalog == 0) {
                    int length = data.getInt("length");
                    int step = data.getInt("step");
                    progressDialog.setMax(length);
                    progressDialog.setProgress(step);
                    if (step < length) {
                        progressDialog.setMessage("正在上传报修图片...");
                    } else {
                        progressDialog.setMessage("报修图片已上传...");
                    }
                } else if (catalog == 1) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    boolean isOk = data.getBoolean("isOk");
                    String message = data.getString("msg");
                    if (isOk)
                        GlobalMethod.showDialog("系统提示", message, "知道了", self);
                    else
                        GlobalMethod.showErrorDialog(message, self);
                }
            }
        }
    };

}
