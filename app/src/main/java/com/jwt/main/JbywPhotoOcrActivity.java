package com.jwt.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.jwt.event.OcrEvent;
import com.jwt.event.OperPhotoEvent;
import com.jwt.thread.OperPhotoThread;
import com.jwt.utils.GlobalMethod;
import com.jwt.view.AutoFitTextureView;

public class JbywPhotoOcrActivity extends Activity {

    private static final int REQ_OCI_SERVICE = 110;
    private Camera mCamera;
    private PictureCallback mPicture;
    private Button capture;
    private ImageButton switchCamera;
    private Context myContext;
    private boolean cameraFront = false;
    private AutoFitTextureView mTextureView;
    private static String TAG = "JbywPhotoOcrActivity";
    //识别证件的种类，0这身份证，1为驾驶证
    private int catalog = 0;

    static {

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    /**
     * This is the output file for our picture.
     */
    //private File mFile;

//    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
//            = new ImageReader.OnImageAvailableListener() {
//
//        @Override
//        public void onImageAvailable(ImageReader reader) {
//            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
//        }
//
//    };
//    private HandlerThread mBackgroundThread;
//
//    private Handler mBackgroundHandler;
    private Camera.Size mPreviewSize;

    private boolean isReturn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_ocr);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Opencv err");
        } else
            Log.e(TAG, "opencv ok");
        String ly = getIntent().getStringExtra("ocr");
        catalog = getIntent().getIntExtra("catalog", 0);
        isReturn = !TextUtils.isEmpty(ly);
        Log.e(TAG, ly + "/" + catalog + "/" + isReturn);
        EventBus.getDefault().register(this);
        //doBindService();
        initialize();
    }


    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        //startBackgroundThread();
