package com.proton.easycooking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoEatItem {
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Items")
    @Expose
    private String items;
    @SerializedName("Action")
    @Expose
    private String action;
    @SerializedName("Status")
    @Expose
    private String status;

    /**
     * No args constructor for use in serialization
     *
     */
    public NoEatItem() {
    }

    /**
     *
     * @param action
     * @param id
     * @param items
     * @param status
     */
    public NoEatItem(String id, String items, String action, String status) {
        super();
        this.id = id;
        this.items = items;
        this.action = action;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
