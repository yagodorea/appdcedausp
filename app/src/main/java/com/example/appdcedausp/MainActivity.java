package com.example.appdcedausp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    // Código de erro para conexão com API da Google (para uso do Maps)
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int SIGN_IN_REQUEST = 9002;

    private static final int REQUEST_AUTHORIZATION = 13;

    // Declaracao dos elementos
    ImageView btnMapa;
    ImageView btnEventos;
    ImageView btnBandejao;
    ImageView btnPermanencia;
    ImageView btnLinks;
    ImageView btnReclamacao;
    FloatingActionButton fabCampus;

    // Preferências
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // Login Google
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    ImageView signInButton;
    AuthCredential mCredential;
    GoogleAccountCredential gCredential;
    // TODO Fazer signOutButton e signInButton ImageView personalizados
    Events events;

    // Login Firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Login básico da conta Google, com perfil básico, ID e email.
        // Pega informações da conta Google que o usuário estiver logado, se não tiver nenhuma,
        // faz a requisição de login na função OnStart()
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.googleSignInButton);
        //signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "Shazam! ->onTouch: " + motionEvent.getActionMasked());
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (mUser != null) {
                        signInButton.setImageResource(R.drawable.googlesign_grena_pressed);
                    } else {
                        signInButton.setImageResource(R.drawable.googlesign_creme_pressed);
                    }
                    return true;
                } else if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP
                        || motionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                    if (mUser != null) {
                        // Sign out
                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(MainActivity.this, "Saiu!", Toast.LENGTH_SHORT).show();
                                        googleRevokeAccess();
                                    }
                                });
                    } else {
                        // Usuário não está logado
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent,SIGN_IN_REQUEST);
                    }
                    return true;
                } else return false;
            }
        });

        // Pegar opções de usuário (seleção de campus)
        pref = getApplicationContext().getSharedPreferences("myConfig",0);
        fabCampus = findViewById(R.id.mudaCampus);
        switch(pref.getInt("Campus",0)){
            case 0: { // Butantã
                fabCampus.setImageResource(R.drawable.txtbutanta);
                break;
            }case 1: { // São Carlos
                fabCampus.setImageResource(R.drawable.txtsanca);
                break;
            }case 2: { // EACH
                fabCampus.setImageResource(R.drawable.txteach);
                break;
            }case 3: { // Sanfran
                fabCampus.setImageResource(R.drawable.txtsanfran);
                break;
            }case 4: { // Piracicaba
                fabCampus.setImageResource(R.drawable.txtpiracicaba);
                break;
            }case 5: { // Ribeirão Preto
                fabCampus.setImageResource(R.drawable.txtribeirao);
                break;
            }case 6: { // Pirassununga
                fabCampus.setImageResource(R.drawable.txtpirassununga);
                break;
            }case 7: { // Bauru
                fabCampus.setImageResource(R.drawable.txtpirassununga);
                break;
            }case 8: { // Santos
                fabCampus.setImageResource(R.drawable.txtpirassununga);
                break;
            }case 9: { // Lorena
                fabCampus.setImageResource(R.drawable.txtpirassununga);
                break;
            } default: askForCampus();
        }

        // Animação de fade para os botões
        final Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);

        // Pegar os botões do layout
        btnMapa = findViewById(R.id.btnmapa);
        btnEventos = findViewById(R.id.btneventos);
        btnBandejao = findViewById(R.id.btnbandejao);
        btnPermanencia = findViewById(R.id.btnpermanencia);
        btnLinks = findViewById(R.id.btnlinks);
        btnReclamacao = findViewById(R.id.btnreclamacao);

        // Listeners dos botoes

        ////////////////////////////////////////// AÇÃO DO MAPA ///////////////////////////////////////////////////

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if (isServicesOK()) {
                    Intent intent = new Intent(MainActivity.this,MapActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_ffadein,R.anim.anim_ffadeout);
                }
            }
        });

        ////////////////////////////////////////// AÇÃO DOS EVENTOS ///////////////////////////////////////////////////

        btnEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Animation a = AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_translatelogo_maintofb);
                AnimationSet a = loadLogoAnimationMain(v);
                findViewById(R.id.logoDCE).startAnimation(a);
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AlphaAnimation alpha = new AlphaAnimation(1f,0f);
                        alpha.setDuration(500);
                        findViewById(R.id.logoDCE).startAnimation(alpha);
                        Animation a2 = AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_ffadeout);
                        //a2.setFillAfter(true);
                        findViewById(R.id.logoDCE).startAnimation(a2);
                        Intent i = new Intent(MainActivity.this,FacebookFeedActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.anim_ffadein,R.anim.anim_ffadeout);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        ////////////////////////////////////////// AÇÃO DO BANDEJAO ///////////////////////////////////////////////////

        btnBandejao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(MainActivity.this,BandejaoActivity.class);
                startActivity(intent);
            }
        });

        ////////////////////////////////////////// AÇÃO DA PERMANENCIA ///////////////////////////////////////////////////

        btnPermanencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
            }
        });

        ////////////////////////////////////////// AÇÃO DOS LINKS ///////////////////////////////////////////////////

        btnLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                getResultsFromApi();
            }
        });

        ////////////////////////////////////////// AÇÃO DE RECLAMAÇÃO ///////////////////////////////////////////////////

        btnReclamacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
            }
        });

        ////////////////////////////////////////// AÇÃO DA MUDANÇA DO CAMPUS ///////////////////////////////////////////////////

        fabCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                askForCampus();
            }
        });
    }
    //////// Fim da função OnCreate() //////////////

    public void googleRevokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut();
                        mUser = mAuth.getCurrentUser();
                        gCredential = null;
                        signInButton.setImageResource(R.drawable.googlesign_creme);
                        ((TextView)findViewById(R.id.googleSignInText)).setText("Entrar:");
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            // Usuário não está logado
            signInButton.setImageResource(R.drawable.googlesign_creme);
            ((TextView)findViewById(R.id.googleSignInText)).setText("Entrar:");

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent,SIGN_IN_REQUEST);
        } else {
            ((TextView)findViewById(R.id.googleSignInText)).setText("Sair:");
            signInButton.setImageResource(R.drawable.googlesign_grena);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// ON ACTIVITY RESULT -> LISTENER DE REQUISIÇÃO ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Verificar se a atividade foi uma solicitação de login Google
        if (requestCode == SIGN_IN_REQUEST) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    if (task.isSuccessful()) {
                        account = task.getResult(ApiException.class);
                        // Login firebase
                        firebaseAuthWithGoogle(account);
                    } else {
                        Toast.makeText(this, "falha de autenticação!", Toast.LENGTH_SHORT).show();
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                signInButton.setImageResource(R.drawable.googlesign_creme);
                Log.d(TAG, "Shazam! ->onActivityResult: login cancelado");
            }
        }
    }

    // Login Firebase com Google Account
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "Shazam! ->firebaseAuthWithGoogle: " + acct.getId());
        // inicializacao GoogleAccountCredential
        gCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(CalendarScopes.CALENDAR_READONLY))
                .setBackOff(new ExponentialBackOff());
        mCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(mCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = task.getResult().getUser();
                            gCredential.setSelectedAccount(account.getAccount());
                            ((TextView)findViewById(R.id.googleSignInText)).setText("Sair:");
                            signInButton.setImageResource(R.drawable.googlesign_grena);
                            Toast.makeText(MainActivity.this, "Logado como " + mUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Login Firebase falhou!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Checagem do Google Services
    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            // Tudo ok, pode requisitar o mapa
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // Se for um erro resolvivel, mostrar
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Erro no Google Services!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    ////////////////////////////////////////// MUDANÇA DA OPÇÃO DE USUÁRIO (CAMPUS) ///////////////////////////////////////////////////

    private void askForCampus() {
        pref = getApplicationContext().getSharedPreferences("myConfig", 0); // 0 - for private mode
        editor = pref.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        TextView title = new TextView(this);
        title.setText("Escolha seu Campus");
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(50,50,50,50);
        title.setTextColor(getResources().getColor(R.color.grena));
        title.setTextSize(24f);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                editor.putInt("Campus", 1); // default: butantã
                editor.apply();
                fabCampus.setImageResource(R.drawable.txtbutanta);
            }
        });
        builder.setCustomTitle(title)
                .setItems(R.array.list_campus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i) {
                            case 0: { // Butantã
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtbutanta);
                                break;
                            }case 1: { // São Carlos
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtsanca);
                                break;
                            }case 2: { // EACH
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txteach);
                                break;
                            }case 3: { // Sanfran
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtsanfran);
                                break;
                            }case 4: { // Piracicaba
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtpiracicaba);
                                break;
                            }case 5: { // Ribeirão Preto
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtribeirao);
                                break;
                            }case 6: { // Pirassununga
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtpirassununga);
                                break;
                            }case 7: { // Bauru
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtpirassununga); // todo fazer txtbauru
                                break;
                            }case 8: { // Santos
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtpirassununga); // todo fazer txtsantos
                                break;
                            }case 9: { // Lorena
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtpirassununga); // todo fazer txtlorena
                                break;
                            } default: break;
                        }
                    }
                })
                .show();
    }

    /////////////////// CALENDAR ////////////////////////
    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isServicesOK()) {
            Log.d(TAG, "Shazam! ->getResultsFromApi: google services not ok!");
        } else if (mUser == null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent,SIGN_IN_REQUEST);
        } else {
            gCredential.setSelectedAccount(account.getAccount());
            new MakeRequestTask(gCredential).execute();
        }
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
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
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            Log.d(TAG, "Shazam! ->getDataFromApi: entrou");
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            events = mService.events().list("qp8p8c1c3t1gbqr9353n7ei6ik@group.calendar.google.com")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            Log.d(TAG, "Shazam! ->getDataFromApi: items: " + items);

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
//
//                // Adicionar views mostrando cada evento
                Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
