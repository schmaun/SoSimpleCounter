package de.schmaun.sosimplecounter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivityFragment extends Fragment {
    private View view;

    public EditActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit, container, false);

        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        Button clearButton = (Button) view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        CounterService counterService = new CounterService(getContext());

        TextView editName = (TextView) view.findViewById(R.id.editCounterName);
        DatePicker editDatePicker = (DatePicker) view.findViewById(R.id.editCounterDate);

        Counter counter = counterService.loadFromPreferences();

        if (!counter.name.equals("")) {
            editName.setText(counter.name);
            editDatePicker.updateDate(counter.year, counter.month, counter.day);
        }

        return view;
    }

    private void save() {
        TextView editName = (TextView) view.findViewById(R.id.editCounterName);
        DatePicker editDatePicker = (DatePicker) view.findViewById(R.id.editCounterDate);

        if (editName.getText().length() == 0) {
            editName.setError(getString(R.string.error_no_name));
            Toast toast = Toast.makeText(getContext(), R.string.toast_error_no_name, Toast.LENGTH_LONG);
            toast.show();
        } else {
            CounterService counterService = new CounterService(getContext());
            counterService.saveToPreferences(
                    new Counter(
                            editName.getText().toString(),
                            editDatePicker.getYear(),
                            editDatePicker.getMonth(),
                            editDatePicker.getDayOfMonth()
                    )
            );

            updateWidgets();

            getActivity().finish();
        }
    }

    private void clear() {
        CounterService counterService = new CounterService(getContext());
        counterService.saveToPreferences(new Counter());

        updateWidgets();

        getActivity().finish();
    }

    private void updateWidgets() {
        Intent intent = new Intent(getContext(), CounterWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), CounterWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        getContext().sendBroadcast(intent);
    }
}
