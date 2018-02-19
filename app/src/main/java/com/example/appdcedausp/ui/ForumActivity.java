package com.example.appdcedausp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdcedausp.R;
import com.example.appdcedausp.utils.FirebaseUtils;
import com.example.appdcedausp.utils.Forum;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by yago_ on 18/02/2018.
 */

public class ForumActivity extends AppCompatActivity {

    private static final String TAG = ForumActivity.class.getName();

    RecyclerView mForumList;

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_activity);

        pref = getApplicationContext().getSharedPreferences("myConfig",0);

        mForumList = findViewById(R.id.forum_list);
        mForumList.setHasFixedSize(true);
        mForumList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Forum,ForumActivity.ForumViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Forum, ForumViewHolder>(
                Forum.class,
                R.layout.forum_fragment,
                ForumActivity.ForumViewHolder.class,
                FirebaseUtils.getMDatabase()
                    .child("Forum")
                    .child(String.valueOf(pref.getInt("Campus",0)))
        ) {
            @Override
            protected void populateViewHolder(ForumViewHolder viewHolder, Forum model, int position) {

                viewHolder.setImage(model.getForum_image());
                viewHolder.setName(model.getForum_name());
                viewHolder.setDescription(model.getForum_description());
                viewHolder.setNPosts(model.getForum_posts());

                final int pos = position;
                final int n = model.getForum_posts();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent forumIntent = new Intent(ForumActivity.this,SimpleBlogActivity.class);
                        Log.d(TAG, "Shazam! ->onClick: pos:" + pos);
                        forumIntent.putExtra("forumId",pos);
                        forumIntent.putExtra("nPosts",n);
                        startActivity(forumIntent);
                    }
                });
            }
        };

        mForumList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ForumViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ForumViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView tName = mView.findViewById(R.id.forum_name);
            tName.setText(name);
        }

        public void setDescription(String description) {
            TextView tDesc = mView.findViewById(R.id.forum_description);
            tDesc.setText(description);
        }

        public void setNPosts(int posts) {
            TextView tPosts = mView.findViewById(R.id.forum_posts);
            tPosts.setText("Número de postagens: " + posts);
        }

        public void setImage(String image) {
            ImageView imageView = mView.findViewById(R.id.forum_image);
            if (image == null) {
                imageView.setVisibility(View.GONE);
            } else {
                Picasso.with(mView.getContext())
                        .load(image)
                        .fit()
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
}
