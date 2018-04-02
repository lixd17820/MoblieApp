package com.jwt.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.jwt.event.OcrEvent;
import com.jwt.event.OperPhotoEvent;
import com.jwt.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OperPhotoThread extends Thread {
    private Mat in;
    //private int blurSize;
    //private int morphW, morphH;
    //private int sfzhOpen = 4, drvOpen = 5;
    private double sfzhGoodBl = 16;
    private String smallFile;
    private int catalog;
    private Context context;
    private static final String TAG = "OperPhotoThread";

    public OperPhotoThread() {

    }

    public OperPhotoThread(Context context) {
        this.context = context;
    }

    public void doStart(Mat in, String smallFile, int catalog) {
        this.smallFile = smallFile;
        this.catalog = catalog;
        this.in = in;
        start();
    }

    @Override
    public void run() {
        OcrEvent event = new OcrEvent();
        Mat out = new Mat();
        int size = 0;
        ArrayList<String> files = new ArrayList<>();
        if (catalog == 0) {
            size = sobelSfzhOper(in, out, 4);
            //if (size == 0) {
            //    size = sobelSfzhOper(in, out, 6);
            //}
            if (size > 0) {
                Imgcodecs.imwrite(smallFile, out);
                List<Mat> mats = segIdNumber(out);
                File dir = context.getExternalFilesDir(null);

                if (mats != null && mats.size() == 18) {
                    Bitmap[] maps = new Bitmap[18];
                    for (int i = 0; i < 18; i++) {
//                        String file = dir + "/seg_sfzh_" + (i++) + ".jpg";
//                        Imgcodecs.imwrite(file, mat);
//                        files.add(file);
                        Mat mat = mats.get(i);
                        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGBA, 4);
                        Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(mat, bmp);
                        maps[i] = bmp;
                    }
                    OcrThread t = new OcrThread(maps, "sfzh", true);
                    String result = t.recText();
                    event.txt = result;
                    event.isOk = true;
                    EventBus.getDefault().post(event);
                    return;
                }
            }
        }
//        OperPhotoEvent event = new OperPhotoEvent();
//        event.filePath = smallFile;
//        event.files = files;
//        event.isOk = size > 0;
//        event.size = size;
        EventBus.getDefault().post(event);
    }

    public int sobelSfzhOper(Mat in, Mat out, int openSize) {
        minArea = in.width() * in.height() / 500;
        Mat mat_blur = new Mat();
        in.copyTo(mat_blur);
        Imgproc.GaussianBlur(mat_blur, mat_blur, new Size(5, 5),
                Core.BORDER_DEFAULT);
        Mat mat_gray = new Mat();
        if (mat_blur.channels() == 3)
            Imgproc.cvtColor(mat_blur, mat_gray, Imgproc.COLOR_RGB2GRAY);
        else
            mat_gray = mat_blur;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;

        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();

        Imgproc.Sobel(mat_gray, grad_x, ddepth, 1, 0, 3, scale, delta,
                Core.BORDER_DEFAULT);
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Imgproc.Sobel(mat_gray, grad_y, ddepth, 0, 1, 3, scale, delta,
                Core.BORDER_DEFAULT);
        Core.convertScaleAbs(grad_y, abs_grad_y);

        Mat grad = new Mat();
        Core.addWeighted(abs_grad_x, (double) 1, abs_grad_y, (double) 0, 0.0, grad);
        Mat mat_threshold = new Mat();
        Imgproc.threshold(grad, mat_threshold, 0, 255, Imgproc.THRESH_OTSU
                + Imgproc.THRESH_BINARY);
        FileUtil.debugImage(context, "debug_threshold.jpg", mat_threshold);
        Size size1 = new Size(openSize, openSize);
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size1);
        Imgproc.morphologyEx(mat_threshold, mat_threshold, Imgproc.MORPH_OPEN,
                element1);
        Size size = new Size(41, 21);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);
        Imgproc.morphologyEx(mat_threshold, mat_threshold, Imgproc.MORPH_CLOSE,
                element);
        FileUtil.debugImage(context, "debug_morphology.jpg", mat_threshold);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mat_threshold, contours, new Mat(),
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f dst = new MatOfPoint2f();
            contours.get(i).convertTo(dst, CvType.CV_32F);
            RotatedRect mr = Imgproc.minAreaRect(dst);
            rects.add(mr);
        }
        filterSamllArea(rects);
        if (catalog == 0) {
            filterSfzhRects(rects, in);
        }
        if (rects.size() == 0)
            return 0;
        Log.e(TAG, "识别结果：" + rects.size());
        RotatedRect rectResult = rects.get(0);
        if (rects.size() > 1) {
            // 如果大于1个，找出最接近标准比例的，定为16
            double blc = Double.MAX_VALUE;
            int index = 0;
            for (int i = 0; i < rects.size(); i++) {
                RotatedRect rect = rects.get(i);
                double bl = Math.abs(Math
                        .max(rect.size.width, rect.size.height)
                        / Math.min(rect.size.width, rect.size.height)
                        - sfzhGoodBl);
                if (bl < blc) {
                    index = i;
                    blc = bl;
                }
            }
            rectResult = rects.get(index);
        }

        // 前提是水平的，如果小于-45则加90变正
        double rAngle = rectResult.angle < -45 ? 90 + rectResult.angle
                : rectResult.angle;
        Mat rotmat = Imgproc.getRotationMatrix2D(rectResult.center, rAngle, 1);
        Mat img_rotated = new Mat();
        Imgproc.warpAffine(in, img_rotated, rotmat, in.size()); // CV_INTER_CUBIC

        // 根据中点和宽高求图形
        Size rsize = rectResult.size;
        double width = Math.max(rsize.width, rsize.height);
        double height = Math.min(rsize.width, rsize.height);
        double yd = height / (catalog == 0 ? 3 : 5);
        Point center = rectResult.center;
        Point leftTop = offsetPoint(center, 0 - width / 2 - yd, 0 - height / 2
                - yd);
        Point rightBottom = offsetPoint(center, width / 2 + yd, height / 2 + yd);
        Log.e(TAG, "识别结果：" + leftTop.toString() + "/" + rightBottom.toString());
        Rect sub = new Rect(leftTop, rightBottom);
        Mat temp = new Mat(img_rotated, sub);
        temp.copyTo(out);
