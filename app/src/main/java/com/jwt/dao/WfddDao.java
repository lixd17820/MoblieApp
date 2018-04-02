package com.jwt.dao;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jwt.bean.KeyValueBean;
import com.jwt.pojo.FavorWfdd;
import com.jwt.pojo.FavorWfdd_;
import com.jwt.pojo.FrmRoadItem;
import com.jwt.pojo.FrmRoadItem_;
import com.jwt.pojo.FrmRoadSeg;
import com.jwt.pojo.FrmRoadSeg_;
import com.jwt.pojo.SeriousStreetBean;
import com.jwt.pojo.SeriousStreetBean_;
import com.jwt.pojo.SysParaValue;
import com.jwt.pojo.SysParaValue_;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/8/28.
 */

public class WfddDao {

    private static final String TAG = "WfddDao";

    public static void checkFavorWfld(Activity context) {
    }

    /**
     * 目前规则，行政区划在范围内，道路代码、路段代码不能为空。名称不能有逗号
     *
     * @param context
     * @param favorWfddList
     */
    public static void checkFavorWfld(Activity context, List<FavorWfdd> favorWfddList) {
        BoxStore boxes = GlobalMethod.getBoxStore(context);
        Box<FavorWfdd> fwDb = boxes.boxFor(FavorWfdd.class);
        int count = 0;
        for (int i = favorWfddList.size() - 1; i >= 0; i--) {
            FavorWfdd w = favorWfddList.get(i);
            String wfdd = w.getXzqh() + GlobalMethod.ifNull(w.getDldm()) +
                    GlobalMethod.ifNull(w.getLddm()) + GlobalMethod.ifNull(w.getMs());
            if (w.getFavorLdmc().indexOf(",") > -1 || !isWfddOk(wfdd, context)) {
                fwDb.remove(w.getId());
                favorWfddList.remove(i);
                count++;
            }
        }
        if (count > 0)
            GlobalMethod.toast(context, "删除不符合规则的自选路段" + count + "条");

    }


    public static List<KeyValueBean> getOwnerXzqhList(String dwdm, BoxStore bs) {
        Box<SysParaValue> box = bs.boxFor(SysParaValue.class);
        SysParaValue v = box.query().equal(SysParaValue_.xtlb, "04").equal(SysParaValue_.glbm, dwdm)
                .equal(SysParaValue_.gjz, "GLXZQH").build().findFirst();
        List<KeyValueBean> kvs = new ArrayList<>();
        if (v != null) {
            String[] ar = v.getCsz().split(",");
            if (ar != null) {
                for (String s : ar) {
                    KeyValueBean kv = GlobalMethod.findKvByKvlist(GlobalData.xzqhList, s);
                    if (kv != null)
                        kvs.add(kv);
                }
            }
        }
        return kvs;

    }

    private static FrmRoadItem queryRoadItem(String xzqh, String dldm, Box<FrmRoadItem> fiBox) {
        return fiBox.query().equal(FrmRoadItem_.dldm, dldm).
                contains(FrmRoadItem_.xzqh, xzqh).build().findFirst();
    }

    public static List<FrmRoadItem> getRoadItemsByXzqh(String xzqh, BoxStore bs) {
        //Log.e(TAG, "getRoadItemsByXzqh: " + xzqh);
        Box<FrmRoadItem> box = bs.boxFor(FrmRoadItem.class);
        List<FrmRoadItem> list = box.query().contains(FrmRoadItem_.xzqh, xzqh).build().find();
        //List<KeyValueBean> kvs = new ArrayList<>();
        //kvs.add(new KeyValueBean("", ""));
        //if (list != null && !list.isEmpty()) {
        //    for (FrmRoadItem r : list) {
        //        //Log.e(TAG, "FrmRoadItem: " + r.getDldm() + "/" + r.getDlmc());
         //       KeyValueBean kv = new KeyValueBean(r.getDldm(), r.getDlmc());
         //       kvs.add(kv);
          //  }
       // }
        //return kvs;
        return list;
    }

