package com.jwt.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.jwt.adapter.JtssLightAdapter;
import com.jwt.adapter.SelectObjectBean;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.LightInfoBean;
import com.jwt.dao.WfddDao;
import com.jwt.main.R;
import com.jwt.pojo.JtssLightPic;
import com.jwt.pojo.JtssLightPic_;
import com.jwt.pojo.JtssParam;
import com.jwt.thread.CommHanderThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class JtssLightFragement extends Fragment {

    private LRecyclerViewAdapter mlAdapter;
    private LRecyclerView mRecycleView;
    private JtssLightAdapter adapter;
    private Spinner spinXzqh, spinRoad, spinCross;
    private Activity self;
    private static String TAG = "JtssLightFragement";
    private Map<String, String> lkfxMap = new HashMap<>();
    private Map<String, String> lightMap = new HashMap<>();

    private String photoName = "";

    private List<SelectObjectBean<LightInfoBean>> lightList = new ArrayList<>();

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static JtssLightFragement newInstance() {
        JtssLightFragement fragment = new JtssLightFragement();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        self = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_item_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        adapter = new JtssLightAdapter(lightList);
        mlAdapter = new LRecyclerViewAdapter(adapter);
        mRecycleView = (LRecyclerView) view.findViewById(R.id.rec_view_light);
        //noinspection ConstantConditions
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mlAdapter);
        mlAdapter.setOnItemClickListener(itemClick);
        spinXzqh = view.findViewById(R.id.spin_xzqh);
        spinRoad = view.findViewById(R.id.spin_road);
        spinCross = view.findViewById(R.id.spin_cross);
        GlobalMethod.changeAdapter(
                spinXzqh, WfddDao.getOwnerXzqhList(GlobalData.grxx.get(GlobalConstant.YBMBH),
                        GlobalMethod.getBoxStore(self)), self, true);
        spinXzqh.setOnItemSelectedListener(xzqhSelListener);
        spinRoad.setOnItemSelectedListener(roadSelListener);
        //GlobalMethod.changeAdapter(spinCross,
        //        Arrays.asList(new KeyValueBean("9031", "人民路濠东路路口")), self, false);
        List<JtssParam> list = GlobalMethod.getBoxStore(self).boxFor(JtssParam.class).getAll();
        for (JtssParam p : list) {
            Log.e(TAG, p.toString());
            if ("1".equals(p.getType())) {
                lkfxMap.put(p.getCode(), p.getValue());
            }
            if ("2".equals(p.getType())) {
                lightMap.put(p.getCode(), p.getValue());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private AdapterView.OnItemSelectedListener xzqhSelListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long i) {
            Log.e(TAG, "spinnerXzqh" + position + "/" + i);
            String xzqh = GlobalMethod.getKeyFromSpinnerSelected(
                    spinXzqh, GlobalConstant.KEY);
            if (TextUtils.isEmpty(xzqh)) {
                //GlobalMethod.changeAdapter(spinRoad, null, self);
                //GlobalMethod.changeAdapter(spinCross, null, self);
            } else {
                CommHanderThread thread = new CommHanderThread(CommHanderThread.QUERY_ROAD,
                        new String[]{xzqh}, mHandler, self);
                thread.doStart();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    private AdapterView.OnItemSelectedListener roadSelListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long i) {
            String dldm = GlobalMethod.getKeyFromSpinnerSelected(
                    spinRoad, GlobalConstant.KEY);
            if (TextUtils.isEmpty(dldm)) {
                //GlobalMethod.changeAdapter(spinRoad, null, self);
                GlobalMethod.changeAdapter(spinCross, null, self);
            } else {
                CommHanderThread thread = new CommHanderThread(CommHanderThread.QUERY_CROSS,
                        new String[]{dldm}, mHandler, self);
                thread.doStart();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    OnItemClickListener itemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            SelectObjectBean<LightInfoBean> temp = lightList.get(position);
            temp.setSel(!temp.isSel());
            adapter.notifyItemChanged(position);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = msg.getData().getString("data");
            JSONObject json = ParserJson.getJsonObject(data);
            if (TextUtils.isEmpty(json.optString("err"))) {
                int catalog = json.optInt("catalog", 0);
                if (catalog == CommHanderThread.UPLOAD_PIC) {
                    int total = json.optInt("total");
                    int count = json.optInt("count");
                    GlobalMethod.toast(self, "共" + total + "条成功" + count + "条");
                    queryLightByCross();
                    return;
                }
                JSONArray array = json.optJSONArray("array");
                if (catalog == CommHanderThread.QUERY_ROAD) {
                    GlobalMethod.changeAdapter(spinRoad, null, self);
                    List<KeyValueBean> roads = new ArrayList<>();
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.optJSONObject(i);
                            roads.add(new KeyValueBean(obj.optString("dldm"),
                                    obj.optString("dlmc")));
                        }
                        GlobalMethod.changeAdapter(spinRoad, roads, self, true);
                    }
                    GlobalMethod.toast(self, "共查询到" + roads.size() + "条道路");
                } else if (catalog == CommHanderThread.QUERY_CROSS) {
                    GlobalMethod.changeAdapter(spinCross, null, self);
                    List<KeyValueBean> crosses = new ArrayList<>();
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.optJSONObject(i);
                            crosses.add(new KeyValueBean(obj.optInt("crossId") + "",
                                    obj.optString("crossName")));
                        }
                        GlobalMethod.changeAdapter(spinCross, crosses, self, true);
                    }
                    GlobalMethod.toast(self, "共查询到" + crosses.size() + "个路口");
                } else if (catalog == CommHanderThread.QUERY_LIGHT) {
                    lightList.clear();
                    String crossId = GlobalMethod.getKeyFromSpinnerSelected(
                            spinCross, GlobalConstant.KEY);
                    Map<String, JSONArray> picMap = getLightPicMap(crossId);
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.optJSONObject(i);
                            String _id = obj.optString("_id");
                            LightInfoBean li = new LightInfoBean();
                            li.set_id(_id);
                            li.setCrossId(obj.optString("crossId"));
                            li.setCrossName(obj.optString("crossName"));
                            li.setLkfx(lkfxMap.get(obj.optString("lkfx")));
                            li.setLight(lightMap.get(obj.optString("light")));
                            JSONArray pics = obj.optJSONArray("pic");
                            if (pics == null || pics.length() == 0) {
                                li.setPicNum(0);
                            } else {
                                li.setPicNum(pics.length());
                                li.setPics(pics);
                            }
                            JSONArray locPics = picMap.get(_id);
                            if (locPics != null)
                                li.setLocalPics(locPics);
                            SelectObjectBean so = new SelectObjectBean();
                            so.setBean(li);
                            lightList.add(so);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                GlobalMethod.showErrorDialog(json.optString("err"), self);
            }

        }
    };

    private Map<String, JSONArray> getLightPicMap(String crossId) {
        Box<JtssLightPic> box = GlobalMethod.getBoxStore(self).boxFor(JtssLightPic.class);
        List<JtssLightPic> list = box.query().equal(JtssLightPic_.crossId, crossId).build().find();
        Map<String, JSONArray> map = new HashMap<>();
        for (JtssLightPic lp : list) {
            String id = lp.getLightId();
            JSONArray pics = map.get(id);
            if (pics == null)
                pics = new JSONArray();
            JSONObject pic = ParserJson.objToJson(lp);
            pics.put(pic);
            map.put(id, pics);
        }
        return map;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, "查找").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 1, 0, "拍照").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 0, "上传").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 0, "看本地图").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 4, 0, "看系统图").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 5, 0, "删除图片").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    private void queryLightByCross() {
        String cross = GlobalMethod.getKeyFromSpinnerSelected(
                spinCross, GlobalConstant.KEY);
        if (TextUtils.isEmpty(cross)) {
            GlobalMethod.showErrorDialog("路口不能为空", self);
            return;
        }
        CommHanderThread thread = new CommHanderThread(CommHanderThread.QUERY_LIGHT,
                new String[]{cross}, mHandler, self);
        thread.doStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == 0) {
            queryLightByCross();
        } else if (item.getItemId() == 1) {
            List<SelectObjectBean> list = getSelItem();
            if (list == null || list.size() != 1) {
                GlobalMethod.toast(self, "请选择一条数据拍照");
                return false;
            }
            startTakePhoto();
        } else if (item.getItemId() == 2) {
            doUploadPic();
        } else if (item.getItemId() == 3) {
            showLocationPics();
        } else if (item.getItemId() == 4) {
            showSystemPic();
        } else if (item.getItemId() == 5) {
            delLocationPic();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSystemPic() {
        List<SelectObjectBean> list = getSelItem();
        if (list == null || list.size() <= 0) {
            GlobalMethod.toast(self, "请选择一条数据");
            return;
        }
        List<String> picList = new ArrayList<>();
        String url = RestfulDaoFactory.getDao().getLightPicUrl();
        for (SelectObjectBean sel : list) {
            final LightInfoBean light = (LightInfoBean) sel.getBean();
            JSONArray pics = light.getPics();
            for (int i = 0; i < pics.length(); i++) {
                JSONObject obj = pics.optJSONObject(i);
                String id = obj.optString("picId");
                picList.add(url + "?id=" + id);
            }
        }
        if (picList == null || picList.isEmpty()) {
            GlobalMethod.showErrorDialog("没有系统图片", self);
            return;
        }
        GlobalMethod.showImageActivity(GlobalMethod.join(picList, ","), self);
    }

    private void delLocationPic() {
        List<SelectObjectBean> list = getSelItem();
        if (list == null || list.size() != 1) {
            GlobalMethod.toast(self, "请选择一条数据");
            return;
        }
        SelectObjectBean sel = list.get(0);
        LightInfoBean light = (LightInfoBean) sel.getBean();
        final JSONArray lpics = light.getLocalPics();
        if (lpics == null || lpics.length() <= 0) {
            GlobalMethod.showErrorDialog("没有本地图片", self);
            return;
        }
        Log.e(TAG, "本地: " + lpics.toString());
        String[] pics = new String[lpics.length()];
        for (int i = 0; i < lpics.length(); i++) {
            JSONObject obj = lpics.optJSONObject(i);
            pics[i] = new File(obj.optString("pic")).getName();
        }
        new MaterialDialog.Builder(self)
                .title("请选择图片")
                .items(pics)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        if (which == null || which.length == 0)
                            return true;
                        Box<JtssLightPic> box = GlobalMethod.getBoxStore(self).boxFor(JtssLightPic.class);
                        for (Integer i : which) {
                            JSONObject obj = lpics.optJSONObject(i);
                            if (obj == null)
                                continue;
                            long id = obj.optLong("id");
                            box.remove(id);
                        }
                        queryLightByCross();
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    private void showLocationPics() {
        List<SelectObjectBean> list = getSelItem();
        if (list == null || list.size() <= 0) {
            GlobalMethod.toast(self, "请选择一条数据");
            return;
        }
        List<String> pics = new ArrayList<>();
        for (SelectObjectBean sel : list) {
            LightInfoBean light = (LightInfoBean) sel.getBean();
            JSONArray lpics = light.getLocalPics();
            if (lpics == null || lpics.length() <= 0)
                continue;
            for (int i = 0; i < lpics.length(); i++) {
                JSONObject obj = lpics.optJSONObject(i);
                pics.add(obj.optString("pic"));
            }
        }
        if (pics.isEmpty()) {
            GlobalMethod.showErrorDialog("没有本地图片", self);
            return;
        }
        GlobalMethod.showImageActivity(GlobalMethod.join(pics, ","), self);
    }

    private void doUploadPic() {
        List<SelectObjectBean> list = getSelItem();
        if (list == null || list.size() <= 0) {
            GlobalMethod.toast(self, "请选择一条数据");
            return;
        }
        JSONArray pics = new JSONArray();
        for (SelectObjectBean sel : list) {
            LightInfoBean light = (LightInfoBean) sel.getBean();
            JSONArray lpics = light.getLocalPics();
            if (lpics == null || lpics.length() <= 0)
                continue;
            for (int i = 0; i < lpics.length(); i++) {
                JSONObject obj = lpics.optJSONObject(i);
                int scbj = obj.optInt("scbj");
                if (scbj == 0) {
                    pics.put(obj);
                }
            }
        }
        if (pics.length() == 0) {
            GlobalMethod.showErrorDialog("没有本地图片", self);
            return;
        }
        CommHanderThread thread = new CommHanderThread(CommHanderThread.UPLOAD_PIC,
                new String[]{pics.toString()}, mHandler, self);
        thread.doStart();
    }


    private List<SelectObjectBean> getSelItem() {
        List<SelectObjectBean> list = new ArrayList<>();
        for (SelectObjectBean sel : lightList) {
            if (sel.isSel())
                list.add(sel);
        }
        return list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
    }

    private final int CAMER_REQUEST = 1110;

    private void startTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(self.getPackageManager()) != null) {
            File photoFile = GlobalMethod.createImageFile(self,
                    false);
            if (photoFile != null) {
                photoName = photoFile.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoURI = FileProvider.getUriForFile(self,
                            "com.jwt.main.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                Log.e("fxc photo", photoFile.getAbsolutePath());
                startActivityForResult(takePictureIntent, CAMER_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("JbywImageView", "onActivityResult");
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMER_REQUEST) {
                camerResult();
            }
        }
    }

    private void camerResult() {
        List<SelectObjectBean> list = getSelItem();
        if (list == null || list.size() != 1) {
            GlobalMethod.toast(self, "没有选择一个记录拍照");
            return;
        }
        if (TextUtils.isEmpty(photoName))
            return;
        File image = new File(photoName);
        Log.e("mCurrentPhotoPath", photoName);
        if (!image.exists()) {
            Toast.makeText(self, "照片拍摄失败", Toast.LENGTH_LONG).show();
            return;
        }
        File dir = image.getParentFile();
        String fn = image.getName();
        dir = new File(dir, "small");
        if (!dir.exists())
            dir.mkdirs();
        File smallF = new File(dir, fn);
        String text = "拍摄时间："
                + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        String jh = GlobalData.grxx.get(GlobalConstant.JH);
        String xm = GlobalData.grxx.get(GlobalConstant.XM);
        text += " 拍摄人：" + xm + "(" + jh + ")";
        Bitmap smallImage = GlobalMethod.compressBitmap(photoName, 800, text);
        if (smallImage == null) {
            Toast.makeText(self, "照片压缩失败", Toast.LENGTH_LONG).show();
            return;
        }
        boolean isSave = GlobalMethod.savePicIntoFile(smallImage, smallF);
        if (!isSave) {
            Toast.makeText(self, "照片保存失败", Toast.LENGTH_LONG).show();
            return;
        }
        LightInfoBean light = (LightInfoBean) list.get(0).getBean();
        JSONArray lps = light.getLocalPics();
        if (lps == null) {
            lps = new JSONArray();
        }
        //List<String> smallPhotoList = adapter.getList();
        //smallPhotoList.add(smallF.getAbsolutePath());
        //adapter.setImageList(smallPhotoList);
        Box<JtssLightPic> box = GlobalMethod.getBoxStore(self).boxFor(JtssLightPic.class);
        JtssLightPic lp = new JtssLightPic();
        lp.setLightId(light.get_id());
        lp.setPic(smallF.getAbsolutePath());
        lp.setCrossId(light.getCrossId());
        lp.setScbj(0);
        box.put(lp);
        JSONObject obj = ParserJson.objToJson(lp);
        lps.put(obj);
        light.setLocalPics(lps);
        adapter.notifyDataSetChanged();
        //GlobalMethod.showImageActivity(smallF.getAbsolutePath(), self);

    }
}
