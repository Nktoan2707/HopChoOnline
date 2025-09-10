package com.example.hopchoonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostDAO {
    private FirebaseFirestore firebaseFirestore;
    Context context;
    public PostDAO(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPost(String idPost, MyCallBack<Post> myCallBack) {
        firebaseFirestore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String title = "";
                    String imageUrl = "";
                    String description = "";
                    String location = "";
                    String author = "";
                    String dateStr = "";
                    String durationStr = "";
                    int price = 0;
                    boolean isBuy = true;


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userMap = document.getData();
                        String id = document.getId();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("title")) {
                                title = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("imageUrl")) {
                                imageUrl = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("description")) {
                                description = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("location")) {
                                location = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("author")) {
                                author = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("price")) {
                                price = Integer.parseInt(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("isBuy")) {
                                isBuy = Boolean.parseBoolean(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("date")) {
                                long timestamp;

                                Object timestampObj = entry.getValue();

                                // Kiểm tra nếu đối tượng là một Firebase Timestamp
                                if (timestampObj instanceof Timestamp) {
                                    timestamp = ((Timestamp) timestampObj).getSeconds() * 1000; // Chuyển đổi giây thành mili-giây
                                } else {
                                    timestamp = Long.parseLong(timestampObj.toString());
                                }

                                Date date = new Date(timestamp);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                dateStr = sdf.format(date);

                                sdf = new SimpleDateFormat("HH:mm:ss");
                                durationStr = sdf.format(date);
                            }
                        }

                        if(id.equals(idPost)) {
                            myCallBack.onCallback(new Post(id, title, imageUrl, price, isBuy, description, location, author, dateStr, durationStr));
                            break;
                        }
                    }
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

//    public ArrayList<Post> getAllPosts() {
//        ArrayList<Post> posts = new ArrayList<>();
//
//        String sql = "SELECT * FROM post";
//        Cursor cursor = db.rawQuery(sql, null);
//
//        while(cursor.moveToNext()) {
//            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
//            // Lưu ảnh
//            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imageProduct"));
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//
//            int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
//            boolean isBuy = true;
//            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
//            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
//            int idUser = cursor.getInt(cursor.getColumnIndexOrThrow("idUser"));
//            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
//            String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
//
//            Post post = new Post(id+"", title, "bitmap", price, isBuy, description, location, "idUser", date, duration);
//            posts.add(post);
//        }
//
//        return posts;
//    }
    public void getAllTitlesProduct(MyCallBack<ArrayList<String>> myCallBack) {
        firebaseFirestore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> keywords = new ArrayList<>();
                String keyword = "";

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("title")) {
                                keyword = entry.getValue().toString();
                                keywords.add(keyword);
                            }
                        }
                    }
                    myCallBack.onCallback(keywords);
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