//        // 截取全身份证
//        if (catalog == 0) {
//            double bl = width / 45;
//            double cardW = bl * 86;
//            double cardH = bl * 54;
//            Point cardLt = offsetPoint(leftTop, 0 - bl * 30, 0 - bl * 44);
//            Point cardRb = offsetPoint(cardLt, cardW, cardH);
//            Rect cardRect = new Rect(cardLt, cardRb);
//            FileUtil.debugImage(context, "debug_card.jpg", new Mat(
//                    img_rotated, cardRect));
//        }
        return rects.size();
    }

//    public int sobelOper(Mat in, int blurSize, int morphW, int morphH,
//                         Mat out, int catalog) {
//        Mat mat_blur = new Mat();
//        in.copyTo(mat_blur);
//        Imgproc.GaussianBlur(mat_blur, mat_blur, new Size(blurSize, blurSize),
//                Core.BORDER_DEFAULT);
//        Mat mat_gray = new Mat();
//        if (mat_blur.channels() == 3)
//            Imgproc.cvtColor(mat_blur, mat_gray, Imgproc.COLOR_RGB2GRAY);
//        else
//            mat_gray = mat_blur;
//        int scale = 1;
//        int delta = 0;
//        int ddepth = CvType.CV_16S;
//
//        Mat grad_x = new Mat(), grad_y = new Mat();
//        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
//
//        Imgproc.Sobel(mat_gray, grad_x, ddepth, 1, 0, 3, scale, delta,
//                Core.BORDER_DEFAULT);
//        Core.convertScaleAbs(grad_x, abs_grad_x);
//        Imgproc.Sobel(mat_gray, grad_y, ddepth, 0, 1, 3, scale, delta,
//                Core.BORDER_DEFAULT);
//        Core.convertScaleAbs(grad_y, abs_grad_y);
//
//        Mat grad = new Mat();
//        Core.addWeighted(abs_grad_x, (double) 1, abs_grad_y, (double) 0, 0.0, grad);
//        Mat mat_threshold = new Mat();
//        Imgproc.threshold(grad, mat_threshold, 0, 255, Imgproc.THRESH_OTSU
//                + Imgproc.THRESH_BINARY);
//        FileUtil.debugImage(context,"debug_threshold.jpg", mat_threshold);
//        Size size1 = new Size(sfzhOpen, sfzhOpen);
//        if (catalog == 1) {
//            size1 = new Size(drvOpen, drvOpen);
//        }
//        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size1);
//        Imgproc.morphologyEx(mat_threshold, mat_threshold, Imgproc.MORPH_OPEN,
//                element1);
//        Size size = new Size(morphW, morphH);
//        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);
//        Imgproc.morphologyEx(mat_threshold, mat_threshold, Imgproc.MORPH_CLOSE,
//                element);
//        FileUtil.debugImage(context,"debug_morphology.jpg", mat_threshold);
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours(mat_threshold, contours, new Mat(),
//                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
//        List<RotatedRect> rects = new ArrayList<RotatedRect>();
//        for (int i = 0; i < contours.size(); i++) {
//            MatOfPoint2f dst = new MatOfPoint2f();
//            contours.get(i).convertTo(dst, CvType.CV_32F);
//            RotatedRect mr = Imgproc.minAreaRect(dst);
//            rects.add(mr);
//        }
//        filterSamllArea(rects);
//        if (catalog == 0) {
//            filterSfzhRects(rects, in);
//        }
//
//        if (rects.size() == 0)
//            return 0;
//        RotatedRect rectResult = rects.get(0);
//        if (rects.size() > 1) {
//            // 如果大于1个，找出最接近标准比例的，定为16
//            double blc = Double.MAX_VALUE;
//            int index = 0;
//            for (int i = 0; i < rects.size(); i++) {
//                RotatedRect rect = rects.get(i);
//                double bl = Math.abs(Math
//                        .max(rect.size.width, rect.size.height)
//                        / Math.min(rect.size.width, rect.size.height)
//                        - sfzhGoodBl);
//                if (bl < blc) {
//                    index = i;
//                    blc = bl;
//                }
//            }
//            rectResult = rects.get(index);
//        }
//
//        // 前提是水平的，如果小于-45则加90变正
//        double rAngle = rectResult.angle < -45 ? 90 + rectResult.angle
//                : rectResult.angle;
//        Mat rotmat = Imgproc.getRotationMatrix2D(rectResult.center, rAngle, 1);
//        Mat img_rotated = new Mat();
//        Imgproc.warpAffine(in, img_rotated, rotmat, in.size()); // CV_INTER_CUBIC
//
//        // 根据中点和宽高求图形
//        Size rsize = rectResult.size;
//        double width = Math.max(rsize.width, rsize.height);
//        double height = Math.min(rsize.width, rsize.height);
//        double yd = height / (catalog == 0 ? 3 : 5);
//        Point center = rectResult.center;
//        Point leftTop = offsetPoint(center, 0 - width / 2 - yd, 0 - height / 2
//                - yd);
//        Point rightBottom = offsetPoint(center, width / 2 + yd, height / 2 + yd);
//        Rect sub = new Rect(leftTop, rightBottom);
//        Mat temp = new Mat(img_rotated, sub);
//        temp.copyTo(out);
//        // 截取全身份证
//        if (catalog == 0) {
//            double bl = width / 45;
//            double cardW = bl * 86;
//            double cardH = bl * 54;
//            Point cardLt = offsetPoint(leftTop, 0 - bl * 30, 0 - bl * 44);
//            Point cardRb = offsetPoint(cardLt, cardW, cardH);
//            Rect cardRect = new Rect(cardLt, cardRb);
//            FileUtil.debugImage(context,"debug_card.jpg", new Mat(
//                    img_rotated, cardRect));
//        }
//        return rects.size();
//    }

    public static double minArea = 10000;
    public static DecimalFormat df = new DecimalFormat("#.##");

    private static void filterSamllArea(List<RotatedRect> rects) {
        for (int i = rects.size() - 1; i >= 0; i--) {
            RotatedRect rect = rects.get(i);
            if (rect.size.width * rect.size.height < minArea)
                rects.remove(i);
        }
    }

    private static void filterSfzhRects(List<RotatedRect> rects, Mat in) {
        for (int i = rects.size() - 1; i >= 0; i--) {
            RotatedRect rect = rects.get(i);
            if (!verifySfzhSize(rect, in, i))
                rects.remove(i);
        }
    }

    private static boolean verifySfzhSize(RotatedRect mr, Mat in, int index) {
        Size size = mr.size;
        double angle = mr.angle;
        double width = size.width;
        double height = size.height;
        // 验证是否为竖直方向
        boolean isH = false;
        if (angle > 45 || angle < -45) {
            isH = width < height;
        } else {
            isH = height < width;
        }
        int matWidth = in.width();
        double w = Math.max(width, height);
        double h = Math.min(width, height);
        System.out.println(index + "：  角度： " + df.format(angle) + "  宽："
                + df.format(width) + "  高：" + df.format(height) + "  比例："
                + df.format(w / h) + "  图宽：" + df.format(matWidth));
        if (!isH) {
            return false;
        }
        // double area = size.width * size.height;
        // int matArea = in.width() * in.height();

        // mr.size.width = w;
        // mr.size.height = h;
        // mr.angle = 90;
        double bl = w / h;
        double maxBl = 19;
        double minBl = 14;

        boolean isOk = false;
        boolean isBl = (bl > minBl && bl < maxBl);
        if (isBl) {
            // Log.e("VerifySize", matWidth + "/" + w + "angle: " + angle + "/"
            // + width + "/" + height
            // + "/" + (w / h) + "/" + matWidth);
            isOk = w * 6 > matWidth;
        }
        return isOk;
    }

    private boolean verifySizes(RotatedRect mr, Mat in, int catalog) {
        Size size = mr.size;
        double angle = mr.angle;
        double width = size.width;
        double height = size.height;
        // 验证是否为竖直方向
        boolean isH = false;
        if (angle > 45 || angle < -45) {
            isH = width < height;
        } else {
            isH = height < width;
        }
        if (!isH) {
            return false;
        }
        // double area = size.width * size.height;
        // int matArea = in.width() * in.height();
        int matWidth = in.width();
        double w = Math.max(width, height);
        double h = Math.min(width, height);
        // mr.size.width = w;
        // mr.size.height = h;
        // mr.angle = 90;
        double bl = w / h;
        double maxBl = 17;
        double minBl = 14;
        if (catalog == 1) {
            maxBl = 11;
            minBl = 10;
        }
        Log.e("VerifySize", matWidth + "/" + w + "angle: " + angle + "/" + width + "/" + height
                + "/" + (w / h) + "/" + matWidth);
        boolean isOk = false;
        boolean isBl = (bl > minBl && bl < maxBl);
        if (isBl) {
            //Log.e("VerifySize", matWidth + "/" + w + "angle: " + angle + "/" + width + "/" + height
            //        + "/" + (w / h) + "/" + matWidth);
            isOk = w * 6 > matWidth;
        }
        return isOk;
    }

    private Point offsetPoint(Point p, double x, double y) {
        Point np = new Point();
        np.x = p.x + x;
        np.y = p.y + y;
        return np;
    }

    public List<Mat> segIdNumber(Mat in) {
        int blurSize = 3;
        int morphW = 5;
        int morphH = 5;
        List<Mat> mats = new ArrayList<Mat>();
        Mat mat_blur = new Mat();
        in.copyTo(mat_blur);
        Imgproc.GaussianBlur(mat_blur, mat_blur, new Size(blurSize, blurSize),
                Core.BORDER_DEFAULT);
        Mat mat_gray = new Mat();
        if (mat_blur.channels() == 3)
            Imgproc.cvtColor(mat_blur, mat_gray, Imgproc.COLOR_RGB2GRAY);
        else
            mat_gray = mat_blur;
        //FileUtil.debugImage(context, "debug_id_gray.jpg", mat_gray);
        Mat mat_threshold1 = new Mat();
        Imgproc.threshold(mat_gray, mat_threshold1, 0, 255, Imgproc.THRESH_OTSU
                + Imgproc.THRESH_BINARY);
        //FileUtil.debugImage(context, "debug_id_threshold1.jpg",
        //        mat_threshold1);
        Mat mat_threshold2 = new Mat();
        Imgproc.threshold(mat_threshold1, mat_threshold2, 0, 255,
                Imgproc.THRESH_BINARY_INV);
        // + Imgproc.THRESH_BINARY);
        //FileUtil.debugImage(context, "debug_id_threshold2.jpg",
        //        mat_threshold2);
        Size sizec = new Size(1, 1);
        Imgproc.morphologyEx(mat_threshold2, mat_threshold2, Imgproc.MORPH_CLOSE,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, sizec));
        Size size = new Size(morphW, morphH);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);
        Imgproc.morphologyEx(mat_threshold2, mat_threshold2, Imgproc.MORPH_CLOSE,
                element);
        //FileUtil.debugImage(context, "debug_id_morph.jpg", mat_threshold2);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mat_threshold2, contours, new Mat(),
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        //       Mat result = new Mat();
//        if (FileUtil.debug) {
//            // Draw red contours on the source image
//            in.copyTo(result);
//            Imgproc.drawContours(result, contours, -1, new Scalar(0, 0, 255,
//                    255));
//            FileUtil.debugImage(context, "debug_Contours.jpg", result);
//        }

        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f dst = new MatOfPoint2f();
            contours.get(i).convertTo(dst, CvType.CV_32F);
            RotatedRect mr = Imgproc.minAreaRect(dst);
            rects.add(mr);
        }
        filterLitterArea(rects, in);
