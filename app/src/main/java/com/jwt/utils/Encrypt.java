package com.jwt.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Encrypt {
	//public static byte[] iv;

	private static Encrypt aes = null;

	//public static byte[] key1;

	private Encrypt() {
		//iv = Base64.decode("CIojrZT9KU2hDBqT0RiMgQ==");
		//key1 = Base64.decode("E0XHaQEd4yCHXbLeScT6HxdxTYbdqpq72VEvuUJvJG4=");
	}

	public static synchronized Encrypt getInstance() {
		if (aes == null) {
			aes = new Encrypt();
		}
		return aes;
	}

	//public String encrypt(String msg) {
	//
	//	return encrypt(msg.getBytes());
	//}

//	public String encrypt(byte[] msg) {
//		String str = "";
//		try {
//			KeyGenerator kgen = KeyGenerator.getInstance("AES");
//			kgen.init(128, new SecureRandom(key1));
//			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
//			SecretKey key = kgen.generateKey();
//			Cipher ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
//			str = asHex(ecipher.doFinal(msg));
//		} catch (BadPaddingException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (NoSuchPaddingException e) {
//			e.printStackTrace();
//		} catch (InvalidKeyException e) {
//			e.printStackTrace();
//		} catch (InvalidAlgorithmParameterException e) {
//			e.printStackTrace();
//		} catch (IllegalBlockSizeException e) {
//			e.printStackTrace();
//		}
//		return str;
//
//	}

//	public String decrypt(String value) {
//		try {
//			KeyGenerator kgen = KeyGenerator.getInstance("AES");
//			kgen.init(128, new SecureRandom(key1));
//			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
//			SecretKey key = kgen.generateKey();
//			Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
//			return new String(dcipher.doFinal(asBin(value)));
//		} catch (BadPaddingException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (NoSuchPaddingException e) {
//			e.printStackTrace();
//		} catch (InvalidKeyException e) {
//			e.printStackTrace();
//		} catch (InvalidAlgorithmParameterException e) {
//			e.printStackTrace();
//		} catch (IllegalBlockSizeException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}

//	private String asHex(byte buf[]) {
//		StringBuffer strbuf = new StringBuffer(buf.length * 2);
//		int i;
//
//		for (i = 0; i < buf.length; i++) {
//			if (((int) buf[i] & 0xff) < 0x10)
//				strbuf.append("0");
//
//			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
//		}
//
//		return strbuf.toString();
//	}

//	private byte[] asBin(String src) {
//		if (src.length() < 1)
//			return null;
//		byte[] encrypted = new byte[src.length() / 2];
//		for (int i = 0; i < src.length() / 2; i++) {
//			int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
//			int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
//
//			encrypted[i] = (byte) (high * 16 + low);
//		}
//		return encrypted;
//	}

	public String hashEncrypt(String source,String codeName) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		byte[] bt1 = source.getBytes(codeName);
		byte[] bt2;
		bt2 = new byte[bt1.length - 2];
		for (int i = 2; i < bt1.length; i++) {
			bt2[i - 2] = bt1[i];
		}
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] bt = md.digest(bt2);

		return bytes2Hex(bt);
	}
	
	
	//哈希加密方法
	public String hashEncrypt(String source) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		// 处理这处是为了保持与原MOBILE程序能加出一样的结果出来
		// JAVA转为BYTE数组之后比C#前面能多一个 -1 -2 两个字节
		// 所以在这先去掉
		//byte[] bt1 = source.getBytes("Unicode");
		return hashEncrypt(source,"Unicode");

	}
	//将字节数组转换为十六进制字符串
	private String bytes2Hex(byte[] b) {
		String result = "";
		int bInt;
		for (int i = 0; i < b.length; i++) {
			bInt = b[i];
			//JAVA里的BYTE是带符号的，先转为与C#一样的无符号表示形式
			//然后转为十六进制
			if (bInt < 0) {
				bInt = bInt + 256;
			}
			String hex = Integer.toHexString(bInt & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			result = result + hex.toUpperCase();
		}
		return result;
	}

}
