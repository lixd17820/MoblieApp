package com.jwt.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.jwt.activity.ActionBarListActivity;
import com.jwt.dao.ViolationDAO;
import com.jwt.dao.WfdmDao;
import com.jwt.globalquery.ZhcxOneRecordListAdapter;
import com.jwt.globalquery.ZhcxQueryResultBean;
import com.jwt.pojo.VioViolation;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JbywPrintJdsDetailActivity extends ActionBarListActivity {

    private Activity self;
    private VioViolation punish;
    private List<ZhcxQueryResultBean> jdsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_no_button_list);
        self = this;
        String jdsbh = getIntent().getStringExtra("jdsbh");
        Log.e("rintJdsDetailActivity", "jdsbh");
        punish = ViolationDAO.getViolationByJdsbh(jdsbh, GlobalMethod.getBoxStore(self));
        createList();
        ZhcxOneRecordListAdapter adapter = new ZhcxOneRecordListAdapter(this,
                jdsDetail);
        getListView().setAdapter(adapter);

    }

    private void createList() {
        if (jdsDetail == null)
            jdsDetail = new ArrayList<ZhcxQueryResultBean>();
        jdsDetail.clear();
        if (punish == null)
            return;
        // 决定书种类
        if ("1".equals(punish.getWslb())) {
            if ("2".equals(punish.getCfzl()))
                jdsDetail.add(new ZhcxQueryResultBean("", "决定书类别", "简易处罚决定书"));
            else if ("1".equals(punish.getCfzl()))
                jdsDetail.add(new ZhcxQueryResultBean("", "决定书类别", "轻微警告决定书"));
        } else if ("3".equals(punish.getWslb()))
            jdsDetail.add(new ZhcxQueryResultBean("", "决定书类别", "强制措施凭证"));
        else if ("3".equals(punish.getWslb()))
            jdsDetail.add(new ZhcxQueryResultBean("", "决定书类别", "违法通知书"));
        jdsDetail.add(new ZhcxQueryResultBean("", "处罚种类", GlobalMethod
                .ifNull(punish.getCfzl())));
        // 决定书编号
        jdsDetail.add(new ZhcxQueryResultBean("", "决定书编号", punish.getJdsbh()));
        jdsDetail.add(new ZhcxQueryResultBean("", "上传标记", (TextUtils.equals("1",
                punish.getScbj()) ? "已上传" : "未上传")));
        jdsDetail.add(new ZhcxQueryResultBean("", "入库情况", GlobalMethod
                .ifNull(punish.getCwxx())));
        // 文书类别
        jdsDetail.add(new ZhcxQueryResultBean("", "文书类别", punish.getWslb()));
        jdsDetail.add(new ZhcxQueryResultBean("", "人员分类", (TextUtils.isEmpty(punish
                .getRyfl()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.ryflList, punish.getRyfl())
                + " 代码："
                + punish.getRyfl())));

        jdsDetail.add(new ZhcxQueryResultBean("", "驾驶证号", GlobalMethod
                .ifNull(punish.getJszh())));
        jdsDetail.add(new ZhcxQueryResultBean("", "档案编号", GlobalMethod
                .ifNull(punish.getDabh())));
        jdsDetail.add(new ZhcxQueryResultBean("", "发证机关", GlobalMethod
                .ifNull(punish.getFzjg())));
        jdsDetail.add(new ZhcxQueryResultBean("", "准驾车型", GlobalMethod
                .ifNull(punish.getZjcx())));
        jdsDetail.add(new ZhcxQueryResultBean("", "驾驶证号", GlobalMethod
                .ifNull(punish.getJszh())));
        jdsDetail.add(new ZhcxQueryResultBean("", "当事人", GlobalMethod.ifNull(punish
                .getDsr())));
        jdsDetail.add(new ZhcxQueryResultBean("", "电话", GlobalMethod.ifNull(punish
                .getDh())));
        jdsDetail.add(new ZhcxQueryResultBean("", "联系方式", GlobalMethod
                .ifNull(punish.getLxfs())));
        //
        String gzxm = punish.getGzxm();
        String[] temp = ViolationDAO.getJsonStrs(gzxm, "zzmm", "zyxx", "syxz");
        String zzmm = GlobalMethod.getStringFromKVListByKey(GlobalData.zzmmList, temp[0]);
        String zyxx = GlobalMethod.getStringFromKVListByKey(GlobalData.zyxxList, temp[1]);
        String syxz = GlobalMethod.getStringFromKVListByKey(GlobalData.syxzList, temp[2]);
        jdsDetail.add(new ZhcxQueryResultBean("", "政治面貌", (TextUtils.isEmpty(zzmm) ? "" : zzmm
                + " 代码："
                + temp[0])));
        jdsDetail.add(new ZhcxQueryResultBean("", "职业", (TextUtils.isEmpty(zyxx) ? "" : zyxx
                + " 代码："
                + temp[1])));
        jdsDetail.add(new ZhcxQueryResultBean("", "车辆分类", (TextUtils.isEmpty(punish
                .getClfl()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.clflList, punish.getClfl())
                + " 代码："
                + punish.getClfl())));
        jdsDetail.add(new ZhcxQueryResultBean("", "号牌种类", (TextUtils.isEmpty(punish
                .getHpzl()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.hpzlList, punish.getHpzl())
                + " 代码："
                + punish.getHpzl())));
        jdsDetail.add(new ZhcxQueryResultBean("", "号牌号码", GlobalMethod
                .ifNull(punish.getHphm())));
        jdsDetail.add(new ZhcxQueryResultBean("", "车辆类型", (TextUtils.isEmpty(punish
                .getJtfs()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.jtfsList, punish.getJtfs())
                + " 代码："
                + punish.getJtfs())));
        jdsDetail.add(new ZhcxQueryResultBean("", "使用性质", (TextUtils.isEmpty(syxz) ? "" : syxz
                + " 代码："
                + temp[2])));
        jdsDetail.add(new ZhcxQueryResultBean("", "违章时间", GlobalMethod
                .ifNull(punish.getWfsj())));
        jdsDetail.add(new ZhcxQueryResultBean("", "违章地点", GlobalMethod
                .ifNull(punish.getWfdd())));
        jdsDetail.add(new ZhcxQueryResultBean("", "违章地址", GlobalMethod
                .ifNull(punish.getWfdz())));
        JSONArray wfxws = ParserJson.getJsonArray(punish.getWfxw());
        for (int i = 0; i < wfxws.length(); i++) {
            JSONObject wfxw = wfxws.optJSONObject(i);
            if (TextUtils.isEmpty(wfxw.optString("wfxw")))
                continue;
            String wfdm = wfxw.optString("wfxw");
            jdsDetail.add(new ZhcxQueryResultBean("", "违法行为", wfdm));
            VioWfdmCode wf = WfdmDao.queryWfxwByWfdm(wfdm, GlobalMethod.getBoxStore(self));
            jdsDetail.add(new ZhcxQueryResultBean("", "违法行为内容", (wf == null ? "错误" : wf.getWfnr())));
            jdsDetail.add(new ZhcxQueryResultBean("", "标准值", wfxw.optInt("bzz") + ""));
            jdsDetail.add(new ZhcxQueryResultBean("", "实测值", wfxw.optInt("scz") + ""));
        }
        jdsDetail.add(new ZhcxQueryResultBean("", "累计记分", punish.getWfjfs() + ""));
        jdsDetail.add(new ZhcxQueryResultBean("", "罚款金额", punish.getFkje() + ""));
        jdsDetail.add(new ZhcxQueryResultBean("", "值勤民警", GlobalMethod.ifNull(punish.getZqmj())));
        jdsDetail.add(new ZhcxQueryResultBean("", "缴款方式", (TextUtils.isEmpty(punish
                .getJkfs()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.jkfsList, punish.getJkfs()) + " 代码：" + punish.getJkfs())));
        jdsDetail.add(new ZhcxQueryResultBean("", "发现机关", GlobalMethod
                .ifNull(punish.getFxjg())));
        jdsDetail.add(new ZhcxQueryResultBean("", "缴款标记", (TextUtils.isEmpty(punish
                .getJkbj()) ? "" : GlobalMethod.getStringFromKVListByKey(
                GlobalData.jkbjList, punish.getJkbj())
                + " 代码："
                + punish.getJkbj())));
        jdsDetail.add(new ZhcxQueryResultBean("", "缴款日期", GlobalMethod
                .ifNull(punish.getJkrq())));
        // jdsDetail.add("罚款金额：" + GlobalMethod.ifNull(punish.getJsjqbj()));
        jdsDetail.add(new ZhcxQueryResultBean("", "强制措施类型", GlobalMethod
                .ifNull(punish.getQzcslx())));
        jdsDetail.add(new ZhcxQueryResultBean("", "更新时间", GlobalMethod
                .ifNull(punish.getGxsj())));
        jdsDetail.add(new ZhcxQueryResultBean("", "处理时间", GlobalMethod
                .ifNull(punish.getClsj())));
        jdsDetail.add(new ZhcxQueryResultBean("", "收缴项目", GlobalMethod
                .ifNull(punish.getSjxm())));
        jdsDetail.add(new ZhcxQueryResultBean("", "收缴项目名称", GlobalMethod
                .ifNull(punish.getSjxmmc())));
        jdsDetail.add(new ZhcxQueryResultBean("", "扣留物品存放点", GlobalMethod
                .ifNull(punish.getKlwpcfd())));
        jdsDetail.add(new ZhcxQueryResultBean("", "收缴物品存放点", GlobalMethod
                .ifNull(punish.getSjwpcfd())));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_vio_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_jds_check:
                if (!TextUtils.equals("1", punish.getScbj())) {
                    GlobalMethod.showErrorDialog("记录未上传，不能查询入库情况，请先上传记录", self);
                    return true;
                }
                QueryRyThread thread = new QueryRyThread(queryRyHandler,
                        punish.getJdsbh(), punish.getWslb());
                thread.doStart();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Handler queryRyHandler = new Handler() {
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            @SuppressWarnings("unchecked")
            WebQueryResult<ZapcReturn> rs = (WebQueryResult<ZapcReturn>) b
                    .getSerializable("queryResult");
            String err = GlobalMethod.getErrorMessageFromWeb(rs);
            if (TextUtils.isEmpty(err)) {
                ZapcReturn upRe = rs.getResult();
                if (upRe != null && !TextUtils.isEmpty(upRe.getScms())) {
                    String cwms = upRe.getScms();
                    punish.setCwxx(cwms);
                    ViolationDAO.uploadViolationRkxx(punish.getJdsbh(), cwms,
                            JbywPrintJdsDetailActivity.this);
                    createList();
                    ((ZhcxOneRecordListAdapter) getListView().getAdapter())
                            .notifyDataSetChanged();
                } else {
                    GlobalMethod.showToast("查询失败", self);
                }
            } else {
                GlobalMethod.showErrorDialog(err, self);
            }
        }
    };

    class QueryRyThread extends Thread {
        private Handler handler;
        private String jdsbh;
        private String wslb;
        private ProgressDialog progressDialog;

        public QueryRyThread(Handler handler, String jdsbh, String wslb) {
            this.handler = handler;
            this.jdsbh = jdsbh;
            this.wslb = wslb;
        }

        public void doStart() {
            // 显示进度对话框
            progressDialog = ProgressDialog.show(self, "提示", "正在查询,请稍等...",
                    true);
            progressDialog.setCancelable(true);
            this.start();
        }

        @Override
        public void run() {
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<ZapcReturn> re = dao.queryVioRkqk(jdsbh, wslb);
            Message msg = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putSerializable("queryResult", re);
            msg.setData(b);
            handler.sendMessage(msg);
            progressDialog.dismiss();
        }

    }
}
