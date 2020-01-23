package com.example.gistlistapp.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gistlistapp.Objects.Gist;

public class AdapterGist extends RecyclerView.Adapter<AdapterGist.ViewHolder> {
    private List<Gist> gists;
    private Context context;

    public AdapterGist(List<Gist> gists, Context context){
        this.gists = gists;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gist, parent, false);
        GistViewHolder holder = new GistViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return gists.size();
    }

    public static class GistViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;
        public TextView tvType;
        public ImageView ivPhoto;

        public GistViewHolder(View v){
            super(v);
        }
    }
}
