package com.example.hopchoonline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedPostDAO {
    private FirebaseFirestore firebaseFirestore;
    Context context;

    public SavedPostDAO(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void savePost(String idUser, String idPost, MyCallBack<Boolean> myCallBack) {
        Map<String, Object> values = new HashMap<>();
        values.put("idUser", idUser);
        values.put("idPost", idPost);

        firebaseFirestore.collection("saved_post")
                .add(values)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
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

    public void removeSavedPost(String idUser, String idPost, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("saved_post")
                .whereEqualTo("idUser", idUser)
                .whereEqualTo("idPost", idPost)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                firebaseFirestore.collection("saved_post").document(document.getId())
                                        .delete()
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
                        } else {
                            myCallBack.onCallback(false);
                        }
                    }
                });
    }

    public void getAllSavedPosts(String idUser, final MyCallBack<ArrayList<Post>> myCallBack) {
        firebaseFirestore.collection("saved_post")
                .whereEqualTo("idUser", idUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final ArrayList<Post> savedPosts = new ArrayList<>();
                            final int[] count = {task.getResult().size()};  // counter for knowing when all posts are retrieved
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String postId = (String) document.get("idPost");
                                firebaseFirestore.collection("post").document(postId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    String id = document.getId();
                                                    String title = document.getString("title");
                                                    String imageUrl = document.getString("imageUrl");
                                                    int price = document.getLong("price").intValue();
                                                    boolean isBuy = document.getBoolean("isBuy");
                                                    String description = document.getString("description");
                                                    String location = document.getString("location");
                                                    String author = document.getString("author");
                                                    long timestamp;

                                                    Object timestampObj = document.get("date");

                                                    // Kiểm tra nếu đối tượng là một Firebase Timestamp
                                                    if (timestampObj instanceof Timestamp) {
                                                        timestamp = ((Timestamp) timestampObj).getSeconds() * 1000; // Chuyển đổi giây thành mili-giây
                                                    } else {
                                                        timestamp = Long.parseLong(timestampObj.toString());
                                                    }

                                                    Date date = new Date(timestamp);

                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                    String dateStr = sdf.format(date);

                                                    sdf = new SimpleDateFormat("HH:mm:ss");
                                                    String durationStr = sdf.format(date);

                                                    savedPosts.add(new Post(id, title, imageUrl, price, isBuy, description, location, author, dateStr, durationStr));
                                                }
                                                count[0] -= 1;
                                                if (count[0] == 0) {
                                                    myCallBack.onCallback(savedPosts);
                                                }
                                            }
                                        });
                            }
                        } else {
                            myCallBack.onCallback(null);
                        }
                    }
                });
    }

    public void isSavedPost(String idUser, String idPost, MyCallBack<Boolean> myCallBack) {
        firebaseFirestore.collection("saved_post")
                .whereEqualTo("idUser", idUser)
                .whereEqualTo("idPost", idPost)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isSaved = !task.getResult().isEmpty();
                        myCallBack.onCallback(isSaved);
                    } else {
                        myCallBack.onCallback(false);
                    }
                });
    }
}
