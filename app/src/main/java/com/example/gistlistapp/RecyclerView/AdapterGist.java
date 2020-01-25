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
import com.example.gistlistapp.Objects.Files;
import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdapterGist extends RecyclerView.Adapter<AdapterGist.GistViewHolder> {
    private List<Gist> gists;
    private Context context;
    private AppDataBase db;

    public static class GistViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvType;
        private ImageView ivPhoto;
        private ImageView ivFavorite;

        private GistViewHolder(View v){
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvType = v.findViewById(R.id.tvType);
            ivPhoto = v.findViewById(R.id.ivPhoto);
            ivFavorite = v.findViewById(R.id.ivFavorite);
        }
    }

    public AdapterGist(List<Gist> gists, Context context, AppDataBase db){
        this.gists = gists;
        this.context = context;
        this.db = db;
    }

    @Override
    public AdapterGist.GistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gist, parent, false);
        return new GistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterGist.GistViewHolder holder, final int position) {
        holder.tvName.setText(gists.get(position).getOwner().getLogin());
        String valueType = "";
        for (Map.Entry<String, Files> entry : gists.get(position).getResult().entrySet())
            valueType = entry.getValue().getType();
        holder.tvType.setText(valueType.equals("")? context.getString(R.string.typeUndefined) : valueType);
        Picasso.get()
                .load(gists.get(position).getOwner().getAvatar_url())
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error)
                .into(holder.ivPhoto);
        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gist gist = gists.get(position);
                User user = new User(gist.getOwner().getId(), gist.getOwner().getLogin(), gist.getOwner().getAvatar_url());

                if (gist.isFavorite()) {
                    gist.setFavorite(false);
                    holder.ivFavorite.setImageResource(R.drawable.ic_favorite);
                    db.userDao().deleteUser(user);
                } else {
                    gist.setFavorite(true);
                    holder.ivFavorite.setImageResource(R.drawable.ic_favorite_selected);
                    db.userDao().insertUser(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gists.size();
    }
}
