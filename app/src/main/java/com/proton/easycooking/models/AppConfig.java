package com.proton.easycooking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppConfig {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Value")
    @Expose
    private String value;

    /**
     * No args constructor for use in serialization
     *
     */
    public AppConfig() {
    }

    /**
     *
     * @param name
     * @param id
     * @param value
     */
    public AppConfig(String id, String name, String value) {
        super();
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
