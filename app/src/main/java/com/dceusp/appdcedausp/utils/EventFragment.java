package com.dceusp.appdcedausp.utils;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dceusp.appdcedausp.R;

import java.util.Random;


public class EventFragment extends Fragment {

    Context c;

    View v;

    RelativeLayout container;
    LinearLayout inner;
    LinearLayout inner2;
    LinearLayout mainContainer;
    ImageView icon;

    TextView tTitle;
    TextView tDateStart;
    TextView tDateEnd;
    TextView tLocal;
    WebView tDesc;

    TextView legend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_fragment,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v = view;
//        container = view.findViewById(R.id.eventContainer);
        mainContainer = view.findViewById(R.id.eventInnerContainer);
    }

    public ImageView setEvent(String title, String start, String end, String local, String desc) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0,0,0,20);

        inner = new LinearLayout(c);
        inner.setLayoutParams(params);
        inner.setOrientation(LinearLayout.VERTICAL);

        tTitle = new TextView(c);
        tTitle.setTextColor(getResources().getColor(R.color.grena));
        tTitle.setTextSize(24f);
        tTitle.setTypeface(null,Typeface.BOLD_ITALIC);
        tTitle.setText(title);
        tTitle.setLayoutParams(params);
        int id = tTitle.getId();
        inner.addView(tTitle);

        tDateStart = new TextView(c);
        tDateStart.setTextSize(12f);
        tDateStart.setTypeface(null,Typeface.ITALIC);
        String txt = "Início: " + start;
        tDateStart.setText(txt);
        tDateStart.setLayoutParams(params);
        inner.addView(tDateStart);

        tDateEnd = new TextView(c);
        tDateEnd.setTextSize(12f);
        tDateEnd.setTypeface(null,Typeface.ITALIC);
        txt = "Fim: " + end;
        tDateEnd.setText(txt);
        tDateEnd.setLayoutParams(params);
        inner.addView(tDateEnd);

        tLocal = new TextView(c);
        tLocal.setTextSize(16f);
        tLocal.setTypeface(null,Typeface.BOLD);
        txt = "Local: " + local;
        tLocal.setText(txt);
        tLocal.setLayoutParams(params);
        inner.addView(tLocal);

        tDesc = new WebView(c);
        tDesc.loadData(desc,"text/html","UTF-8");
//        tDesc.setTextSize(14f);
//        tDesc.setText(desc);
        tDesc.setLayoutParams(params);
        tDesc.setPadding(0,0,0,20);
        inner.addView(tDesc);


        container = new RelativeLayout(c);


        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(params);
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        params2.addRule(RelativeLayout.BELOW,id);
        params2.setMargins(10,80,20,0);
        params2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params2.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        inner2 = new LinearLayout(c);
        inner2.setLayoutParams(params2);
        inner2.setOrientation(LinearLayout.VERTICAL);
        inner2.setGravity(Gravity.CENTER);

        icon = new ImageView(c);
        icon.setImageResource(R.drawable.ic_add);
        icon.setId(new Random(19831983).nextInt()); // Precisa guardar esse Id para inicializar o OnClickListener
        icon.setLayoutParams(params);
        icon.getLayoutParams().height = 200;
        inner2.addView(icon);
        //icon.getLayoutParams().width = 200;

        legend = new TextView(c);
        txt = "Adicionar evento";
        legend.setText(txt);
        legend.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        legend.setTextSize(12f);
        legend.setTypeface(null,Typeface.ITALIC);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        legend.setLayoutParams(params);
        inner2.addView(legend);

        container.setPadding(10,10,10,10);
        params2 = new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        params2.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        params2.addRule(RelativeLayout.ALIGN_PARENT_START,RelativeLayout.TRUE);
        params2.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        inner.setLayoutParams(params2);
        container.addView(inner);
        container.addView(inner2);

        mainContainer.addView(container);
        return icon;
    }
}
