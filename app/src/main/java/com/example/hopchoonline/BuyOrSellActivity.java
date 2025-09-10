package com.example.hopchoonline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.hopchoonline.callback.MyCallBack;

import java.util.ArrayList;

public class BuyOrSellActivity extends AppCompatActivity {
    ImageView backBtn;
    ListView sellOrBuyListView;
    ArrayList<Post> listBuyOrSell = new ArrayList<>();
    DatabaseHelper databaseHelper = new DatabaseHelper(BuyOrSellActivity.this);
    int idUserLogged;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_sell);

        id = getIntent().getStringExtra("IdUserFromOtherActivityToBuyOrSell");
        sellOrBuyListView = (ListView) findViewById(R.id.listBuyOrSellPost);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        PostDAO postDAO = new PostDAO(BuyOrSellActivity.this);
        postDAO.getAllMyPosts(id, new MyCallBack<ArrayList<Post>>() {
            @Override
            public void onCallback(ArrayList<Post> postsOfOtherUser) {
                sellOrBuyListView.setAdapter(new CustomPostAdapter(BuyOrSellActivity.this, R.layout.post_summary, postsOfOtherUser));
                sellOrBuyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Post post = postsOfOtherUser.get(i);
                        Intent otherProfileToPostDetail = new Intent(BuyOrSellActivity.this, PostActivity.class);
                        otherProfileToPostDetail.putExtra("postAtOtherProfile_PostDetail", post.getId());
                        startActivity(otherProfileToPostDetail);
                    }
                });
            }
        });
    }
}