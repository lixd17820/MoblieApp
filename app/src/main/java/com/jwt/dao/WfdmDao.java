package com.jwt.dao;

import android.app.Activity;
import android.text.TextUtils;

import com.jwt.pojo.VioWfdmCode;
import com.jwt.pojo.VioWfdmCode_;
import com.jwt.pojo.WfxwForce;
import com.jwt.pojo.WfxwForce_;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/8/29.
 */

public class WfdmDao {
    public static boolean isYxWfdm(VioWfdmCode w) {
        long yxqs = w.getYxqs().getTime();
        long yxqz = w.getYxqz().getTime();
        long time = new Date().getTime();
        return time > yxqs && time < yxqz;
    }

    public static String getQzcslxMs(String qzcslx) {
        List<String> name = getQzcslxMsList(qzcslx);
        if(name == null || name.isEmpty())
            return "";
        return GlobalMethod.join(name, ",");
    }

    public static List<String> getQzcslxMsList(String qzcslx) {
        List<String> name = new ArrayList<>();
        if (!TextUtils.isEmpty(qzcslx)) {
            for (int i = 0; i < qzcslx.length(); i++) {
                String key = qzcslx.substring(i, i + 1);
                name.add(GlobalMethod.getStringFromKVListByKey(
                        GlobalData.qzcslxList, key));
            }
        }
        return name;
    }

    public static List<WfxwForce> queryQzcsByCond(String where, BoxStore bs) {

        return null;
    }

    public static VioWfdmCode queryWfxwByWfdm(String wfxw, BoxStore bs) {
        Box<VioWfdmCode> box = bs.boxFor(VioWfdmCode.class);
        VioWfdmCode v = box.query().equal(VioWfdmCode_.wfxw, wfxw).build().findFirst();
        return v;
    }

    public static String queryQzcsYj(String wfxw, BoxStore bs) {
        WfxwForce f = bs.boxFor(WfxwForce.class).query().equal(WfxwForce_.wfdm, wfxw).build().findFirst();
        if (f == null)
            return "";
        return f.getQzyj();
    }

    /**
     * 验证违法行为和车辆类型的逻辑关系
     *
     * @param wfxws
     * @param jtfs
     * @return
     */
    public static String checkWfxwCllx(String wfxws, String jtfs) {
        return null;
    }
}
