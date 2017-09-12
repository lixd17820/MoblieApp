package com.jwt.dao;

import com.jwt.pojo.RepairBean;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by lixiaodong on 2017/9/12.
 */

public class RepairDao {
    public static List<RepairBean> queryRepairs(BoxStore boxStore) {
        Box<RepairBean> box = boxStore.boxFor(RepairBean.class);
        return box.query().build().find();
    }

    public static void delRepair(long id, BoxStore boxStore) {
        Box<RepairBean> box = boxStore.boxFor(RepairBean.class);
        box.remove(id);
    }

    public static long updateRepair(RepairBean repair, BoxStore boxStore) {
        Box<RepairBean> box = boxStore.boxFor(RepairBean.class);
        box.put(repair);
        return repair.getId();
    }
}
