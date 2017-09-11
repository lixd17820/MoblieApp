package com.jwt.utils;

import android.text.TextUtils;

public class IDCard {
	// wi =2(n-1)(mod 11)
	private final static int[] wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };

	// verify digit
	private final static int[] vi = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };


	// verify
	public static boolean Verify(String idcard) {
		if (idcard.length() == 15 && TextUtils.isDigitsOnly(idcard)) {
			idcard = uptoeighteen(idcard);
		}
		if (idcard.length() != 18 || !TextUtils.isDigitsOnly(idcard.substring(0,17))) {
			return false;
		}
        idcard = idcard.toUpperCase();
		String verify = idcard.substring(17, 18);
		if (verify.equals(getVerify(idcard))) {
			return true;
		}
		return false;
	}

	// get verify
	public static String getVerify(String eightcardid) {
		int remaining = 0;

		if (eightcardid.length() == 18) {
			eightcardid = eightcardid.substring(0, 17);
		}

		if (eightcardid.length() == 17) {
			int sum = 0;
			for (int i = 0; i < 17; i++) {
				String k = eightcardid.substring(i, i + 1);
				sum += (wi[i] * Integer.valueOf(k));
			}
			remaining = sum % 11;
		}

		return remaining == 2 ? "X" : String.valueOf(vi[remaining]);
	}

	// 15 update to 18
	public static String uptoeighteen(String fifteencardid) {
		String eightcardid = fifteencardid.substring(0, 6);
		eightcardid = eightcardid + "19";
		eightcardid = eightcardid + fifteencardid.substring(6, 15);
		eightcardid = eightcardid + getVerify(eightcardid);
		return eightcardid;
	}

}
