package com.library.library_api.dto;

public class VersionWrapper<T> {

    private T data;
    private String version;

    public VersionWrapper(T data, String version) {
        this.data = data;
        this.version = version;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
