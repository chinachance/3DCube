package com.haohao.framwork.a3dcube;

/**
 * Created by Ma1 on 2017/4/17.
 */

public class MessageEvent {
    private int messageId;
    private float message;
    private boolean fourFace;

    public MessageEvent(int messageId, float message,boolean fourFace) {
        this.messageId = messageId;
        this.message = message;
        this.fourFace = fourFace;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public float getMessage(){
        return message;
    }

    public void setMessage(float message){
        this.message = message;
    }

    public boolean getFourFace(){
        return fourFace;
    }

}
