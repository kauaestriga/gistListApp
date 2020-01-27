package com.example.gistlistapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import com.example.gistlistapp.Favorite.User;
import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.example.gistlistapp.RecyclerView.AdapterGist;
import com.example.gistlistapp.Retrofit.RetrofitService;
import com.example.gistlistapp.Retrofit.ServiceGenerator;
import com.example.gistlistapp.Utils.PaginationScrollListener;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GistList extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Context context;
    private RecyclerView recyclerView;
    private AdapterGist adapterGist;
    private SwipeRefreshLayout srlRecycler;
    private RetrofitService service;

    private static final int PAGE_START = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private boolean isLastPage = false;
//    private static final int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;

    public GistList() {
        // Required empty public constructor
    }

    public static GistList newInstance() {
        GistList fragment = new GistList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = ServiceGenerator.createService((RetrofitService.class));
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

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        srlRecycler = view.findViewById(R.id.srlGistList);
        srlRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        loadFirstPage();
    }

    private Call<List<Gist>> callGistListApi(){
        return service.loadGists(String.valueOf(currentPage));
    }

    private void loadFirstPage() {
        currentPage = PAGE_START;
        srlRecycler.setRefreshing(true);

        callGistListApi().enqueue(new Callback<List<Gist>> () {
            @Override
            public void onResponse(Call<List<Gist>> call, Response<List<Gist>> response) {
                srlRecycler.setRefreshing(true);

                if (response.isSuccessful()){

                    if (response.body() != null) {
                        setResponseList(response.body());
                        Log.d("GistResponse", new Gson().toJson(response.body()));
                    } else {
                        Toast.makeText(context,"Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                }


//                if (currentPage <= TOTAL_PAGES)
                    adapterGist.addLoadingFooter();
//                else
//                    isLastPage = true;

                if (srlRecycler.isRefreshing())
                    srlRecycler.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Gist>> call, Throwable t) {
            }
        });
    }

    private void loadNextPage() {
        callGistListApi().enqueue(new Callback<List<Gist>>() {
            @Override
            public void onResponse(Call<List<Gist>> call, Response<List<Gist>> response) {

                adapterGist.removeLoadingFooter();
                isLoading = false;

                adapterGist.addAll(response.body());

//                if (currentPage != TOTAL_PAGES)
                    adapterGist.addLoadingFooter();
//                else
//                    isLastPage = true;
            }

            @Override
            public void onFailure(Call<List<Gist>> call, Throwable t) {
            }
        });
    }

    private void doRefresh() {
        srlRecycler.setRefreshing(true);

        if (callGistListApi().isExecuted())
            callGistListApi().cancel();

        adapterGist.clear();
        adapterGist.notifyDataSetChanged();
        loadFirstPage();
    }

    public void setResponseList(List<Gist> gists){
        adapterGist = new AdapterGist(gists, context);
        recyclerView.setAdapter(adapterGist);

        if (srlRecycler.isRefreshing())
            srlRecycler.setRefreshing(false);
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
