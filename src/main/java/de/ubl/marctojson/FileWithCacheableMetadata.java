package de.ubl.marctojson;

import java.io.File;

import org.mapdb.DB;

public class FileWithCacheableMetadata extends File {

    private DB cache;

    private static final long serialVersionUID = -265899951507353083L;

    public FileWithCacheableMetadata(String pathname) {
        super(pathname);
    }

    public DB getCache() {
        return this.cache;
    }

    public void setCache(DB cache) {
        this.cache = cache;
    }
}
