package com.example.appdcedausp.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdcedausp.R;
import com.example.appdcedausp.utils.FirebaseUtils;
import com.example.appdcedausp.utils.Forum;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static com.example.appdcedausp.utils.Constants.*;

public class ForumActivity extends AppCompatActivity {

    private static final String TAG = ForumActivity.class.getName();

    RecyclerView mForumList;
    FloatingActionButton fabLeave;
    FloatingActionButton fabCreate;
    TextView forumsTitle;

    CreateForumDialog dialog;
    static ProgressDialog progressDialog;

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_activity);

        pref = getApplicationContext().getSharedPreferences("myConfig",0);
        setForumsTitle();

        progressDialog = new ProgressDialog(this);

        mForumList = findViewById(R.id.forum_list);
        mForumList.setHasFixedSize(true);
        mForumList.setLayoutManager(new LinearLayoutManager(this));

        fabLeave = findViewById(R.id.fabLeave);
        fabLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForumActivity.this.finish();
            }
        });
        fabCreate = findViewById(R.id.fabCreateForum);
        verifyAdm();
    }

    public void verifyAdm() {
        DatabaseReference ref = FirebaseUtils.getMDatabase();
        ref = ref.child("Forum").child("adms");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                String userEmail = FirebaseUtils.getUser().getEmail();
                while (dataSnapshot.child(String.valueOf(i)).exists()) {
                    String adm = dataSnapshot.child(String.valueOf(i)).getValue(String.class);
                    if (adm.equals(userEmail)) {
                        // mostra caixa de dialogo
                        Toast.makeText(ForumActivity.this, "Você é um administrador", Toast.LENGTH_SHORT).show();
                        ForumActivity.this.fabCreate.setVisibility(View.VISIBLE);
                        ForumActivity.this.fabCreate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showCreateForumDialog();
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

    public void showCreateForumDialog() {
        dialog = new CreateForumDialog();
        dialog.setInfos(pref.getInt("Campus",0));
        dialog.show(getSupportFragmentManager(),"CreateForumDialog");
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

                final long forumId = model.getForum_created();
                final int n = model.getForum_posts();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent forumIntent = new Intent(ForumActivity.this,SimpleBlogActivity.class);
                        Log.d(TAG, "Shazam! ->onClick: pos:" + forumId);
                        forumIntent.putExtra("forumId",forumId);
                        forumIntent.putExtra("nPosts",n);
                        startActivity(forumIntent);
                    }
                });
            }
        };

        mForumList.setAdapter(firebaseRecyclerAdapter);
        progressDialog.show();
        mForumList.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                progressDialog.dismiss();
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }

    public static class ForumViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ForumViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setName(String name) {
            TextView tName = mView.findViewById(R.id.forum_name);
            tName.setText(name);
        }

        void setDescription(String description) {
            TextView tDesc = mView.findViewById(R.id.forum_description);
            tDesc.setText(description);
        }

        void setNPosts(int posts) {
            TextView tPosts = mView.findViewById(R.id.forum_posts);
            String nPostagens = "Número de postagens: " + posts;
            tPosts.setText(nPostagens);
        }

        void setImage(String image) {
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

    public static class CreateForumDialog extends DialogFragment {
        View mView;
        TextView ok;
        TextView cancel;
        EditText nome;
        EditText desc;
        ImageView image;

        int campus;
        boolean imageModified;
        Uri imgUri;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            @SuppressWarnings("ConstantConditions") AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            mView = inflater.inflate(R.layout.dialog_createforum,null);
            builder.setView(mView);

            imageModified = false;
            ok = mView.findViewById(R.id.forumCreateButton);
            cancel = mView.findViewById(R.id.forumCreateCancel);
            image = mView.findViewById(R.id.newForumImage);
            nome = mView.findViewById(R.id.newForumTitle);
            desc = mView.findViewById(R.id.newForumDesc);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateForumDialog.this.dismiss();
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent getImg = new Intent(Intent.ACTION_GET_CONTENT);
                    getImg.setType("image/*");
                    Log.d(TAG, "Shazam! ->onClick: starting gallery, code: " + GALLERY_REQUEST);
                    getActivity().startActivityForResult(getImg,GALLERY_REQUEST);
                }
            });

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!imageModified
                        || nome.getText().toString().length() < 3
                        || desc.getText().toString().length() < 5) {
                        Toast.makeText(mView.getContext(), "Precisa inserir todos os dados!", Toast.LENGTH_SHORT).show();
                    } else {
                        final long now = System.currentTimeMillis();
                        progressDialog.setMessage("Criando Fórum...");
                        progressDialog.show();
                        // Insert forum in database
                        StorageReference storage = FirebaseUtils.getMStorage()
                                .child("forum_images")
                                .child("forum:" + String.valueOf(campus) + "-" + String.valueOf(now));

                        storage.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                DatabaseReference ref = FirebaseUtils.getMDatabase()
                                        .child("Forum")
                                        .child(String.valueOf(campus))
                                        .child(String.valueOf(now)); // id do forum vai ser long

                                ref.child("forum_name").setValue(nome.getText().toString());
                                ref.child("forum_description").setValue(desc.getText().toString());
                                ref.child("forum_image").setValue(taskSnapshot.getDownloadUrl().toString());
                                ref.child("forum_posts").setValue(0);
                                ref.child("forum_created").setValue(now);
                                progressDialog.dismiss();
                                CreateForumDialog.this.dismiss();
                            }
                        });
                    }
                }
            });

            return builder.create();
        }

        public void setInfos(int campusId) {
            campus = campusId;
        }

        public void setImage(Bitmap bitmap,Uri url) {
            image.setImageBitmap(bitmap);
            imgUri = url;
            imageModified = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                Log.d(TAG, "Shazam! ->onActivityResult: pegou a imagem");
                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                // Tratar uri, upar no servidor
                dialog.setImage(bm,data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Shazam! ->onActivityResult: resultCode: " + resultCode);
            Log.d(TAG, "Shazam! ->onActivityResult: requestCode: " + requestCode);
        }
    }

    public void setForumsTitle() {
        forumsTitle = findViewById(R.id.forums_name);
        String[] list_campus = getResources().getStringArray(R.array.list_campus);
        forumsTitle.setText("Fóruns - " + list_campus[pref.getInt("Campus",0)]);
    }
}
