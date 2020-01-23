package com.example.gistlistapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.gistlistapp.Objects.Gist;
import com.example.gistlistapp.R;
import com.example.gistlistapp.RecyclerView.AdapterGist;
import com.example.gistlistapp.Retrofit.RetrofitService;
import com.example.gistlistapp.Retrofit.ServiceGenerator;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListScreen extends AppCompatActivity {

    private List<Gist> gistsResponse;
    private ProgressDialog progress;
    private RecyclerView recyclerView;
    private AdapterGist adapterGist;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        viewsConfigs();

        chamarAPI("0");
    }

    public void viewsConfigs(){
        recyclerView = findViewById(R.id.rvList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void chamarAPI(String page){
        progress = new ProgressDialog(ListScreen.this);
        progress.setTitle("Carregando...");
        progress.show();

        RetrofitService service = ServiceGenerator.createService((RetrofitService.class));

        Call<List<Gist>> call = service.loadGists();
        call.enqueue(new Callback<List<Gist>>() {
            @Override
            public void onResponse(Call<List<Gist>> call, Response<List<Gist>> response) {
                if (response.isSuccessful()){

                    if (response.body() != null) {
                        progress.dismiss();
                        gistsResponse = response.body();
                    } else {
                        Toast.makeText(getApplicationContext(),"Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                }

                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Gist>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                t.printStackTrace();
            }
        });
    }
}
