package com.example.hopchoonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.util.ArrayList;

public class FollowedDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    Context context;
    public FollowedDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
//        db = dbHelper.getWritableDatabase();
    }

    public boolean followUser(String idUser, String idUserFollowing) {
//        ContentValues values = new ContentValues();
//        values.put("idUser", idUser);
//        values.put("idUserFollowing", idUserFollowing);
//
//        long result = db.insert("following", null, values);
//        if(result > 0) {
//            return true;
//        }
        return false;
    }

    public boolean unfollowUser(String idUser, String idUserFollowing) {
//        int result = db.delete("following", "idUser=? AND idUserFollowing=?",
//                new String[] {idUser+"", idUserFollowing+""});
//
//        if(result > 0) {
//            return true;
//        }

        return false;
    }

    public int getFollowedUser(String id) {
        ContentValues values = new ContentValues();
//        values.put("idUser", idUser);
//        values.put("idUserFollowing", idUserFollowing);

//        long result = db.query("following", null, values);

        String[] selectionArgs = { id + "" };
        Cursor cursor = db.query("following", null, "id = ?", selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            return cursor.getInt(cursor.getColumnIndexOrThrow("idUserFollowing"));

        }
        return -1;
    }

    public ArrayList<User> getFollowedList(int idUser) {
        String sql = "SELECT * FROM user, following WHERE following.idUser = ? and following.idUserFollowing = user.id";
        Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(idUser) });

        ArrayList<User> followedUsers = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));


            followedUsers.add(new User(id, username, password, username, address));
        }
        return followedUsers;
    }

    public boolean isFollowed(String idUser, String idUserFollowing) {
        String sql = "SELECT * FROM following WHERE idUser=? AND idUserFollowing=?";
        Cursor cursor = db.rawQuery(sql, new String[] { idUser+"", idUserFollowing+"" } );

        if(cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public void close() {
        db.close();
    }
}
