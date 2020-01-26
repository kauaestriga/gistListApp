package com.example.gistlistapp.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import androidx.room.Room;

import com.example.gistlistapp.Favorite.AppDataBase;
import com.example.gistlistapp.Favorite.User;
import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.FavoriteViewHolder> {
    private List<User> users;
    private Context context;
    private static AppDataBase db;

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

    public AdapterFavorite(Context context){
        this.context = context;
        this.db = Room.databaseBuilder(context, AppDataBase.class, "mydb")
            .build();
    }

    public void setUsers(List<User> users){
        this.users = users;
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
                deleteUser(users.get(position));
                users.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static void deleteUser(final User user){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                db.userDao().deleteUser(user);
                return null;
            }
        }.execute();
    }
}