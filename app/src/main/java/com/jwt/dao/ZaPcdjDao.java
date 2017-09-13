package com.jwt.dao;

import com.jwt.bean.KeyValueBean;
import com.jwt.pojo.FrmDptCode;
import com.jwt.pojo.FrmDptCode_;
import com.jwt.pojo.ZapcGzxxBean;
import com.jwt.pojo.ZapcGzxxBean_;
import com.jwt.pojo.ZapcLxxx;
import com.jwt.pojo.ZapcLxxx_;
import com.jwt.pojo.ZapcRyjbxxBean;
import com.jwt.pojo.ZapcRypcxxBean;
import com.jwt.pojo.ZapcRypcxxBean_;
import com.jwt.pojo.ZapcWppcxxBean;
import com.jwt.pojo.ZapcWppcxxBean_;
import com.jwt.pojo.Zapcxx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/9/13.
 */

public class ZaPcdjDao {

    /**
     * 治安盘查所有字典表的集合
     */
    public static Map<String, List<KeyValueBean>> zapcDic;
    /**
     * 治安盘查中字符常量的列表
     */
    public static List<String> zapcZfclb;

    // 治安盘查中用到的字符常量
    public static String XFFS = "XFFS";
    /**
     * 盘查原因
     */
    public static String PCYY = "PCYY";
    public static String XB = "XB";
    public static String WHCD = "WHCD";
    public static String MZ = "MZ";
    public static String BYZK = "BYZK";
    public static String HYZK = "HYZK";
    public static String ZJXY = "ZJXY";
    public static String SF = "SF";
    public static String JCCFX = "JCCFX";
    public static String PCBDJG = "PCBDJG";
    public static String PCCLJG = "PCCLJG";
    // public static String WPCLCS = "WPCLCS";

    /**
     * 初始化治安盘查字典表方法
     *
     * @param box
     */
    public static void initZapcData(BoxStore box) {
        zapcDic = new HashMap<String, List<KeyValueBean>>();
        zapcDic.put(XFFS, getAllDptCode(XFFS, box));
        zapcDic.put(PCYY, getAllDptCode(PCYY, box));
        zapcDic.put(XB, getAllDptCode(XB, box));
        zapcDic.put(WHCD, getAllDptCode(WHCD, box));
        zapcDic.put(MZ, getAllDptCode(MZ, box));
        zapcDic.put(BYZK, getAllDptCode(BYZK, box));
        zapcDic.put(HYZK, getAllDptCode(HYZK, box));
        zapcDic.put(ZJXY, getAllDptCode(ZJXY, box));
        zapcDic.put(SF, getAllDptCode(SF, box));
        zapcDic.put(JCCFX, getAllDptCode(JCCFX, box));
        zapcDic.put(PCBDJG, getAllDptCode(PCBDJG, box));
        zapcDic.put(PCCLJG, getAllDptCode(PCCLJG, box));
        // zapcDic.put(WPCLCS,
        // getAllDptCode(WPCLCS, resolver, select, null, "dmz"));
        KeyValueBean nullKv = new KeyValueBean("", "");
        Set<Map.Entry<String, List<KeyValueBean>>> set = zapcDic.entrySet();
        for (Map.Entry<String, List<KeyValueBean>> entry : set) {
            entry.getValue().add(0, nullKv);
        }
    }

    private static List<KeyValueBean> getAllDptCode(String zl, BoxStore box) {
        List<FrmDptCode> codes = box.boxFor(FrmDptCode.class).query()
                .equal(FrmDptCode_.dmlb, zl).build().find();
        List<KeyValueBean> kvs = new ArrayList<>();
        if (codes == null || codes.isEmpty())
            return kvs;
        for (FrmDptCode c : codes) {
            kvs.add(new KeyValueBean(c.getDmz(), c.getDmsm1()));
        }
        return kvs;
    }

    /**
     * 大平台日期格式
     */
    public static SimpleDateFormat sdfDpt = new SimpleDateFormat(
            "yyyyMMddHHmmss");
    /**
     * 普通格式转换 *
     */
    public static SimpleDateFormat sdfNor = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");


    public static List<KeyValueBean> getZapcLxxx(String dwdm, BoxStore boxStore) {
        List<ZapcLxxx> codes = boxStore.boxFor(ZapcLxxx.class).query().equal(ZapcLxxx_.trffbh, dwdm).build().find();
        List<KeyValueBean> kvs = new ArrayList<>();
        if (codes == null || codes.isEmpty())
            return kvs;
        for (ZapcLxxx c : codes) {
            kvs.add(new KeyValueBean(c.getXldm(), c.getXlxl()));
        }
        return kvs;
    }

    public static void insertWpxx(ZapcWppcxxBean wpxx, BoxStore boxStore) {
        boxStore.boxFor(ZapcWppcxxBean.class).put(wpxx);
    }