    public static boolean isGsd(String road) {
        if (TextUtils.isEmpty(road))
            return false;
        char c = road.charAt(0);
        return Integer.valueOf(c) < 53;
    }

    public static List<FrmRoadSeg> getRoadSegByRoad(String road, String xzqh, BoxStore bs) {
        Box<FrmRoadSeg> box = bs.boxFor(FrmRoadSeg.class);
        List<FrmRoadSeg> list = box.query().equal(FrmRoadSeg_.xzqh, xzqh).equal(FrmRoadSeg_.dldm, road).build().find();
        //List<KeyValueBean> kvs = new ArrayList<>();
        //kvs.add(new KeyValueBean("", ""));
        //if (list != null) {
        //    for (FrmRoadSeg r : list) {
         //       KeyValueBean kv = new KeyValueBean(r.getLddm(), r.getLdmc());
          //      kvs.add(kv);
          //  }
        //}
        return list;
    }

    public static int addFavorWfdd(FavorWfdd dd, BoxStore bs) {
        Box<FavorWfdd> box = bs.boxFor(FavorWfdd.class);
        long i = box.query().equal(FavorWfdd_.favorLdmc, dd.getFavorLdmc())
                .build().count();
        if (i > 0)
            return -10;
        box.put(dd);
        return 1;
    }

    public static void delFavorWfddById(long id, BoxStore bs) {
        Box<FavorWfdd> box = bs.boxFor(FavorWfdd.class);
        box.remove(id);
    }

    public static List<FavorWfdd> getAllFavorWfdd(BoxStore bs) {
        Box<FavorWfdd> box = bs.boxFor(FavorWfdd.class);
        return box.query().build().find();
    }

    public static boolean isWfddOk(String wfdd, Context context) {
        if (TextUtils.isEmpty(wfdd) || wfdd.length() != 18)
            return false;
        List<KeyValueBean> xzqhs = getOwnerXzqhList(GlobalData.grxx.get(GlobalConstant.YBMBH),
                GlobalMethod.getBoxStore(context));
        Set<String> qhSets = new HashSet<>();
        for (KeyValueBean kvs : xzqhs) {
            qhSets.add(kvs.getKey());
        }
        String xzqh = wfdd.substring(0, 6);
        String dldm = wfdd.substring(6, 11);
        String lddm = wfdd.substring(11, 15);
        //int ms = Integer.valueOf(wfdd.substring(15, 18));
        if (!qhSets.contains(xzqh)) {
            return false;
        }
        return checkGsdGls(xzqh, dldm, lddm, context);
    }

    /**
     * 验证国省道公里数是否在范围以内
     *
     * @param xzqh
     * @param dldm
     * @param lddm
     * @param context
     * @return
     */
    public static boolean checkGsdGls(String xzqh, String dldm, String lddm, Context context) {
        if (!isGsd(dldm))
            return true;
        Box<FrmRoadItem> fiBox = GlobalMethod.getBoxStore(context).boxFor(FrmRoadItem.class);
        FrmRoadItem roadItem = queryRoadItem(xzqh, dldm, fiBox);
        if (roadItem == null) {
            return false;
        }
        if (!TextUtils.isEmpty(roadItem.getXzqhxxlc())) {
            String[] xxls = roadItem.getXzqhxxlc().split(",");
            for (String lcs : xxls) {
                String[] qslcs = lcs.split(":");
                int iLddm = Integer.valueOf(lddm);
                if (iLddm >= Integer.valueOf(qslcs[0]) && iLddm <= Integer.valueOf(qslcs[1])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkIsSeriousStreet(String wfdd, BoxStore boxStore) {
        Box<SeriousStreetBean> box = boxStore.boxFor(SeriousStreetBean.class);
        long c = box.query().equal(SeriousStreetBean_.wfdd, wfdd).build().count();
        return c > 0;
    }

    public static String[] getDlmx(String xzqh, String lh, BoxStore boxStore) {
        return null;
    }
}
