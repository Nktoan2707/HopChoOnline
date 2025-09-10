package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Objects;

public class RatingPage extends AppCompatActivity {
    RatingBar edtStar;
    EditText ratingCommentGet;

    private MaterialButton btnSave;
    String idUserLogged;
    String usernameLogged = null;

    DatabaseHelper databaseHelper = new DatabaseHelper(RatingPage.this);

    boolean isValid;
    ArrayList<Rating> ratingArrayList;
    RecyclerView ratingRecycleview;

    RatingListAdapter ratingListAdapter;
    String idUserReceivedVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        idUserReceivedVote = getIntent().getStringExtra("FromOtherProfileToRating");
        usernameLogged = Login.loggedUsernamePref.getString("usernameLogged", "nolog");
        idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");

        btnSave = (MaterialButton) findViewById(R.id.btn_save);
        Intent callerIntent = getIntent();
        RatingDAO ratingDAO1 = new RatingDAO(RatingPage.this);
        if (callerIntent != null) {
            // ratingArrayList = ratingDAO1.getAllUserRating(id);

            databaseHelper.getAllUserRating(idUserReceivedVote, new MyCallBack<ArrayList<Rating>>() {
                @Override
                public void onCallback(ArrayList<Rating> ratingArrayList) {
                    ratingRecycleview = findViewById(R.id.ratingRecycleView);
                    ratingListAdapter = new RatingListAdapter(RatingPage.this, ratingArrayList);
                    ratingRecycleview.setAdapter(ratingListAdapter);
                    ratingRecycleview.setLayoutManager(
                            new LinearLayoutManager(RatingPage.this, LinearLayoutManager.VERTICAL, false));
                    edtStar = (RatingBar) findViewById(R.id.edtStar);
                    ratingCommentGet = (EditText) findViewById(R.id.edtRating);

                }
            });
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
                String ratingComment = ratingCommentGet.getText().toString();
                Integer starNumber = 0;
                starNumber = Math.round(edtStar.getRating());

                // check
                if (starNumber > 5 || starNumber < 0) {
                    Toast.makeText(RatingPage.this, "Số sao cần phải nằm trong khoảng từ 0 tới 5", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (ratingComment.isEmpty()) {
                    Toast.makeText(RatingPage.this, "Bạn cần nhập vào đánh giá chi tiết", Toast.LENGTH_SHORT).show();
                    return;
                }

                // save to DB
                Rating rating = new Rating(idUserLogged, idUserReceivedVote, starNumber, ratingComment);

                databaseHelper.addRating(rating, new MyCallBack<Boolean>() {
                    @Override
                    public void onCallback(Boolean result) {
                        if (result) {
                            databaseHelper.getUserById(idUserReceivedVote, new MyCallBack<User>() {
                                @Override
                                public void onCallback(User user) {
                                    if (Objects.nonNull(user)) {
                                        if(user.getRating() == 0){
                                            user.setRating((rating.getPoint()));
                                        }else{
                                            user.setRating((rating.getPoint() + user.getRating()) * 1.0 / 2);
                                        }
                                        databaseHelper.updateUserRating(user, new MyCallBack<Boolean>() {
                                            @Override
                                            public void onCallback(Boolean success) {
                                                if (success) {
                                                    ratingListAdapter.addRating(rating);
                                                    Intent moveToOtherProfile = new Intent(RatingPage.this,
                                                            OtherProfileActivity.class);
                                                    moveToOtherProfile.putExtra("FromPostDetailToOtherProfile",
                                                            idUserReceivedVote);
                                                    startActivity(moveToOtherProfile);
                                                } else {
                                                    Toast.makeText(RatingPage.this, "Lỗi không thể đánh giá",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RatingPage.this, "Error: comment is not sent!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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

}