//
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
                if (event.getEnd().getDateTime() != null) {
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEnd().getDateTime());
                }
                intent.putExtra(CalendarContract.Events.TITLE, event.getSummary());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
                if (getPackageManager().queryIntentActivities(intent,0).size() > 0) {
                    //startActivity(intent);
                }
//
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Shazam! ->onPreExecute: entrou!");
        }

        @Override
        protected void onPostExecute(List<String> output) {
            Log.d(TAG, "Shazam! ->onPostExecute: output: " + output);
            Intent intent = new Intent(MainActivity.this,EventsActivity.class);
            for (int i = 0;i < output.size();i++) {
                intent.putExtra("saida" + i,output.get(i));
            }
            startActivity(intent);
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
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.d(TAG, "Shazam! ->onCancelled: The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.d(TAG, "Shazam! ->onCancelled: request cancelled");
            }
        }
    }

    public AnimationSet loadLogoAnimationMain(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // get logo location
        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        // Bizarro: a localização horizontal é no meio e a vertical é no fim...

        View rootLayout = findViewById(R.id.mainRootLayout);
        int statusBarOffset = dm.heightPixels - rootLayout.getMeasuredHeight();

        int xDelta = (- originalPos[0] + dm.widthPixels - view.getMeasuredWidth()/4);
        int yDelta = (- originalPos[1]/2 + dm.heightPixels - view.getMeasuredHeight()/2 + statusBarOffset);


        Log.d(TAG, "Shazam! ->loadLogoAnimationMain: pivotX: " + view.getPivotX());
        Log.d(TAG, "Shazam! ->loadLogoAnimationMain: pivotY: " + view.getPivotY());
        AnimationSet a = new AnimationSet(true);
        a.setFillAfter(true);
        a.setDuration(1500);
        a.setInterpolator(new DecelerateInterpolator());
        ScaleAnimation scale = new ScaleAnimation(1f,0.5f,1.0f,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        a.addAnimation(scale);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE,0,
                TranslateAnimation.ABSOLUTE,xDelta,
                TranslateAnimation.ABSOLUTE,0,
                TranslateAnimation.ABSOLUTE,yDelta);
        a.addAnimation(translate);
        return a;
    }
}

/**
 * TODO: Atividade da permanência
 * TODO: Atividade dos links
 * TODO: Atividade da denúncia
 * TODO: Fragmento inicial para o mural de eventos e informes
 * TODO: Mecanismo que retorna ao fragmento inicial dos informes e eventos
 */