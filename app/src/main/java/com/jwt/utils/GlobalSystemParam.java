package com.jwt.utils;

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

}
