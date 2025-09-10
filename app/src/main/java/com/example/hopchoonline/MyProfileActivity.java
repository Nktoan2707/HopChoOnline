package com.example.hopchoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyProfileActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(MyProfileActivity.this);
    BottomNavigationView bottomNavigationView;
    String usernameLogged = null;
    String idUserLogged;
    TextView usernameTextView;
    TextView fullNameTextView;
    RatingBar ratingBarProfile;
    TextView btnEditProfile;
    TextView txtViewLogout;
    TextView addressTextView;
    TextView txtViewPostedItems;
    TextView txtViewSavedItems;
    TextView txtViewRegisterVip;
ImageView avtProfile;
    TextView txtFollowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_activity);

        usernameLogged = Login.loggedUsernamePref.getString("usernameLogged", "nolog");
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigateView);
        txtViewLogout = (TextView) findViewById(R.id.txtViewLogout);
        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        fullNameTextView = (TextView) findViewById(R.id.fullNameTextView);
        ratingBarProfile = (RatingBar) findViewById(R.id.ratingBarProfile);
        addressTextView = (TextView) findViewById(R.id.addressTextView);

        txtViewPostedItems = (TextView) findViewById(R.id.posted_items);
        txtViewSavedItems = (TextView) findViewById(R.id.saved_items);
        txtViewRegisterVip = (TextView) findViewById(R.id.register_vip);

        avtProfile = (ImageView) findViewById(R.id.avatarImg);

        //showUserInfo(idUserLogged);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addPostItemMenu:
                        // Xử lý sự kiện click trên mục menu 1 ở đây
                        Toast.makeText(MyProfileActivity.this, "Add Post", Toast.LENGTH_SHORT).show();
                        Intent moveToCreatePost = new Intent(MyProfileActivity.this, CreatePostActivity.class);
                        moveToCreatePost.putExtra("MyProfileToCreatePost",usernameLogged);
                        startActivity(moveToCreatePost);
                        return;
                    case R.id.homeItemMenu:
                        Intent moveToHomePage = new Intent(MyProfileActivity.this,HomePageActivity.class);
                        startActivity(moveToHomePage);
                        break;
                    case R.id.profileItemMenu:
//                        Intent moveToProfile = new Intent(MyProfileActivity.this, MyProfileActivity.class);
//                        startActivity(moveToProfile);
                    case R.id.mapItemMenu:
                        Intent moveToFindPostByDistance = new Intent(MyProfileActivity.this, FindPostsByDistanceActivity.class);
                        startActivity(moveToFindPostByDistance);
                        break;
                    default:
                        return;
                }
            }
        });

        txtViewPostedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToPostedPosts = new Intent(MyProfileActivity.this, PostedPostsActivity.class);
                moveToPostedPosts.putExtra("idUserLogged", idUserLogged);
                startActivity(moveToPostedPosts);
            }
        });
        txtViewSavedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToSavedPosts = new Intent(MyProfileActivity.this, SavedPosts.class);
                moveToSavedPosts.putExtra("idUserLogged", idUserLogged);
                startActivity(moveToSavedPosts);
            }
        });

        txtViewRegisterVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToRegisterVip = new Intent(MyProfileActivity.this, RegisterVipActivity.class);
                moveToRegisterVip.putExtra("idUserLogged", idUserLogged);
                startActivity(moveToRegisterVip);
            }
        });

        txtFollowList = (TextView) findViewById(R.id.txtFollowingList);

        showUserInfo(idUserLogged);
        bottomNavigationView
                .setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addPostItemMenu:
                                // Xử lý sự kiện click trên mục menu 1 ở đây
                                Intent moveToCreatePost = new Intent(MyProfileActivity.this, CreatePostActivity.class);
                                moveToCreatePost.putExtra("MyProfileToCreatePost", usernameLogged);
                                startActivity(moveToCreatePost);
                                return;
                            case R.id.homeItemMenu:
                                Intent moveToHomePage = new Intent(MyProfileActivity.this, HomePageActivity.class);
                                startActivity(moveToHomePage);
                                break;
                            case R.id.profileItemMenu:
                                // Intent moveToProfile = new Intent(MyProfileActivity.this,
                                // MyProfileActivity.class);
                                // startActivity(moveToProfile);
                                return;
                            case R.id.mapItemMenu:
                                Intent moveToFindPostByDistance = new Intent(MyProfileActivity.this,
                                        FindPostsByDistanceActivity.class);
                                startActivity(moveToFindPostByDistance);
                                break;
                            default:
                                return;
                        }
                    }
                });

        txtViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backLoginActivity = new Intent(MyProfileActivity.this, Login.class);
                startActivity(backLoginActivity);
            }
        });

        txtFollowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToFollowedUserActivity = new Intent(MyProfileActivity.this, FollowedUsersActivity.class);
                startActivity(moveToFollowedUserActivity);

            }
        });

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callEditProfileIntent = new Intent(MyProfileActivity.this, EditProfile.class);
                startActivity(callEditProfileIntent);
            }
        });
    }

    private void showUserInfo(String id) {
        databaseHelper.getUserById(id, new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                usernameTextView.setText(user.getUsername());
                fullNameTextView.setText(user.getFullName());
                addressTextView.setText(user.getAddress());
                ratingBarProfile.setRating((float) user.getRating());
                Glide.with(MyProfileActivity.this).load(user.getAvatarUrl())
                        .placeholder(R.drawable.avatar_default)
                        .into(avtProfile);
            }
        });
    }
}