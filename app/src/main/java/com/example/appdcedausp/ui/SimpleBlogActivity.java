package com.example.appdcedausp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdcedausp.R;
import com.example.appdcedausp.utils.FirebaseUtils;
import com.example.appdcedausp.utils.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.appdcedausp.utils.Constants.*;

public class SimpleBlogActivity extends AppCompatActivity {

    private static final String TAG = SimpleBlogActivity.class.getName();

    FloatingActionButton fabAddPost;

    private RecyclerView mPostList;

    TextView naoHaPosts;

    SharedPreferences pref;
    int forumId;
    int nPosts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_blog);

        naoHaPosts = findViewById(R.id.naoHaPosts);
        naoHaPosts.setVisibility(View.GONE);

        pref = getApplicationContext().getSharedPreferences("myConfig",0);
        forumId = getIntent().getIntExtra("forumId",-1);
        nPosts = getIntent().getIntExtra("nPosts",-1);

        mPostList = findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);
        mPostList.setLayoutManager(new LinearLayoutManager(this));

        fabAddPost = findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleBlogActivity.this,MakePostActivity.class);
                intent.putExtra("forumId",forumId);
                intent.putExtra("nPosts",nPosts);
                startActivityForResult(intent,NEWPOST_REQUEST);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (forumId == -1) {
            Log.d(TAG, "Shazam! ->onStart: noExtra!");
        }

        FirebaseRecyclerAdapter<Post,SimpleBlogActivity.PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, SimpleBlogActivity.PostViewHolder>(
                Post.class,
                R.layout.post_fragment,
                SimpleBlogActivity.PostViewHolder.class,
                FirebaseUtils.getMDatabase()
                        .child("Forum")
                        .child(String.valueOf(pref.getInt("Campus",0)))
                        .child(String.valueOf(forumId))
                        .child("posts")
        ) {
            @Override
            public void onBindViewHolder(PostViewHolder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);
                Log.d(TAG, "Shazam! ->onBindViewHolder: entered, pos: " + position);
            }

            @Override
            protected void populateViewHolder(SimpleBlogActivity.PostViewHolder viewHolder, Post model, int position) {

                Log.d(TAG, "Shazam! ->populateViewHolder: position: " + position);
                Log.d(TAG, "Shazam! ->populateViewHolder: viewType: " + viewHolder.getItemViewType());

                viewHolder.setTitle(model.getTitulo());
                viewHolder.setDescription(model.getDescricao());
                viewHolder.setAuthorAndDate(model.getAutor(), model.getCriadoem());
                viewHolder.setImage(model.getImagem());

                final Post post = model;

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Shazam! ->onClick: post title: " + post.getTitulo());
                        Intent postIntent = new Intent(SimpleBlogActivity.this, PostActivity.class);
                        Bundle extras = new Bundle();
                        extras.putSerializable("post", post);
                        postIntent.putExtras(extras);
                        startActivity(postIntent);
                    }
                });
            }
        };


        // TODO fazer essa bosta funcionar, de aparecer o texto "não há posts"

        Log.d(TAG, "Shazam! ->onStart: invocou setAdapter");
        Log.d(TAG, "Shazam! ->onStart: item count: " + firebaseRecyclerAdapter.getItemCount());

        mPostList.setAdapter(firebaseRecyclerAdapter);

        if (mPostList.getAdapter().getItemCount() > 0) {
            naoHaPosts.setVisibility(View.GONE);
        } else {
            naoHaPosts.setVisibility(View.GONE);
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "Shazam! ->PostViewHolder: entrou no construtor");
            mView = itemView;
        }

        void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        void setDescription(String desc) {
            TextView post_desc = mView.findViewById(R.id.post_description);
            post_desc.setText(desc);
        }

        void setAuthorAndDate(String author,long created) {
            Date date = new Date(created);
            TextView post_author = mView.findViewById(R.id.post_author_date);
            String txt = author + ", " + new SimpleDateFormat("EEE, d MMM, h:mm a",new Locale("pt","BR")).format(date);
            post_author.setText(txt);
        }

        public void setImage(String image) {
            // Tratar download da imagem (fazer async)
            ImageView post_image = mView.findViewById(R.id.post_image);

            if (image == null) {
                post_image.setVisibility(View.GONE);
            } else {
                // this is ingenious
                Picasso.with(mView.getContext())
                        .load(image)
                        .fit()
                        .centerCrop()
                        .into(post_image);
            }
        }
    }
}
