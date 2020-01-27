package com.example.gistlistapp.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Gist {

    private String created_at;
    private Owner owner;
    @SerializedName("files")
    @Expose
    private Map<String, Files> result;

    public Map<String, Files> getResult() {
        return result;
    }

    public void setResult(Map<String, Files> result) {
        this.result = result;
    }

    private boolean favorite;

    public Gist(){
        setCreated_at("");
        setFavorite(false);
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}