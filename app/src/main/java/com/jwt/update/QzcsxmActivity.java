package com.jwt.update;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jwt.adapter.WfxwBzzAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.WfxwBzz;
import com.jwt.dao.WfdmDao;
import com.jwt.event.QzcsxmEvent;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QzcsxmActivity extends AppCompatActivity {

    @BindView(R.id.view_wfxw_bzz)
    LinearLayout wfxwViews;
    @BindView(R.id.view_qzcsxm)
    LinearLayout qzcsxmView;
    @BindView(R.id.tv_wfxwms)
    TextView tvWfxwms;

    private List<View> viewList = new ArrayList<>();
    private List<Boolean> selList = new ArrayList<>();
    //
    private List<View> qzcsxmViewList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qzcsxm);
        ButterKnife.bind(this);
        addWfxwClick(null);
    }

    @OnClick(R.id.btn_add_wfxw)
    public void addWfxwClick(View v) {
        int count = viewList.size();
        if (count >= 5) {
            return;
        }
        viewList.add(createView(count));
        selList.add(false);
    }

    @OnClick(R.id.btn_del_wfxw)
    public void delWfxwClick(View v) {
        List<Integer> list = new ArrayList<>();
        for (int i = viewList.size() - 1; i >= 0; i--) {
            if (selList.get(i)) {
                list.add(i);
            }
        }
        if (list.isEmpty())
            return;
        for (Integer i : list) {
            View temp = viewList.get(i);
            wfxwViews.removeView(temp);
            viewList.remove(i);
            selList.remove(i);
        }
    }

    @OnClick(R.id.btn_save)
    public void saveWfxwBzzClick(View v) {
        if (viewList.size() == 0) {
            return;
        }
        List<WfxwBzz> re = getWfxwBzzFromView();
        if (re == null || re.isEmpty()) {
            GlobalMethod.showErrorDialog("至少需要一个违法代码", this);
            return;
        }
        List<String> yjs = new ArrayList<>();
        //是否均为有效代码
        for (WfxwBzz w : re) {
            VioWfdmCode wf = WfdmDao.queryWfxwByWfdm(w.getWfxw(), GlobalMethod.getBoxStore(this));
            if (wf == null) {
                GlobalMethod.showErrorDialog("不存在的违法代码", this);
                return;
            }
            boolean isyx = WfdmDao.isYxWfdm(wf);
            if (!isyx) {
                GlobalMethod.showErrorDialog("存在无效的违法代码", this);
                return;
            }
            String s = WfdmDao.queryQzcsYj(w.getWfxw(), GlobalMethod.getBoxStore(this));
            if (!TextUtils.isEmpty(s))
                yjs.add(s);
        }
        if (yjs.isEmpty()) {
            GlobalMethod.showErrorDialog("违法代码不可开具强制措施，请到系统配置->强制措施代码模块中查询！", this);
            return;
        }
        String qz ="";
        String sj = "";
        for (int i = 0; i < selQzcsxm.size(); i++) {
            if (selQzcsxm.get(i)) {
                int xm = qzcsxmList.get(i);
                if (xm > 9)
                    sj += (xm - 10);
                else
                    qz += xm ;
            }
        }
        if (qz.isEmpty()) {
            GlobalMethod.showErrorDialog("至少需要一个强制措施项目", this);
            return;
        }
        if (qz.indexOf("5") > -1 && sj.isEmpty()) {
            GlobalMethod.showErrorDialog("强制措施包含收缴,却没有提供具体收缴项目", this);
            return;
        }
        QzcsxmEvent event = new QzcsxmEvent();
        event.wfxw = re;
        event.qzcsxms = qz;
        if (!sj.isEmpty())
            event.sjxms = sj;
//        JSONObject qzObj = new JSONObject();
//        ParserJson.putJsonVal(qzObj, "qzcsxms", GlobalMethod.join(qz, ","));
//        if(!sj.isEmpty())
//            ParserJson.putJsonVal(qzObj, "sjxms", GlobalMethod.join(sj, ","));
        EventBus.getDefault().post(event);
        finish();
    }

    public List<WfxwBzz> getWfxwBzzFromView() {
        List<WfxwBzz> re = new ArrayList<>();
        for (int i = 0; i < viewList.size(); i++) {
            View view = viewList.get(i);
            String wfxw = ((EditText) view.findViewById(R.id.edit_wfxw)).getText().toString();
            if (TextUtils.isEmpty(wfxw))
                continue;
            WfxwBzz w = new WfxwBzz();
            w.setWfxw(wfxw);
            String bzz = ((EditText) view.findViewById(R.id.edit_bzz)).getText().toString();
            String scz = ((EditText) view.findViewById(R.id.edit_scz)).getText().toString();
            if (!TextUtils.isEmpty(bzz) && !TextUtils.isEmpty(scz)) {
                if (Long.valueOf(bzz) > Long.valueOf(scz)) {
                    GlobalMethod.showErrorDialog("实测值不能小于标准值", this);
                    return re;
                }
                w.setBzz(bzz);
                w.setScz(scz);
            }
            re.add(w);
        }
        return re;
    }

    private View createView(final int pos) {
        View v = getLayoutInflater().inflate(R.layout.activity_qzcs_wfdm, wfxwViews, false);
        wfxwViews.addView(v);
        ((EditText) v.findViewById(R.id.edit_wfxw)).addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 4 || s.length() == 5) {
                    char[] dest = new char[s.length()];
                    s.getChars(0, s.length(), dest, 0);
                    queryAndShowWfxw(new String(dest));
                } else {
                    tvWfxwms.setText("");
                    clearQzcsxmView();
                }
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickWfxwView(v, pos);
            }
        });
        return v;
    }

    private void clickWfxwView(View v, int pos) {
        boolean isSel = !selList.get(pos);
        ImageView img = (ImageView) v.findViewById(R.id.image1);
        img.setImageResource(isSel ? R.drawable.ic_check_box_black_24dp :
                R.drawable.ic_check_blank_black_24dp);
        Log.e("click view", isSel + "");
        selList.set(pos, isSel);
    }

    private void queryAndShowWfxw(String wfdm) {
        VioWfdmCode w = WfdmDao.queryWfxwByWfdm(wfdm, GlobalMethod.getBoxStore(this));
        String xm = GlobalMethod.getStringFromKVListByKey(GlobalData.qzcslxList, w.getQzcslx());
        if (w != null) {
            String s = w.getWfxw() + ": " + w.getWfms();
            // 强制措施将显示收缴或扣留项目
            s += "\n强制措施  "
                    + (TextUtils.isEmpty(xm) ? "无" : xm);
            s += "| " + (WfdmDao.isYxWfdm(w) ? "有效代码" : "无效代码");
            tvWfxwms.setText(s);
        } else {
            tvWfxwms.setText("");
        }
    }

    @OnClick(R.id.btn_create_qzcsxm)
    public void createQzcsxm() {
        clearQzcsxmView();
        List<WfxwBzz> re = getWfxwBzzFromView();
        if (re == null || re.size() == 0)
            return;
        String[] wfs = new String[re.size()];
        for (int i = 0; i < re.size(); i++) {
            wfs[i] = re.get(i).getWfxw();
        }
        Map<Integer, Boolean> map = synQzcslxFromList(wfs);
        if (map == null || map.isEmpty()) {
            return;
        }
        for (int i = 0; i < map.size(); i++) {
            qzcsxmViewList.add(createQzcxxmView(i));
        }
    }

    private void clearQzcsxmView() {
        for (View v : qzcsxmViewList) {
            qzcsxmView.removeView(v);
        }
        qzcsxmViewList.clear();
        selQzcsxm.clear();
        qzcsxmList.clear();
    }

    public View createQzcxxmView(final int pos) {
        View v = getLayoutInflater().inflate(R.layout.one_row_select_item, qzcsxmView, false);
        TextView tv = (TextView) v.findViewById(R.id.text1);
        ImageView img = (ImageView) v.findViewById(R.id.image1);
        Integer xm = qzcsxmList.get(pos);
        boolean isSel = selQzcsxm.get(pos);
        tv.setText(GlobalMethod.getStringFromKVListByKey(
                xm < 10 ? GlobalData.qzcslxList : GlobalData.sjxmList, xm + ""));
        img.setImageResource(isSel ? R.drawable.ic_check_box_black_24dp :
                R.drawable.ic_check_blank_black_24dp);
        qzcsxmView.addView(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickQzcsxm(v, pos);
            }
        });
        return v;
    }

    private List<Integer> qzcsxmList = new ArrayList<>();
    private List<Boolean> selQzcsxm = new ArrayList<>();

    private Map<Integer, Boolean> synQzcslxFromList(String[] wfs) {
        qzcsxmList.clear();
        selQzcsxm.clear();
        TreeMap<Integer, Boolean> qzcsMap = new TreeMap<>();
        if (wfs == null && wfs.length == 0)
            return null;
        for (String wfdm : wfs) {
            VioWfdmCode wfxw = WfdmDao.queryWfxwByWfdm(wfdm, GlobalMethod.getBoxStore(this));
            if (wfxw == null)
                continue;
            List<Integer> list = findQzcdxmAndSjxmByWfdm(wfxw);
            if (list == null || list.isEmpty())
                continue;
            for (Integer key : list) {
                qzcsMap.put(key, false);
            }
        }
        Set<Map.Entry<Integer, Boolean>> set = qzcsMap.entrySet();
        for (Map.Entry<Integer, Boolean> e : set) {
            qzcsxmList.add(e.getKey());
            selQzcsxm.add(e.getValue());
        }
        return qzcsMap;
    }

    private List<Integer> findQzcdxmAndSjxmByWfdm(VioWfdmCode wf) {
        List<Integer> cs = new ArrayList<Integer>();
        String qzlx = wf.getQzcslx();
        if (TextUtils.isEmpty(qzlx))
            return cs;
        // 有强制措施内容
        for (int j = 0; j < qzlx.length(); j++) {
            int key = Integer.valueOf(qzlx.substring(j, j + 1));
            cs.add(key);
            // 判断是否为收缴项目
            if (key == 5) {
                for (KeyValueBean kv : GlobalData.sjxmList)
                    cs.add(Integer.valueOf(kv.getKey()));
            }
        }
        return cs;
    }

    private void clickQzcsxm(View v, int pos) {
        boolean isSel = !selQzcsxm.get(pos);
        ImageView img = (ImageView) v.findViewById(R.id.image1);
        img.setImageResource(isSel ? R.drawable.ic_check_box_black_24dp :
                R.drawable.ic_check_blank_black_24dp);
        Log.e("click view", isSel + "");
        selQzcsxm.set(pos, isSel);
    }
}
