package edu.uw.team02tcss450.model;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class EveryMessage implements Serializable {
    private int mChatId;
    private String mName;
    private String mMessageContent;
    private String mEmail;
    private Timestamp mTimeStamp;

    private static final long serialVersionUID = 7526472295622776147L;


    public EveryMessage(String name, String messageContent, String email) {
        mName = name;
        mMessageContent = messageContent;
        mEmail = email;
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

    public Date getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = Timestamp.valueOf(timeStamp);
    }

    public int getChatId() {
        return mChatId;
    }

    public void setChatId(int chatId) {
        mChatId = chatId;
    }
}
