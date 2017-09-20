package com.jwt.update;

import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;


import com.jwt.activity.ActionBarListActivity;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.adapter.RywpListAdapter;
import com.jwt.adapter.SelectObjectBean;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.ZaPcdjDao;
import com.jwt.pojo.ZapcGzxxBean;
import com.jwt.pojo.ZapcWppcxxBean;
import com.jwt.utils.CommParserXml;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.pojo.ZapcRypcxxBean;
import com.jwt.pojo.Zapcxx;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZapcGzxxActivity extends ActionBarListActivity {
    private static final int MENU_PCJDC = 1;
    private static final int REQ_PCJDC = 4;
    private static final int REQ_PCRY = 5;
    protected static final int MENU_DELETE_PCXX = 3;
    protected static final int MENU_UPLOAD_PCXX = 2;
    private Spinner spXqxd, spXffs;
    private EditText edGzxxId, edGzdd, edKssj, edFjrs;
    private List<KeyValueBean> xqxds;
    private Context self;
    private List<SelectObjectBean<Zapcxx>> ryWpxxList = new ArrayList<>();
    private RywpListAdapter adapter;

    private boolean isNew = true, isOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zapc_gzxx_pcxxlb);

        // 初如化控件
        edGzxxId = (EditText) findViewById(R.id.edit_gzxx_id);
        spXqxd = (Spinner) findViewById(R.id.Spinn_xqxd);
        edGzdd = (EditText) findViewById(R.id.Edit_gzdd);
        spXffs = (Spinner) findViewById(R.id.Spin_xffs);
        edKssj = (EditText) findViewById(R.id.Edit_kspcsj);
        edFjrs = (EditText) findViewById(R.id.Edit_fjrs);
        edKssj.setEnabled(false);
        edGzxxId.setEnabled(false);
        // 初始化数据

        xqxds = ZaPcdjDao.getZapcLxxx(GlobalData.grxx.get(GlobalConstant.YBMBH),
                GlobalMethod.getBoxStore(self));
        GlobalMethod.changeAdapter(spXqxd, xqxds, (Activity) self);
        GlobalMethod.changeAdapter(spXffs,
                ZaPcdjDao.zapcDic.get(ZaPcdjDao.XFFS), (Activity) self);
        ZapcGzxxBean gzxx = (ZapcGzxxBean) getIntent().getSerializableExtra("gzxx");
        isNew = gzxx == null;
        // 从列表选择中获取盘查信息，并对界面赋值
        if (!isNew) {
            edGzxxId.setText(gzxx.getId() + "");
            // isOver = !TextUtils.isEmpty(gzxx.getJssj());
            spXqxd.setSelection(GlobalMethod.getPositionByKey(xqxds,
                    gzxx.getXlmc()));
            spXffs.setSelection(GlobalMethod.getPositionByKey(
                    ZaPcdjDao.zapcDic.get(ZaPcdjDao.XFFS), gzxx.getXffs()));
            edKssj.setText(ZaPcdjDao.changeDptModNor(gzxx.getKssj()));
            edFjrs.setText(gzxx.getFjrs());
            if (!TextUtils.isEmpty(gzxx.getJssj())) {
                LinearLayout line = (LinearLayout) findViewById(R.id.bottom_but);
                RelativeLayout main = (RelativeLayout) findViewById(R.id.main_relative_layout);
                main.removeView(line);
            }
            isOver = !TextUtils.isEmpty(gzxx.getJssj());
        } else {
            gzxx = new ZapcGzxxBean();
            edKssj.setText(ZaPcdjDao.sdfNor.format(new Date()));
            edFjrs.setText("1");
        }
        if (TextUtils.isEmpty(gzxx.getJssj())) {
            findViewById(R.id.but_gz_pcry).setOnClickListener(startPcBut);
            findViewById(R.id.but_pause_gz).setOnClickListener(pausePcBut);
            findViewById(R.id.but_over_gz).setOnClickListener(overPcBut);
        }
        spXqxd.setEnabled(isNew);
        spXffs.setEnabled(isNew);
        edGzdd.setEnabled(isNew);
        edFjrs.setEnabled(isNew);
        RecyclerView view = (RecyclerView) findViewById(R.id.gridView1);
        view.setHasFixedSize(false);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        adapter = new RywpListAdapter(ryWpxxList, clickListener);
        view.setAdapter(adapter);
        refershView();

        // 巡区巡段变化，对工作地点进行相应的赋值
        spXqxd.setOnItemSelectedListener(new OnSpinnerItemSelected() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {
                KeyValueBean kv = (KeyValueBean) adapter.getAdapter().getItem(
                        position);
                edGzdd.setText(kv.getValue());
            }

        });
        setTitle("治安盘查");
    }

    RywpListAdapter.ClickListener clickListener = new RywpListAdapter.ClickListener() {

        @Override
        public void onNoteClick(int position) {
            boolean isSel = ryWpxxList.get(position).isSel();
            for (SelectObjectBean tt : ryWpxxList)
                tt.setSel(false);
            ryWpxxList.get(position).setSel(!isSel);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.zapc_gzxx_pc_menu, menu);
        if (isOver) {
            menu.removeItem(R.id.gzxx_pcjdc);
            menu.removeItem(R.id.gzxx_del);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long gzxxId = Long.valueOf(edGzxxId.getText().toString());
        final Zapcxx pcxx = adapter.getSelectItem();
        if (pcxx == null) {
            GlobalMethod.showErrorDialog("请选择一条记录操作", self);
            return true;
        }
        switch (item.getItemId()) {
            case R.id.gzxx_pcjdc: {
                Intent intent = new Intent(this,
                        ZapcJdcActivity.class);
                intent.putExtra("pcrybh", pcxx.getId());
                intent.putExtra("gzbh", gzxxId);
                intent.putExtra("pcdd", pcxx.getPcdd());
                startActivityForResult(intent, REQ_PCJDC);
            }
            return true;
            case R.id.gzxx_del: {
                if ("0".equals(pcxx.getScbj()) || TextUtils.isEmpty(pcxx.getScbj())) {
                    GlobalMethod.showDialogTwoListener("系统提示", "是否确定删除?", "确定",
                            "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    if (pcxx.getPcZl() == Zapcxx.PCRYXXZL) {
                                        ZaPcdjDao.delPcryxxById(pcxx.getId(),
                                                GlobalMethod.getBoxStore(self));
                                    } else if (pcxx.getPcZl() == Zapcxx.PCWPXXZL) {
                                        ZaPcdjDao.delPcwyxxById(pcxx.getId(),
                                                GlobalMethod.getBoxStore(self));
                                    }
                                    refershView();
                                }
                            }, self
                    );
                } else {
                    GlobalMethod.showErrorDialog("已上传，请删除整个工作记录", self);
                }
            }
            return true;
            case R.id.gzxx_detail: {
                if (pcxx.getPcZl() == Zapcxx.PCRYXXZL) {
                    Intent intent = new Intent(ZapcGzxxActivity.this,
                            ZapcRyxxActivity.class);
                    intent.putExtra("pcryxx", (ZapcRypcxxBean) pcxx);
                    startActivityForResult(intent, REQ_PCRY);
                } else if (pcxx.getPcZl() == Zapcxx.PCWPXXZL) {
                    Intent intent = new Intent(ZapcGzxxActivity.this,
                            ZapcJdcActivity.class);
                    intent.putExtra("pcwpxx", (ZapcWppcxxBean) pcxx);
                    startActivityForResult(intent, REQ_PCJDC);
                }
            }
            return true;
            default:
                break;
        }
        return false;
    }

    // 结束盘查
    private View.OnClickListener overPcBut = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (isOver) {
                GlobalMethod.showErrorDialog("盘查已结束，无需结果", self);
                return;
            }
            ZapcGzxxBean gz = new ZapcGzxxBean();
            String err = saveGzxx(gz);
            if (!TextUtils.isEmpty(err)) {
                GlobalMethod.showErrorDialog(err, self);
            } else {
                closeViewAndReturn(gz, true);
            }
        }
    };
    // 暂停盘查
    private View.OnClickListener pausePcBut = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (isOver) {
                GlobalMethod.showErrorDialog("盘查已结束，无需暂停", self);
                return;
            }
            ZapcGzxxBean gz = new ZapcGzxxBean();
            String err = saveGzxx(gz);
            if (!TextUtils.isEmpty(err)) {
                GlobalMethod.showErrorDialog(err, self);
            } else {
                closeViewAndReturn(gz, false);
            }
        }
    };
    // 盘查人员
    private View.OnClickListener startPcBut = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (isOver) {
                GlobalMethod.showErrorDialog("盘查已结束，不能继续盘查人员", self);
                return;
            }
            ZapcGzxxBean gz = null;
            if (isNew) {
                gz = new ZapcGzxxBean();
                String err = saveGzxx(gz);
                if (!TextUtils.isEmpty(err)) {
                    GlobalMethod.showErrorDialog(err, self);
                    return;
                }
                ZaPcdjDao.updateGzxx(gz, GlobalMethod.getBoxStore(self));
                isNew = false;
                edGzxxId.setText(gz.getId() + "");
            } else {
                gz = ZaPcdjDao.getGzxxById(Long.valueOf(edGzxxId.getText().toString()),
                        GlobalMethod.getBoxStore(self));
            }
            Intent intent = new Intent(ZapcGzxxActivity.this,
                    ZapcRyxxActivity.class);
            intent.putExtra("gzxx", gz);
            startActivityForResult(intent, REQ_PCRY);
        }
    };

    private void setTitleByPcxx() {
        String title = "治安盘查工作";
        if (ryWpxxList != null && ryWpxxList.size() > 0)
            title += "--共有" + ryWpxxList.size() + "人员或物品信息";
        setTitle(title);
    }

    /**
     * 验证接口数据返回状态
     *
     * @param re
     * @return
     */
    private boolean checkWebResult(WebQueryResult<ZapcReturn> re) {
        return re.getStatus() == HttpURLConnection.HTTP_OK && re.getResult() != null
                && TextUtils.equals(re.getResult().getCgbj(), "1")
                && re.getResult().getPcbh() != null
                && re.getResult().getPcbh().length > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_PCRY || requestCode == REQ_PCJDC) {
                refershView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refershView() {
        ryWpxxList.clear();
        String sid = edGzxxId.getText().toString();
        List<Zapcxx> list = null;
        if (!TextUtils.isEmpty(sid))
            list = ZaPcdjDao.getPcxxByGzbh(Long.valueOf(sid), GlobalMethod.getBoxStore(self));
        if (list != null) {
            for (Zapcxx pc : list)
                ryWpxxList.add(new SelectObjectBean<Zapcxx>(pc, false));
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 将界面的内容保存到工作信息中，返回的字串表示有错误
     *
     * @return
     */
    private String saveGzxx(ZapcGzxxBean gz) {
        String fjrs = edFjrs.getText().toString();
        if (TextUtils.isEmpty(fjrs) || !TextUtils.isDigitsOnly(fjrs))
            return "请正确填写辅警人数";
        if (spXffs.getSelectedItemPosition() == 0)
            return "请选择巡防方式";
        gz.setFjrs(fjrs);
        gz.setGzdd(edGzdd.getText().toString());
        gz.setXlmc(GlobalMethod.getKeyFromSpinnerSelected(spXqxd,
                GlobalConstant.KEY));
        gz.setXffs(GlobalMethod.getKeyFromSpinnerSelected(spXffs,
                GlobalConstant.KEY));
        gz.setCsbj("0");
        gz.setDjdw(GlobalData.grxx.get(GlobalConstant.YBMBH));
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        gz.setJybh(jh.length() == 8 ? jh.substring(2) : jh);
        gz.setKssj(ZaPcdjDao.changeNorModDpt(edKssj.getText().toString()));
        gz.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
        return null;
    }


    /**
     * 暂停或结束盘查
     *
     * @param isOver 结束为TRUE,暂停为FALSE
     */
    private void closeViewAndReturn(ZapcGzxxBean gz, boolean isOver) {
        Intent i = new Intent();
        Bundle b = new Bundle();
        if (isOver)
            // 已结束，设置结束时间
            gz.setJssj(ZaPcdjDao.sdfDpt.format(new Date()));
        String id = edGzxxId.getText().toString();
        if (!TextUtils.isEmpty(id))
            gz.setId(Long.valueOf(id));
        ZaPcdjDao.updateGzxx(gz, GlobalMethod.getBoxStore(self));
        b.putBoolean("isOver", isOver);
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();

    }
}
