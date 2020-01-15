package com.dating.datesinglegetmingle.Pojo;

import java.io.Serializable;

/**
 * Created by Ravindra Birla on 20/07/2019.
 */
public class Message implements Serializable {
    public String message;
    public String senderId;
    public String image;
    public Long time;
    public String receiverId;
    public boolean seen;
}
