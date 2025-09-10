package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hopchoonline.callback.MyCallBack;

import java.util.ArrayList;

public class PostCommentsActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(PostCommentsActivity.this);
    String idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");

    ImageView btnBack;

    RecyclerView commentRecView;
    PostCommentRecyclerViewAdapter commentRecViewAdapter;

    EditText commentContent;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent callerIntent = getIntent();
        if (callerIntent != null) {
            String postId = callerIntent.getStringExtra("postId");
            databaseHelper.getPostById(postId, new MyCallBack<Post>() {
                @Override
                public void onCallback(Post post) {
                    databaseHelper.getCommentsByPostId(postId, new MyCallBack<ArrayList<PostComment>>() {
                        @Override
                        public void onCallback(ArrayList<PostComment> postComments) {
                            commentRecView = findViewById(R.id.commentRecView);
                            commentRecViewAdapter = new PostCommentRecyclerViewAdapter(PostCommentsActivity.this, postComments);
                            commentRecView.setAdapter(commentRecViewAdapter);
                            commentRecView.setLayoutManager(new LinearLayoutManager(PostCommentsActivity.this, LinearLayoutManager.VERTICAL, false));

                            commentContent = findViewById(R.id.commentContent);
                        }
                    });


                    btnSend = findViewById(R.id.btnSend);
                    btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PostComment postComment = new PostComment(idUserLogged, post.getId(), commentContent.getText() + "");
                            databaseHelper.addPostComment(postComment, new MyCallBack<Boolean>() {
                                @Override
                                public void onCallback(Boolean result) {
                                    if (result) {
                                        commentRecViewAdapter.addPostComment(postComment);
                                    } else {
                                        Toast.makeText(PostCommentsActivity.this, "Error: comment is not sent!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            });
        }
    }
}