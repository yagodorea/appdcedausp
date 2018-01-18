package com.example.appdcedausp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yago_ on 17/01/2018.
 */

public class FacebookFeedActivity extends AppCompatActivity {

    private static final String TAG = FacebookFeedActivity.class.getName();

    int i;
    String created_time = null;
    String story = null;
    String picture = null;
    String message = null;
    String permalink = null;

    // Fragmentos
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FbFeedFragment fbFeedFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_activity);

        Bundle param = new Bundle();
        param.putString("fields","created_time,name,story,picture,full_picture,message,description,permalink_url");
        param.putString("locale","pt_BR");

//        fbFeedFragment = new FbFeedFragment();
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.fbFeedContainer,fbFeedFragment);
//        fragmentTransaction.commit();

        addFragment();

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/169245436486734/feed",
                param,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        processResponse(response);
                    }
                }
        ).executeAsync();

        ImageView logoDCEfb = findViewById(R.id.logoDCEfb);
        logoDCEfb.setOnClickListener(new View.OnClickListener() {
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
                    message = obj.getString("message");
                } else if (obj.has("description")) {
                    message = obj.getString("description");
                }

                if(obj.has("permalink_url"))
                    permalink = obj.getString("permalink_url");

                ((FbFeedFragment) getSupportFragmentManager().findFragmentByTag("tag_fbFeed"))
                    .setPost(created_time,story,picture,message,permalink);

//                                FbFeedFragment frag = (FbFeedFragment)getSupportFragmentManager()
//                                        .findFragmentById(R.id.fbFeedContainer);
//                                frag.setPost(message, story, created_time, permalink, picture);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        super.onBackPressed();
        exitAnimation();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_ffadein,R.anim.anim_ffadeout);
    }

    public void exitAnimation() {
        Animation a = AnimationUtils.loadAnimation(FacebookFeedActivity.this,R.anim.anim_translatelogo_fbtomain);
        findViewById(R.id.logoDCEfb).startAnimation(a);
        a.setFillAfter(true);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation a2 = AnimationUtils.loadAnimation(FacebookFeedActivity.this,R.anim.anim_ffadeout);
                //a2.setFillAfter(true);
                findViewById(R.id.logoDCEfb).startAnimation(a2);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
