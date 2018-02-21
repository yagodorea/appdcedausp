package com.example.appdcedausp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.appdcedausp.R;
import com.example.appdcedausp.utils.FirebaseUtils;
import com.example.appdcedausp.utils.GoogleUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static com.example.appdcedausp.utils.Constants.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    // Declaracao dos elementos
    ImageView btnMapa;
    ImageView btnEventos;
    ImageView btnBandejao;
    ImageView btnPermanencia;
    ImageView btnLinks;
    ImageView btnReclamacao;
    ImageView btnLogo;
    FloatingActionButton fabCampus;

    ImageView signInButton;

    // Preferências
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleUtils.setContext(this);
        FirebaseUtils.setContext(this);

        GoogleUtils.checkGoogleLoginStatus();

        signInButton = findViewById(R.id.googleSignInButton);
        signInButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "Shazam! ->onTouch: " + motionEvent.getActionMasked());
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (FirebaseUtils.checkSignInStatus()) {
                        signInButton.setImageResource(R.drawable.googlesign_grena_pressed);
                    } else {
                        signInButton.setImageResource(R.drawable.googlesign_creme_pressed);
                    }
                    return true;
                } else if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP
                        || motionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                    if (FirebaseUtils.checkSignInStatus()) {
                        // Sign out
                        FirebaseUtils.signOut();
                    } else {
                        // Usuário não está logado
                        FirebaseUtils.login();
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
        btnLogo = findViewById(R.id.logoDCE);

        // Listeners dos botoes

        ////////////////////////////////////////// AÇÃO DO MAPA ///////////////////////////////////////////////////

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if (GoogleUtils.isServicesOK()) {
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
                v.startAnimation(animAlpha);
                if (GoogleUtils.isServicesOK()) {
                    Intent intent = new Intent(MainActivity.this,EventsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_ffadein,R.anim.anim_ffadeout);
                }
            }
        });

        ////////////////////////////////////////// AÇÃO DO BANDEJAO ///////////////////////////////////////////////////

        btnBandejao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                String appPackageName = "br.usp.cardapio_usp";
                Intent launch = getPackageManager().getLaunchIntentForPackage(appPackageName);
                if (launch != null) {
                    startActivity(launch);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                }
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
            }
        });

        ////////////////////////////////////////// AÇÃO DE RECLAMAÇÃO ///////////////////////////////////////////////////

        btnReclamacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);

                Intent intent = new Intent(MainActivity.this,ForumActivity.class);
                startActivity(intent);
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

        btnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }
    //////// Fim da função OnCreate() //////////////




    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseUtils.checkSignInStatus()) {
            ((TextView)findViewById(R.id.googleSignInText)).setText(R.string.sair);
            signInButton.setImageResource(R.drawable.googlesign_grena);
        } else {
            // Usuário não está logado
            signInButton.setImageResource(R.drawable.googlesign_creme);
            ((TextView)findViewById(R.id.googleSignInText)).setText(R.string.entrar);

            // Tentar logar
            FirebaseUtils.login();
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
                        // Login firebase
                        FirebaseUtils.firebaseAuthWithGoogle(task.getResult(ApiException.class),this);
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


    ////////////////////////////////////////// MUDANÇA DA OPÇÃO DE USUÁRIO (CAMPUS) ///////////////////////////////////////////////////

    private void askForCampus() {
        pref = getApplicationContext().getSharedPreferences("myConfig", 0); // 0 - for private mode
        editor = pref.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        TextView title = new TextView(this);
        title.setText(R.string.choose_campus);
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

    public AnimationSet loadLogoAnimationMain(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // get logo location
        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);

        int xDelta = (dm.widthPixels - view.getMeasuredWidth() - originalPos[0]);
        int yDelta = (dm.heightPixels - view.getMeasuredHeight() - originalPos[1]);


//        Log.d(TAG, "Shazam! ->loadLogoAnimationMain: origX+w: " + (originalPos[0]+view.getMeasuredWidth()));
//        Log.d(TAG, "Shazam! ->loadLogoAnimationMain: origY+h: " + (originalPos[1]+view.getMeasuredHeight()));
        AnimationSet a = new AnimationSet(true);
        a.setFillAfter(true);
        a.setDuration(500);
        a.setInterpolator(new DecelerateInterpolator());
        ScaleAnimation scale = new ScaleAnimation(1f,0.5f,1.0f,0.5f,ScaleAnimation.RELATIVE_TO_SELF,1f,ScaleAnimation.RELATIVE_TO_SELF,1f);
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

/*
 * TODO: Atividade da permanência
 * TODO: Atividade dos links
 * TODO: Fragmento inicial para o mural de eventos e informes
 * TODO: Mecanismo que retorna ao fragmento inicial dos informes e eventos
 */