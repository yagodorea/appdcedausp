package com.example.appdcedausp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    String created_time;
    String story;
    String picture;
    String message;
    String permalink;

    // Fragmentos
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FbFeedFragment fbFeedFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_activity);

        Bundle param = new Bundle();
        param.putString("fields","story,created_time,full_picture,permalink_url");
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
    }

    public void processResponse(GraphResponse r) {
        try {
            int length = r.getJSONObject().getJSONArray("data").length();
            Log.d(TAG, "Shazam! ->processResponse: length: " + length);
            for (i=0;i < length;i++) {

                JSONObject obj = r.getJSONObject().getJSONArray("data").getJSONObject(i);
                story = obj.getString("story");
                created_time = obj.getString("created_time");
                picture = obj.getString("full_picture");
                //message = obj.getString("message");
                permalink = obj.getString("permalink_url");
                Log.d(TAG, "Shazam! ->onCompleted: permalink: " + permalink);

                ((FbFeedFragment) getSupportFragmentManager().findFragmentByTag("tag_fbFeed"))
                    .setPost(story, created_time, permalink, picture);

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
}
