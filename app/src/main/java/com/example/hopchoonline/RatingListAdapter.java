package com.example.hopchoonline;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hopchoonline.callback.MyCallBack;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.ViewHolder> {
    DatabaseHelper databaseHelper;
    String idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");

    private ArrayList<Rating> ratings;

    private Context parentActivityContext;


    public RatingListAdapter(Context parentActivityContext) {
        this.parentActivityContext = parentActivityContext;
    }

    public RatingListAdapter(Context parentActivityContext, ArrayList<Rating> ratings) {
        this.parentActivityContext = parentActivityContext;
        this.ratings = ratings;
        databaseHelper = new DatabaseHelper(parentActivityContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_rating_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rating rating = ratings.get(position);

//        User user = databaseHelper.getUserById(rating.getVoteUserId());
//        User user = new User();
//        databaseHelper.getUserById(rating.getVoteUserId());
       User user = new User();
        databaseHelper.getUserById(rating.getVoteUserId(), new MyCallBack<User>() {
            @Override
            public void onCallback(User _user) {
               holder.txtUserFullName.setText(_user.getFullName());

            }
        });
        if (!rating.getContent().isEmpty() && Objects.nonNull(holder)) {
            holder.txtUserFullName.setText(user.getFullName());

            holder.txtContent.setText(rating.getContent());
            holder.ratingPoint.setRating(rating.getPoint());
        }


    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout parent;
        private ShapeableImageView imgAvatar;
        private TextView txtUserFullName;

        private TextView txtContent;

        private RatingBar ratingPoint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.parent);
//            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtUserFullName = itemView.findViewById(R.id.txtUserFullName);
            txtContent = itemView.findViewById(R.id.txtRatingComment);
            ratingPoint = itemView.findViewById(R.id.ratingBarProfile);
        }
    }

    public void setPostComments(ArrayList<Rating> ratings) {
        this.ratings = ratings;
        notifyDataSetChanged();
    }

    public void addRating(Rating r) {
        this.ratings.add(r);
        notifyItemChanged(ratings.size() - 1);
    }
}

