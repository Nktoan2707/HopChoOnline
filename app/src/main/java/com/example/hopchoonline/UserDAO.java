package com.example.hopchoonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class UserDAO {
    private FirebaseFirestore firebaseFirestore;
    Context context;
    public UserDAO(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

//    public void addUser(User user) {
//        // Convert the Bitmap to a byte array
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
////        user.getAvatar().compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//
//        // Create a new ContentValues object and put the Post data into it
//        ContentValues values = new ContentValues();
//        values.put("username", user.getUsername());
//        values.put("password", user.getPassword());
//        values.put("fullname", user.getFullName());
//        values.put("address", user.getAddress());
//        values.put("phone", user.getPhone());
//        values.put("avatar", byteArray);
//        values.put("isPriority", user.isPriority());
//        values.put("totalRating", user.getRating());
//
//        // Insert the new row into the "post" table
//        db.insert("user", null, values);
//    }

//    public boolean updateUser(User user){
//
//        ContentValues values = new ContentValues();
//        values.put("username", user.getUsername());
//        values.put("password", user.getPassword());
//        values.put("fullname", user.getFullName());
//        values.put("address", user.getAddress());
//        values.put("phone", user.getPhone());
//        int rowsAffected =db.update("user", values, "id=?", new String[] { user.getId()+"" });
//
//        if (rowsAffected > 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    public void updateImage(int idUser, byte[] imageIntoDB) {
//        ContentValues values = new ContentValues();
//        if(Objects.isNull(imageIntoDB) || imageIntoDB.length == 0){
//            Toast.makeText(context, "Cập nhật ảnh đại diện thất bại", Toast.LENGTH_SHORT).show();
//return;
//        }
//        values.put("avatar", imageIntoDB);
//
//        int rowsAffected = db.update("user", values, "id=?", new String[] { idUser+"" });
//
//        if (rowsAffected > 0) {
//            Toast.makeText(context, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, "Cập nhật ảnh đại diện thất bại", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void getPriority_Rating_Address(String idUser, MyCallBack<User> myCallBack) {
        firebaseFirestore.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String address = "";
                    boolean isPriority = false;
                    double rating = 0;
                    User user = null;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userMap = document.getData();
                        String id = document.getId();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if(entry.getKey().equals("address")) {
                                address = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("isPriority")) {
                                isPriority = Boolean.parseBoolean(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("totalRating")) {
                                String ratingStr = entry.getValue().toString();
                                if(!ratingStr.equals("")) {
                                    rating = Double.parseDouble(entry.getValue().toString());
                                }
                            }
                        }

                        if(id.equals(idUser)) {
                            user = new User(address, isPriority, rating);
                            break;
                        }
                    }
                    myCallBack.onCallback(user);
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void getTwoUsers_Priority_Rating_Address(String idUser1, String idUser2, MyCallBack<ArrayList<User>> myCallBack) {
        firebaseFirestore.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String address = "";
                    boolean isPriority = false;
                    double rating = 0;
                    ArrayList<User> users = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if(entry.getKey().equals("address")) {
                                address = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("isPriority")) {
                                isPriority = Boolean.parseBoolean(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("totalRating")) {
                                String ratingStr = entry.getValue().toString();
                                if(!ratingStr.equals("")) {
                                    rating = Double.parseDouble(entry.getValue().toString());
                                }
                            }
                        }

                        if(id.equals(idUser1) || id.equals(idUser2)) {
                            users.add(new User(address, isPriority, rating));
                        }
                    }
                    myCallBack.onCallback(users);
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void registerVip(String idUserLogged, boolean isRegister, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("user").document(idUserLogged).update(
                        "isPriority", isRegister)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Cập nhật thành công
                        myCallBack.onCallback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xảy ra lỗi trong quá trình cập nhật
                        myCallBack.onCallback(false);
                    }
                });
    }

//    public User getUserById(String idUser) {
//        User user = null;
//
//        String sql = "SELECT * FROM user WHERE id=?";
//        Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(idUser) });
//
//        if(cursor.moveToFirst()) {
//            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
//            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
//            String fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
//            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
//            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
//            int isPriority = cursor.getInt(cursor.getColumnIndexOrThrow("isPriority"));
//            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow("totalRating"));
//            // Lấy avatar
//            byte[] avatarByte = cursor.getBlob(6);
//            if(!Objects.isNull(avatarByte)){
//                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarByte, 0, avatarByte.length);
//                user = new User(username, password, fullName, address, phone, "avatarBitmap", isPriority > 0 ? true: false, rating);
//            } else{
//                user = new User(username, password, fullName, address, phone, rating);
//            }
//        }
//        return user;
//    }

    public void getUserById2(String idUser, MyCallBack<User> myCallBack) {
        firebaseFirestore.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String username = "";
                    String password = "";
                    String fullName = "";
                    String address = "";
                    String phone = "";
                    boolean isPriority = false;
                    double rating = 0;
                    User user = null;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userMap = document.getData();
                        String id = document.getId();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("username")) {
                                username = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("password")) {
                                password = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("fullName")) {
                                fullName = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("address")) {
                                address = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("phone")) {
                                phone = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("isPriority")) {
                                isPriority = Boolean.parseBoolean(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("totalRating")) {
                                rating = Double.parseDouble(entry.getValue().toString());
                            }
                        }

                        if(id.equals(idUser)) {
                            user = new User(id, username, password, fullName, address, phone, isPriority, rating, null);
                            break;
                        }
                    }
                    myCallBack.onCallback(user);
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }
}