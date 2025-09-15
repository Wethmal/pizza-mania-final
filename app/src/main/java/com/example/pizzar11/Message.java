package com.example.pizzar11;

public class Message {
    private String message;
    private long timestamp;
    private String uid;

    // Empty constructor required by Firestore
    public Message() {}

    public Message(String message, long timestamp, String uid) {
        this.message = message;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUid() {
        return uid;
    }
}
