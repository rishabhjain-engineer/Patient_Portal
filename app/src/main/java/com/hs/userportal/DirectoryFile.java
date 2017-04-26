package com.hs.userportal;

import java.util.Date;

/**
 * Created by rishabh on 6/4/17.
 */

public class DirectoryFile {

    private String key;
    private String path;
    private Date lastModified;
    private long size;
    private double roundOff;
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
        if (key.contains("ZurekaTempPatientConfig")) {

        } else {
            String[] thumbs = key.split("\\.");
            thumb = thumbs[0] + "_thumb." + thumbs[1];
        }

    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public double getSize() {
        return roundOff;
    }

    public void setSize(long size) {
        this.size = size;
        double sizeinKb = size / 1000;
        roundOff = Math.round(sizeinKb * 100.0) / 100.0;
    }

    public String getThumb() {
        return thumb;
    }
}
