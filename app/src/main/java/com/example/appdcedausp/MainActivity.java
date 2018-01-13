package com.example.appdcedausp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Animação de fade para os botões
        final Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);

        btnMapa = (ImageView) findViewById(R.id.btnmapa);
        btnEventos = (ImageView) findViewById(R.id.btneventos);
        btnBandejao = (ImageView) findViewById(R.id.btnbandejao);
        btnPermanencia = (ImageView) findViewById(R.id.btnpermanencia);
        btnLinks = (ImageView) findViewById(R.id.btnlinks);
        btnReclamacao = (ImageView) findViewById(R.id.btnreclamacao);

        fabCampus = (FloatingActionButton) findViewById(R.id.mudaCampus);

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
                v.startAnimation(animAlpha);
            }
        });

        btnBandejao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
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
                makeSnack("Muda o campus", view);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
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
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }

    public void makeToast(String text) {
        Toast.makeText(this, text,Toast.LENGTH_LONG).show();
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
}

/**
 * TODO: Atividade do bandejão
 * TODO: Atividade da permanência
 * TODO: Atividade dos links
 * TODO: Atividade da denúncia
 * TODO: Fragmento inicial para o mural de eventos e informes
 * TODO: Configuração para escolha do campus
 * TODO: Ajustes das atividades de acordo com a configuração de campus
 * TODO: Mecanismo que retorna ao fragmento inicial dos informes e eventos
 */