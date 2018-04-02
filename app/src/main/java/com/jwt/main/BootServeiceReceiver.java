package com.jwt.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootServeiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, MainReferService.class));
	}

}
