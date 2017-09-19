package com.jwt.update;

import java.util.ArrayList;
import java.util.List;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.activity.ActionBarSelectListActivity;
import com.jwt.bean.TwoColTwoSelectBean;
import com.jwt.dao.AcdSimpleDao;
import com.jwt.event.CommEvent;
import com.jwt.event.FxcUploadEvent;
import com.jwt.pojo.AcdPhotoBean;
import com.jwt.pojo.AcdSimpleBean;
import com.jwt.thread.AcdUploadPhotoThread;
import com.jwt.utils.GlobalMethod;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AcdPhotoListActivity extends ActionBarSelectListActivity {
    public static final int SEQ_NEW_ACD_PHOTO = 0;

    protected static final int MENU_MODIFY_ACD = 1;

    protected static final int MENU_DETAIL_ACD = 2;

    public static final int SEQ_MODIFY_ACD_PHOTO = 3;

    public static final int SEQ_SHOW_ACD_PHOTO = 4;

    private Button btnNewAcd, btnUpload, btnJycx, btnDel;

    private List<AcdPhotoBean> photos;

    private Context self;

    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.comm_four_btn_show_list);
        btnUpload = (Button) findViewById(R.id.btn_two);
        btnUpload.setText("上传");
        btnNewAcd = (Button) findViewById(R.id.btn_one);
        btnNewAcd.setText("新增");
        btnJycx = (Button) findViewById(R.id.btn_three);
        btnJycx.setText("简易程序");
        btnDel = (Button) findViewById(R.id.btn_four);
        btnDel.setText("删除");
        btnNewAcd.setOnClickListener(clickListener);
        btnUpload.setOnClickListener(clickListener);
        btnJycx.setOnClickListener(clickListener);
        btnDel.setOnClickListener(clickListener);
        changeDataFromDb();
        initView();

        getListView().setOnCreateContextMenuListener(contextMenuListener);
        //setTitle(getIntent().getStringExtra("title"));
        dialog = new MaterialDialog.Builder(self)
                .title("正在上传")
                .content("上传中...")
                .progress(false, 5, true).build();
    }

    private OnCreateContextMenuListener contextMenuListener = new OnCreateContextMenuListener() {

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenuInfo menuInfo) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int pos = mi.position;
            if (pos > -1 && photos != null && photos.size() > 0) {
                //AcdPhotoBean acd = photos.get(pos);
                //if (acd.getScbj() != 1)
                //	menu.add(Menu.NONE, MENU_MODIFY_ACD, Menu.NONE, "修改该事故");
                menu.add(Menu.NONE, MENU_DETAIL_ACD, Menu.NONE, "显示详细信息");
            }
        }
    };


    /**
     * 按扭的监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnNewAcd) {
                Intent intent = new Intent(self, AcdTakePhotoActivity.class);
                intent.putExtra(AcdSimpleDao.OPER_MOD, AcdSimpleDao.ACD_MOD_NEW);
                startActivityForResult(intent, SEQ_NEW_ACD_PHOTO);
            } else {
                if (selectedIndex < 0 || photos == null
                        || photos.get(selectedIndex) == null) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return;
                }
                AcdPhotoBean acd = photos.get(selectedIndex);
                if (v == btnUpload) {
                    if (acd.getScbj() == 1) {
                        GlobalMethod.showErrorDialog("记录已上传，无需重复上传", self);
                        return;
                    }
                    uploadAcd(acd);
                } else if (v == btnJycx) {
                    String wsbh = acd.getSgbh();
                    List<AcdSimpleBean> l = AcdSimpleDao.getAllAcd(wsbh, GlobalMethod.getBoxStore(self));
                    if (l == null || l.size() == 0) {
                        Intent intent = new Intent(self,
                                AcdJycxJbqklrActivity.class);
                        intent.putExtra(AcdSimpleDao.OPER_MOD,
                                AcdSimpleDao.ACD_MOD_PHOTO_NEW);
                        intent.putExtra(AcdTakePhotoActivity.ACD_PHOTO_BEAN,
                                acd);
                        startActivity(intent);
                    } else {
                        GlobalMethod.showErrorDialog("已存在简易程序,无需重复录入,也可删除重录",
                                self);
                    }

                } else if (v == btnDel) {
                    GlobalMethod.showDialogTwoListener("系统提示",
                            "是否确定删除，此操作无法恢复", "删除", "取消", delRecodeListener,
                            self);
                }
            }
        }
    };

    /**
     * 删除对话框中删除的监听
     */
    private DialogInterface.OnClickListener delRecodeListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            AcdPhotoBean acd = photos.get(selectedIndex);
            AcdSimpleDao.delAcdPhotoRecode(acd, GlobalMethod.getBoxStore(self));
            changeDataFromDb();
            getCommAdapter().notifyDataSetChanged();
            selectedIndex = -1;
        }
    };

    /**
     * 重新加载数据列表，并更新显示列表，不触发重显示列表
     */
    private void changeDataFromDb() {
        photos = AcdSimpleDao.getAllAcdPhoto(null, GlobalMethod.getBoxStore(self));
        if (beanList == null)
            beanList = new ArrayList<TwoColTwoSelectBean>();
        createBeanFromAcdPhoto(photos);
        setTitle("事故图片－共" + photos.size() + "条");
    }

    /**
     * 上传记录的方法实现
     *
     * @param acd
     */
    protected void uploadAcd(AcdPhotoBean acd) {
        AcdUploadPhotoThread thread = new AcdUploadPhotoThread(acd, self);
        thread.doStart();
        dialog.show();
    }

    /**
     * 根据数据列表，重加载显示列表数据
     *
     * @param photos
     */
    private void createBeanFromAcdPhoto(List<AcdPhotoBean> photos) {
        beanList.clear();
        for (AcdPhotoBean photo : photos) {
            String[] ps = photo.getPhoto().split(",");
            String text1 = photo.getSgbh() + "|" + photo.getSgsj() + "|" + ps.length + "张";
            String text2 = getString(R.string.acd_position) + ":"
                    + photo.getSgdd();
            boolean isSc = photo.getScbj() == 1;
            beanList.add(new TwoColTwoSelectBean(text1, text2, isSc, false));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEQ_NEW_ACD_PHOTO
                || requestCode == SEQ_MODIFY_ACD_PHOTO) {
            if (resultCode == RESULT_OK) {
                changeDataFromDb();
                getCommAdapter().notifyDataSetChanged();
                selectedIndex = -1;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadEvent(FxcUploadEvent event) {
        if (event.err > 0) {
            dialog.dismiss();
            GlobalMethod.showErrorDialog(event.message, self);
            return;
        }
        if (event.isDone) {
            Toast.makeText(self, "上传成功", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            changeDataFromDb();
            getCommAdapter().notifyDataSetChanged();
            return;
        }
        dialog.setMaxProgress(event.total);
        dialog.setProgress(event.step);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acd_photo_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_detail:
                if (selectedIndex < 0 || photos == null
                        || photos.get(selectedIndex) == null) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return true;
                }
                AcdPhotoBean acd = photos.get(selectedIndex);
                Intent intent = new Intent(self, AcdTakePhotoActivity.class);
                intent.putExtra(AcdSimpleDao.PHOTO_BEAN, acd);
                intent.putExtra(AcdSimpleDao.OPER_MOD,
                        AcdSimpleDao.ACD_MOD_SHOW);
                startActivityForResult(intent, SEQ_SHOW_ACD_PHOTO);
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
