package com.example.hopchoonline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends Activity {
    DatabaseHelper databaseHelper=new DatabaseHelper(MainActivity.this);
    ListView optionEditListView;
    ListView postOptionListView;
    ListView logoutOptionsListView;
    ArrayList<Option> editProfileOptions = new ArrayList<Option>();
    ArrayList<Option> postOptions = new ArrayList<Option>();
    ArrayList<Option> logoutOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_activity);

//        editProfileOptions.add(new Option("Chỉnh sửa", R.drawable.setting));
//        postOptions.add(new Option("Tin đã đăng", R.drawable.post));
//        postOptions.add(new Option("Tin đã lưu", R.drawable.save_post));
//
//        logoutOptions.add(new Option("Đăng xuất", R.drawable.logout));
//        optionEditListView = (ListView) findViewById(R.id.listOptionsEdit);
//        postOptionListView = (ListView) findViewById(R.id.listOptionsPost);
//        logoutOptionsListView = (ListView) findViewById(R.id.logoutOption);
//        optionEditListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(MainActivity.this, EditProfile.class);
//                startActivity(intent);
//            }
//        });
//        boolean check = databaseHelper.addOne();
//        if (check) {
//            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
//        }

//        postOptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });
//
//        logoutOptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });
//        optionEditListView.setAdapter(new CustomOptionAdapter(this, R.layout.option, editProfileOptions));
//        postOptionListView.setAdapter(new CustomOptionAdapter(this, R.layout.option, postOptions));
//        logoutOptionsListView.setAdapter(new CustomOptionAdapter(this, R.layout.option, logoutOptions));

    }
}