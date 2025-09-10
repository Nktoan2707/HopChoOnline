package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.hopchoonline.callback.MyCallBack;

import java.util.ArrayList;

public class SavedPosts extends AppCompatActivity {
    String idUserLogged;
    TextView txtViewNoResult;

    public static SharedPreferences loggedUsernamePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_posts_activity);

        // Lấy id của user
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged","nolog");
        txtViewNoResult = (TextView)findViewById(R.id.txtViewNoResult);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Truyền vào id của user đang đăng nhập
        loadSavedPosts(idUserLogged);
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

    public void loadSavedPosts(String idUser) {
        SavedPostDAO savedPostDAO = new SavedPostDAO(this);
        savedPostDAO.getAllSavedPosts(idUserLogged, new MyCallBack<ArrayList<Post>>() {
            @Override
            public void onCallback(ArrayList<Post> savedPosts) {
                if(savedPosts.size() > 0) {
                    txtViewNoResult.setVisibility(View.GONE);
                } else {
                    txtViewNoResult.setText("Chưa có tin lưu");
                    txtViewNoResult.setVisibility(View.VISIBLE);
                }

                RecyclerView recyclerView = findViewById(R.id.recycler_view_saved_posts);
                recyclerView.setLayoutManager(new LinearLayoutManager(SavedPosts.this));
                SavedPostItemAdapter adapter = new SavedPostItemAdapter(savedPosts, SavedPosts.this, idUserLogged);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}