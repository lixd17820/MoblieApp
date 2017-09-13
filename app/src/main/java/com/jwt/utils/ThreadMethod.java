package com.jwt.utils;

import com.jwt.pojo.FrmDptCode;
import com.jwt.pojo.FrmRoadItem;
import com.jwt.pojo.FrmRoadSeg;
import com.jwt.pojo.SeriousStreetBean;
import com.jwt.pojo.SysParaValue;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.pojo.WfxwForce;
import com.jwt.pojo.ZapcLxxx;
import com.jwt.web.WebQueryResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

/**
 * Created by lixiaodong on 2017/8/29.
 */

public class ThreadMethod {

    public static void saveWfdmInDb(WebQueryResult<String> dict, Box<VioWfdmCode> box) {
        box.removeAll();
        String re = dict.getResult();
        List<VioWfdmCode> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                VioWfdmCode c = ParserJson.parseJsonToObj(obj, VioWfdmCode.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveRoadItemInDb(WebQueryResult<String> dict,Box<FrmRoadItem> box) {
        box.removeAll();
        String re = dict.getResult();
        List<FrmRoadItem> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                FrmRoadItem c = ParserJson.parseJsonToObj(obj, FrmRoadItem.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveRoadSegInDb(WebQueryResult<String> dict,Box<FrmRoadSeg> box) {
        box.removeAll();
        String re = dict.getResult();
        List<FrmRoadSeg> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                FrmRoadSeg c = ParserJson.parseJsonToObj(obj, FrmRoadSeg.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveSysParaInDb(WebQueryResult<String> dict, Box<SysParaValue> box) {
        box.removeAll();
        String re = dict.getResult();
        List<SysParaValue> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SysParaValue c = ParserJson.parseJsonToObj(obj, SysParaValue.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveForceDb(WebQueryResult<String> data, Box<WfxwForce> box) {
        box.removeAll();
        String re = data.getResult();
        List<WfxwForce> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                WfxwForce c = ParserJson.parseJsonToObj(obj, WfxwForce.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveStreeDataInDb(WebQueryResult<String> data, Box<SeriousStreetBean> box) {
        box.removeAll();
        String re = data.getResult();
        List<SeriousStreetBean> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SeriousStreetBean c = ParserJson.parseJsonToObj(obj, SeriousStreetBean.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDptCodeInDb(WebQueryResult<String> data, Box<FrmDptCode> box) {
        box.removeAll();
        String re = data.getResult();
        List<FrmDptCode> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                FrmDptCode c = ParserJson.parseJsonToObj(obj, FrmDptCode.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveZapcLxxxInDb(WebQueryResult<String> data, Box<ZapcLxxx> box) {
        box.removeAll();
        String re = data.getResult();
        List<ZapcLxxx> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(re);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ZapcLxxx c = ParserJson.parseJsonToObj(obj, ZapcLxxx.class);
                codes.add(c);
            }
            if (codes != null && codes.size() > 0) {
                box.put(codes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
