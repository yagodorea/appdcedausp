package com.dceusp.appdcedausp.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.dceusp.appdcedausp.utils.FbFeedFragment;
import com.dceusp.appdcedausp.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookFeedActivity extends AppCompatActivity {

    private static final String TAG = FacebookFeedActivity.class.getName();

    int i;
    String created_time = null;
    String story = null;
    String picture = null;
    String message = null;
    String permalink = null;

    FloatingActionButton fabBack;
    ProgressDialog dialog;

    // Fragmentos
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FbFeedFragment fbFeedFragment;

    AccessToken accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_activity);

        Bundle param = new Bundle();
        param.putString("fields","created_time,name,story,picture,full_picture,message,description,permalink_url");
        param.putString("locale","pt_BR");

        if (AccessToken.getCurrentAccessToken() == null) {
            Toast.makeText(this, "Can't get current access token", Toast.LENGTH_SHORT).show();
            String token = "992935517526832|5f71e86a6be11ff8e76bfc650250bf1e";
            accessToken = new AccessToken(token,
                    "992935517526832",
                    "100001065407967",
                    null,
                    null,
                    null,
                    null,
                    null);
        } else {
            accessToken = AccessToken.getCurrentAccessToken();
        }
        addFragment();

        Log.d(TAG, "Shazam! ->onCreate: current access token: " + AccessToken.getCurrentAccessToken());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.show();
        new GraphRequest(
                accessToken,
                getResources().getString(R.string.fb_page_id),
                param,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Log.d(TAG, "Shazam! ->onCompleted: response: " + response);
                        processResponse(response);
                    }
                }
        ).executeAsync();

        fabBack = findViewById(R.id.fabFbBack);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitAnimation();
            }
        });
    }

    public void processResponse(GraphResponse r) {
        try {
            int length = r.getJSONObject().getJSONArray("data").length();
            Log.d(TAG, "Shazam! ->processResponse: length: " + length);
            for (i=0;i < length;i++) {

                JSONObject obj = r.getJSONObject().getJSONArray("data").getJSONObject(i);
                if (obj.has("created_time"))
                    created_time = obj.getString("created_time");

                if (obj.has("name")) {
                    story = obj.getString("name");
                } else if(obj.has("story")) {
                    story = obj.getString("story");
                }

                if(obj.has("full_picture")) {
                    picture = obj.getString("full_picture");
                } else if (obj.has("picture")) {
                    picture = obj.getString("picture");
                }

                if(obj.has("message")) {
                    message = delimitMessage(obj.getString("message"));
//                    message = obj.getString("message");
                } else if (obj.has("description")) {
                    message = delimitMessage(obj.getString("description"));
//                    message = obj.getString("description");
                }

                if(obj.has("permalink_url"))
                    permalink = obj.getString("permalink_url");

                ((FbFeedFragment) getSupportFragmentManager().findFragmentByTag("tag_fbFeed"))
                    .setPost(created_time,story,picture,message,permalink);
                dialog.dismiss();

//                                FbFeedFragment frag = (FbFeedFragment)getSupportFragmentManager()
//                                        .findFragmentById(R.id.fbFeedContainer);
//                                frag.setPost(message, story, created_time, permalink, picture);
            }
        } catch (JSONException | NullPointerException e) {
            dialog.dismiss();
            Toast.makeText(this, "Erro ao carregar notícias", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public String delimitMessage(String s) {
        // return string with first 30 words and "..."
        int i = 50;
        String[] tokens = s.split(" ");
        if (i > tokens.length) return s;
        while(tokens[i].length() < 5){ i++; } // Não usar palavras pequenas com alta ocorrência (que, e, se, do, de...)
        while (tokens[i].contains("(") || tokens[i].contains(")")) { i++; }
        String[] broken = s.split(" " + tokens[i]); // separar string e pegar parte antes da ocorrência.
        return (broken[0] + "...");
    }

    public void addFragment() {
        // initialize fragment elements (inflate View)
        fbFeedFragment = new FbFeedFragment();
        // initialize fragment transaction (addition) to current Viewgroup
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        // Add inflated view to the container
        fragmentTransaction.replace(R.id.fbFeedContainer,fbFeedFragment,"tag_fbFeed").commit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        exitAnimation();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_ffadein,R.anim.anim_ffadeout);
    }

    public void exitAnimation() {
        AnimationSet a = loadLogoAnimationFacebook(fabBack);
        fabBack.startAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                AlphaAnimation alpha = new AlphaAnimation(1f,0f);
                alpha.setDuration(500);
                fabBack.startAnimation(alpha);

                Animation a2 = AnimationUtils.loadAnimation(FacebookFeedActivity.this,R.anim.anim_ffadeout);
                //a2.setFillAfter(true);
                fabBack.startAnimation(a2);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public AnimationSet loadLogoAnimationFacebook(View view) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        View rootLayout = findViewById(R.id.fbActivContainer);
        int statusBarOffset = dm.heightPixels - rootLayout.getMeasuredHeight();

        int destPosX = dm.widthPixels;
        int destPosY = 2*view.getMeasuredHeight() - statusBarOffset;

        int xDelta = - (destPosX - view.getMeasuredWidth()*2);
        int yDelta = - (dm.heightPixels - destPosY);

        AnimationSet a = new AnimationSet(true);
        a.setFillAfter(true);
        a.setDuration(500);
        a.setInterpolator(new LinearOutSlowInInterpolator());
//        ScaleAnimation scale = new ScaleAnimation(1f,2f,1f,2f,ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
//        a.addAnimation(scale);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE,0,
                TranslateAnimation.ABSOLUTE,xDelta,
                TranslateAnimation.ABSOLUTE,0,
                TranslateAnimation.ABSOLUTE,0);
        a.addAnimation(translate);
        return a;
    }
}
