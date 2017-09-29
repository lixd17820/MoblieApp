package com.jwt.update;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.widget.RemoteViews;
import android.widget.Toast;

public class JwtAppWidgetProvider extends AppWidgetProvider {
	private RemoteViews remoteViews;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_jwt);
//		Time time = new Time();
//		time.setToNow();
		Intent intent = new Intent(context, JbywFxcActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.widet_btn, pendingIntent);
		Intent intent2 = new Intent(context, AcdTakePhotoActivity.class);
		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 1,
				intent2,PendingIntent.FLAG_CANCEL_CURRENT);
		//		Intent.FLAG_ACTIVITY_NEW_TASK);
		remoteViews.setOnClickPendingIntent(R.id.widet_btn_jtsg, pendingIntent2);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

}
