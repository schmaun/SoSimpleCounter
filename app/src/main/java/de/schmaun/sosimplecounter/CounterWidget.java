package de.schmaun.sosimplecounter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class CounterWidget extends AppWidgetProvider {
    private static final String ACTION_SCHEDULED_UPDATE = "de.schmaun.sosimplecounter.CounterWidget.SCHEDULED_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CounterWidget", "onReceive: " + intent.getAction());

        if (intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, CounterWidget.class));
            onUpdate(context, manager, ids);
        }

        super.onReceive(context, intent);
    }


    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions)
    {
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("CounterWidget", "onEnabled");
        setAlarm(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d("CounterWidget", "onDisabled");

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingAlarmIntent = getPendingIntent(context);
        alarmManager.cancel(pendingAlarmIntent);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.counter_widget);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        views.setImageViewBitmap(R.id.widget_content, createContent(context, options));

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_content, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingAlarmIntent = getPendingIntent(context);
        alarmManager.cancel(pendingAlarmIntent);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);

        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingAlarmIntent);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, CounterWidget.class);
        alarmIntent.setAction(ACTION_SCHEDULED_UPDATE);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
    }

    private static Bitmap createContent(Context context, Bundle options) {
        CounterService counterService = new CounterService(context);
        Counter counter = counterService.loadFromPreferences();
        int days = counterService.getDays(counter);
        String counterUnit = context.getResources().getQuantityString(R.plurals.counter_unit, days);

        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);

        float density = context.getResources().getDisplayMetrics().density;

        if (maxWidth < 110) {
            maxWidth = 110;
        }
        if (maxHeight < 110) {
            maxHeight = 110;
        }

        Log.d("CounterWidget", String.format(Locale.getDefault(), "width: %s, height: %s", maxWidth, maxHeight));
        int targetWidth = (int)(maxWidth * density);
        int targetHeight = (int)(maxHeight * density);

        Bitmap myBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(myBitmap);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView nameTextView = getTextView(context);
        nameTextView.setText(counter.name);

        TextView counterResultTextView = getTextView(context);
        counterResultTextView.setText(String.format(Locale.getDefault(), "%d", days));
        counterResultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);

        TextView counterResultUnitTextView = getTextView(context);
        counterResultUnitTextView.setText(counterUnit);

        if (counter.font != null) {
            Typeface font = Typeface.createFromAsset(context.getAssets(), counter.font);
            nameTextView.setTypeface(font);
            counterResultTextView.setTypeface(font);
            counterResultUnitTextView.setTypeface(font);
        }

        layout.addView(nameTextView);
        layout.addView(counterResultTextView);
        layout.addView(counterResultUnitTextView);

        layout.setDrawingCacheEnabled(true);
        layout.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

        return layout.getDrawingCache();
    }

    @NonNull
    private static TextView getTextView(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0,0,0,0);
        TextView nameTextView = new TextView(context);
        nameTextView.setVisibility(View.VISIBLE);
        nameTextView.setTextColor(0xffffffff);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        nameTextView.setLayoutParams(layoutParams);
        nameTextView.setGravity(Gravity.CENTER);

        return nameTextView;
    }
}
