package com.example.gistlistapp.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.gistlistapp.Favorite.AppDataBase;
import com.example.gistlistapp.Favorite.User;
import com.example.gistlistapp.Objects.Files;
import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;
import java.util.Map;

public class AdapterGist extends RecyclerView.Adapter<AdapterGist.GistViewHolder> {

    private List<Gist> gists;
    private Context context;
    private static AppDataBase db;
    private Dialog myDialog;

    public static class GistViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item;
        private TextView tvName;
        private TextView tvType;
        private ImageView ivPhoto;
        private ImageView ivFavorite;

        private GistViewHolder(View v){
            super(v);
            item = v.findViewById(R.id.ll_gist_item);
            tvName = v.findViewById(R.id.tvName);
            tvType = v.findViewById(R.id.tvType);
            ivPhoto = v.findViewById(R.id.ivPhoto);
            ivFavorite = v.findViewById(R.id.ivFavorite);
        }
    }

    public AdapterGist(List<Gist> gists, Context context){
        this.gists = gists;
        this.context = context;
        this.db = Room.databaseBuilder(context, AppDataBase.class, "mydb")
                .build();
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
                    deleteUser(user);
                } else {
                    gist.setFavorite(true);
                    holder.ivFavorite.setImageResource(R.drawable.ic_favorite_selected);
                    insertUser(user);
                }
            }
        });

        if (gists.get(position).isFavorite())
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite_selected);
        else
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog(gists.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return gists.size();
    }

    public static void insertUser(final User user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.userDao().insertUser(user);
                return null;
            }
        }.execute();
    }

    public static void deleteUser(final User user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.userDao().deleteUser(user);
                return null;
            }
        }.execute();
    }

    public static void getUser(final Gist gist){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids){
                gist.setFavorite(db.userDao().getUserById(gist.getOwner().getId()) == null);
                return null;
            }
        }.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadDialog(final Gist gist){
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.dialog_details);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialog_tvName = myDialog.findViewById(R.id.dialog_login);
        TextView dialog_tvDate = myDialog.findViewById(R.id.dialog_createdDate);
        ImageView dialog_ivImageUser = myDialog.findViewById(R.id.dialog_userImage);
        Button dialog_btnURL = myDialog.findViewById(R.id.dialog_btnURL);

        dialog_tvName.setText(gist.getOwner().getLogin());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(gist.getCreated_at());
        dialog_tvDate.setText(context.getString(R.string.created, date));
        Picasso.get()
                .load(gist.getOwner().getAvatar_url())
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error)
                .into(dialog_ivImageUser);
        dialog_btnURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gist.getOwner().getHtml_url()));
                context.startActivity(browserIntent);
            }
        });
        myDialog.show();
    }
}
