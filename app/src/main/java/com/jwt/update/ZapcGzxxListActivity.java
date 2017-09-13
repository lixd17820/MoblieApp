package com.jwt.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.jwt.activity.ActionBarListActivity;
import com.jwt.adapter.GzxxListAdapter;
import com.jwt.adapter.SelectObjectBean;
import com.jwt.dao.ZaPcdjDao;
import com.jwt.pojo.ZapcRypcxxBean;
import com.jwt.pojo.ZapcWppcxxBean;
import com.jwt.pojo.Zapcxx;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.pojo.ZapcGzxxBean;
import com.jwt.zapc.ZapcReturn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZapcGzxxListActivity extends ActionBarListActivity {
    private static final int ADDGZXX = 0;
    private static final int MENU_DELETE_GZXX = 0;
    protected static final int MENU_DETAIL_GZXX = 2;
    protected static final int MENU_CONTINUE_GZXX = 3;
    protected static final int MENU_DELETE_ALL_GZXX = 4;
    private static final int MENU_OVER_GZXX = 5;
    private List<SelectObjectBean<ZapcGzxxBean>> gzxxs;
    //private List<TwoColTwoSelectBean> selList;
    private Context self;
    private GzxxListAdapter adapter;

    private ProgressDialog progressDialog;
    private Button btnAdd, btnCon, btnOver, btnUpload;
    private String zqmj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setTheme(R.style.AppTheme);
        zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);
        self = this;
        setContentView(R.layout.zapc_gzxx_list);
        setTitle(getIntent().getStringExtra("title"));
        gzxxs = new ArrayList<>();
        referListSel();
        RecyclerView view = (RecyclerView) findViewById(R.id.gridView1);
        view.setHasFixedSize(false);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        adapter = new GzxxListAdapter(gzxxs, clickListener);
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        if (gzxxs != null && gzxxs.size() > 30) {
//            GlobalMethod.showDialog("系统提示", "盘查工作信息已达" + gzxxs.size()
//                    + "条，为提高性能，请删除已上传的记录！", "知道了", self);
//        }
        btnAdd = (Button) findViewById(R.id.btn_one);
        btnAdd.setText("新增盘查");
        btnCon = (Button) findViewById(R.id.btn_two);
        btnCon.setText("继续盘查");
        btnOver = (Button) findViewById(R.id.btn_three);
        btnOver.setText("结束盘查");
        btnUpload = (Button) findViewById(R.id.btn_four);
        btnUpload.setText("上传盘查");

        // 新增加一个工作信息
        btnAdd.setOnClickListener(btnCilck);
        btnOver.setOnClickListener(btnCilck);
        btnUpload.setOnClickListener(btnCilck);
        btnCon.setOnClickListener(btnCilck);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.zapc_gzxx_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final ZapcGzxxBean gzxx = adapter.getSelectItem();
        if (gzxx == null) {
            GlobalMethod.showErrorDialog("请选择一条记录操作", self);
            return true;
        }
        switch (item.getItemId()) {
            case R.id.gzxx_detail: {
                Intent intent = new Intent(ZapcGzxxListActivity.this,
                        ZapcGzxxActivity.class);
                intent.putExtra("gzxx", gzxx);
                startActivityForResult(intent, MENU_DETAIL_GZXX);
            }
            return true;
            case R.id.gzxx_del: {
                if (TextUtils.isEmpty(gzxx.getJssj())
                        || "0".equals(gzxx.getCsbj())) {
                    // 工作信息没有结束
                    GlobalMethod.showCanCancelDialogWithListener("系统确认",
                            "盘查工作还未结束或未上传，是否删除？", "删除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    delGzxx(gzxx);
                                }
                            }, self
                    );
                } else {
                    delGzxx(gzxx);
                }
            }
            return true;
            default:
                break;
        }
        return false;
    }


    GzxxListAdapter.ClickListener clickListener = new GzxxListAdapter.ClickListener() {

        @Override
        public void onNoteClick(int position) {
            boolean isSel = gzxxs.get(position).isSel();
            for (SelectObjectBean tt : gzxxs)
                tt.setSel(false);
            gzxxs.get(position).setSel(!isSel);
            adapter.notifyDataSetChanged();
        }
    };

    View.OnClickListener btnCilck = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnUpload) {
                ZapcGzxxBean gzxx = adapter.getSelectItem();
                if (gzxx == null) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return;
                }
                if (TextUtils.equals(gzxx.getCsbj(), "1")) {
                    GlobalMethod.showErrorDialog("记录已上传,无需重复上传", self);
                    return;
                }
                if (TextUtils.isEmpty(gzxx.getJssj())) {
                    GlobalMethod.showErrorDialog("工作未结束，不能上传", self);
                    return;
                }
                List<Zapcxx> list = ZaPcdjDao.getPcxxByGzbh(gzxx.getId(),
                        GlobalMethod.getBoxStore(self));
                if (list == null || list.isEmpty()) {
                    GlobalMethod
                            .showErrorDialog("工作信息中不包含人员或物品，无需上传", self);
                    return;
                }
                UploadZapcThread thread = new UploadZapcThread(
                        uploadHandler);
                thread.doStart(gzxx);
            } else {
                ZapcGzxxBean gzxx = getUnOverGzxx();
                if (v == btnAdd) {
                    if (gzxx != null) {
                        GlobalMethod.showErrorDialog("还有盘查工作信息没有结束，请结束后再新增！", self);
                        return;
                    }
                    Intent intent = new Intent(ZapcGzxxListActivity.this,
                            ZapcGzxxActivity.class);
                    startActivityForResult(intent, ADDGZXX);
                } else if (v == btnCon) {
                    if(gzxx == null){
                        GlobalMethod.showErrorDialog("盘查全部结束,不能继续盘查", self);
                        return;
                    }
                    Intent intent = new Intent(ZapcGzxxListActivity.this,
                            ZapcGzxxActivity.class);
                    intent.putExtra("gzxx", gzxx);
                    startActivityForResult(intent, ADDGZXX);
                } else if (v == btnOver) {
                    if (gzxx != null) {
                        gzxx.setJssj(ZaPcdjDao.sdfDpt.format(new Date()));
                        ZaPcdjDao.updateGzxx(gzxx, GlobalMethod.getBoxStore(self));
                        referListSel();
                        adapter.notifyDataSetChanged();
                    } else {
                        GlobalMethod.showErrorDialog("工作已结束，无需重复操作", self);
                    }
                }
            }
        }

    };

    /**
     * 重新载入数据
     */
    private void referListSel() {
        List<ZapcGzxxBean> xxs = ZaPcdjDao.getZapcGzxx(zqmj, GlobalMethod.getBoxStore(self));
        if (xxs == null)
            xxs = new ArrayList<ZapcGzxxBean>();
        gzxxs.clear();
        for (ZapcGzxxBean xx : xxs) {
            gzxxs.add(new SelectObjectBean<ZapcGzxxBean>(xx));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 暂停或提交了工作信息
            if (requestCode == MENU_CONTINUE_GZXX || requestCode == ADDGZXX)
                referListSel();
            adapter.notifyDataSetChanged();
        }
    }


    private void delGzxx(ZapcGzxxBean gz) {
        ZaPcdjDao.deleteGzxx(gz, GlobalMethod.getBoxStore(self));
        referListSel();
        adapter.notifyDataSetChanged();
    }

    // private int getGzxxPosition(ZapcGzxxBean g) {
    // for (int i = 0; i < adapter.getCount(); i++) {
    // if (adapter.getItem(i).getId().equals(g.getId()))
    // return i;
    // }
    // return -1;
    // }

    /**
     * 上传所有未上传的工作信息，包括人员和物品信息,所有动作在线程的RUN方法中
     */
    // private void uploadAllUnsendGzxx(ZapcGzxxBean gzxx) {
    // int needRow = ZaPcdjDao.getAllUnsendCount(getContentResolver());
    // if (needRow > 0) {
    // UploadZapcThread thread = new UploadZapcThread(uploadHandler);
    // thread.doStart(needRow);
    // } else {
    // GlobalMethod.showErrorDialog("所有盘查均已上传或还有工作信息未结束", self);
    // }
    // }
    private ZapcGzxxBean getUnOverGzxx() {
        for (SelectObjectBean<ZapcGzxxBean> gzxx : gzxxs) {
            if (TextUtils.isEmpty(gzxx.getBean().getJssj()))
                return gzxx.getBean();
        }
        return null;
    }

    class UploadZapcThread extends Thread {

        private Handler mHandler;
        private ZapcGzxxBean curGzxx;

        public UploadZapcThread(Handler handler) {
            this.mHandler = handler;
        }

        /**
         * 启动线程
         */
        public void doStart(ZapcGzxxBean curGzxx) {
            this.curGzxx = curGzxx;
            // 显示进度对话框
            progressDialog = new ProgressDialog(self);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("系统正在上传请稍等...");
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            this.start();
        }

        private String checkZapcIsUpOk(WebQueryResult<ZapcReturn> wr) {
            String error = GlobalMethod.getErrorMessageFromWeb(wr);
            if (TextUtils.isEmpty(error)
                    && TextUtils.equals("1", wr.getResult().getCgbj()))
                return "";
            return error;
        }

        /**
         * 线程运行，上传盘查，成功后发送信号给进度条
         */
        @Override
        public void run() {
            int total = 0;
            int okTotal = 0;
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<ZapcReturn> wr = dao.uploadZapcGzxx(curGzxx);
            total++;
            String totalError = "";
            String error = checkZapcIsUpOk(wr);
            if (TextUtils.isEmpty(error)) {
                // 从数据库中更新数据,重新刷新界面
                ZapcReturn zr = wr.getResult();
                if ("1".equals(zr.getCgbj())
                        && !TextUtils.isEmpty(wr.getResult().getPcbh()[0])) {
                    okTotal++;
                    curGzxx.setGzxxbh(wr.getResult().getPcbh()[0]);
                    ZaPcdjDao.updateGzxx(curGzxx, GlobalMethod.getBoxStore(self));
                    // 获取盘查人员信息
                    List<Zapcxx> list = ZaPcdjDao.getPcxxByGzbh(curGzxx.getId(),
                            GlobalMethod.getBoxStore(self));
                    // 上传物品或人员信息
                    for (Zapcxx zapcxx : list) {
                        // 区别对待物品和人员
                        if (zapcxx.getPcZl() == Zapcxx.PCRYXXZL) {
                            ZapcRypcxxBean ryxx = ZaPcdjDao.queryRyxxByBh(
                                    zapcxx.getId(), GlobalMethod.getBoxStore(self));
                            // 更新上传标记
                            wr = dao.uploadZapcRypcxx(ryxx, curGzxx.getId() + "",
                                    curGzxx.getKssj());
                            total++;
                            error = checkZapcIsUpOk(wr);
                            if (TextUtils.isEmpty(error)
                                    && "1".equals(wr.getResult().getCgbj())) {
                                okTotal++;
                                ZaPcdjDao.setPcryxxIsUpload(ryxx.getId(),
                                        GlobalMethod.getBoxStore(self));
                            } else {
                                totalError += "上传人员信息" + error + "\n";
                            }
                        } else if (zapcxx.getPcZl() == Zapcxx.PCWPXXZL) {
                            ZapcWppcxxBean wpxx = ZaPcdjDao.queryWpxxByBh(
                                    zapcxx.getId(), GlobalMethod.getBoxStore(self));
                            wr = dao.uploadZapcWpxx(wpxx, curGzxx.getId() + "",
                                    curGzxx.getKssj());
                            total++;
                            error = checkZapcIsUpOk(wr);
                            if (TextUtils.isEmpty(error)
                                    && "1".equals(wr.getResult().getCgbj())) {
                                okTotal++;
                                ZaPcdjDao.setWpxxIsUpload(wpxx.getId(), GlobalMethod.getBoxStore(self));
                            } else {
                                totalError += "上传物品信息" + error + "\n";
                            }
                        }
                    }
                }
            } else {
                // 上传失败
                totalError += "上传工作信息" + error + "\n";
            }
            if (total > 0 && total == okTotal && TextUtils.isEmpty(totalError)) {
                curGzxx.setCsbj("1");
                ZaPcdjDao.updateGzxx(curGzxx, GlobalMethod.getBoxStore(self));
            }
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("error", "上传" + total + "条记录，成功" + okTotal + "条");
            msg.setData(b);
            mHandler.sendMessage(msg);

            // int state = 0;
            // int errorMessage = 0;
            // int row = 0;
            // for (CommKeySelectedBean ks : gzxxs) {
            // ZapcGzxxBean gzxx = (ZapcGzxxBean) ks.getKey();
            // if (!TextUtils.isEmpty(gzxx.getJssj())
            // && !"1".equals(gzxx.getCsbj())) {
            // // 上传工作信息
            // WebQueryResult<ZapcReturn> re = dao.uploadZapcGzxx(gzxx);
            // // 成功则更新对象并存入数所库中
            // if (checkWebResult(re)) {
            // gzxx.setCsbj("1");
            // gzxx.setGzxxbh(re.getResult().getPcbh()[0]);
            // ZaPcdjDao.updateGzxx(gzxx, getContentResolver());
            // // 更新相对应的盘查信息的工作编号
            // ZaPcdjDao.updateRyWpGzxxbh(gzxx, getContentResolver());
            // } else {
            // // 失败则直接跳入下一个工作信息中，不再上传盘查信息
            // sendData(errorMessage, state, ++row);
            // continue;
            // }
            // sendData(errorMessage, state, ++row);
            // List<Zapcxx> list = ZaPcdjDao.getPcxxByGzbh(gzxx,
            // getContentResolver());
            // if (list != null && list.size() > 0) {
            //
            // sendData(errorMessage, state, ++row);
            // }
            // }
            // }
            // sendData(0, 0, 100000);
        }

        /**
         * 验证接口数据返回状态
         *
         * @param re
         * @return
         */
        // private boolean checkWebResult(WebQueryResult<ZapcReturn> re) {
        // return re.getStatus() == HttpStatus.SC_OK && re.getResult() != null
        // && TextUtils.equals(re.getResult().getCgbj(), "1")
        // && re.getResult().getPcbh() != null
        // && re.getResult().getPcbh().length > 0;
        // }

        // private void sendData(int err, int what, int step) {
        // Message msg = mHandler.obtainMessage();
        // msg.arg1 = err;
        // msg.what = what;
        // msg.arg2 = step;
        // mHandler.sendMessage(msg);
        // }
    }

    Handler uploadHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String totalError = b.getString("error");
            GlobalMethod.showDialog("系统提示", totalError, "确定", self);
            referListSel();
            adapter.notifyDataSetChanged();
        }
    };

}
