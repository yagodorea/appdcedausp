package com.dceusp.appdcedausp.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.Toast;

import com.dceusp.appdcedausp.R;
import com.dceusp.appdcedausp.utils.EventFragment;
import com.dceusp.appdcedausp.utils.FirebaseUtils;
import com.dceusp.appdcedausp.utils.GoogleUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.dceusp.appdcedausp.utils.Constants.*;


public class EventsActivity extends AppCompatActivity {

    private static final String TAG = EventsActivity.class.getName();

    Events events;

    EventFragment eventFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    TabHost tabHost;

    ScrollView scroller;
    LinearLayout fabBack;
    ProgressDialog progressDialog;

    boolean[] idLoaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando eventos...");

        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        TabHost.TabSpec spec = tabHost.newTabSpec("me");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Movimento Estudantil");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("festas");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Festas");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("institucional");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Institucional");
        tabHost.addTab(spec);

        getResultsFromApi(0);
        idLoaded = new boolean[]{true,false,false};

        addFragment(0);
        addFragment(1);
        addFragment(2);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                Log.d(TAG, "Shazam! ->onTabChanged: s: " + s);
                if (s.equals("me") && !idLoaded[0]) {
                    getResultsFromApi(0);
                    idLoaded[0] = true;
                }else if (s.equals("festas") && !idLoaded[1]) {
                    getResultsFromApi(1);
                    idLoaded[1] = true;
                } else if (s.equals("institucional") && !idLoaded[2]) {
                    getResultsFromApi(2);
                    idLoaded[2] = true;
                }
            }
        });

        scroller = findViewById(R.id.calendarTextContainer3);
        fabBack = findViewById(R.id.fabCalendar);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /////////////////// CALENDAR ////////////////////////
    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi(int cal) {
        if (!FirebaseUtils.checkSignInStatus()) {
            Log.d(TAG, "Shazam! ->getResultsFromApi: User is null");
        } else {
            //gCredential.setSelectedAccount(account.getAccount());
            progressDialog.show();
            new MakeRequestTask(cal).execute();
        }
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    @SuppressLint("StaticFieldLeak")
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;
        private int calId;

        MakeRequestTask(int cal) {
            calId = cal;
            Log.d(TAG, "Shazam! ->MakeRequestTask: gCredential: " + GoogleUtils.getgCredential());
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, GoogleUtils.getgCredential())
                    .setApplicationName("DCE Livre da USP")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            Log.d(TAG, "Shazam! ->doInBackground: entrou");
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         */
        private List<String> getDataFromApi() throws IOException {
            // Escolha do calendário
            String calendarId;
            switch (calId) {
                case 0: {
                    calendarId = "qp8p8c1c3t1gbqr9353n7ei6ik@group.calendar.google.com"; // Calendário do ME
                    break;
                }case 1: {
                    calendarId = "ku3bfo1ve7ehaq1gljr7g7cal4@group.calendar.google.com"; // Calendário de festas
                    break;
                }case 2: {
                    calendarId = "jm2qijsvn1lui4ftpfjf52p15g@group.calendar.google.com"; // Calendário acadêmico
                    break;
                } default: {
                    calendarId = "qp8p8c1c3t1gbqr9353n7ei6ik@group.calendar.google.com";
                    break;
                }
            }

            Log.d(TAG, "Shazam! ->getDataFromApi: entrou");
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<>();
            events = null;
            events = mService.events().list(calendarId)
                    .setMaxResults(CALENDAR_MAXRESULTS)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            //Log.d(TAG, "Shazam! ->getDataFromApi: items: " + items);
            if (items.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(EventsActivity.this, "Não há eventos dessa agenda próximos dias", Toast.LENGTH_SHORT).show();
            }

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                    Log.d(TAG, "Shazam! ->getDataFromApi: start.getTimeZone: " + event.getEnd().getTimeZone());
                    Log.d(TAG, "Shazam! ->getDataFromApi: start.getTimeZoneShift: " + event.getEnd().getDate().getTimeZoneShift());
                }

                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    end = event.getEnd().getDate();
                }

                String local = event.getLocation();
                if (local == null) {
                    local = "Sem local definido.";
                }

                String descricao = event.getDescription();
                if (descricao == null) {
                    descricao = "...";
                }

                String startFull = new SimpleDateFormat("EEE, d MMM, h:mm a",new Locale("pt","BR"))
                        .format(new Date(start.getValue()));
                String endFull = new SimpleDateFormat("EEE, d MMM, h:mm a",new Locale("pt","BR"))
                        .format(new Date(end.getValue()));

                eventStrings.add(
                        String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n", event.getSummary(), startFull, endFull, local, descricao, String.valueOf(start.getValue()), String.valueOf(end.getValue())));
            }

            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Shazam! ->onPreExecute: entrou!");
        }

        @Override
        protected void onPostExecute(List<String> output) {
            //Log.d(TAG, "Shazam! ->onPostExecute: output: " + output);
            for (String s : output) {
                String[] parts = s.split("\n");
                final String[] evento = parts;
                EventFragment eventFragment = (EventFragment) getSupportFragmentManager().findFragmentByTag("tag_event" + calId);
                ImageView ic = eventFragment.setEvent(parts[0],parts[1],parts[2],parts[3],parts[4]);
                ic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Shazam! ->onClick: clicou em: " + evento[0]);
                        long beginTime;
                        long endTime;

                        beginTime = Long.parseLong(evento[5]);
                        endTime = Long.parseLong(evento[6]);

                        Intent addEvent = new Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
                                .putExtra(CalendarContract.Events.TITLE, evento[0])
                                .putExtra(CalendarContract.Events.DESCRIPTION, evento[4])
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, evento[3]);
                        startActivity(addEvent);
                    }
                });
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "Shazam! ->onCancelled: entrou");
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    Log.d(TAG, "Shazam! ->onCancelled: google services not available");
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    Log.d(TAG, "Shazam! ->onCancelled: UserRecoverableAuthIOException");
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),REQUEST_AUTHORIZATION);
                } else {
                    Log.d(TAG, "Shazam! ->onCancelled: The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.d(TAG, "Shazam! ->onCancelled: request cancelled");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUtils.setContext(this);
    }

    private void addFragment(int id) {
        // initialize fragment elements (inflate View)
        eventFragment = new EventFragment();
        // initialize fragment transaction (addition) to current Viewgroup
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        // Add inflated view to the container
        switch(id) {
            case 0: {
                fragmentTransaction.replace(R.id.calendarTextContainer1,eventFragment,"tag_event0").commit(); break;
            }case 1: {
                fragmentTransaction.replace(R.id.calendarTextContainer2,eventFragment,"tag_event1").commit(); break;
            }case 2: {
                fragmentTransaction.replace(R.id.calendarTextContainer3,eventFragment,"tag_event2").commit(); break;
            } default: break;
        }

    }
}