//    public void updateImage(int idPost, byte[] imageIntoDB) {
//        ContentValues values = new ContentValues();
//        values.put("imageProduct", imageIntoDB);
//
//        int rowsAffected = db.update("post", values, "id=?", new String[] { idPost+"" });
//
//        if (rowsAffected > 0) {
//            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public boolean updatePost(int idPost, String title, String
//            description, int price, byte[]imageProductByte, boolean isBuy, String location) {
//
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String currentDate = dateFormat.format(calendar.getTime());
//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        String currentTime = timeFormat.format(calendar.getTime());
//
//        ContentValues values = new ContentValues();
//        values.put("title", title);
//        values.put("imageProduct", imageProductByte);
//        values.put("price", price);
//        values.put("location", location);
//        values.put("description", description);
//        values.put("isBuy", isBuy);
//        values.put("date", currentDate);
//        values.put("duration", currentTime);
//
//        int rowsAffected = db.update("post", values, "id=?", new String[] { idPost+"" });
//
//        if (rowsAffected > 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public void updatePostWithoutSelectingImage(String idPost, String title, int price, String description, boolean isBuy, String imageUrl,
                                                String location, Timestamp date,  MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("post").document(idPost).update(
                        "title", title,
                        "price", price,
                        "description", description,
                        "isBuy", isBuy,
                        "imageUrl", imageUrl,
                        "location", location,
                        "date", date)
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

    public void updatePost(String idPost, String title, int price, String description, boolean isBuy, byte[] imageData, String imagePathFile, String location, MyCallBack<Boolean> myCallBack) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post").document(idPost).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference imagesRef = storageRef.child("images");
                        StorageReference imageRef = imagesRef.child(imagePathFile);
                        UploadTask uploadTask = imageRef.putBytes(imageData);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Image uploaded successfully
                                // Get the download URL of the image
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Save the download URL to Firestore
                                        Map<String, Object> post = new HashMap<>();
                                        post.put("title", title);
                                        post.put("price", price);
                                        post.put("location", location);
                                        post.put("isBuy", isBuy);
                                        post.put("imageUrl", uri.toString());
                                        post.put("description", description);
                                        // Tạo đối tượng Date với thời gian hiện tại
                                        Date now = new Date();
                                        // Tạo đối tượng Timestamp từ đối tượng Date
                                        Timestamp date = new Timestamp(now.getTime() / 1000, (int) ((now.getTime() % 1000) * 1000000));
                                        post.put("date", date);
                                        db.collection("post").document(idPost).update(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                myCallBack.onCallback(true);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                myCallBack.onCallback(false);
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    } else {
                        // Document does not exist
                        myCallBack.onCallback(false);
                    }
                } else {
                    // Handle errors here
                    myCallBack.onCallback(false);
                }
            }
        });
    }

    public void searchPosts(String query, MyCallBack<ArrayList<Post>> myCallBack) {
        firebaseFirestore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Post> searchedResults = new ArrayList<>();
                String id = "";
                String title = "";
                String imageUrl = "";
                int price = 0;
                boolean isBuy = true;
                String description = "";
                String location = "";
                String author = "";
                String dateStr = "";
                String durationStr = "";

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("title")) {
                                title = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("imageUrl")) {
                                imageUrl = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("price")) {
                                price = Integer.parseInt(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("isBuy")) {
                                isBuy = Boolean.parseBoolean(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("description")) {
                                description = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("location")) {
                                location = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("author")) {
                                author = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("date")) {
                                long timestamp;

                                Object timestampObj = entry.getValue();

                                // Kiểm tra nếu đối tượng là một Firebase Timestamp
                                if (timestampObj instanceof Timestamp) {
                                    timestamp = ((Timestamp) timestampObj).getSeconds() * 1000; // Chuyển đổi giây thành mili-giây
                                } else {
                                    timestamp = Long.parseLong(timestampObj.toString());
                                }

                                Date date = new Date(timestamp);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                dateStr = sdf.format(date);

                                sdf = new SimpleDateFormat("HH:mm:ss");
                                durationStr = sdf.format(date);
                            }
                        }

                        if(title.contains(query)) {
                            searchedResults.add(new Post(id, title, imageUrl, price, isBuy, description, location, author, dateStr, durationStr));
                        }
                    }
                    myCallBack.onCallback(searchedResults);
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void getAllMyPosts(String idUserLogged, MyCallBack<ArrayList<Post>> myCallBack) {
        firebaseFirestore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String title = "";
                    String imageUrl = "";
                    String description = "";
                    String location = "";
                    String author = "";
                    String dateStr = "";
                    String durationStr = "";
                    int price = 0;
                    boolean isBuy = true;

                    ArrayList<Post> myPosts = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> userMap = document.getData();
                        String id = document.getId();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("title")) {
                                title = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("imageUrl")) {
                                imageUrl = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("description")) {
                                description = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("location")) {
                                location = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("author")) {
                                author = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("price")) {
                                price = Integer.parseInt(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("isBuy")) {
                                isBuy = Boolean.parseBoolean(entry.getValue().toString());
                            }
                            if(entry.getKey().equals("date")) {
                                long timestamp;

                                Object timestampObj = entry.getValue();

                                // Kiểm tra nếu đối tượng là một Firebase Timestamp
                                if (timestampObj instanceof Timestamp) {
                                    timestamp = ((Timestamp) timestampObj).getSeconds() * 1000; // Chuyển đổi giây thành mili-giây
                                } else {
                                    timestamp = Long.parseLong(timestampObj.toString());
                                }

                                Date date = new Date(timestamp);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                dateStr = sdf.format(date);

                                sdf = new SimpleDateFormat("HH:mm:ss");
                                durationStr = sdf.format(date);
                            }
                        }

                        if(author.equals(idUserLogged)) {
                            myPosts.add(new Post(id, title, imageUrl, price, isBuy, description, location, author, dateStr, durationStr ));
                        }
                        myCallBack.onCallback(myPosts);
                    }
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
//        ArrayList<Post> myPosts = new ArrayList<>();
//        String sql = "SELECT * FROM post WHERE idUser=?";
//        Cursor cursor = db.rawQuery(sql, new String[] {idUserLogged+""});
//
//        while (cursor.moveToNext()) {
//            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
//            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
//
//            byte[] imageProductByte = cursor.getBlob(4);
//            Bitmap imageProductBitmap = BitmapFactory.decodeByteArray(imageProductByte, 0, imageProductByte.length);
//
//            int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
//            boolean isBuy = true;
//
//            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
//            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
//            int idUser = cursor.getInt(cursor.getColumnIndexOrThrow("idUser"));
//            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
//            String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
//
//            myPosts.add(new Post(id+"", title, "imageProductBitmap", price, isBuy, description, location, "idUser", date, duration));
//        }
//
//        return myPosts;
    }

    public void removeMyPost(String idPost, String idUserLogged, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("post")
                .whereEqualTo("author", idUserLogged)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                if(id.equals(idPost)) {
                                    document.getReference().delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                myCallBack.onCallback(true);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                myCallBack.onCallback(false);
                                            }
                                        });
                                }
                            }
                        } else {
                            myCallBack.onCallback(false);
                        }
                    }
                });
    }

}