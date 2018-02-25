package com.example.appdcedausp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdcedausp.R;
import com.example.appdcedausp.utils.FirebaseUtils;
import com.example.appdcedausp.utils.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.appdcedausp.utils.Constants.*;

public class SimpleBlogActivity extends AppCompatActivity {

    private static final String TAG = SimpleBlogActivity.class.getName();

    FloatingActionButton fabAddPost;
    FloatingActionButton fabLeaveForum;

    private RecyclerView mPostList;

    TextView naoHaPosts;

    SharedPreferences pref;
    long forumId;
    int nPosts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_blog);

        naoHaPosts = findViewById(R.id.naoHaPosts);
        naoHaPosts.setVisibility(View.GONE);

        pref = getApplicationContext().getSharedPreferences("myConfig",0);
        forumId = getIntent().getLongExtra("forumId",-1);
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

        fabLeaveForum = findViewById(R.id.fabLeaveForum);
        fabLeaveForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleBlogActivity.this.finish();
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

                if (!model.getAprovado()) {
                    viewHolder.mView.setVisibility(View.GONE);
                    verifyAdm(viewHolder.mView,model.getCriadoem());
                }
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
                        postIntent.putExtra("forumId", forumId);
                        postIntent.putExtra("nPosts", nPosts);
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

        void setApproved(boolean approved) {
            // TODO mecanismo de moderação aqui?
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

    public void verifyAdm(View v, long post) {
        Log.d(TAG, "Shazam! ->verifyAdm: post nao aprovado");

        final View view = v;
        final long postId = post;
        DatabaseReference ref = FirebaseUtils.getMDatabase().child("Forum").child("adms");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                String userEmail = FirebaseUtils.getUser().getEmail();
                while (dataSnapshot.child(String.valueOf(i)).exists()) {
                    String adm = dataSnapshot.child(String.valueOf(i)).getValue(String.class);
                    if (adm.equals(userEmail)) {
                        // mostra caixa de dialogo
                        Toast.makeText(SimpleBlogActivity.this, "Existem posts a serem aprovados", Toast.LENGTH_SHORT).show();
                        view.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.postOuterContainer).setAlpha(0.4f);
                        view.findViewById(R.id.aprovarPost).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.aprovarPost).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog aprovar = new AlertDialog.Builder(SimpleBlogActivity.this).create();
                                aprovar.setTitle("Moderação");
                                aprovar.setMessage("Deseja aprovar esse post?");
                                aprovar.setButton(DialogInterface.BUTTON_POSITIVE, "Aprovar",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Task t = FirebaseUtils.getMDatabase()
                                                        .child("Forum")
                                                        .child(String.valueOf(pref.getInt("Campus",0)))
                                                        .child(String.valueOf(forumId))
                                                        .child("posts")
                                                        .child(Long.toString(postId))
                                                        .child("aprovado").setValue(true);
                                                dialogInterface.dismiss();
                                            }
                                        });
                                aprovar.setButton(DialogInterface.BUTTON_NEGATIVE, "Apagar",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Task t = FirebaseUtils.getMDatabase()
                                                        .child("Forum")
                                                        .child(String.valueOf(pref.getInt("Campus",0)))
                                                        .child(String.valueOf(forumId))
                                                        .child("posts")
                                                        .child(Long.toString(postId)).removeValue();
                                                dialogInterface.dismiss();
                                            }
                                        });
                                aprovar.show();
                            }
                        });
                        break;
                    } else {
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
