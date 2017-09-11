package com.jwt.thread;

import android.content.Context;
import android.os.Handler;

public class UpdateInfoThread extends Thread {
    private Context self;
    private Handler mHandler;
    public static final String BOOL_OPER_GPS = "isOpenGps";

    public UpdateInfoThread(Context context, Handler handler) {
        self = context;
        mHandler = handler;
    }

//    private void synZhcxMenu(RestfulDao dao, File zhcxFile, String jh) {
//        String xml = GlobalMethod.readFileContent(zhcxFile);
//        if (!TextUtils.isEmpty(xml) && checkMd5(dao, xml, jh, "1001")) {
//            return;
//        }
//        WebQueryResult<String> mu = dao.getSystemMenu();
//        String err2 = GlobalMethod.getErrorMessageFromWeb(mu);
//        if (TextUtils.isEmpty(err2) && !TextUtils.isEmpty(mu.getResult())) {
//            GlobalMethod
//                    .writeInDisk(zhcxFile.getAbsolutePath(), mu.getResult());
//        }
//    }

    @Override
    public void run() {
        //验证自选路段
//        WfddDao.checkFavorWfld(self.getContentResolver());
//
//        int crossRow = WfddDao.testCrossCount(self.getContentResolver());
//        Log.e("UpThread", "路口测试数据" + crossRow);
//
//        File innDir = self.getFilesDir();
//        if (!innDir.exists())
//            innDir.mkdirs();
//        File zhcxFile = new File(innDir, "zhcx.xml");
//        File wfxwCllxFile = new File(innDir, "wfxw.xml");
//        String jh = GlobalData.grxx.get(GlobalConstant.YHBH);
//        RestfulDao dao = RestfulDaoFactory.getDao();
//        if(dao == null){
//            return;
//        }
        // 与服务器同步决定书编号
//        synJdsHmb(dao, jh);
//        // synJdsHmb("002", dao, jh);
//        // 同步综合查询菜单
//        synZhcxMenu(dao, zhcxFile, jh);
//
//        // 更新违法行为和车辆类型关系模型
//        synWfxwAndCllx(dao, wfxwCllxFile, jh);
//        //同步严管违停
//        synSeriousStreet();
//
//        // 验证民警非现场是否完整
//        WebQueryResult<ZapcReturn> re = dao.checkFxcZqmjAllUpload(jh);
//        if (re != null && re.getStatus() == HttpStatus.SC_OK && re.getResult() != null && "1".equals(re.getResult().getCgbj())) {
//            String[] phs = re.getResult().getPcbh();
//            if (phs != null && phs.length > 0) {
//                FxczfDao fxcDao = new FxczfDao(self);
//                for (String ph : phs) {
//                    fxcDao.setXtbhScbj(ph, "0");
//                }
//                fxcDao.closeDb();
//            }
//        }
//
//        // 从服务器更新GPS开关和身份证等信息
//        GlobalMethod.updateSysConfig();
//        GlobalMethod.saveParam(self);
//        // String pf = Environment.getExternalStorageDirectory()
//        // .getAbsolutePath() + "/jwtdb/param.xml";
//        // GlobalMethod.saveParam(self, pf);
//        // 无需上专GPS，GPS设备已打开，则关闭
//        if (GlobalSystemParam.isGpsUpload) {
//            LocationManager locm = (LocationManager) self
//                    .getSystemService(Context.LOCATION_SERVICE);
//            if (!locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Message msg = mHandler.obtainMessage();
//                Bundle b = new Bundle();
//                b.putBoolean(BOOL_OPER_GPS, true);
//                msg.setData(b);
//                mHandler.sendMessage(msg);
//            }
//        }
    }

    private void synSeriousStreet() {
//        MessageDao dao = new MessageDao(self);
//        int version = dao.getSeriousVersion();
//        RestfulDao rd = RestfulDaoFactory.getDao();
//        WebQueryResult<List<SeriousStreetBean>> we = rd.getSeriousStreet("" + version);
//        if (we.getStatus() == HttpStatus.SC_OK && we.getResult() != null) {
//            List<SeriousStreetBean> list = we.getResult();
//            if (!list.isEmpty()) {
//                int del = dao.delAllSerious();
//                int add = dao.addSeriousList(list);
//                Log.e("UpdateThread serious", "更新严管违停：删除" + del + "新增" + add);
//            }
//        }
//        dao.closeDb();

    }

//    private boolean checkMd5(RestfulDao dao, String xml, String jh,
//                             String catalog) {
//        String md5 = ZipUtils.getMD5(xml);
//        WebQueryResult<String> checkMd5 = dao.checkUserMd5(jh,
//                GlobalData.serialNumber, md5, "10008");
//        String err = GlobalMethod.getErrorMessageFromWeb(checkMd5);
//        if (TextUtils.isEmpty(err) && !TextUtils.isEmpty(checkMd5.getResult())
//                && TextUtils.equals(checkMd5.getResult(), md5)) {
//            return true;
//        }
//        return false;
//    }

//    private void synWfxwAndCllx(RestfulDao dao, File wfxwCllxFile, String jh) {
//        String xml = GlobalMethod.readFileContent(wfxwCllxFile);
//        if (!TextUtils.isEmpty(xml) && checkMd5(dao, xml, jh, "1008")) {
//            return;
//        }
//        int row = 0;
//        WebQueryResult<String> str = dao.getAllWfxwCllxCheck();
//        WebQueryResult<List<WfxwCllxCheckBean>> wfre = GlobalMethod
//                .webXmlStrToListObj(str, WfxwCllxCheckBean.class);
//        String err = GlobalMethod.getErrorMessageFromWeb(wfre);
//        if (TextUtils.isEmpty(err) && wfre.getResult() != null) {
//            List<WfxwCllxCheckBean> wfxws = wfre.getResult();
//            MessageDao mdao = new MessageDao(self);
//            for (WfxwCllxCheckBean w : wfxws) {
//                mdao.delWfxwCllx(w.getWfxw());
//                row += mdao.addWfxwCllx(w);
//            }
//            mdao.closeDb();
//            GlobalMethod.writeInDisk(wfxwCllxFile.getAbsolutePath(),
//                    str.getResult());
//        }
//        Log.e("UpdateInfoThread", "update wfxw cllx: " + row);
//
//    }


}
