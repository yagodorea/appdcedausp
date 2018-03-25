package com.dceusp.appdcedausp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dceusp.appdcedausp.R;
import com.dceusp.appdcedausp.utils.Constants;
import com.dceusp.appdcedausp.utils.FirebaseUtils;
import com.dceusp.appdcedausp.utils.GoogleUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static com.dceusp.appdcedausp.utils.Constants.SIGN_IN_REQUEST;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = Main2Activity.class.getName();

    private boolean animated = false;

    // Preferências
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // Elementos
    ImageView fab;
    FloatingActionButton fabCampus;
    ImageView signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        GoogleUtils.setContext(this);
        FirebaseUtils.setContext(this);

        signInButton = findViewById(R.id.loginButton);
        signInButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "Shazam! ->onTouch: " + motionEvent.getActionMasked());
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (FirebaseUtils.checkSignInStatus()) {
                        signInButton.setImageResource(R.drawable.googlesign_creme_pressed);
                    } else {
                        signInButton.setImageResource(R.drawable.googlesign_grena_pressed);
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
        fabCampus = findViewById(R.id.fabCampus);
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
            }case 10: { // Saúde
                fabCampus.setImageResource(R.drawable.txtpirassununga);
                break;
            } default: askForCampus();
        }
        fabCampus.setVisibility(View.INVISIBLE);
        fabCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForCampus();
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        TranslateAnimation translate = new TranslateAnimation(0,drawer.getMeasuredWidth(),0,0);
        translate.setInterpolator(new FastOutLinearInInterpolator());
        translate.setDuration(200);
        drawer.setAnimation(translate);

        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START,true);
                } else {
                    drawer.openDrawer(GravityCompat.START, true);
                    findViewById(R.id.nav_header).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = "fb://page/";
                            String page = "169245436486734";
                            Intent fbIntent = new Intent(Intent.ACTION_VIEW);
                            fbIntent.setData(Uri.parse(url + page));
                            startActivity(fbIntent);
                        }
                    });
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    ////////////////////////// LOGIN GOOGLE ///////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();

        if (!animated) {
            final RelativeLayout intro = findViewById(R.id.appIntro);
            intro.setVisibility(View.VISIBLE);
            AlphaAnimation wait2s = new AlphaAnimation(1f, 1f);
            wait2s.setDuration(2000);
            intro.startAnimation(wait2s);
            intro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.clearAnimation();
                }
            });
            wait2s.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    AlphaAnimation fadeout = new AlphaAnimation(1f, 0f);
                    fadeout.setDuration(500);
                    fadeout.setInterpolator(new AccelerateInterpolator());
                    intro.startAnimation(fadeout);
                    fadeout.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            intro.setVisibility(View.GONE);
                            AlphaAnimation a = new AlphaAnimation(0f,1f);
                            a.setDuration(1000);
                            fab.setAnimation(a);
                            fab.animate();
                            fab.setVisibility(View.VISIBLE);

                            fabCampus.setAnimation(a);
                            fabCampus.animate();
                            fabCampus.setVisibility(View.VISIBLE);
                            ///////////////////////////////// INÍCIO DO APP ///////////////////////////////////
                            GoogleUtils.checkGoogleLoginStatus();

                            if (FirebaseUtils.checkSignInStatus()) {
                                ((TextView) findViewById(R.id.loginText)).setText(R.string.sair);
                                signInButton.setImageResource(R.drawable.googlesign_creme);
                            } else {
                                // Usuário não está logado
                                signInButton.setImageResource(R.drawable.googlesign_grena);
                                ((TextView) findViewById(R.id.loginText)).setText(R.string.entrar);

                                // Tentar logar
                                FirebaseUtils.login();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            animated = true;
        }
    }

    ////////////////////////////////////////// MUDANÇA DA OPÇÃO DE USUÁRIO (CAMPUS) ///////////////////////////////////////////////////

    private void askForCampus() {
        pref = getApplicationContext().getSharedPreferences("myConfig", 0); // 0 - for private mode
        editor = pref.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);

        TextView title = new TextView(this);
        title.setText(R.string.choose_campus);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(50,50,50,50);
        title.setTextColor(getResources().getColor(R.color.grena));
        title.setTextSize(24f);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                editor.putInt("Campus", 1); // defaultmin: butantã
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
                                fabCampus.setImageResource(R.drawable.txtbauru);
                                break;
                            }case 8: { // Santos
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtsantos);
                                break;
                            }case 9: { // Lorena
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtlorena);
                                break;
                            } case 10: { // Quadrilátero
                                editor.putInt("Campus",i);
                                editor.apply();
                                fabCampus.setImageResource(R.drawable.txtsaude);
                                break;
                            } default: break;
                        }
                    }
                })
                .show();
    }

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
                signInButton.setImageResource(R.drawable.googlesign_grena);
                Log.d(TAG, "Shazam! ->onActivityResult: login cancelado");
                Toast.makeText(this, "Problema no login", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);

        if (id == R.id.nav_option1) {
            // Mapa do Campus
            if (GoogleUtils.isServicesOK()) {
                Intent intent = new Intent(Main2Activity.this,MapActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_option2) {
            // Fórum
            if (FirebaseUtils.getUser() != null) {
                Intent intent = new Intent(Main2Activity.this, ForumActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Você precisa estar logado para usar o fórum", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_option3) {
            // Agenda
            if (GoogleUtils.isServicesOK()) {
                Intent intent = new Intent(Main2Activity.this,EventsActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_option4) {
            // Bandejão
            String appPackageName = "br.usp.cardapio_usp";
            Intent launch = getPackageManager().getLaunchIntentForPackage(appPackageName);
            if (launch != null) {
                startActivity(launch);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            }
//        } else if (id == R.id.nav_option5) {
//            // Informações úteis
        } else if (id == R.id.nav_option6) {
            // Documentos da gestão
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/open?id=" + Constants.DRIVE_FOLDER_ID)));
        } else if (id == R.id.nav_option7) {
            // Notícias
            Intent i = new Intent(Main2Activity.this,FacebookFeedActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_option8) {
            // Transporte
            Intent i = new Intent(Main2Activity.this,OnibusActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_option9) {
            // Atendimento
            Intent i = new Intent(Main2Activity.this,MedicoActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_option10) {
            AlertDialog email = new AlertDialog.Builder(this).create();
            email.setTitle("Contato");
            email.setMessage("Para dúvidas, sugestões, reclamações e elogios, entre em contato pelo facebook ou envie email para:\n"
            + "contatodceusp@gmail.com\n"
            + "Fora Temer!");
            email.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            email.show();
        }

        return true;
    }
}
