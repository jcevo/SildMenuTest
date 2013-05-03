package com.alvin.api.model;

import android.database.Cursor;

import java.text.ParseException;

/**
 * 系统信息数据实体
 */
public class Info {
    private long id;
    private String name;
    private String value;
    private int status;
    private long time;

    public final static String NAME_VERSION_CODE = "versionCode";

    public void parse(Cursor cur) throws ParseException {
        this.setId(cur.getLong(cur.getColumnIndex("id")));
        this.setName(cur.getString(cur.getColumnIndex("name")));
        this.setValue(cur.getString(cur.getColumnIndex("value")));
        this.setStatus(cur.getInt(cur.getColumnIndex("status")));
        this.setTime(cur.getLong(cur.getColumnIndex("time")));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
