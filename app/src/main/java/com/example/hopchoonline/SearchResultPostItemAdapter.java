package com.example.hopchoonline;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class SearchResultPostItemAdapter extends RecyclerView.Adapter<SearchResultPostItemAdapter.ViewHolder> {
    private ArrayList<Post> items;
    private String idUserLogged;
    private OnSortingCompleteListener sortingCompleteListener;
    private Context context;
    private int priories = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parent;
        public ImageView imageView;
        public ImageView iconSavePost;
        public ImageView iconIsPriority;
        public TextView titleView;
        public TextView priceView;
        public TextView reliableView;
        public TextView placeView;
        public TextView distanceView;
        public TextView isPriorityView;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            imageView = itemView.findViewById(R.id.resultSearchPostImage);
            titleView = itemView.findViewById(R.id.item_title);
            priceView = itemView.findViewById(R.id.item_price);
            reliableView = itemView.findViewById(R.id.item_reliable);
            iconSavePost = itemView.findViewById(R.id.iconSavePost);
            placeView = itemView.findViewById(R.id.text_place);
            distanceView = itemView.findViewById(R.id.text_distance);
            isPriorityView = itemView.findViewById(R.id.text_is_priority);
            iconIsPriority = itemView.findViewById(R.id.icon_is_priority);
            ratingBar = itemView.findViewById(R.id.ratingbar);
            context = itemView.getContext();
            iconSavePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    // Kiểm tra post có được người dùng lưu chưa
                    Post post = items.get(position);

                    SavedPostDAO savedPostDAO = new SavedPostDAO(context);
                    savedPostDAO.isSavedPost(idUserLogged, post.getId(), new MyCallBack<Boolean>() {
                        @Override
                        public void onCallback(Boolean isCorrectSavedPost) {
                            if (isCorrectSavedPost) {
                                removeSavedPost(position, iconSavePost);
                            } else {
                                addSavedPost(position, iconSavePost);
                            }
                        }
                    });
                }
            });
        }
    }

    public SearchResultPostItemAdapter(ArrayList<Post> items, String idUserLogged, Context context, OnSortingCompleteListener sortingCompleteListener) {
        this.items = items;
        this.idUserLogged = idUserLogged;
        this.context = context;
        this.sortingCompleteListener = sortingCompleteListener;
    }

    @Override
    public SearchResultPostItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultPostItemAdapter.ViewHolder holder, int position) {
        Post post = items.get(position);
        
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callPostActivityIntent = new Intent(context, PostActivity.class);
                callPostActivityIntent.putExtra("postId", post.getId());
                context.startActivity(callPostActivityIntent);
            }
        });

        if (items.size() > 0) {
            changeIconSavedPost(holder.iconSavePost, position);
            Post currentItem = items.get(position);

            UserDAO userDAO = new UserDAO(context.getApplicationContext());
            userDAO.getPriority_Rating_Address(currentItem.getAuthor(), new MyCallBack<User>() {
                @Override
                public void onCallback(User user) {
                    boolean isPriority = user.isPriority();
                    double rating = user.getRating();

                    Glide.with(context).load(post.getImageUrl())
                            .placeholder(R.color.white)
                            .into(holder.imageView);
                    holder.titleView.setText(currentItem.getTitle());
                    String priceStr = CommonConverter.convertCurrencyVND(Integer.parseInt(currentItem.getPrice() + ""));
                    holder.priceView.setText(priceStr);
                    holder.ratingBar.setRating((float) rating);

                    String addressString = currentItem.getLocation();
                    //TODO: try catch address
                    String city = CommonMethod.getCityFromAddress(context.getApplicationContext(), addressString);
                    holder.placeView.setText(" - " + city);


                    // So sánh khoảng cách với user đang đăng nhập
                    UserDAO userDAOLogged = new UserDAO(context);
                    userDAOLogged.getPriority_Rating_Address(idUserLogged, new MyCallBack<User>() {
                        @Override
                        public void onCallback(User user) {
                            String currentUserLocationString = user.getAddress();
                            Location currentUserLocation = CommonMethod.getLocationFromString(context.getApplicationContext(), currentUserLocationString);
                            Location locationPost = CommonMethod.getLocationFromString(context.getApplicationContext(), currentItem.getLocation());
                            float distanceBetweenUserAndPost = CommonMethod.getDistance(currentUserLocation, locationPost);
                            String distance = "";
                            if (distanceBetweenUserAndPost >= 1000) {
                                distanceBetweenUserAndPost /= 1000;
                                distance = " - " + String.format("%.2f", distanceBetweenUserAndPost) + "km";
                            } else {
                                distance = " - " + (int) distanceBetweenUserAndPost + "m";
                            }
                            holder.distanceView.setText(distance);

                            if (isPriority == false) {
                                // Lấy ngày và giờ hiện tại
                                Date currentDate = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String currentDateString = dateFormat.format(currentDate);

                                // So sánh và tính khoảng thời gian
                                String otherDateString = CommonConverter.convertDateVN(currentItem.getDate()) + " " + currentItem.getDuration();
                                holder.isPriorityView.setText(otherDateString);
                                Date otherDate;
                                try {
                                    otherDate = dateFormat.parse(otherDateString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                // Tính khoảng cách giữa hai ngày
                                long timeDiff = Math.abs(currentDate.getTime() - otherDate.getTime());
                                long dayDiff = timeDiff / (24 * 60 * 60 * 1000);
                                long secondsDiff = timeDiff / 1000;
                                long minutesDiff = secondsDiff / 60;
                                long hoursDiff = minutesDiff / 60;

                                if (secondsDiff < 60) {
                                    holder.isPriorityView.setText(" " + secondsDiff + " giây trước");
                                } else if (minutesDiff < 60) {
                                    holder.isPriorityView.setText(" " + minutesDiff + " phút trước");
                                } else if (hoursDiff < 24) {
                                    holder.isPriorityView.setText(" " + hoursDiff + " giờ trước");
                                } else if (dayDiff < 32) {
                                    holder.isPriorityView.setText(" " + dayDiff + " ngày trước");
                                } else {
                                    long monthDiff = dayDiff / 31;
                                    if (monthDiff < 13) {
                                        holder.isPriorityView.setText(" " + monthDiff + " tháng trước");
                                    } else {
                                        long yearDiff = monthDiff / 12;
                                        holder.isPriorityView.setText(" " + yearDiff + " năm trước");
                                    }
                                }

                                holder.iconIsPriority.setImageResource(R.drawable.ic_market_place_30);
                            } else {
                                holder.iconIsPriority.setImageResource(R.drawable.priority_icon_30);
                                holder.isPriorityView.setText("Tin Ưu tiên");

                                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.iconIsPriority.getLayoutParams();
                                // thiết lập giá trị marginLeft
                                marginLayoutParams.leftMargin = -4; // giá trị pixel
                                // cập nhật MarginLayoutParams của ImageView
                                holder.iconIsPriority.setLayoutParams(marginLayoutParams);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void sortItems(Context context, ArrayList<Post> items, String currentUserLocationString) {
        // Tạo bộ so sánh tùy chỉnh
        Comparator<Post> customComparator = new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                UserDAO userDAO = new UserDAO(context.getApplicationContext());

                CompletableFuture<User> user1Future = new CompletableFuture<>();
                CompletableFuture<User> user2Future = new CompletableFuture<>();

                userDAO.getPriority_Rating_Address(post1.getAuthor(), new MyCallBack<User>() {
                    @Override
                    public void onCallback(User user) {
                        user1Future.complete(user);
                    }
                });

                userDAO.getPriority_Rating_Address(post2.getAuthor(), new MyCallBack<User>() {
                    @Override
                    public void onCallback(User user) {
                        user2Future.complete(user);
                    }
                });

                // Đợi cả hai kết quả trả về
                CompletableFuture.allOf(user1Future, user2Future).join();

                User user1 = user1Future.getNow(null);
                User user2 = user2Future.getNow(null);

                if (user1 != null && user2 != null) {
                    boolean isPriority1 = user1.isPriority();
                    boolean isPriority2 = user2.isPriority();
                    double rating1 = user1.getRating();
                    double rating2 = user2.getRating();

                    if (isPriority1 && !isPriority2) {
                        return -1;
                    } else if (!isPriority1 && isPriority2) {
                        return 1;
                    } else {
                        //So sánh khoảng cách với user đang đăng nhập
                        Location currentUserLocation = CommonMethod.getLocationFromString(context.getApplicationContext(), currentUserLocationString);
                        Location location1 = CommonMethod.getLocationFromString(context.getApplicationContext(), user1.getAddress());
                        Location location2 = CommonMethod.getLocationFromString(context.getApplicationContext(), user2.getAddress());

                        if (currentUserLocation != null && location1 != null && location2 != null) {
                            float distance1 = CommonMethod.getDistance(currentUserLocation, location1);
                            float distance2 = CommonMethod.getDistance(currentUserLocation, location2);

                            return Float.compare(distance1, distance2);
                        } else {
                            return Double.compare(rating2, rating1);
                        }
                    }
                } else {
                    return 0;
                }
            }

        };

        // Sắp xếp danh sách với bộ so sánh tùy chỉnh
        Collections.sort(items, customComparator);
    }

    public void sortItemsAsync(Context context, ArrayList<Post> items, String currentUserLocationString) {
        new SortItemsTask(context, items).execute(currentUserLocationString);
    }

    private class SortItemsTask extends AsyncTask<String, Void, Void> {
        private Context context;
        private ArrayList<Post> items;

        SortItemsTask(Context context, ArrayList<Post> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        protected Void doInBackground(String... currentUserLocationStrings) {
            String currentUserLocationString = currentUserLocationStrings[0];
            sortItems(context.getApplicationContext(), items, currentUserLocationString);
            return null;
        }

        @Override
        protected void onPreExecute() {
            sortingCompleteListener.onSortingStart();
        }

        @Override
        protected void onPostExecute(Void result) {
            sortingCompleteListener.onSortingComplete();
            notifyDataSetChanged(); // Thông báo cho Adapter rằng danh sách đã được sắp xếp lại
        }
    }

    public void changeIconSavedPost(ImageView iconSavedPost, int position) {
        // Kiểm tra post có được người dùng lưu chưa
        Post post = items.get(position);

        SavedPostDAO savedPostDAO = new SavedPostDAO(context);
        savedPostDAO.isSavedPost(idUserLogged, post.getId(), new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean isCorrectSavedPost) {
                if (isCorrectSavedPost) {
                    iconSavedPost.setImageResource(R.drawable.icon_favorite_fill_30);
                } else {
                    iconSavedPost.setImageResource(R.drawable.ic_not_favorite);
                }
            }
        });
    }

    public void addSavedPost(int position, ImageView iconSavePost) {
        if (position != RecyclerView.NO_POSITION) {
            SavedPostDAO savedPostDAO = new SavedPostDAO(context);
            String idPost = items.get(position).getId();
            savedPostDAO.savePost(idUserLogged, idPost, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean isSuccess) {
                    if (isSuccess) {
                        changeIconSavedPost(iconSavePost, position);
                    } else {
                        Toast.makeText(context, "Lưu tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void removeSavedPost(int position, ImageView iconSavePost) {
        if (position != RecyclerView.NO_POSITION) {
            SavedPostDAO savedPostDAO = new SavedPostDAO(context);
            String idPost = items.get(position).getId();
            savedPostDAO.removeSavedPost(idUserLogged, idPost, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean isSuccess) {
                    if(isSuccess) {
                        changeIconSavedPost(iconSavePost, position);
                    } else {
                        Toast.makeText(context, "Bỏ lưu tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
