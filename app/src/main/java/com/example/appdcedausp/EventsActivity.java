package com.example.appdcedausp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by yago_ on 19/01/2018.
 */

public class EventsActivity extends AppCompatActivity {
    TextView title;
    TextView description;
    CalendarView calendarView;
    long umDia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);
        Bundle extras = getIntent().getExtras();
        long time = System.currentTimeMillis();
        umDia = 86400000L;
        DateTime date = new DateTime(time);
        extras.putLong("date",time);

        if (extras != null) {
            title = findViewById(R.id.titleCalendar);
            title.setText(extras.getString("saida0"));

            description = findViewById(R.id.descriptionCalendar);
//            title.setText(extras.getString("saida0"));
        }

        calendarView = findViewById(R.id.calendarView);
        calendarView.setDate(time + umDia*2);
        Date today = new Date(calendarView.getDate());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date otherDay = new GregorianCalendar(i,i1,i2).getTime();
                //otherDay = new Date(otherDay.getTime() + 3*umDia);
                Toast.makeText(EventsActivity.this, "dia: "+ df.format(otherDay), Toast.LENGTH_SHORT).show();
                calendarView.setDate(otherDay.getTime());
                title.setText(df.format(new GregorianCalendar(i,i1,i2).getTime()));
                description.setText(DateFormat.getDateInstance(DateFormat.FULL,new Locale("pt","BR")).format(otherDay)
                        + "\n" + DateFormat.getDateInstance(DateFormat.FULL,new Locale("en","US")).format(otherDay)
                        + "\n" + DateFormat.getDateInstance(DateFormat.FULL,new Locale("ja","JP")).format(otherDay)
                        + "\n" + DateFormat.getDateInstance(DateFormat.FULL,new Locale("ru","RU")).format(otherDay)
                        + "\n" + DateFormat.getDateInstance(DateFormat.FULL,new Locale("ko","KR")).format(otherDay));
            }
        });
    }
}
