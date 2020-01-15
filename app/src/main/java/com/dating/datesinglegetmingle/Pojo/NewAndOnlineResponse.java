package com.dating.datesinglegetmingle.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewAndOnlineResponse {

    @SerializedName("data")
    @Expose
    private List<NewAndOnlineList> data = null;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("msg")
    @Expose
    private String msg;

    public List<NewAndOnlineList> getData() {
        return data;
    }

    public void setData(List<NewAndOnlineList> data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
