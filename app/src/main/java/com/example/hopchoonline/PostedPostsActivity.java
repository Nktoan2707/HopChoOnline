package com.example.hopchoonline;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hopchoonline.callback.MyCallBack;

import java.util.ArrayList;

public class PostedPostsActivity extends AppCompatActivity {
    String idUserLogged;
    TextView txtViewNoResult;

    public static SharedPreferences loggedUsernamePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_posts_list);

        // Lấy id của user
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged","nolog");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtViewNoResult = (TextView)findViewById(R.id.txtViewNoResult);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Truyền vào id của user đang đăng nhập
        loadMyPosts(idUserLogged);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadMyPosts(String idUser) {
        PostDAO postDAO = new PostDAO(this);
        postDAO.getAllMyPosts(idUser, new MyCallBack<ArrayList<Post>>() {
            @Override
            public void onCallback(ArrayList<Post> myPosts) {
                if(myPosts.size() > 0) {
                    txtViewNoResult.setVisibility(View.GONE);
                } else {
                    txtViewNoResult.setText("Chưa có bài đăng nào");
                    txtViewNoResult.setVisibility(View.VISIBLE);
                }

                RecyclerView recyclerView = findViewById(R.id.recycler_view_posted_posts);
                recyclerView.setLayoutManager(new LinearLayoutManager(PostedPostsActivity.this));
                PostedPostItemAdapter adapter = new PostedPostItemAdapter(myPosts, PostedPostsActivity.this, idUserLogged);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