//        if (mCamera == null) {
//            //if the front facing camera does not exist
//            if (findFrontFacingCamera() < 0) {
//                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
//                switchCamera.setVisibility(View.GONE);
//            }
//            mCamera = Camera.open(findBackFacingCamera());
//            //mPicture = getPictureCallback();
//           // mPreview.refreshCamera(mCamera);
//        }
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    public void initialize() {
        mTextureView = findViewById(R.id.camera_preview);
        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                if (!mIsBound) {
//                    PackageInfo pk = null;
//                    try {
//                        pk = myContext.getPackageManager().getPackageInfo("com.ntjxj.cardocr", 0);
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    if (pk == null) {
//                        GlobalMethod.showErrorDialog("未能成功绑定识别服务，可能识别软件未能正确安装", myContext);
//                    } else {
//                        Intent intent = new Intent();
//                        ComponentName comp = new ComponentName("com.ntjxj.cardocr",
//                                "com.ntjxj.cardocr.PermissionActivity");
//                        intent.setComponent(comp);
//                        intent.putExtra("connCata", 2);
//                        intent.setAction("android.intent.action.VIEW");
//                        startActivityForResult(intent, REQ_OCI_SERVICE);
//                    }
//                    return;
//                }
                capture.setEnabled(false);
                mCamera.startPreview();
                isOperOver = true;
                Toast.makeText(myContext,
                        "开始识别，请不要移动手机", Toast.LENGTH_LONG).show();
            }
        });
        switchCamera = findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(myContext)
                        .title("请选择证件类型")
                        .items(R.array.regc_items)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                catalog = which;
                                return true;
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_OCI_SERVICE) {
            doBindService();
            GlobalMethod.showErrorDialog("授权成功，请重新点击识别按扭", JbywPhotoOcrActivity.this);
        }
    }

    private int mCameraId;

    private void openCamera(int width, int height) {
        mCameraId = findBackFacingCamera();
        mCamera = Camera.open(mCameraId);
        setUpCameraOutputs(width, height);
        //configureTransform(width, height);
        mCamera.setPreviewCallback(previewCallback);
        try {
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
        //stopBackgroundThread();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            //mCamera.startPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    //输出图片的大小为大于1000的最接近的
    private Camera.Size getMidImgSize(List<Camera.Size> list) {
        for (int i = 0; i < list.size(); i++) {
            Camera.Size s = list.get(i);
            if (s.width > 1000) {
                if (i < list.size() - 2)
                    return list.get(i + 1);
                else
                    return list.get(i);
            }
        }
        return list.get(0);
    }

    private void setUpCameraOutputs(int width, int height) {
        if (mCamera == null)
            return;
        Camera.Parameters param = mCamera.getParameters();
        List<Camera.Size> preSizes = param.getSupportedPreviewSizes();
        List<Camera.Size> imgSizes = param.getSupportedPictureSizes();
        Collections.sort(imgSizes, new CompareSizesByArea());
        Camera.Size jpeg = getMidImgSize(imgSizes);
        param.setPictureSize(jpeg.width, jpeg.height);
//        mImageReader = ImageReader.newInstance(jpeg.width, jpeg.height,
//                ImageFormat.JPEG, /*maxImages*/2);
//        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
        int orient = getDisplayOrientation();
        param.setRotation(orient);
        mCamera.setDisplayOrientation(orient);


        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        int w = (int) Math.round((double) Math.max(displaySize.x, displaySize.y) * 0.9);
        int h = Math.min(displaySize.x, displaySize.y);
        //找出小于或等于这个比例的预览尺寸
        List<PreSize> preSize = new ArrayList<>();
        double viewBl = (double) w / h;
        for (Camera.Size size : preSizes) {
            double sizeBl = (double) size.width / size.height;
            int cj = (int) Math.round((viewBl - sizeBl) * 10000);
            int sizeHeight = Math.min(size.width, size.height);
            if (cj >= 0 && sizeHeight <= h * 2 && sizeHeight > h / 2) {
                //小于预览比例
                preSize.add(new PreSize(size, cj));
            }
        }
        if (preSize.isEmpty())
            preSize.add(new PreSize(preSizes.get(0), 0));
        PreSize min = Collections.min(preSize, preCompare);
        mPreviewSize = min.size;
        param.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(param);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTextureView.setAspectRatio(
                    mPreviewSize.width, mPreviewSize.height);
        } else {
            mTextureView.setAspectRatio(
                    mPreviewSize.height, mPreviewSize.width);
        }
    }


//    private void startBackgroundThread() {
//        mBackgroundThread = new HandlerThread("CameraBackground");
//        mBackgroundThread.start();
//        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
//    }
//
//    private void stopBackgroundThread() {
//        mBackgroundThread.quitSafely();
//        try {
//            mBackgroundThread.join();
//            mBackgroundThread = null;
//            mBackgroundHandler = null;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    private static class CompareSizesByArea implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            return Long.signum((long) lhs.width * lhs.height -
                    (long) rhs.width * rhs.height);
        }
    }

    public int getDisplayOrientation() {
        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.e(TAG, "相机角度：" + rotation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = (camInfo.orientation - degrees + 360) % 360;
        return result;
    }

    private Comparator<PreSize> preCompare = new Comparator<PreSize>() {
        @Override
        public int compare(PreSize ll, PreSize rl) {
            int c = ll.cj - rl.cj;
            if (c != 0)
                return c;
            return rl.size.width - ll.size.width;
        }
    };

    static class PreSize {
        public Camera.Size size;
        public int cj;

        public PreSize(Camera.Size size, int cj) {
            this.size = size;
            this.cj = cj;
        }

        @Override
        public String toString() {
            return "差距：" + cj + "；宽度：" + size.width + "；高度：" + size.height;
        }
    }

    public static long count = 0;
    public boolean isOperOver = false;

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            count++;
            if (count % 100 == 0 && isOperOver) {
                isOperOver = false;
                Log.e(TAG, "COUNT: " + count + "；byte size: " + bytes.length);
                Camera.Parameters parameters = camera.getParameters();
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                String dir = getExternalFilesDir(null).getAbsolutePath();
                Log.e(TAG, "DIR: " + dir);
                int imageFormat = parameters.getPreviewFormat();
                int w = parameters.getPreviewSize().width;
                int h = parameters.getPreviewSize().height;

                if (imageFormat == ImageFormat.NV21) {
                    try {
                        File tempFile = new File(dir, "temp.jpg");
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, w, h, null);
                        yuvImage.compressToJpeg(new Rect(0, 0, w, h), 100, fos);
                        fos.close();
                        Mat mat = Imgcodecs.imread(tempFile.getAbsolutePath());
                        if (rotation == 0) {
                            Core.transpose(mat, mat);
                            Core.flip(mat, mat, 1);
                            Log.e(TAG, "旋转: " + 90);
                            Imgcodecs.imwrite(tempFile.getAbsolutePath(), mat);
                        }
                        //String fn = dir + "/" + System.currentTimeMillis()
                        //        + "_" + rotation + ".jpg";
                        String small = dir + "/sfzh_" + System.currentTimeMillis()
                                + "_" + rotation + ".jpg";
                        //Log.e(TAG, "文件名: " + fn);
                        //Imgcodecs.imwrite(fn, mat);
                        OperPhotoThread thread = new OperPhotoThread(myContext);
                        thread.doStart(mat, small, catalog);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void operPhotoEvent(OcrEvent event) {

        if (event.isOk) {
            //有了结果
            Toast.makeText(this, "有了结果", Toast.LENGTH_SHORT).show();
            mCamera.stopPreview();
            ocrText = event.txt;
            new MaterialDialog.Builder(myContext)
                    .title("识别结果")
                    .content("证件号码为：" + ocrText)
                    .negativeText("错误")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            capture.setEnabled(true);
                            capture.setText("重新识别");
                        }
                    })
                    .positiveText("正确")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (isReturn) {
                                returnResult(ocrText);
                            } else
                                capture.setEnabled(false);
                        }
                    })
                    .build().show();
        } else {
            Toast.makeText(this, "无识别区域，请移动手机", Toast.LENGTH_SHORT).show();
        }
        isOperOver = true;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void operPhotoEvent(OperPhotoEvent event) {
//        isOperOver = event.size == 0;
//        if (event.size > 0) {
//            //有了结果
//            ArrayList<String> files = event.files;
//            if (!mIsBound) {
//                toast(myContext, "远程服务未绑定");
//                return;
//            }
//            if (files.isEmpty()) {
//                toast(myContext, "识别文件不存在");
//                return;
//            }
//            ArrayList<String> base64s = new ArrayList<>();
//            for (String f : files) {
//                File file = new File(f);
//                long len = file.length();
//                byte[] b = new byte[(int) len];
//                try {
//                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
//                    in.read(b);
//                } catch (IOException e) {
//                    toast(myContext, "文件读取出现错误");
//                    return;
//                }
//                String s = Base64.encodeToString(b, Base64.DEFAULT);
//                //Log.e(TAG, s);
//                base64s.add(s);
//            }
//            Message msg = new Message();
//            msg.what = 3;
//            Bundle data = new Bundle();
//            data.putStringArrayList("file", base64s);
//            data.putString("catalog", "sfzh");
//            data.putBoolean("isSign", true);
//            data.putBoolean("isBase64", true);
//            msg.setData(data);
//            try {
//                mService.send(msg);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                toast(myContext, "识别服务调取错误");
//                return;
//            }
//            Toast.makeText(this, "有了结果", Toast.LENGTH_SHORT).show();
//            mCamera.stopPreview();
//            //分割并编码BASE64，参加识别
////            if (TextUtils.isEmpty(event.filePath)) {
////                Toast.makeText(this, "文件名是空", Toast.LENGTH_SHORT).show();
////                return;
////            }
////            Mat id = Imgcodecs.imread(event.filePath);
////            List<Mat> list = OperPhotoThread.segIdNumber(id, AndroidCameraExample.this);
////            String dir = getExternalFilesDir(null).getAbsolutePath();
////            if (list != null && !list.isEmpty()) {
////                Toast.makeText(this, "文件个数" + list.size(), Toast.LENGTH_SHORT).show();
////                int i = 1;
////                for (Mat mat : list) {
////                    Imgcodecs.imwrite(dir + "/dfzh_" + (i++) + ".jpg", mat);
////                }
////            } else
////                Toast.makeText(this, "文件个数为0", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "无识别区域，请移动手机", Toast.LENGTH_SHORT).show();
//        }
//    }


    //---------------------------------------------------------------------------
    boolean mIsBound;
    Messenger mService = null;
    final Messenger mMessage = new Messenger(new IncomingHandler());

    private String ocrText = "";


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    Bundle data = msg.getData();
                    List<String> texts = data.getStringArrayList("text");
                    if (texts != null && !texts.isEmpty()) {
                        String text = "";
                        for (String s : texts)
                            text += s;
                        ocrText = text;
                        new MaterialDialog.Builder(myContext)
                                .title("识别结果")
                                .content("证件号码为：" + ocrText)
                                .negativeText("错误")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        capture.setEnabled(true);
                                        capture.setText("重新识别");
                                    }
                                })
                                .positiveText("正确")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if (isReturn) {
                                            returnResult(ocrText);
                                        } else
                                            capture.setEnabled(false);
                                    }
                                })
                                .build().show();
                        toast(myContext, text);
                    } else
                        toast(myContext, "文字识别错误");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void returnResult(String txt) {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putString("txt", txt);
        i.putExtras(b);
        JbywPhotoOcrActivity.this.setResult(Activity.RESULT_OK, i);
        JbywPhotoOcrActivity.this.finish();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onBindingDied(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mIsBound = true;
            mService = new Messenger(service);
            Message msg = Message.obtain(null, 1);
            msg.replyTo = mMessage;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            toast(myContext, "远程服务已连接");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            toast(myContext, "远程服务已断开");
        }
    };

    private void toast(Context myContext, String s) {
        Toast.makeText(myContext, s, Toast.LENGTH_LONG).show();
    }

    void doBindService() {
        Log.e(TAG, "开始绑定服务");
        final Intent intent = new Intent();
        //intent.setAction("com.ntjxj.cardocr.SERVICE");
        //intent.setPackage("com.ntjxj.cardocr");
        intent.setComponent(new ComponentName("com.ntjxj.cardocr", "com.ntjxj.cardocr.OcrService"));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //startService(intent);
    }
}
