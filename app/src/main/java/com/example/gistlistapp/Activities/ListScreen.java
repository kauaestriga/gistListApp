package com.example.gistlistapp.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.gistlistapp.Favorite.AppDataBase;
import com.example.gistlistapp.Fragments.FavoriteList;
import com.example.gistlistapp.Fragments.GistList;
import com.example.gistlistapp.R;

public class ListScreen extends AppCompatActivity implements GistList.OnFragmentInteractionListener, FavoriteList.OnFragmentInteractionListener {

    private FragmentTransaction ft;
    private AppDataBase db;
    private boolean inFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "user").allowMainThreadQueries().build();

        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_content, FavoriteList.newInstance(db));
        ft.commit();
        inFavorite = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                ft.replace(R.id.fragment_content, FavoriteList.newInstance(db));
                ft.commit();
                inFavorite = true;
                invalidateOptionsMenu();
                break;
            case R.id.action_list:
                ft.replace(R.id.fragment_content, GistList.newInstance(db));
                ft.commit();
                inFavorite = false;
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (inFavorite) {
            inflater.inflate(R.menu.menu_list, menu);
        } else {
            inflater.inflate(R.menu.menu_favorite, menu);
        }
        return true;
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
