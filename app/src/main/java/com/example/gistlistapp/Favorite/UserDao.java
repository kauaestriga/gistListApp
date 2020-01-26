package com.example.gistlistapp.Favorite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE user_id = :user_id")
    User getUserById(String user_id);

    @Insert
    void insertUser(User... users);

    @Delete
    void deleteUser(User user);
}
