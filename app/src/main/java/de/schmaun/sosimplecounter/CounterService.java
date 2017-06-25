package de.schmaun.sosimplecounter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

class CounterService {
    private final Context context;

    public CounterService(Context context) {
        this.context = context;
    }

    public Counter loadFromPreferences() {
        Counter counter = new Counter();
        Calendar calendar = Calendar.getInstance();

        SharedPreferences sharedPref = getSharedPreferences();
        counter.name = sharedPref.getString(context.getString(R.string.preference_name), "");
        counter.year = sharedPref.getInt(context.getString(R.string.preference_date_year), calendar.get(Calendar.YEAR));
        counter.month = sharedPref.getInt(context.getString(R.string.preference_date_month), calendar.get(Calendar.MONTH));
        counter.day = sharedPref.getInt(context.getString(R.string.preference_date_day), calendar.get(Calendar.DAY_OF_MONTH));

        return counter;
    }

    public void saveToPreferences(Counter counter) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(R.string.preference_name), counter.name);
        editor.putInt(context.getString(R.string.preference_date_year), counter.year);
        editor.putInt(context.getString(R.string.preference_date_month), counter.month);
        editor.putInt(context.getString(R.string.preference_date_day), counter.day);
        editor.apply();
    }

    public int getDays(Counter counter) {
        Calendar currentDate = Calendar.getInstance();
        Calendar counterDate = Calendar.getInstance();
        counterDate.set(counter.year, counter.month, counter.day);

        long timeInMillis = counterDate.getTimeInMillis();
        long timeInMillisCurrent = currentDate.getTimeInMillis();
        return (int)((timeInMillis - timeInMillisCurrent) / (24*60*60*1000));
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
}
