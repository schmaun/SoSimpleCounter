package de.schmaun.sosimplecounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivityFragment extends Fragment {
    private View view;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        Button button = (Button) view.findViewById(R.id.editButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent intent = new Intent(context, EditActivity.class);
                context.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        CounterService counterService = new CounterService(getContext());
        Counter counter = counterService.loadFromPreferences();

        TextView nameTextView = (TextView) view.findViewById(R.id.counterName);
        TextView dateTextView = (TextView) view.findViewById(R.id.counterDate);
        TextView resultTextView = (TextView) view.findViewById(R.id.counterResult);
        TextView currentCounterHeadline = (TextView) view.findViewById(R.id.currentCounterHeadline);

        java.text.DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getContext());

        Calendar calendar = Calendar.getInstance();
        calendar.set(counter.year, counter.month, counter.day);
        dateFormat.setCalendar(calendar);

        nameTextView.setText(counter.name);
        dateTextView.setText("");
        resultTextView.setText("");
        currentCounterHeadline.setVisibility(View.INVISIBLE);

        if (!counter.name.equals("")) {
            dateTextView.setText(dateFormat.format(calendar.getTime()));

            int days = counterService.getDays(counter);
            String counterResult = getResources().getQuantityString(R.plurals.counter_result, days, days);

            resultTextView.setText(counterResult);

            currentCounterHeadline.setVisibility(View.VISIBLE);
        }
    }
}
