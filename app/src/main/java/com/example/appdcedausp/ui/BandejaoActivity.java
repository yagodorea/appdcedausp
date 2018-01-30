package com.example.appdcedausp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.appdcedausp.R;

/**
 * Created by yago_ on 14/01/2018.
 */

public class BandejaoActivity extends AppCompatActivity {

    FloatingActionButton fabClose;
    FloatingActionButton fabChange;
    WebView wBandejao;

    // URis dos bandejoes
    final String common = "https://uspdigital.usp.br/rucard/Jsp/cardapioSAS.jsp?codrtn=";
    final int piracicaba = 1;
    final int sanca1 = 2;
    final int sanca2 = 3;
    final int sanca3 = 4;
    final int pirassununga1 = 5;
    final int butanta1 = 6;
    final int butanta2 = 7;
    final int butanta3 = 8;
    final int butanta4 = 9;
    final int butanta5 = 10;
    final int fsp1 = 11;
    final int fsp2 = 12;
    final int each = 13;
    final int sanfran = 14;
    final int dpscuaso = 15;
    final int dpseach = 16;
    final int lorena = 17;
    final int medicina = 18;
    final int ribeirao = 19;
    final int bauru = 20;
    final int pirassununga2 = 21;
    private int bandex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bandejao_activity);

        bandex = piracicaba;

        wBandejao = (WebView)findViewById(R.id.webViewBandejao);
        wBandejao.getSettings().setJavaScriptEnabled(true);
        wBandejao.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                formatPage();
            }
        });
        fabClose = (FloatingActionButton)findViewById(R.id.closeBandejao);
        fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fabChange = (FloatingActionButton)findViewById(R.id.changeBandejao);
        fabChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Tratar lista de bandejoes para cada campus
                bandex = (bandex+1)%22;
                wBandejao.loadUrl(common + bandex);
            }
        });

        wBandejao.loadUrl(common + bandex);
    }

    public void formatPage() {
        wBandejao.loadUrl("javascript:document.getElementById('layout_conteudo').parentNode.removeChild(document.getElementById('layout_cabecalho'));");
        wBandejao.loadUrl("javascript:document.getElementById('layout_conteudo').parentNode.removeChild(document.getElementById('layout_menu'));");
        wBandejao.loadUrl("javascript:document.getElementById('layout_conteudo').setAttribute('style','padding:5px');");
        wBandejao.loadUrl("javascript:document.getElementById('cardapioSAS').setAttribute('style','margin:5px;width:100%');");
        wBandejao.loadUrl("javascript:document.getElementById('restaurante').setAttribute('style','margin:5px;width:100%');");
        wBandejao.loadUrl("javascript:document.getElementById('telefoneRestaurante').parentNode.setAttribute('style','margin:5px;width:100%');");
        wBandejao.loadUrl("javascript:document.getElementById('dataSemana').parentNode.setAttribute('style','margin:5px;width:100%');");
        wBandejao.loadUrl("javascript:document.getElementById('observacao').setAttribute('style','margin:5px;width:100%');");
        wBandejao.loadUrl("javascript:document.getElementsByClassName('link_olive')[0].parentNode.parentNode.parentNode.parentNode.parentNode.setAttribute('style','margin:5px;width:100%');");
    }

}
/**
 * Link dos cardápios: https://uspdigital.usp.br/rucard/Jsp/cardapioSAS.jsp?codrtn=NUMERO
 *
 * RUCAS (Piracicaba): 1
 *
 * Bandejão C1 (Sanca): 2
 * Bandejão C2 (Sanca): 3
 * CRHEA (Sanca): 4
 *
 * Bandejão Pirassununga: 5
 *
 * Restaurante Central (Butantã): 6
 * PUSP-C (Butantã): 7
 * Restaurante da Física (Butantã): 8
 * Restaurante das Químicas (Butantã): 9
 *
 * Faculdade de Saúde Pública (Butantã?): 11
 * Bandejão da Escola de Enfermagem: 12
 *
 * Bandejão EACH: 13
 *
 * Bandejão Sanfran: 14
 *
 * Bandejão Lorena: 17
 *
 * Bandejão Ribeirão Preto: 19
 *
 * Bandejão Bauru: 20
 *
 *
*/