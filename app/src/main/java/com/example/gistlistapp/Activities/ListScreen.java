package com.example.gistlistapp.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class ListScreen extends AppCompatActivity {

    private ProgressDialog progress;
    private RecyclerView recyclerView;
    private AdapterGist adapterGist;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout srlRecycler;
    private AppDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        viewsConfigs();

        chamarAPI("0");
    }

    public void viewsConfigs(){
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("");

        recyclerView = findViewById(R.id.rvList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        srlRecycler = findViewById(R.id.srlRecycler);
        srlRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chamarAPI("0");
            }
        });

        db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "user").build();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                break;
            case R.id.action_list:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void chamarAPI(String page){
        if (!srlRecycler.isRefreshing()) {
            progress = new ProgressDialog(ListScreen.this);
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
                        Toast.makeText(getApplicationContext(),"Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                }

                progress.dismiss();
                if (srlRecycler.isRefreshing())
                    srlRecycler.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Gist>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                if (srlRecycler.isRefreshing())
                    srlRecycler.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    public void setResponseList(List<Gist> gists){
        adapterGist = new AdapterGist(gists, getApplicationContext(), db);
        recyclerView.setAdapter(adapterGist);
    }
}
