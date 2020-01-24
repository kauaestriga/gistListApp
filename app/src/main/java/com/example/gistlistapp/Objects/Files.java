package com.example.gistlistapp.Objects;

import com.google.gson.annotations.SerializedName;

public class Files {

    private String type;

    public Files(){
        setType("");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
