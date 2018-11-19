package com.guyu.android.base;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/9/27.
 */

public class BaseEntity<E> {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private E data;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public E getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
}
