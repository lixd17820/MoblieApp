package com.jwt.web;

//import android.text.TextUtils;

import android.util.Log;

//import com.ntga.bean.VioFxczfBean;
//import com.ntga.bean.WebQueryResult;
//import com.ntga.dao.GlobalMethod;
//import com.ntga.tools.ZipUtils;
//import com.ntga.xml.CommParserXml;
//import com.ntga.zapc.ZapcReturn;
//
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.ArrayList;
//import java.util.List;

/**
 * 内外网交换
 */
public class ThreeTeamDao extends RestfulDao {

    public ThreeTeamDao() {
        Log.e("ThreeTeamDao", "ThreeTeamDao create");
    }

    @Override
    public String getUrl() {
        //主服务器
        //return "http://127.0.0.1:8999";
        //备用服务器
        //return "http://127.0.0.1:8099";
        //测试服务器
        return "http://127.0.0.1:8088";
    }

    @Override
    public String getMqttUrl() {
        return "tcp://127.0.0.1:8099";
    }

    @Override
    public String getPicUrl() {
        return getUrl() + PIC_URL;
    }

    @Override
    public String getFileUrl() {
        return getUrl() + "/ydjw/DownloadFile?pack=";
    }

    @Override
    public String getJqtbFileUrl() {
        return getUrl() + JQTB_FILE_URL;
    }

    @Override
    public String getClassName() {
        return "ThreeTeamDao";
    }

    //private String pic_url = "http://127.0.0.1:8099/jwt_pic/services/pic/";

//    /**
//     * 上传非现场执法的文本信息
//     *
//     * @param fxc
//     * @return
//     */
//    @Override
//    public WebQueryResult<ZapcReturn> uploadFxczfJl(VioFxczfBean fxc) {
//        try {
//            String xml = CommParserXml.objToXml(fxc);
//            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
//            postParams.add(new BasicNameValuePair("fxcjl", xml));
//            WebQueryResult<String> r = restfulQuery(pic_url + "uploadFxcJl",
//                    postParams, POST, true);
//            // uploadAction(postParams, UPLOADREP);
//            return GlobalMethod.webXmlStrToObj(r, ZapcReturn.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private boolean isFileUpload(long xtbh, String md5) {
//        boolean isUp = false;
//        try {
//            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
//            postParams.add(new BasicNameValuePair("xtbh", "" + xtbh));
//            postParams.add(new BasicNameValuePair("md5", md5.toUpperCase()));
//            WebQueryResult<String> r = restfulQuery(pic_url + "uploadFxcPhotoCheck",
//                    postParams, POST, true);
//            // uploadAction(postParams, UPLOADREP);
//            WebQueryResult<ZapcReturn> zr = GlobalMethod.webXmlStrToObj(r, ZapcReturn.class);
//            if (TextUtils.isEmpty(GlobalMethod.getErrorMessageFromWeb(zr))) {
//                ZapcReturn za = zr.getResult();
//                if (za != null && za.getCgbj() != null) {
//                    isUp = TextUtils.equals(za.getCgbj(), "1");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isUp;
//    }
//
//    /**
//     * 上传非现场照片
//     *
//     * @param file
//     * @param xtbh
//     * @return
//     */
//    @Override
//    public WebQueryResult<ZapcReturn> uploadFxcPhoto(File file, Long xtbh) {
//        String md5 = ZipUtils.getFileMd5(file);
//        WebQueryResult<ZapcReturn> wr = new WebQueryResult<ZapcReturn>();
//        if (isFileUpload(xtbh, md5)) {
//            ZapcReturn zr = new ZapcReturn();
//            wr.setStatus(HttpStatus.SC_OK);
//            zr.setCgbj("1");
//            zr.setScms("上传成功");
//            wr.setResult(zr);
//            return wr;
//        }
//        wr.setStatus(HttpStatus.SC_BAD_REQUEST);
//        long fileLen = file.length();
//        String jwtUpUrl = pic_url + "uploadFxcPhoto?xtbh=" + xtbh + "&md5=" + md5.toUpperCase();
//        try {
//            FileInputStream in = new FileInputStream(file);
//            WebQueryResult<String> sre = uploadByte(jwtUpUrl, null, in,
//                    fileLen, null);
//            in.close();
//            if (sre.getStatus() == HttpStatus.SC_OK
//                    && !TextUtils.isEmpty(sre.getResult())) {
//                ZapcReturn g = CommParserXml.parseXmlToObj(sre.getResult(),
//                        ZapcReturn.class);
//                wr.setResult(g);
//                wr.setStatus(HttpStatus.SC_OK);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return wr;
//
//    }
//
//    @Override
//    public WebQueryResult<ZapcReturn> checkFxcIsUpOK(String xtbh) {
//        WebQueryResult<ZapcReturn> re = new WebQueryResult<ZapcReturn>();
//        try {
//            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
//            postParams.add(new BasicNameValuePair("xtbh", xtbh));
//            re = GlobalMethod.webXmlStrToObj(
//                    restfulQuery(pic_url + "checkFxcAllOk", postParams, POST,
//                            true), ZapcReturn.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return re;
//    }
//
//    /**
//     * 查询非现场入库情况
//     *
//     * @param xtbh
//     * @return
//     */
//    public WebQueryResult<String> queryFxcRkqk(String xtbh) {
//        WebQueryResult<String> re = null;
//        try {
//            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
//            postParams.add(new BasicNameValuePair("xtbh", xtbh));
//            re = restfulQuery(pic_url + "queryFxcRkqk", postParams, POST);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return re;
//    }

}
