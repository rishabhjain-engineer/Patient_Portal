package com.hs.userportal;

/**
 * Created by rishabh on 6/4/17.
 */

public class DirectoryFile {

    private String key;
    private String path;
    private String lastModified;
    private double size;
    private String thumb;
    private String name;

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        if(key.contains("ZurekaTempPatientConfig")){

        } else {
            String[] thumbs = key.split("\\.");
            thumb = thumbs[0] + "_thumb." + thumbs[1];
        }

    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getThumb() {
        return thumb;
    }
}
