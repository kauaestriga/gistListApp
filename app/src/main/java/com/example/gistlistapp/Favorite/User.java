package com.example.gistlistapp.Favorite;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    @NonNull private Long id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }
}
