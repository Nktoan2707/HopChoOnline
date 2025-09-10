package com.example.hopchoonline;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class PostCommentRecyclerViewAdapter extends RecyclerView.Adapter<PostCommentRecyclerViewAdapter.ViewHolder> {
    DatabaseHelper databaseHelper;
    String idUserLogged = Login.loggedUsernamePref.getString("idUserLogged", "nolog");

    private ArrayList<PostComment> postComments;

    private Context parentActivityContext;


    public PostCommentRecyclerViewAdapter(Context parentActivityContext) {
        this.parentActivityContext = parentActivityContext;
    }

    public PostCommentRecyclerViewAdapter(Context parentActivityContext, ArrayList<PostComment> postComments) {
        this.parentActivityContext = parentActivityContext;
        this.postComments = postComments;
        databaseHelper = new DatabaseHelper(parentActivityContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostComment postComment = postComments.get(position);
        databaseHelper.getUserById(postComment.getUserId(), new MyCallBack<User>() {
            @Override
            public void onCallback(User user) {
                Glide.with(parentActivityContext).load(user.getAvatarUrl())
                        .placeholder(R.drawable.avatar_default)
                        .into(holder.imgAvatar);
                holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user.getId().equals(idUserLogged)) {
                            Intent callMyProfileActivityIntent = new Intent(parentActivityContext, MyProfileActivity.class);
                            parentActivityContext.startActivity(callMyProfileActivityIntent);
                        } else {
                            Intent moveToOtherProfile = new Intent(parentActivityContext, OtherProfileActivity.class);
                            moveToOtherProfile.putExtra("FromPostDetailToOtherProfile", user.getId());
                            parentActivityContext.startActivity(moveToOtherProfile);
                        }
                    }
                });

                holder.txtUserFullName.setText(user.getFullName());
                holder.txtUserFullName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user.getId().equals(idUserLogged)) {
                            Intent callMyProfileActivityIntent = new Intent(parentActivityContext, MyProfileActivity.class);
                            parentActivityContext.startActivity(callMyProfileActivityIntent);
                        } else {
                            Intent moveToOtherProfile = new Intent(parentActivityContext, OtherProfileActivity.class);
                            moveToOtherProfile.putExtra("FromPostDetailToOtherProfile", user.getId());
                            parentActivityContext.startActivity(moveToOtherProfile);
                        }
                    }
                });
            }
        });

        holder.txtDescription.setText(postComment.getContent());
    }

    @Override
    public int getItemCount() {
        return postComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout parent;
        private ShapeableImageView imgAvatar;
        private TextView txtUserFullName;

        private TextView txtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.parent);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtUserFullName = itemView.findViewById(R.id.txtUserFullName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }

    public void setPostComments(ArrayList<PostComment> postComments) {
        this.postComments = postComments;
        notifyDataSetChanged();
    }

    public void addPostComment(PostComment pc) {
        this.postComments.add(pc);
        notifyItemChanged(postComments.size() - 1);
    }
}

