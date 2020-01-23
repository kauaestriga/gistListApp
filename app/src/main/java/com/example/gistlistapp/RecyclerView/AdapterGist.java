package com.example.gistlistapp.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterGist extends RecyclerView.Adapter<AdapterGist.GistViewHolder> {
    private List<Gist> gists;
    private Context context;

    public static class GistViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;
        public TextView tvType;
        public ImageView ivPhoto;

        public GistViewHolder(View v){
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvType = v.findViewById(R.id.tvType);
            ivPhoto = v.findViewById(R.id.ivPhoto);
        }
    }

    public AdapterGist(List<Gist> gists, Context context){
        this.gists = gists;
        this.context = context;
    }

    @Override
    public AdapterGist.GistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gist, parent, false);
        GistViewHolder holder = new GistViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterGist.GistViewHolder holder, int position) {
        holder.tvName.setText(gists.get(position).getOwner().getLogin());
        holder.tvType.setText(gists.get(position).getType());
        Picasso.get()
                .load(gists.get(position).getOwner().getUrl())
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error)
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return gists.size();
    }
}
