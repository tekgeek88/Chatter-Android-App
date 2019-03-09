package edu.uw.team02tcss450.model;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class EveryMessage {
    private int mChatId;
    private String mName;
    private String mMessageContent;
    private String mEmail;
    private Timestamp mTimeStamp;

    public EveryMessage(String name, String messageContent, String email) {
        mName = name;
        mMessageContent = messageContent;
        mEmail = email;
        /*


        "username": "TheRealTek",
            "email": "tekgeek88@yahoo.com",
            "message": "Hi",
            "timestamp": "2019-03-07 22:41:20.811259"

         */
    }

    public String getSenderName() {
        return mName;
    }
    public String getSenderMessageContent() {
        return mMessageContent;
    }
    public String getSenderEmail() {
        return mEmail;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = Timestamp.valueOf(timeStamp);
    }

    public Date getmChatId() {
        return mTimeStamp;
    }

    public void setChatId(int chatId) {
        mChatId = chatId;
    }
}
