package de.schmaun.sosimplecounter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Locale;

public class CounterWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.counter_widget);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        views.setImageViewBitmap(R.id.widget_content, createContent(context, options));

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_content, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static Bitmap createContent(Context context, Bundle options) {
        CounterService counterService = new CounterService(context);
        Counter counter = counterService.loadFromPreferences();
        int days = counterService.getDays(counter);
        String counterUnit = context.getResources().getQuantityString(R.plurals.counter_unit, days);

        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);

        float density = context.getResources().getDisplayMetrics().density;
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
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

