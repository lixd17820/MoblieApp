package com.jwt.dao;

import com.jwt.jbyw.AcdPhotoBean;
import com.jwt.jbyw.AcdPhotoBean_;
import com.jwt.jbyw.AcdSimpleBean;
import com.jwt.jbyw.AcdSimpleBean_;
import com.jwt.jbyw.AcdSimpleHumanBean;
import com.jwt.jbyw.AcdSimpleHumanBean_;
import com.jwt.jbyw.AcdWftLawBean;
import com.jwt.jbyw.AcdWfxwBean;
import com.jwt.pojo.FrmCode;
import com.jwt.pojo.FrmCode_;
import com.jwt.utils.GlobalConstant;

import java.util.ArrayList;
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
        return box.query().build().find();
    }

    public static List<AcdSimpleBean> getAllAcd(String wsbh, BoxStore boxStore) {
        Box<AcdSimpleBean> box = boxStore.boxFor(AcdSimpleBean.class);
        return box.query().equal(AcdSimpleBean_.wsbh, wsbh).build().find();
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

    public static void saveAcdSimpleScbj(String sgbh, BoxStore boxStore) {
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

    public static AcdWftLawBean queryWftknrByXh(String tk1, BoxStore boxStore) {
        return null;
    }

    public static int updateAcdPhotoRecode(long recID, long acdID, BoxStore boxStore) {
        return 0;
    }

    public static int updateAcdPhotoRecodeScbj(long acdID, BoxStore boxStore) {
        Box<AcdPhotoBean> box = boxStore.boxFor(AcdPhotoBean.class);
        AcdPhotoBean p = box.query().equal(AcdPhotoBean_.id, acdID).build().findUnique();
        if (p == null)
            return 0;
        p.setSgsj("1");
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
        //query = box.query();
        return box.query().build().find();
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
}
