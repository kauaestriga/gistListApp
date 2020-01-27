package com.example.gistlistapp.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gistlistapp.Fragments.FavoriteList;
import com.example.gistlistapp.Fragments.GistList;
import com.example.gistlistapp.R;

public class ListScreen extends AppCompatActivity implements GistList.OnFragmentInteractionListener, FavoriteList.OnFragmentInteractionListener {

    private FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    private boolean inFavorite;
    private Toolbar myToolbar;
    private GistList fmGist = new GistList();
    private FavoriteList fmFavorite = new FavoriteList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);

        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.list);
        setSupportActionBar(myToolbar);

        ft.add(R.id.fragment_content, fmGist);
        ft.commit();
        inFavorite = false;
        myToolbar.setTitle(R.string.list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                openFragment(fmFavorite);
                inFavorite = true;
                myToolbar.setTitle(R.string.favorite);
                invalidateOptionsMenu();
                break;
            case R.id.action_list:
                openFragment(fmGist);
                inFavorite = false;
                myToolbar.setTitle(R.string.list);
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

    private void openFragment(final Fragment fragment)   {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
