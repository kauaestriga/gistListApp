package com.example.gistlistapp.Favorite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

//    public User(){}

    public User(String userId, String login, String avatarUrl){
        this.setUserId(userId);
        this.setLogin(login);
        this.setAvatarUrl(avatarUrl);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(int id) {
        this.id = id;
    }
}