//        System.out.println(rects.size());
        // ------------------画边框线开始-----------------------------
//        Mat lines = new Mat();
//        in.copyTo(lines);
//        int c = 0;
//        for (int i = 0; i < rects.size(); i++) {
//            RotatedRect minRect = rects.get(i);
//            if (FileUtil.debug) {
//                Point[] rect_points = new Point[4];
//                minRect.points(rect_points);
//                for (int j = 0; j < 4; j++) {
//                    Point pt1 = rect_points[j];
//                    Point pt2 = rect_points[(j + 1) % 4];
//                    if (j == 0)
//                        Imgproc.line(lines, pt1, pt2, new Scalar(255, 0, 255,
//                                255), 4, 8, 0);
//                    else if (j == 1) {
//                        Imgproc.line(lines, pt1, pt2, new Scalar(255, 255, 0,
//                                255), 4, 8, 0);
//                    } else
//                        Imgproc.line(lines, pt1, pt2, new Scalar(0, 255, 255,
//                                255), 4, 8, 0);
//                    // System.out.println(pt1);
//                }
//            }
//            Imgproc.putText(lines, "" + (i), minRect.center,
//                    Core.FONT_HERSHEY_SIMPLEX, 2.0,
//                    new Scalar(0, 255, 255, 255));
//        }
//        FileUtil.debugImage(context, "debug_rect.jpg", lines);
        Collections.sort(rects, new Comparator<RotatedRect>() {

            @Override
            public int compare(RotatedRect o1, RotatedRect o2) {
                return (int) (o1.center.x - o2.center.x);
            }

        });
        for (int i = 0; i < rects.size(); i++) {
            RotatedRect minRect = rects.get(i);
            Point[] rect_points = new Point[4];
            minRect.points(rect_points);
            double maxX = rect_points[0].x;
            double minX = rect_points[0].x;
            for (int j = 1; j < 4; j++) {
                Point p = rect_points[j];
                maxX = Math.max(maxX, p.x);
                minX = Math.min(minX, p.x);
            }
            // System.out.println(minX + "--" + maxX);
            double w = (double) in.width() / 18;
            double add = (w - (maxX - minX)) / 2 * 0.9;
            //System.out.println(w + "/" + (maxX - minX));
            int x = (int) Math.round(minX - add);
            x = x < 0 ? 0 : x;
            int width = (int) Math.ceil(maxX - minX + 2 * add);
            width = x + width > in.width() ? (in.width() - x) : width;
            Rect subSize = new Rect(x, 0, width, in.height());
            Mat sub = new Mat(mat_threshold1, subSize);
            //System.out.println(sub.width());
            mats.add(sub);
            //FileUtil.debugImage(context, "sub" + i + ".jpg", sub);
        }

        return mats;
    }

    private static void filterLitterArea(List<RotatedRect> rects, Mat in) {
        double w = (double) in.width() / 256;
        double h = (double) in.height();
        double min = w * h;
        //System.out.println("min: " + min);
        for (int i = rects.size() - 1; i >= 0; i--) {
            RotatedRect rect = rects.get(i);
            //System.out.println(rect);
            //double he = Math.max(rect.size.width, rect.size.height);
            // System.out.println(rect.size + "/" + (rect.size.width *
            // rect.size.height));
            if (rect.size.width * rect.size.height < min)
                //if (he < h / 2)
                rects.remove(i);
        }
    }

}

