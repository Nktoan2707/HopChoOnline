package com.example.hopchoonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.core.OrderBy;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DatabaseHelper {

    private FirebaseFirestore firebaseFirestore;

    DatabaseHelper(Context parentActivity) {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    DatabaseHelper() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void getListUser(MyCallBack<ArrayList<User>> myCallBack) {
        ArrayList<User> listUser = new ArrayList<>();
        firebaseFirestore.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String id = "";
                String username = "";
                String password = "";
                String fullname = "";
                String address = "";
                String phone = "";
                double rating = 0.0;
                String avatarUrl = "";
                byte[] avatar;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("username")) {
                                username = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("password")) {
                                password = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("fullName")) {
                                fullname = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("address")) {
                                address = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("phone")) {
                                phone = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("rating")) {
                                if(entry.getValue().toString().isEmpty()){
                                    rating = 0.0;
                                } else{
                                    rating = Double.parseDouble(entry.getValue().toString());
                                }
                            }
                            if(entry.getKey().equals("avatarUrl")){
                                avatarUrl = entry.getValue().toString();
                            }
                        }
                        listUser.add(new User(id, username, password, fullname, address, phone, rating, avatarUrl));
                    }
                    myCallBack.onCallback(listUser);
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void getListUserByUsername(String usernameQuery, MyCallBack<ArrayList<User>> myCallBack) {
        Task<QuerySnapshot> task = firebaseFirestore.collection("user").get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<User> users = new ArrayList<>();
                String id = "";
                String username = "";
                String password = "";
                String fullname = "";
                String address = "";
                String phone = "";
                double rating = 0.0;
                byte[] avatar;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("username")) {
                                username = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("password")) {
                                password = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("fullName")) {
                                fullname = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("address")) {
                                address = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("phone")) {
                                phone = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("rating")) {
                                if(entry.getValue().toString().isEmpty()){
                                    rating = 0.0;
                                } else{
                                    rating = Double.parseDouble(entry.getValue().toString());
                                }
                            }
                        }
                        if (username.equals(usernameQuery)) {
                            users.add(new User(id, username, password, fullname, address, phone,rating));
                            myCallBack.onCallback(users);
                            return;
                        }
                    }
                    users.add(new User("mock","mock","mock","mock"));
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void getUserById(String idUser, MyCallBack<User> myCallBack) {
        ArrayList<User> user = new ArrayList<>();
        firebaseFirestore.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String id = "";
                String username = "";
                String password = "";
                String fullname = "";
                String address = "";
                String phone = "";
                double rating = 0.0;
                String avatarUrl = "";
                byte[] avatar;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("username")) {
                                username = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("password")) {
                                password = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("fullName")) {
                                fullname = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("address")) {
                                address = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("phone")) {
                                phone = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("totalRating")) {
                                if(entry.getValue().toString().isEmpty()){
                                    rating = 0.0;
                                } else{
                                    rating = Double.parseDouble(entry.getValue().toString());
                                }
                            }
                            if (entry.getKey().equals("avatarUrl")) {
                                avatarUrl = entry.getValue().toString();
                            }
                        }
                        if (id.equals(idUser)) {
                            user.add(new User(id, username, password, fullname, address, phone,rating, avatarUrl));
                            myCallBack.onCallback(user.get(0));
                            break;
                        }
                    }
                } else {
                    Log.e("error", "Some thing went wrong");

                }
            }
        });
    }

    public void addUser(User user, MyCallBack<Boolean> myCallBack) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", user.getUsername());
        newUser.put("password", user.getPassword());
        newUser.put("fullName",user.getFullName() );
        newUser.put("address",user.getAddress());
        newUser.put("phone","");
        newUser.put("isPriority",false);
        newUser.put("avatarUrl", "");
        newUser.put("totalRating", "");
        firebaseFirestore.collection("user").add(newUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e("Error", "Success");
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

    public void isExistUsername(String usernameQuery, MyCallBack<Boolean> myCallBack) {
        getListUserByUsername(usernameQuery, new MyCallBack<ArrayList<User>>() {
            @Override
            public void onCallback(ArrayList<User> users) {
                User userExist = users.get(0);
                if(!userExist.getUsername().equals("mock")){
                    myCallBack.onCallback(true);
                }else{
                    myCallBack.onCallback(false);
                }
            }
        });
    }
                              

    public void addPost(String title,int price, String description,boolean isSell, byte[] imageData, String imagePathFile,String idAuthor,String address, MyCallBack<Boolean> myCallBack) {
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
                        post.put("title",title);
                        post.put("price",price);
                        post.put("location",address);
                        post.put("isBuy", isSell);
                        post.put("imageUrl",uri.toString());
                        post.put("description",description);
                        post.put("author", idAuthor);


                        Timestamp timestamp = Timestamp.now();
                        post.put("date", timestamp);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("post").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
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
    }

    public void getAllPost(MyCallBack<ArrayList<Post>> myCallBack) {
        ArrayList<Post> listPost = new ArrayList<>();
        firebaseFirestore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String title = "";
                String description = "";
                String location = "";
                int price = 0;
                String imageUrl = "";
                Boolean isBuy = true;
                String authorId = "";
                String date;
                String duration;

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idPost = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("title")) {
                                title = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("description")) {
                                description = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("location")) {
                                location = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("price")) {
                                price = Integer.parseInt(entry.getValue().toString());
                            }
                            if (entry.getKey().equals("imageUrl")) {
                                imageUrl = entry.getValue().toString();
                            }
                            if(entry.getKey().equals("isBuy")){
                                isBuy = (Boolean) entry.getValue();
                            }
                            if(entry.getKey().equals("author")){
                                authorId = entry.getValue().toString();
                            }
                        }
                        listPost.add(new Post(idPost, title, price, description, isBuy, imageUrl, authorId, location));
                    }
                    myCallBack.onCallback(listPost);
                } else {
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void getPosts(int currentPage, int numberOfPostsPerPage, MyCallBack<ArrayList<Post>> myCallBack) {
        long currentIndex = (long) currentPage * numberOfPostsPerPage;
        if (currentIndex <= 0) {
            Task<QuerySnapshot> task = firebaseFirestore.collection("post")
                    .orderBy("date", Query.Direction.DESCENDING).limit(numberOfPostsPerPage).get();
            task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<Post> posts = new ArrayList<>();
                    String id = "";
                    String title = "";
                    int price = -1;
                    String description = "";
                    String imageUrl = "";
                    String author = "";
                    boolean isBuy = true;
                    String location = "";
                    String date = "";
                    String duration = "";
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            id = document.getId();
                            Map<String, Object> userMap = document.getData();
                            for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                                if (entry.getKey().equals("title")) {
                                    title = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("price")) {
                                    price = Integer.parseInt(entry.getValue().toString());
                                }
                                if (entry.getKey().equals("description")) {
                                    description = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("imageUrl")) {
                                    imageUrl = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("author")) {
                                    author = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("isBuy")) {
                                    isBuy = Boolean.parseBoolean(entry.getValue().toString());
                                }
                                if (entry.getKey().equals("location")) {
                                    location = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("date")) {
                                    date = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("duration")) {
                                    duration = entry.getValue().toString();
                                }
                            }
                            posts.add(new Post(id, title, price, description, imageUrl, author, isBuy, location, date
                                    , duration));
                        }
                        myCallBack.onCallback(posts);
                    } else {
                        Log.e("error", "Some thing went wrong");
                    }
                }
            });
            return;
        }

        Task<QuerySnapshot> first = firebaseFirestore.collection("post")
                .orderBy("date", Query.Direction.DESCENDING).
                limit(currentIndex).get();

        first.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                        .get(documentSnapshots.size() - 1);

                Task<QuerySnapshot> task = firebaseFirestore.collection("post")
                        .orderBy("date", Query.Direction.DESCENDING).startAfter(lastVisible).limit(numberOfPostsPerPage).get();

                task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Post> posts = new ArrayList<>();
                        String id = "";
                        String title = "";
                        int price = -1;
                        String description = "";
                        String imageUrl = "";
                        String author = "";
                        boolean isBuy = true;
                        String location = "";
                        String date = "";
                        String duration = "";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                                Map<String, Object> userMap = document.getData();
                                for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                                    if (entry.getKey().equals("title")) {
                                        title = entry.getValue().toString();
                                    }
                                    if (entry.getKey().equals("price")) {
                                        price = Integer.parseInt(entry.getValue().toString());
                                    }
                                    if (entry.getKey().equals("description")) {
                                        description = entry.getValue().toString();
                                    }
                                    if (entry.getKey().equals("imageUrl")) {
                                        imageUrl = entry.getValue().toString();
                                    }
                                    if (entry.getKey().equals("author")) {
                                        author = entry.getValue().toString();
                                    }
                                    if (entry.getKey().equals("isBuy")) {
                                        isBuy = Boolean.parseBoolean(entry.getValue().toString());
                                    }
                                    if (entry.getKey().equals("location")) {
                                        location = entry.getValue().toString();
                                    }
                                    if (entry.getKey().equals("date")) {
                                        date = entry.getValue().toString();
                                    }
                                    if (entry.getKey().equals("duration")) {
                                        duration = entry.getValue().toString();
                                    }
                                }
                                posts.add(new Post(id, title, price, description, imageUrl, author, isBuy, location, date
                                        , duration));
                            }
                            myCallBack.onCallback(posts);

                        } else {
                            Log.e("error", "Some thing went wrong");
                        }
                    }
                });
            }
        });
    }

    public void getRelatedPosts(String currentPostId, int numberOfPosts, MyCallBack<ArrayList<Post>> myCallBack) {
        Task<QuerySnapshot> task = firebaseFirestore.collection("post")
                .whereNotEqualTo(FieldPath.documentId(), currentPostId).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Post> posts = new ArrayList<>();
                String id = "";
                String title = "";
                int price = -1;
                String description = "";
                String imageUrl = "";
                String author = "";
                boolean isBuy = true;
                String location = "";
                String date = "";
                String duration = "";
                if (task.isSuccessful()) {
                    int count = 0;
                    Random random = new Random();
                    while (count == 0){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (count == numberOfPosts){
                                break;
                            }
                            if (!random.nextBoolean()){
                                continue;
                            }
                            count++;
                            id = document.getId();
                            Map<String, Object> userMap = document.getData();
                            for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                                if (entry.getKey().equals("title")) {
                                    title = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("price")) {
                                    price = Integer.parseInt(entry.getValue().toString());
                                }
                                if (entry.getKey().equals("description")) {
                                    description = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("imageUrl")) {
                                    imageUrl = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("author")) {
                                    author = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("isBuy")) {
                                    isBuy = Boolean.parseBoolean(entry.getValue().toString());
                                }
                                if (entry.getKey().equals("location")) {
                                    location = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("date")) {
                                    date = entry.getValue().toString();
                                }
                                if (entry.getKey().equals("duration")) {
                                    duration = entry.getValue().toString();
                                }
                            }
                            posts.add(new Post(id, title, price, description, imageUrl, author, isBuy, location, date, duration));
                        }
                    }
                    myCallBack.onCallback(posts);
                } else {
                    myCallBack.onCallback(null);
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void getPostByType(boolean isSellPost, MyCallBack<ArrayList<Post>> myCallBack) {
        ArrayList<Post> listPostSell = new ArrayList<>();
        ArrayList<Post> listPostBuy = new ArrayList<>();
        getAllPost(new MyCallBack<ArrayList<Post>>() {
            @Override
            public void onCallback(ArrayList<Post> posts) {
                for (Post post: posts) {
                    if(!post.isBuy()){
                        listPostSell.add(post);
                    }else{
                        listPostBuy.add(post);
                    }
                }
                if(isSellPost){
                    myCallBack.onCallback(listPostSell);
                }else{
                    myCallBack.onCallback(listPostBuy);
                }
            }
        });
    }

    public void getCommentsByPostId(String postId, MyCallBack<ArrayList<PostComment>> myCallBack) {
        Task<QuerySnapshot> task = firebaseFirestore.collection("comment").whereEqualTo("postId", postId).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<PostComment> postComments = new ArrayList<>();
                String id = "";
                String userId = "";
                String postId = "";
                String content = "";

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("userId")) {
                                userId = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("postId")) {
                                postId = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("content")) {
                                content = entry.getValue().toString();
                            }
                        }
                        postComments.add(new PostComment(id, userId, postId, content));
                    }
                    myCallBack.onCallback(postComments);
                } else {
                    myCallBack.onCallback(null);
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void addPostComment(PostComment postComment, MyCallBack<Boolean> myCallBack) {
        Task<DocumentReference> task = firebaseFirestore.collection("comment").add(postComment);
        task.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    myCallBack.onCallback(true);
                } else {
                    myCallBack.onCallback(false);
                }
            }
        });
    }

    public void getPostById(String postId, MyCallBack<Post> myCallBack) {
        Task<DocumentSnapshot> task = firebaseFirestore.collection("post").document(postId).get();
        task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String id = "";
                String title = "";
                int price = -1;
                String description = "";
                String imageUrl = "";
                String author = "";
                boolean isBuy = true;
                String location = "";
                String date = "";
                String duration = "";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    id = document.getId();
                    Map<String, Object> userMap = document.getData();
                    for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                        if (entry.getKey().equals("title")) {
                            title = entry.getValue().toString();
                        }
                        if (entry.getKey().equals("price")) {
                            price = Integer.parseInt(entry.getValue().toString());
                        }
                        if (entry.getKey().equals("description")) {
                            description = entry.getValue().toString();
                        }
                        if (entry.getKey().equals("imageUrl")) {
                            imageUrl = entry.getValue().toString();
                        }
                        if (entry.getKey().equals("author")) {
                            author = entry.getValue().toString();
                        }
                        if (entry.getKey().equals("isBuy")) {
                            isBuy = Boolean.parseBoolean(entry.getValue().toString());
                        }
                        if (entry.getKey().equals("location")) {
                            location = entry.getValue().toString();
                        }
                        if (entry.getKey().equals("date")) {
                            date = entry.getValue().toString();
                        }
                        if (entry.getKey().equals("duration")) {
                            duration = entry.getValue().toString();
                        }
                    }
                    myCallBack.onCallback(new Post(id, title, price, description, imageUrl, author, isBuy, location, date, duration));
                } else {
                    myCallBack.onCallback(null);
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void getAllUserRating(String receivedUserIdParam, MyCallBack<ArrayList<Rating>> myCallBack) {
        Task<QuerySnapshot> task = firebaseFirestore.collection("rating").whereEqualTo("receivedUserId", receivedUserIdParam).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Rating> ratings = new ArrayList<>();
                String id = "";
                int point = 0;
                String receivedUserId = "";
                String voteUserId = "";
                String content = "";


                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("voteUserId")) {
                                voteUserId = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("receivedUserId")) {
                                receivedUserId = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("point")) {
                                point = Integer.parseInt(entry.getValue().toString());
                            }
                            if (entry.getKey().equals("content")) {
                                content = entry.getValue().toString();
                            }
                        }
                        ratings.add(new Rating(voteUserId, receivedUserId, point, content));
                    }
                    myCallBack.onCallback(ratings);
                } else {
                    myCallBack.onCallback(null);
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

    public void addRating(Rating rating, MyCallBack<Boolean> myCallBack) {
        Task<DocumentReference> task = firebaseFirestore.collection("rating").add(rating);
        task.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    myCallBack.onCallback(true);
                } else {
                    myCallBack.onCallback(false);
                }
            }
        });
    }

    public void unFollowUser(Follow follow, MyCallBack<Boolean> myCallBack) {
        Task<QuerySnapshot> task =  firebaseFirestore.collection("following").whereEqualTo("idUser", follow.idUser).whereEqualTo("idUserFollowing",follow.idUserFollowing).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                    myCallBack.onCallback(true);
                } else {
                    myCallBack.onCallback(false);
                }
            }
        });
    }

    public void followUser(Follow follow, MyCallBack<Boolean> myCallBack) {
        Task<DocumentReference> task = firebaseFirestore.collection("following").add(follow);
        task.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    myCallBack.onCallback(true);
                } else {
                    myCallBack.onCallback(false);
                }
            }
        });
    }

    public void isFollowed(Follow follow, MyCallBack<Boolean> myCallBack) {
        Task<QuerySnapshot> task = firebaseFirestore.collection("following").whereEqualTo("idUserFollowing", follow.idUserFollowing).whereEqualTo("idUser",follow.idUser).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().size() > 0) {
                    myCallBack.onCallback(true);
                } else {
                    myCallBack.onCallback(false);
                }
            }
        });
    }


    public void updateUser(User user, String imagePathFile, byte[] imageData, MyCallBack<Boolean> myCallBack) {
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
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("username", user.getUsername());
                        newUser.put("password", user.getPassword());
                        newUser.put("fullName",user.getFullName() );
                        newUser.put("address",user.getAddress());
                        newUser.put("phone",user.getPhone());
                        newUser.put("isPriority",user.isPriority());
                        newUser.put("avatarUrl", uri.toString());
                        newUser.put("totalRating", user.getRating()+"");
                        String userId = user.getId();

                        DocumentReference documentReference = firebaseFirestore.collection("user").document(userId);
                        Task<Void> task =  documentReference.set(newUser);
                        task.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    myCallBack.onCallback(true);
                                } else {
                                    myCallBack.onCallback(false);
                                }
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

    }

    public void updateUserRating(User user, MyCallBack<Boolean> myCallBack) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", user.getUsername());
        newUser.put("password", user.getPassword());
        newUser.put("fullName",user.getFullName() );
        newUser.put("address",user.getAddress());
        newUser.put("phone",user.getPhone());
        newUser.put("isPriority",user.isPriority());
        newUser.put("avatarUrl", user.getAvatarUrl());
        newUser.put("totalRating", user.getRating()+"");

        String userId = user.getId();
        DocumentReference documentReference = firebaseFirestore.collection("user").document(userId);
        Task<Void> task =  documentReference.set(newUser);
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    myCallBack.onCallback(true);
                } else {
                    myCallBack.onCallback(false);
                }
            }
        });
    }

    public void getFollowingUser(String userId, MyCallBack<ArrayList<Follow>> myCallBack) {
        Task<QuerySnapshot> task = firebaseFirestore.collection("following").whereEqualTo("idUser", userId).get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Follow> postComments = new ArrayList<>();
                String id = "";
                String idUserFollowing = "";
                String idUser = "";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                        Map<String, Object> userMap = document.getData();
                        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                            if (entry.getKey().equals("idUser")) {
                                idUser = entry.getValue().toString();
                            }
                            if (entry.getKey().equals("idUserFollowing")) {
                                idUserFollowing = entry.getValue().toString();
                            }
                        }
                        postComments.add(new Follow( idUser, idUserFollowing));
                    }
                    myCallBack.onCallback(postComments);
                } else {
                    myCallBack.onCallback(null);
                    Log.e("error", "Some thing went wrong");
                }
            }
        });
    }

}
