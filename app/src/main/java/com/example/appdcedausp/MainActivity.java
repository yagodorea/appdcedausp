package com.example.appdcedausp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Código de erro para conexão com API da Google (para uso do Maps)
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // Declaracao dos elementos
    ImageView btnMapa;
    ImageView btnEventos;
    ImageView btnBandejao;
    ImageView btnPermanencia;
    ImageView btnLinks;
    ImageView btnReclamacao;
    FloatingActionButton fabCampus;

    CallbackManager callbackManager;
    LoginButton loginButton;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("myConfig",0);

        fabCampus = (FloatingActionButton) findViewById(R.id.mudaCampus);

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

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile","user_friends");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(MainActivity.this,"Auth cancelled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(MainActivity.this,"Auth error: " + exception.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        // Animação de fade para os botões
        final Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);

        btnMapa = (ImageView) findViewById(R.id.btnmapa);
        btnEventos = (ImageView) findViewById(R.id.btneventos);
        btnBandejao = (ImageView) findViewById(R.id.btnbandejao);
        btnPermanencia = (ImageView) findViewById(R.id.btnpermanencia);
        btnLinks = (ImageView) findViewById(R.id.btnlinks);
        btnReclamacao = (ImageView) findViewById(R.id.btnreclamacao);

        // Listeners dos botoes
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                if (isServicesOK()) {
                    Intent intent = new Intent(MainActivity.this,MapActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation a = AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_translatelogo_maintofb);
                findViewById(R.id.logoDCE).startAnimation(a);
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent i = new Intent(MainActivity.this,FacebookFeedActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        btnBandejao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Intent intent = new Intent(MainActivity.this,BandejaoActivity.class);
                startActivity(intent);
            }
        });

        btnPermanencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
            }
        });

        btnLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
            }
        });

        btnReclamacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
            }
        });

        fabCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                askForCampus();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void fbClick(View v) {
        if (user != null) { // logout
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void makeSnack(String text, View view) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    public void makeToast(String text) {
        Toast.makeText(this, text,Toast.LENGTH_SHORT).show();
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
            makeToast("Erro no Google Services!");
        }
        return false;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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
}

/**
 * TODO: Atividade da permanência
 * TODO: Atividade dos links
 * TODO: Atividade da denúncia
 * TODO: Fragmento inicial para o mural de eventos e informes
 * TODO: Mecanismo que retorna ao fragmento inicial dos informes e eventos
 */