package com.example.gistlistapp.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.gistlistapp.Favorite.AppDataBase;
import com.example.gistlistapp.Favorite.User;
import com.example.gistlistapp.R;
import com.example.gistlistapp.RecyclerView.AdapterFavorite;
import com.example.gistlistapp.RecyclerView.AdapterGist;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavoriteList extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Context context;
    private static RecyclerView recyclerView;
    private AdapterFavorite adapterFavorite;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout srlRecycler;

    private static AppDataBase db;
    private static List<User> users;

    public FavoriteList() {
        // Required empty public constructor
    }

    public static FavoriteList newInstance() {
        FavoriteList fragment = new FavoriteList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        viewsConfigs(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void viewsConfigs(View view){
        recyclerView = view.findViewById(R.id.rvFavoriteList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        srlRecycler = view.findViewById(R.id.srlFavoriteList);
        srlRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                carregarBanco();
            }
        });

        carregarBanco();
    }

    public void carregarBanco(){
        db = Room.databaseBuilder(context, AppDataBase.class, "mydb").build();
        adapterFavorite = new AdapterFavorite(context);
        carregarUsers(context, srlRecycler, adapterFavorite);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static void carregarUsers(final Context context, final SwipeRefreshLayout srl, final AdapterFavorite adapter){
        new AsyncTask<Void, Void, Void>(){
            private ProgressDialog progress = new ProgressDialog(context);

            @Override
            protected void onPreExecute() {
                if (!srl.isRefreshing()) {
                    progress.setTitle(R.string.loading);
                    progress.show();
                }
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                users = db.userDao().getAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (users == null){
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Aviso");
                    alertDialog.setMessage(context.getString(R.string.favoriteNull));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    adapter.setUsers(users);
                    recyclerView.setAdapter(adapter);
                }

                if (srl.isRefreshing())
                    srl.setRefreshing(false);

                progress.dismiss();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
}
