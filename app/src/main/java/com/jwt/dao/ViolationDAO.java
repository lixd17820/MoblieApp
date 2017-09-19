package com.jwt.dao;

import android.content.ContentResolver;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.WfxwBzz;
import com.jwt.pojo.VioViolation;
import com.jwt.pojo.VioViolation_;
import com.jwt.pojo.FrmCode;
import com.jwt.pojo.FrmCode_;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.update.JbywPrintJdsDetailActivity;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/8/28.
 */

public class ViolationDAO {

    public static String[] getJsonStrs(String... s) {
        if (s == null || s.length < 2)
            return null;
        String[] re = new String[s.length - 1];
        try {
            JSONObject obj = new JSONObject(s[0]);
            for (int i = 0; i < re.length; i++) {
                re[i] = obj.optString(s[i + 1], "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            for (int i = 0; i < re.length; i++) {
                re[i] = "";
            }
        }

        return re;
    }

    public static VioViolation getViolationByJdsbh(String jdsbh, BoxStore bs) {
        Box<VioViolation> box = bs.boxFor(VioViolation.class);
        return box.query().equal(VioViolation_.jdsbh, jdsbh).build().findFirst();
    }

    public static String getFzjgChinaName(String fzjg, BoxStore bs) {
        String name = "无";
        String[] qs = GlobalConstant.CHENSHI.split("/");
        Box<FrmCode> box = bs.boxFor(FrmCode.class);
        FrmCode code = box.query().equal(FrmCode_.dmz, fzjg).equal(FrmCode_.xtlb, qs[0]).equal(FrmCode_.dmlb, qs[1])
                .build().findFirst();
        if (code != null)
            name = code.getDmsm1();
        return name;
    }

    /**
     * 检查违法行为和人员分类的一致性
     *
     * @param wfxws 多个违法行为用，分隔
     * @param ryfl
     * @param bs
     * @return
     */
    public static boolean checkWfxwAndRyfl(String wfxws, int ryfl, BoxStore bs) {
        Log.e("ViolationDao", wfxws);
        JSONArray array = ParserJson.getJsonArray(wfxws);
        Log.e("ViolationDao", array.toString());

        for (int i = 0; i < array.length(); i++) {
            String wfdm = array.optJSONObject(i).optString("wfxw");
            if (TextUtils.isEmpty(wfdm))
                continue;
            VioWfdmCode wf = WfdmDao.queryWfxwByWfdm(wfdm, bs);
            if (wf == null)
                continue;
            int wfzl = Integer.valueOf(wf.getDmzl());
            // 人员分类是行人和乘车人
            if (ryfl == 1 && wfzl == GlobalConstant.WFDMZL_XRCCR) {
                return true;
            } else if (ryfl == 2 && wfzl == GlobalConstant.WFDMZL_FJDC) {
                return true;
            } else if ((ryfl == 4 || ryfl == 3 || ryfl == 7)
                    && (wfzl == GlobalConstant.WFDMZL_JDC || wfzl == GlobalConstant.WFDMZL_OTHER)) {
                return true;
            } else if (wfzl == GlobalConstant.WFDMZL_SG) {
                return true;
            }
        }
        return false;
    }

    public static VioViolation getLastVio(BoxStore bs) {
        Box<VioViolation> box = bs.boxFor(VioViolation.class);
        long maxId = box.query().build().max(VioViolation_.id);
        if (maxId <= 0)
            return null;
        return box.query().equal(VioViolation_.id, maxId).build().findUnique();
    }

    public static String saveIntoJsonStr(String... s) {
        if (s == null || s.length == 0 || s.length % 2 != 0) return null;
        JSONObject obj = new JSONObject();
        for (int i = 0; i < s.length / 2; i++) {
            try {
                if (!TextUtils.isEmpty(s[i * 2 + 1])) {
                    obj.put(s[i * 2], s[i * 2 + 1]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return obj.toString();
    }

    public static WebQueryResult<ZapcReturn> uploadViolation(VioViolation vio,
                                                             BoxStore bs) {
        RestfulDao dao = RestfulDaoFactory.getDao();
        JSONObject vobj = ParserJson.objToJson(vio);
        try {
            JSONArray array = new JSONArray(vio.getWfxw());
            vobj.put("wfxw", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject temp = new JSONObject(vio.getGzxm());
            vobj.put("gzxm", temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dao.uploadViolationMobile(vobj);
    }

    public static long saveViolationIntoDB(VioViolation violation, BoxStore bs) {
        Box<VioViolation> box = bs.boxFor(VioViolation.class);
        box.put(violation);
        return violation.getId();
    }

    public static boolean isViolationDuplicate(VioViolation v, BoxStore bs) {
        Box<VioViolation> box = bs.boxFor(VioViolation.class);
        long c = box.query().equal(VioViolation_.wfsj, v.getWfsj()).equal(VioViolation_.wfdz, v.getWfdz())
                .equal(VioViolation_.wfxw, v.getWfxw()).equal(VioViolation_.wslb, v.getWslb())
                .build().count();
        return c > 0;
    }

    public static void uploadViolationRkxx(String jdsbh, String cwms, JbywPrintJdsDetailActivity jbywPrintJdsDetailActivity) {
    }

    public static void delOldViolation(int maxRecords, BoxStore boxStore) {
    }

    public static ArrayList<KeyValueBean> getAllFrmCode(String jtfs, String dmz, String dmsm1, BoxStore boxStore) {
        return null;
    }

    public static void setVioUploadStatus(long id, boolean b, BoxStore boxStore) {
        Box<VioViolation> box = boxStore.boxFor(VioViolation.class);
        VioViolation v = box.get(id);
        v.setScbj(b ? "1" : "0");
        box.put(v);
    }

    public static void setVioPicUploadStatus(long id, boolean b, BoxStore boxStore) {
        Box<VioViolation> box = boxStore.boxFor(VioViolation.class);
        VioViolation v = box.get(id);
        v.setPicScbj(b ? 1 : 0);
        box.put(v);
    }
}
