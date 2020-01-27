package com.example.gistlistapp.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.gistlistapp.Favorite.AppDataBase;
import com.example.gistlistapp.Favorite.User;
import com.example.gistlistapp.Objects.Files;
import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.example.gistlistapp.Utils.PaginationAdapterCallback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdapterGist extends RecyclerView.Adapter<AdapterGist.GistViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private String errorMsg;
    private PaginationAdapterCallback mCallback;

    private List<Gist> gists;
    private Context context;
    private static AppDataBase db;
    private Dialog myDialog;

    public AdapterGist(List<Gist> gists, Context context){
        this.gists = gists;
        this.context = context;
        this.db = Room.databaseBuilder(context, AppDataBase.class, "mydb")
                .build();
    }

    @Override
    public AdapterGist.GistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterGist.GistViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }

        return viewHolder;
    }

    private AdapterGist.GistViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        AdapterGist.GistViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_gist, parent, false);
        viewHolder = new AdapterGist.GistViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterGist.GistViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case ITEM:
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
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return gists == null ? 0 : gists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == gists.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
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

    public void loadDialog(final Gist gist){
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.dialog_details);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialog_tvName = myDialog.findViewById(R.id.dialog_login);
        TextView dialog_tvDate = myDialog.findViewById(R.id.dialog_createdDate);
        ImageView dialog_ivImageUser = myDialog.findViewById(R.id.dialog_userImage);
        Button dialog_btnURL = myDialog.findViewById(R.id.dialog_btnURL);

        dialog_tvName.setText(gist.getOwner().getLogin());
        dialog_tvDate.setText(formatDate(gist.getCreated_at()));
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

    public String formatDate(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date date = new Date();
        try {
            date = sdf.parse(dateStr);
        }catch (ParseException e){
            e.printStackTrace();
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/M/yyyy");
        return sdf2.format(date.getTime());
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Gist gist) {
        gists.add(gist);
        notifyItemInserted(gists.size() - 1);
    }

    public void addAll(List<Gist> moveGists) {
        for (Gist result : moveGists) {
            add(result);
        }
    }

    public void remove(Gist gist) {
        int position = gists.indexOf(gist);
        if (position > -1) {
            gists.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Gist());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = gists.size() - 1;
        Gist result = getItem(position);

        if (result != null) {
            gists.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Gist getItem(int position) {
        return gists.get(position);
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(gists.size() - 1);

        if (errorMsg != null)
            this.errorMsg = errorMsg;
    }

    /*
   View Holders
   _________________________________________________________________________________________________
    */

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

    public class LoadingVH extends AdapterGist.GistViewHolder implements View.OnClickListener{
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }
}
