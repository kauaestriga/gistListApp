package com.example.gistlistapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gistlistapp.Activities.ListScreen;
import com.example.gistlistapp.Favorite.AppDataBase;
import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.example.gistlistapp.RecyclerView.AdapterGist;
import com.example.gistlistapp.Retrofit.RetrofitService;
import com.example.gistlistapp.Retrofit.ServiceGenerator;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GistList extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Context context;
    private ProgressDialog progress;
    private RecyclerView recyclerView;
    private AdapterGist adapterGist;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout srlRecycler;

    private AppDataBase db;
    private static final String DATABASE = "database";

    public GistList() {
        // Required empty public constructor
    }

    public static GistList newInstance(AppDataBase appDataBase) {
        GistList fragment = new GistList();
        Bundle args = new Bundle();
        args.putSerializable(DATABASE, appDataBase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            db = (AppDataBase) getArguments().getSerializable(DATABASE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gist_list, container, false);

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
        recyclerView = view.findViewById(R.id.rvGistList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        srlRecycler = view.findViewById(R.id.srlGistList);
        srlRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chamarAPI("0");
            }
        });

        chamarAPI("0");
    }

    public void chamarAPI(String page){
        if (!srlRecycler.isRefreshing()) {
            progress = new ProgressDialog(context);
            progress.setTitle(R.string.loading);
            progress.show();
        }

        RetrofitService service = ServiceGenerator.createService((RetrofitService.class));

        Call<List<Gist>> call = service.loadGists();
        call.enqueue(new Callback<List<Gist>>() {
            @Override
            public void onResponse(Call<List<Gist>> call, Response<List<Gist>> response) {
                if (response.isSuccessful()){

                    if (response.body() != null) {
                        setResponseList(response.body());
                        Log.d("GistResponse", new Gson().toJson(response.body()));
                    } else {
                        Toast.makeText(context,"Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                }

                progress.dismiss();
                if (srlRecycler.isRefreshing())
                    srlRecycler.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Gist>> call, Throwable t) {
                Toast.makeText(context,"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                if (srlRecycler.isRefreshing())
                    srlRecycler.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    public void setResponseList(List<Gist> gists){
        adapterGist = new AdapterGist(gists, context, db);
        recyclerView.setAdapter(adapterGist);
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
}