    public static String changeDptModNor(String sj) {
        String result = sj;
        try {
            result = sdfNor.format(sdfDpt.parse(sj));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ZapcRypcxxBean queryRyxxByBh(long bh, BoxStore boxStore) {
        return boxStore.boxFor(ZapcRypcxxBean.class).query().equal(ZapcRypcxxBean_.gzbh, bh).build().findFirst();
    }

    public static ZapcWppcxxBean queryWpxxByBh(long bh, BoxStore boxStore) {
        return boxStore.boxFor(ZapcWppcxxBean.class).get(bh);
    }

    public static void delPcryxxById(long id, BoxStore boxStore) {
        boxStore.boxFor(ZapcRypcxxBean.class).remove(id);
    }

    public static void delPcwyxxById(long id, BoxStore boxStore) {
        boxStore.boxFor(ZapcWppcxxBean.class).remove(id);
    }

    public static List<Zapcxx> getPcxxByGzbh(long gzxxId, BoxStore boxStore) {
        Box<ZapcRypcxxBean> box = boxStore.boxFor(ZapcRypcxxBean.class);
        List<ZapcRypcxxBean> list = box.query().equal(ZapcRypcxxBean_.gzbh, gzxxId).build().find();
        Box<ZapcWppcxxBean> boxwp = boxStore.boxFor(ZapcWppcxxBean.class);

        List<Zapcxx> pcxx = new ArrayList<>();
        if (list != null) {
            for (ZapcRypcxxBean ry : list) {
                pcxx.add(ry);
                List<ZapcWppcxxBean> list2 = boxwp.query()
                        .equal(ZapcWppcxxBean_.bpcwpgzqkbh, String.valueOf(gzxxId))
                        .equal(ZapcWppcxxBean_.bpcwprybh, String.valueOf(ry.getId()))
                        .build().find();
                if (list2 != null) {
                    for (ZapcWppcxxBean wp : list2) {
                        pcxx.add(wp);
                    }
                }
            }
        }
        return pcxx;
    }

    public static String changeNorModDpt(String sj) {
        String result = sj;
        try {
            result = sdfDpt.format(sdfNor.parse(sj));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void updateGzxx(ZapcGzxxBean gzxx, BoxStore boxStore) {
        boxStore.boxFor(ZapcGzxxBean.class).put(gzxx);
    }

    public static ZapcGzxxBean getGzxxById(long id, BoxStore boxStore) {
        return boxStore.boxFor(ZapcGzxxBean.class).get(id);
    }

    public static void setPcryxxIsUpload(long id, BoxStore boxStore) {
        Box<ZapcRypcxxBean> box = boxStore.boxFor(ZapcRypcxxBean.class);
        ZapcRypcxxBean pc = box.get(id);
        pc.setScbj("1");
        box.put(pc);
    }

    public static void setWpxxIsUpload(long id, BoxStore boxStore) {
        Box<ZapcWppcxxBean> box = boxStore.boxFor(ZapcWppcxxBean.class);
        ZapcWppcxxBean pc = box.get(id);
        pc.setScbj("1");
        box.put(pc);
    }

    public static void deleteSendGzxx(BoxStore boxStore) {
        Box<ZapcGzxxBean> box = boxStore.boxFor(ZapcGzxxBean.class);
        List<ZapcGzxxBean> list = box.query().equal(ZapcGzxxBean_.csbj, "1").build().find();
        box.remove(list);
    }

    public static void deleteGzxx(ZapcGzxxBean gz, BoxStore boxStore) {
        boxStore.boxFor(ZapcGzxxBean.class).remove(gz.getId());
    }

    public static List<ZapcGzxxBean> getZapcGzxx(String zqmj, BoxStore boxStore) {
        Box<ZapcGzxxBean> box = boxStore.boxFor(ZapcGzxxBean.class);
        return box.getAll();
    }

    public static String queryLastRypcxx(BoxStore boxStore) {
        Box<ZapcRypcxxBean> box = boxStore.boxFor(ZapcRypcxxBean.class);
        long count = box.count();
        if (count == 0)
            return "";
        List<ZapcRypcxxBean> list = box.query().build().find(count - 1, 1);
        ZapcRypcxxBean p = list.get(0);
        return p.getPcdd();
    }

    public static void insertPcryxx(ZapcRypcxxBean pcryxx, BoxStore boxStore) {
        Box<ZapcRypcxxBean> box = boxStore.boxFor(ZapcRypcxxBean.class);
        box.put(pcryxx);
    }

    public static ZapcRyjbxxBean getRyjbxxById(long ryjbxxId, BoxStore boxStore) {
        Box<ZapcRyjbxxBean> box = boxStore.boxFor(ZapcRyjbxxBean.class);
        return box.get(ryjbxxId);
    }

    public static void saveRyjbxx(ZapcRyjbxxBean ryjbxx, BoxStore boxStore) {
        Box<ZapcRyjbxxBean> box = boxStore.boxFor(ZapcRyjbxxBean.class);
        box.put(ryjbxx);
    }
}
