package com.example.hopchoonline;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RelatedPostCardRecyclerViewAdapter extends RecyclerView.Adapter<RelatedPostCardRecyclerViewAdapter.ViewHolder> {
    DatabaseHelper databaseHelper;
    String idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");
    private ArrayList<Post> posts;

    private Context parentActivityContext;


    public RelatedPostCardRecyclerViewAdapter(Context parentActivityContext) {
        this.parentActivityContext = parentActivityContext;
    }

    public RelatedPostCardRecyclerViewAdapter(Context parentActivityContext, ArrayList<Post> posts) {
        this.parentActivityContext = parentActivityContext;
        this.posts = posts;
        databaseHelper = new DatabaseHelper(parentActivityContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_related_post_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        databaseHelper.getUserById(post.getAuthor(), new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                holder.displayRatingStars(user.getRating());
            }
        });

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callPostActivityIntent = new Intent(parentActivityContext, PostActivity.class);
                callPostActivityIntent.putExtra("postId", post.getId());
                parentActivityContext.startActivity(callPostActivityIntent);
            }
        });

        Glide.with(parentActivityContext).load(post.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.thumbnail);
        holder.txtTitle.setText(post.getTitle());

        Locale locale = new Locale("vi", "VN");
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);

        // Format the amount as VND currency
        String formattedAmount = nf.format(post.getPrice());

        // Remove the currency symbol and replace the decimal separator with a comma
        formattedAmount = formattedAmount.substring(0, formattedAmount.length() - 2)  // Remove the currency symbol
                .concat(" đ");  // Add the VND symbol
        holder.txtPrice.setText(formattedAmount);
      
      
        changeIconSavedPost(holder.btnFavorite, holder.getAdapterPosition());
        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra post có được người dùng lưu chưa
                Post post = posts.get(holder.getAdapterPosition());

                SavedPostDAO savedPostDAO = new SavedPostDAO(parentActivityContext);
                savedPostDAO.isSavedPost(idUserLogged, post.getId(), new MyCallBack<Boolean>() {
                    @Override
                    public void onCallback(Boolean isCorrectSavedPost) {
                        if (isCorrectSavedPost) {
                            removeSavedPost(holder.getAdapterPosition(), holder.btnFavorite);
                        } else {
                            addSavedPost(holder.getAdapterPosition(), holder.btnFavorite);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout parent;
        private ImageView thumbnail;
        private TextView txtTitle;
        private TextView txtPrice;

        private LinearLayout holderRating;
        private ImageView btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.parent);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);

            holderRating = itemView.findViewById(R.id.holderRating);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }

        private void displayRatingStars(double rating) {
            holderRating.removeAllViews();

            int count = 5;
            while (rating >= 1) {
                ImageView imageView = (ImageView) LayoutInflater.from(holderRating.getContext()).inflate(R.layout.post_card_star_rate, holderRating, false);
                holderRating.addView(imageView);
                rating--;
                count--;
            }

            if (rating >= 0.5) {
                ImageView imageView = (ImageView) LayoutInflater.from(holderRating.getContext()).inflate(R.layout.post_card_star_rate_half, holderRating, false);
                holderRating.addView(imageView);
                rating--;
                count--;
            }

            while (count >= 1) {
                ImageView imageView = (ImageView) LayoutInflater.from(holderRating.getContext()).inflate(R.layout.post_card_star_rate_empty, holderRating, false);
                holderRating.addView(imageView);
                count--;
            }
        }
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void changeIconSavedPost(ImageView iconSavedPost, int position) {
        if (iconSavedPost == null){
            return;
        }

        // Kiểm tra post có được người dùng lưu chưa
        Post post = posts.get(position);

        SavedPostDAO savedPostDAO = new SavedPostDAO(parentActivityContext);
        savedPostDAO.isSavedPost(idUserLogged, post.getId(), new MyCallBack<Boolean>() {
            @Override
            public void onCallback(Boolean isCorrectSavedPost) {
                if (isCorrectSavedPost) {
                    iconSavedPost.setImageResource(R.drawable.ic_favorite);
                } else {
                    iconSavedPost.setImageResource(R.drawable.ic_not_favorite);
                }
            }
        });
    }

    public void addSavedPost(int position, ImageView iconSavePost) {
        if (position != RecyclerView.NO_POSITION) {
            SavedPostDAO savedPostDAO = new SavedPostDAO(parentActivityContext);
            String idPost = posts.get(position).getId();
            savedPostDAO.savePost(idUserLogged, idPost, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean isSuccess) {
                    if (isSuccess) {
                        changeIconSavedPost(iconSavePost, position);
                    } else {
                        Toast.makeText(parentActivityContext, "Lưu tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void removeSavedPost(int position, ImageView iconSavePost) {
        if (position != RecyclerView.NO_POSITION) {
            SavedPostDAO savedPostDAO = new SavedPostDAO(parentActivityContext);
            String idPost = posts.get(position).getId();
            savedPostDAO.removeSavedPost(idUserLogged, idPost, new MyCallBack<Boolean>() {
                @Override
                public void onCallback(Boolean isSuccess) {
                    if(isSuccess) {
                        changeIconSavedPost(iconSavePost, position);
                    } else {
                        Toast.makeText(parentActivityContext, "Bỏ lưu tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

