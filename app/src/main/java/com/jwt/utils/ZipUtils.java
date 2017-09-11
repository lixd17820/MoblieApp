package com.jwt.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void unzipFile(String zipFile, String unzipDir) {
        byte[] b = new byte[1024];
        File unDir = new File(unzipDir);
        try {
            ZipFile zip = new ZipFile(zipFile);
            Enumeration<? extends ZipEntry> em = zip.entries();
            while (em.hasMoreElements()) {
                ZipEntry entry = em.nextElement();
                if (entry.isDirectory()) {
                    new File(unDir, entry.getName()).mkdirs();
                    continue;
                }
                BufferedInputStream bis = new BufferedInputStream(
                        zip.getInputStream(entry));
                File file = new File(unDir, entry.getName());
                File parent = file.getParentFile();
                if (parent != null && !parent.exists())
                    parent.mkdirs();
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(file));
                int len = 0;
                while ((len = bis.read(b)) > 0) {
                    out.write(b, 0, len);
                }
                bis.close();
                out.close();
            }
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int zipDir(String dir, String dest) {
        int count = 0;
        byte[] b = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    new FileOutputStream(dest)));

            File iconDir = new File(dir);
            File[] icons = iconDir.listFiles();

            for (File icon : icons) {
                count++;
                ZipEntry entry = new ZipEntry(icon.getName());
                out.putNextEntry(entry);
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(icon));
                int len = 0;
                while ((len = in.read(b)) > 0) {
                    out.write(b, 0, len);
                }
                in.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static int unMegFile(String sour, String destDir) {
        int count = 0;
        File dest = new File(destDir);
        if (!dest.exists())
            dest.mkdirs();
        byte[] longByte = new byte[8];
        byte[] buffer = null;
        try {
            BufferedInputStream in = new BufferedInputStream(
                    new FileInputStream(sour));
            int i = 0;
            String fileName = "";
            while (in.read(longByte) > 0) {
                long len = TypeCenvert.byte2Long(longByte);
                buffer = new byte[(int) len];
                in.read(buffer);
                if (i % 2 == 0) {
                    // Read file name.
                    fileName = new String(buffer);
                } else {
                    File f = new File(destDir, fileName);
                    BufferedOutputStream out = new BufferedOutputStream(
                            new FileOutputStream(f));
                    out.write(buffer);
                    out.close();
                    count++;
                }
                i++;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void megFiles(String destFile, File... files) {
        byte[] b = null;
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(destFile));
            for (File file : files) {
                String fn = file.getName();
                byte[] fnByte = fn.getBytes();
                out.write(TypeCenvert.long2Byte(fnByte.length));
                out.write(fnByte);
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(file));
                long fileLn = file.length();
                b = new byte[(int) file.length()];
                in.read(b);
                in.close();
                out.write(TypeCenvert.long2Byte(fileLn));
                out.write(b);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int megDir(String dir, String dest, String suffix) {
        int count = 0;
        byte[] b = null;
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(dest));

            File iconDir = new File(dir);
            File[] icons = iconDir.listFiles();

            for (File icon : icons) {

                if (!icon.getName().endsWith(suffix))
                    continue;
                count++;
                String fn = icon.getName();
                byte[] fnByte = fn.getBytes();
                out.write(TypeCenvert.long2Byte(fnByte.length));
                out.write(fnByte);
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(icon));
                long fileLn = icon.length();
                b = new byte[(int) icon.length()];
                in.read(b);
                in.close();
                out.write(TypeCenvert.long2Byte(fileLn));
                out.write(b);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 数组转字符串大写
     *
     * @param tmp
     * @return
     */
    private static String byte2Hex(byte[] tmp) {
        String s = "";
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};// 用来将字节转换成16进制表示的字符
        // 用字节表示就是 16 个字节
        char[] str = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
        // 进制需要 32 个字符
        int k = 0;// 表示转换结果中对应的字符位置
        for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
            // 进制字符的转换
            byte byte0 = tmp[i];// 取第 i 个字节
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
            // 为逻辑右移，将符号位一起右移
            str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换

        }
        s = new String(str).toUpperCase();// 换后的结果转换为字符串

        return s;
    }

    /**
     * 获取文件的MD5值
     *
     * @param file
     * @return
     */
    public static String getFileMd5(File file) {
        String md5 = "";
        byte[] b = new byte[1024];
        try {
            MessageDigest dig = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            int len = 0;
            while ((len = in.read(b)) > 0) {
                dig.update(b, 0, len);
            }
            in.close();
            byte[] temp = dig.digest();
            md5 = byte2Hex(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 获取字节数组的MD5值
     *
     * @param source
     * @return
     */
    public static String getMD5(byte[] source) {
        String s = "";
        try {
            MessageDigest md = MessageDigest
                    .getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
            s += byte2Hex(tmp);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 将一段字符串转为MD5，加密
     *
     * @param s
     * @return
     */
    public static String getMD5(String s) {
        try {
            byte[] b = s.getBytes("utf-8");
            return getMD5(b);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
