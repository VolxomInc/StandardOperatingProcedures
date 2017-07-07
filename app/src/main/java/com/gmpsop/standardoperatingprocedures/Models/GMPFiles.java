package com.gmpsop.standardoperatingprocedures.Models;

import com.gmpsop.standardoperatingprocedures.Helper.Constants;

/**
 * Created by BD1 on 09-May-17.
 */

public class GMPFiles {
    int id;
    String name;
    String type;
    String path;
    int parentId;

    public GMPFiles() {
        this.id = 0;
        this.name = "";
        this.type = Constants.TYPE_FOLDER;
        this.path = "";
        this.parentId = 0;

    }

    public GMPFiles(int id,String name, String type, String path, int parentId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.path = path;
        this.parentId = parentId;
    }
    public GMPFiles(int id,String name, String path, int parentId) {
        this.id = id;
        this.name = name;
        this.type = Constants.TYPE_FILE;
        this.path = path;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
