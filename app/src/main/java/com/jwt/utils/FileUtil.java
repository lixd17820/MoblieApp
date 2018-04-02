package com.jwt.utils;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

/**
 * Created by lixd1 on 2017/12/13.
 */

public class FileUtil {

    public static boolean debug = true;

    //public static final String dir = "/storage/emulated/0/DCIM/Camera/";

    public static void debugImage(Context context, String filename, Mat mat) {
        File f = context.getExternalFilesDir(null);
        if (debug)
            Imgcodecs.imwrite(f.getAbsolutePath() + "/" + filename, mat);

    }
}
