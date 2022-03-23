package org.barrelmc.barrel.network.data;

import com.alibaba.fastjson.JSONObject;

public class Form {
    public Type type;
    public int array;
    public JSONObject data;

    public Form(Type type, int array, JSONObject data) {
        this.type = type;
        this.array = array;
        this.data = data;
    }

    public enum Type {
        SIMPLE, MODAL, CUSTOM
    }
}
