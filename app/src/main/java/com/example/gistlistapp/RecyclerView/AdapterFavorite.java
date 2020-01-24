package com.example.gistlistapp.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gistlistapp.Favorite.AppDataBase;
import com.example.gistlistapp.Favorite.User;
import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.FavoriteViewHolder> {
    private List<User> users;
    private Context context;
    private AppDataBase db;

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNameFavorite;
        private ImageView ivPhotoFavorite;
        private ImageView ivDelete;

        private FavoriteViewHolder(View v){
            super(v);
            tvNameFavorite = v.findViewById(R.id.tvNameFavorite);
            ivPhotoFavorite = v.findViewById(R.id.ivPhotoFavorite);
            ivDelete = v.findViewById(R.id.ivDelete);
        }
    }

    public AdapterFavorite(List<User> users, Context context, AppDataBase db){
        this.users = users;
        this.context = context;
        this.db = db;
    }

    @Override
    public AdapterFavorite.FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterFavorite.FavoriteViewHolder holder, final int position) {
        holder.tvNameFavorite.setText(users.get(position).getLogin());
        Picasso.get()
                .load(users.get(position).getAvatarUrl())
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error)
                .into(holder.ivPhotoFavorite);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.userDao().deleteUser(users.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
