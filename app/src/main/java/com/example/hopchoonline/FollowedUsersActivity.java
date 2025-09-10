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
import java.util.stream.Stream;

public class FollowedUsersActivity extends AppCompatActivity {
    String idUserLogged;
    TextView txtViewNoResult;
    DatabaseHelper databaseHelper=new DatabaseHelper(FollowedUsersActivity.this);
    RecyclerView recyclerView;
    FollowedUserItemAdapter adapter;
    public static SharedPreferences loggedUsernamePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_users);

        // Lấy id của user
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged","nolog");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtViewNoResult = (TextView)findViewById(R.id.txtViewNoResult);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Truyền vào id của user đang đăng nhập
        loadFollowedUser(idUserLogged);
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

    public void loadFollowedUser(String idUser) {

        databaseHelper.getFollowingUser(
                idUserLogged, new MyCallBack<ArrayList<Follow>>() {
                    @Override
                    public void onCallback(ArrayList<Follow> followingUserList) {
                        if(followingUserList.size() > 0) {
                            txtViewNoResult.setVisibility(View.GONE);
                        } else {
                            txtViewNoResult.setText("Chưa theo dõi ai");
                            txtViewNoResult.setVisibility(View.VISIBLE);
                        }

                        databaseHelper.getListUser(new MyCallBack<ArrayList<User>>() {
                                                       @Override
                                                       public void onCallback(ArrayList<User> users) {
                                                           ArrayList<User> usersFollowingList = new ArrayList<>();
//                                                            usersFollowingList.add(
//                                                                    users.stream().filter(u->u.getId().equals())
//                                                            )
                                                           for (Follow followIterator : followingUserList){
                                                               for (User userIterator : users){
                                                                   if(followIterator.getIdUserFollowing().equals(userIterator.getId())){
                                                                       usersFollowingList.add(userIterator);
                                                                       break;
                                                                   }
                                                               }
                                                           }
                                                           recyclerView = findViewById(R.id.recycler_view_followed_list);
                                                           recyclerView.setLayoutManager(new LinearLayoutManager(FollowedUsersActivity.this));
                                                           adapter = new FollowedUserItemAdapter(usersFollowingList, FollowedUsersActivity.this, idUserLogged);
                                                           recyclerView.setAdapter(adapter);
                                                       }
                                                   });



                    }
                }
        );


    }
}