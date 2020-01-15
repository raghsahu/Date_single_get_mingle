package com.dating.datesinglegetmingle.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InterestList {

    @SerializedName("interest_id")
    @Expose
    private String interestId;
    @SerializedName("interest_field_name")
    @Expose
    private String interestFieldName;
    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInterestId() {
        return interestId;
    }

    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }

    public String getInterestFieldName() {
        return interestFieldName;
    }

    public void setInterestFieldName(String interestFieldName) {
        this.interestFieldName = interestFieldName;
    }
}
