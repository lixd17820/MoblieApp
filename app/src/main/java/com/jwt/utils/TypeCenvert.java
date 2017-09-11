package com.jwt.utils;


public class TypeCenvert {
	public static float byte2Float(byte[] b) {
		int i = 0;
		for (int j = 0; j < b.length; j++) {
			i = i | (b[j] & 0xff) << (j * 8);
		}
		return Float.intBitsToFloat(i);
	}

	public static byte[] float2Byte(float f) {
		long l = Float.floatToRawIntBits(f);
		byte[] bf = new byte[4];
		for (int i = 0; i < bf.length; i++) {
			bf[i] = (byte) (l & 0xff);
			l = l >>> 8;
		}
		return bf;
	}

	public static byte[] double2Byte(double d) {
		long l = Double.doubleToRawLongBits(d);
		byte[] bf = new byte[8];
		for (int i = 0; i < bf.length; i++) {
			bf[i] = (byte) (l & 0xff);
			l = l >>> 8;
		}
		return bf;
	}

	public static double byte2Double(byte[] b) {
		long l = 0;
		for (int i = 0; i < b.length; i++) {
			long lt = b[i] & 0xff;
			lt = lt << (i * 8);
			l = l | lt;
		}
		return Double.longBitsToDouble(l);
	}

	public static byte[] ip2Byte(String ip) {
		// ip = ip.replaceAll(".", ",");
		// String[] ips = ip.split(",");
		int index = 0;
		int c = 0;
		String[] ips = new String[4];
		while (index != -1) {
			index = ip.indexOf(".");
			if (index == -1) {
				ips[c] = ip;
			} else {
				ips[c] = ip.substring(0, index);
				c++;
				ip = ip.substring(index + 1);
			}
		}
		byte[] b = new byte[4];
		for (int i = 0; i < ips.length; i++) {
			b[i] = (byte) ((Integer.valueOf(ips[i])) & 0xff);
		}
		return b;
	}

	public static String byte2Ip(byte[] b) {
		String ip = "";
		for (int i = 0; i < b.length; i++) {
			ip += String.valueOf((int) (b[i] & 0xff)) + ".";
		}
		return ip.substring(0, ip.length() - 1);
	}

	public static byte[] addByte(byte[] source, byte[] b) {
		byte[] n = new byte[source.length + b.length];
		for (int i = 0; i < source.length; i++) {
			n[i] = source[i];
		}
		for (int j = 0; j < b.length; j++) {
			n[source.length + j] = b[j];
		}
		return n;

	}

	public static byte[] long2Byte(long l) {
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = Long.valueOf(l & 0xff).byteValue();
			l = l >> 8;
		}
		return b;
	}

	public static long byte2Long(byte[] b) {
		long s = 0;
		for (int j = 0; j < b.length; j++) {
			long bt = b[j] & 0xff;
			bt = bt << (j * 8);
			s = s | bt;
		}
		return s;
	}

	public static byte[] subArray(byte[] source, int stPos, int endPos) {
		byte[] b = new byte[endPos - stPos];
		for (int i = 0; i < b.length; i++) {
			b[i] = source[stPos + i];
		}
		return b;
	}

	public static byte[] short2Byte(short l){
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = Integer.valueOf(l & 0xff).byteValue();
			l = (short)(l >> 8);
		}
		return b;
	}

	public static byte[] addByte(byte[] source, byte b) {
		byte[] n = new byte[source.length + 1];
		for (int i = 0; i < source.length; i++) {
			n[i] = source[i];
		}
		n[source.length] = b;
		return n;
	}

	public static byte[] int2Byte(int l) {
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] =  Integer.valueOf(l & 0xff).byteValue();
			l = l >> 8;
		}
		return b;
	}

}
