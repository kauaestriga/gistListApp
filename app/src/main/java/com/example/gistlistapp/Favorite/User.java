package com.example.gistlistapp.Favorite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "login")
    public String login;

    @ColumnInfo(name = "avatar_url")
    public String avatarUrl;

    public User(String id, String login, String avatarUrl){
        this.id = id;
        this.login = login;
        this.avatarUrl = avatarUrl;
    }

}
