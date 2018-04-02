package com.jwt.dao;

import android.text.TextUtils;

import com.jwt.pojo.AcdLawBean;
import com.jwt.pojo.AcdLawBean_;
import com.jwt.pojo.AcdPhotoBean;
import com.jwt.pojo.AcdPhotoBean_;
import com.jwt.pojo.AcdSimpleBean;
import com.jwt.pojo.AcdSimpleBean_;
import com.jwt.pojo.AcdSimpleHumanBean;
import com.jwt.pojo.AcdSimpleHumanBean_;
import com.jwt.jbyw.AcdWfxwBean;
import com.jwt.pojo.FrmCode;
import com.jwt.pojo.FrmCode_;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;

import java.util.Collections;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;

/**
 * Created by lixiaodong on 2017/9/8.
 */

public class AcdSimpleDao {

    public static final int ACD_MOD_NEW = 0;
    public static final int ACD_MOD_SHOW = 1;
    public static final int ACD_MOD_MODITY = 2;
    public static final int ACD_MOD_PHOTO_NEW = 3;
    public static final String OPER_MOD = "oper_mod";

    public static final String PHOTO_BEAN = "photoBean";


    public static List<AcdSimpleBean> getAllAcd(BoxStore boxStore) {
        Box<AcdSimpleBean> box = boxStore.boxFor(AcdSimpleBean.class);
        List<AcdSimpleBean> list= box.query().build().find();
        Collections.reverse(list);
        return list;
    }

    public static List<AcdSimpleBean> getAllAcd(String wsbh, BoxStore boxStore) {
        Box<AcdSimpleBean> box = boxStore.boxFor(AcdSimpleBean.class);
        List<AcdSimpleBean> list= box.query().equal(AcdSimpleBean_.wsbh, wsbh).build().find();
        Collections.reverse(list);
        return list;
    }

    public static AcdSimpleBean getAcdById(long id, BoxStore boxStore) {
        Box<AcdSimpleBean> box = boxStore.boxFor(AcdSimpleBean.class);
        return box.query().equal(AcdSimpleBean_.id, id).build().findUnique();
    }

    public static List<AcdSimpleHumanBean> queryHumanByCond(long sgbh, BoxStore boxStore) {
        Box<AcdSimpleHumanBean> box = boxStore.boxFor(AcdSimpleHumanBean.class);
        return box.query().equal(AcdSimpleHumanBean_.sgbh, String.valueOf(sgbh)).build().find();
    }

    public static AcdSimpleHumanBean getHumanById(long id, BoxStore boxStore) {
        Box<AcdSimpleHumanBean> box = boxStore.boxFor(AcdSimpleHumanBean.class);
        return box.query().equal(AcdSimpleHumanBean_.id, id).build().findUnique();
    }

    public static void saveAcdSimpleScbj(long id, BoxStore boxStore) {
        Box<AcdSimpleBean> box = boxStore.boxFor(AcdSimpleBean.class);
    }

    public static AcdWfxwBean getAcdWfxwByWfdm(String wfdm, BoxStore boxStore) {
        Box<FrmCode> box = boxStore.boxFor(FrmCode.class);
        QueryBuilder<FrmCode> query = box.query().equal(FrmCode_.dmz, wfdm);
        String[] wf = GlobalConstant.ACD_WFXW.split("/");
        query = query.equal(FrmCode_.xtlb, wf[0]).equal(FrmCode_.dmlb, wf[1]);
        FrmCode code = query.build().findFirst();
        AcdWfxwBean acdWfxw = new AcdWfxwBean();
        if (code != null) {
            acdWfxw.setWfxwdm(code.getDmz());
            acdWfxw.setWfnr(code.getDmsm1());
            acdWfxw.setRdyy(code.getDmsm2());
            acdWfxw.setWffl(code.getDmsm3());
        }
        return acdWfxw;
    }

    public static AcdLawBean queryWftknrByXh(String xh, BoxStore boxStore) {
        return boxStore.boxFor(AcdLawBean.class).query().equal(AcdLawBean_.xh, xh).build().findFirst();
    }

    public static int updateAcdPhotoRecodeScbj(long id, long acdID, BoxStore boxStore) {
        Box<AcdPhotoBean> box = boxStore.boxFor(AcdPhotoBean.class);
        AcdPhotoBean p = box.get(id);
        if (p == null)
            return 0;
        p.setScbj(1);
        p.setXtbh(acdID + "");
        box.put(p);
        return 1;
    }

    public static long addAcdPhoto(AcdPhotoBean acdPhoto, BoxStore boxStore) {
        Box<AcdPhotoBean> box = boxStore.boxFor(AcdPhotoBean.class);
        box.put(acdPhoto);
        return acdPhoto.getId();
    }

    public static void delAcdPhotoRecode(AcdPhotoBean acdPhoto, BoxStore boxStore) {
        Box<AcdPhotoBean> box = boxStore.boxFor(AcdPhotoBean.class);
        box.remove(acdPhoto.getId());
    }

    public static List<AcdPhotoBean> getAllAcdPhoto(String wsbh, BoxStore boxStore) {
        Box<AcdPhotoBean> box = boxStore.boxFor(AcdPhotoBean.class);
        if (TextUtils.isEmpty(wsbh))
            return box.getAll();
        return box.query().equal(AcdPhotoBean_.sgbh, wsbh).build().find();
    }

    public static void delAcdAndHuman(long id, BoxStore boxStore) {
        Box<AcdSimpleBean> box = boxStore.boxFor(AcdSimpleBean.class);
        box.remove(id);
        Box<AcdSimpleHumanBean> box2 = boxStore.boxFor(AcdSimpleHumanBean.class);
        List<AcdSimpleHumanBean> humans = box2.query().equal(
                AcdSimpleHumanBean_.sgbh, String.valueOf(id)).build().find();
        box2.remove(humans);
    }

    public static long saveAcdHumanInDb(AcdSimpleHumanBean human, BoxStore boxStore) {
        Box<AcdSimpleHumanBean> box = boxStore.boxFor(AcdSimpleHumanBean.class);
        box.put(human);
        return human.getId();
    }

    public static long saveAcdJbxxIntoDb(AcdSimpleBean acdJbqk, BoxStore boxStore) {
        Box<AcdSimpleBean> box = boxStore.boxFor(AcdSimpleBean.class);
        box.put(acdJbqk);
        return acdJbqk.getId();
    }

    public static String createRyxxStr(AcdSimpleHumanBean human) {
        String delimiter1 = "$$";
        String delimiter2 = "~~";
        String ryxx = "";
        ryxx += delimiter1;// 无需事故编号
        ryxx += GlobalMethod.ifNull(human.getXzqh()) + delimiter1;
        ryxx += (human.getRybh() + 1) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getXm()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getXb()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getSfzmhm()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getNl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getZz()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getDh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getRylx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getShcd()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getWfxw1()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getWfxw2()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getWfxw3()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getTk1()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getTk2()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getTk3()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getZyysdw()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJtfs()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getGlxzqh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getDabh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJszzl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getZjcx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCclzrq()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJsrgxd()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getFzjg()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getSgzr()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getHphm()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getHpzl()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClfzjg()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getFdjh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClsbdh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJdcxh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClpp()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClxh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCsys()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCllx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJdczt()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getSyq()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJdcsyr()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClsyxz()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getBx()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getBxgs()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getBxpzh()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClzzwp()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getClgxd()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getCjcxbj()) + delimiter1;
        ryxx += GlobalMethod.ifNull(human.getJyw()) + delimiter1 + delimiter2;
        return ryxx;
    }
}
