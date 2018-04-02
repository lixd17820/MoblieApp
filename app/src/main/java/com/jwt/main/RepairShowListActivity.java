package com.jwt.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import com.jwt.activity.CommTwoRowSelectAcbarListActivity;
import com.jwt.bean.TwoColTwoSelectBean;
import com.jwt.dao.RepairDao;
import com.jwt.pojo.RepairBean;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RepairShowListActivity extends CommTwoRowSelectAcbarListActivity {

    //protected static final int MENU_UPLOAD_REP = 1;
    //protected static final int MENU_DETAIL_REP = 2;
    protected static final int MENU_DEL_REP = 3;
    private List<RepairBean> repairs;
    private Context self;
    private Button btnUpload, btnNewRepair, btnDetail;
    private int SEQ_NEW_REP = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_three_btn_show_list);
        self = this;
        changeDataFromDb();
        initView();
        btnNewRepair = (Button) findViewById(R.id.btn_left);
        btnNewRepair.setText("新增");
        btnUpload = (Button) findViewById(R.id.btn_center);
        btnUpload.setText("上传");

        btnDetail = (Button) findViewById(R.id.btn_right);
        btnDetail.setText("详细");
        btnUpload.setOnClickListener(btnListener);
        btnNewRepair.setOnClickListener(btnListener);
        btnDetail.setOnClickListener(btnListener);
        setTitle("设施报修列表");
    }

    /**
     * 将数据变化更新到控制列表中
     */
    private void createBeansList() {
        if (repairs != null && !repairs.isEmpty()) {
            beanList.clear();
            for (RepairBean rep : repairs) {
                boolean isSc = rep.getScbj() == 1;
                String text1 = rep.getBxsj();
                String text2 = rep.getItem() + "|" + rep.getBxdd();
                beanList.add(new TwoColTwoSelectBean(text1, text2, isSc, false));
            }
        }
    }

    private void changeDataFromDb() {
        repairs = RepairDao.queryRepairs(GlobalMethod.getBoxStore(self));
        if (beanList == null)
            beanList = new ArrayList<TwoColTwoSelectBean>();
        createBeansList();
    }

    /**
     * 刷新列表视图
     */
    private void referListView() {
        changeDataFromDb();
        getCommAdapter().notifyDataSetChanged();
        selectedIndex = -1;
    }

    private OnClickListener btnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            RepairBean rep = null;
            int index = getCommAdapter().getSelectIndex();
            if (index > -1)
                rep = repairs.get(index);
            if (v == btnUpload) {
                if (rep == null) {
                    GlobalMethod.showErrorDialog("请选择一条记录上传", self);
                    return;
                }
                if (TextUtils.isEmpty(rep.getPic())
                        || !(new File(rep.getPic()).exists())) {
                    GlobalMethod.showErrorDialog("没有报修图片或图片不能读取，不能上传", self);
                }
                if (1 == rep.getScbj()) {
                    GlobalMethod.showErrorDialog("已上传，无需重复上传", self);
                    return;
                }
                UploadPicThread thread = new UploadPicThread();
                thread.doStart(uploadPicHander, rep);
            } else if (v == btnNewRepair) {
                // 新建设施报修
                Intent intent = new Intent(self, RepairJtssActivity.class);
                startActivityForResult(intent, SEQ_NEW_REP);
            } else if (v == btnDetail) {
                if (rep == null) {
                    GlobalMethod.showErrorDialog("请选择一条记录查看", self);
                    return;
                }
//                Intent intent = new Intent(self,
//                        RepairJtssActivity.class);
//                intent.putExtra("rep",rep);
//                startActivity(intent);
                String t = "报修时间：" + rep.getBxsj() + "\n";
                t += "报修地点：" + rep.getBxdd() + "\n";
                t += "报修项目：" + rep.getItem() + "\n";
                t += "报修内容：" + rep.getBxnr() + "\n";
                t += "图片地址：" + (rep.getPic() == null ? "无" : rep.getPic())
                        + "\n";
                t += "系统编号："
                        + (rep.getXtbh() == null ? "无\n" : rep.getXtbh() + "\n");
                t += "是否上传：" + (rep.getScbj() == 1 ? "已上传" : "未上传");
                GlobalMethod.showDialog("详细信息", t, "确定", self);
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SEQ_NEW_REP) {
                referListView();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_DEL_REP, 0, "删除");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_DEL_REP) {
            GlobalMethod.showDialogTwoListener("系统提示", "是否确定删除，此操作无法恢复！",
                    "删除", "返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            RepairBean rep = null;
                            int index = getCommAdapter().getSelectIndex();
                            if (index > -1)
                                rep = repairs.get(index);
                            RepairDao.delRepair(rep.getId(), GlobalMethod.getBoxStore(self));
                            referListView();
                        }
                    }, self
            );
        }
        return false;
    }

    class UploadPicThread extends Thread {

        private Handler mHandler;
        private RepairBean repair;

        /**
         * 启动线程
         */
        public void doStart(Handler mHandler, RepairBean repair) {
            // 显示进度对话框
            progressDialog = new ProgressDialog(self);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在上传报修记录...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            this.mHandler = mHandler;
            this.repair = repair;
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
                    referListView();
                }
            }
        }
    };

}
