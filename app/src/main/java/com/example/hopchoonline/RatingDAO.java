package com.example.hopchoonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class RatingDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    Context context;
    public RatingDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
//        db = dbHelper.getWritableDatabase();

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public boolean addRating(Rating rating) {
//
//        ContentValues values = new ContentValues();
//        values.put("voteUserId", rating.getVoteUserId());
//        values.put("receivedUserId", rating.getReceivedUserId());
//        values.put("point", rating.getPoint());
//        values.put("content", rating.getContent());
//        db.insert("rating", null, values);
//
//        UserDAO userDAO = new UserDAO(context);
//        User userGet = userDAO.getUserById(rating.getReceivedUserId());
//        double currentRating = 0;
//
//        if(Objects.nonNull(userGet)){
//           currentRating=  userGet.getRating();
//        }
//        double updateRating = 0;
//        if(currentRating == 0){
//            updateRating = rating.getPoint();
//        } else {
//             updateRating = (currentRating + rating.getPoint())*1.0/2;
//
//        }
//        ContentValues values2 = new ContentValues();
//        values2.put("totalRating", updateRating);
//        String selection = "id = ?";
//        String[] args = { rating.getReceivedUserId() + "" };
//        int count = db.update("user", values2, selection, args);
//        if(count > 0){
//            Toast.makeText(context, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
//            return true;
//        } else{
//            Toast.makeText(context, "Đánh giá thất bại", Toast.LENGTH_SHORT).show();
//            return  false;
//        }
//    }

    public ArrayList<Rating> getAllUserRating(String receivedUserIdParam) {
//        ArrayList<Rating> ratings = new ArrayList<>();
//        String sql = "SELECT * FROM rating WHERE receivedUserId=?";
//        Cursor cursor = db.rawQuery(sql, new String[] {receivedUserIdParam+""});
//
//        while (cursor.moveToNext()) {
//            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//            int voteUserId = cursor.getInt(cursor.getColumnIndexOrThrow("voteUserId"));
//            int receivedUserId = cursor.getInt(cursor.getColumnIndexOrThrow("receivedUserId"));
//            int point = cursor.getInt(cursor.getColumnIndexOrThrow("point"));
//            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
//
//            ratings.add(new Rating(voteUserId,receivedUserId, point, content));
//        }
//
//        return ratings;
        return null;
    }

    public double getRatingOfUser(int receivedUserIdParam){
        ArrayList<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM rating WHERE receivedUserId=?";
        Cursor cursor = db.rawQuery(sql, new String[] {receivedUserIdParam+""});
        double sumRating = 0;
        int count = 0;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int point = cursor.getInt(cursor.getColumnIndexOrThrow("point"));
            sumRating += point;
            count += 1;

        }
        return sumRating * 1.0/count;

    }

    public void close() {
        db.close();
    }
}
