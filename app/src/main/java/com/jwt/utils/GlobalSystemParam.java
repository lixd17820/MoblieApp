package com.jwt.utils;

import java.util.HashSet;
import java.util.Set;

public class GlobalSystemParam {

    /**
     * 图片压缩比例
     */
    public static int picCompress = 60;
    /**
     * 保存决定书时验证驾驶员和机动车的方式,初始化为本地车和证
     */
    public static int drvCheckFs = 2;
    /**
     * 机动车验证方式
     */
    public static int vehCheckFs = 2;

    /**
     * 是否上传GPS位置
     */
    public static boolean isGpsUpload = false;
    /**
     * 是否对非机动车身份证明进行严格证认
     */
    public static boolean isCheckFjdcSfzm = false;
    /**
     * 心跳包和GPS包上传频率，单位是分钟
     */
    public static int uploadFreq = 2;

    /**
     * 拍照后是否预览
     */
    public static boolean isPreviewPhoto = true;

    /**
     * 是否跳过两次下拉框联动赋值
     */
    public static boolean isSkipSpinner = false;

    public static int unsend_fxc_hours = 48;

    //是否接收报警
    public static boolean isReciveBj = true;
    //是否接收同事信息
    public static boolean isReciveText = true;

    public static boolean isNotNotice = true;

    public static boolean isConnBjbd = true;

    public static String bjRingtone = "";

    public static Set<String> recBjbdZl = new HashSet<>();

    public static Set<String> recBjbdFW = new HashSet<>();

    public static ConnCata connCata = ConnCata.OUTSIDECONN;

    public static Set<String> bjzlNames = new HashSet<>();

    public static void syncBjzl(){
        bjzlNames.clear();
        if (recBjbdZl != null && !recBjbdZl.isEmpty()) {
            for (String key : recBjbdZl) {
                String name = GlobalMethod.getStringFromKVListByKey(GlobalData.bjzlList, key);
                bjzlNames.add(name);
            }
        }
    }
}
