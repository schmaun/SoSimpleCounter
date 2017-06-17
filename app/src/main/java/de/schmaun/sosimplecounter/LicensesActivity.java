package de.schmaun.sosimplecounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);

        TextView otherText = (TextView) findViewById(R.id.licenses_other);
        String[] licenses = getResources().getStringArray(R.array.licenses_other);

        for (String license : licenses) {
            otherText.setText(otherText.getText() + license + "\n----------\n");
        }

    }
}
