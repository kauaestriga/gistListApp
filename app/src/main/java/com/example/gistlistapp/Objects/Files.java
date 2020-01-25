package com.example.gistlistapp.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Files {

    private String type;

    public Files(){
        this.setType("");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    private Map<String, FilesProperties> result;
//
//    public Map<String, FilesProperties> getResult() {
//        return result;
//    }
//
//    public void setResult(Map<String, FilesProperties> result) {
//        this.result = result;
//    }
}
