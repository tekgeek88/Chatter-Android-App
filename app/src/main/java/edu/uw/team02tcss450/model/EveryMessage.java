package edu.uw.team02tcss450.model;

public class EveryMessage {
    private String mName;
    private String mMessageContent;
    private String mEmail;

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
}
