package edu.uw.team02tcss450.model;

import android.util.Log;

import java.io.Serializable;

public class ChatThread implements Serializable {
    private final int mChatId;
    private  String mName;
    private final String mUserName;
    private static final long serialVersionUID = 752647229332776147L;


    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final int mChatId;
        private  String mName;
        private final String mUserName;


        /**
         * Constructs a new Builder.
         *
         * @param chatId the id of the user
         */
        public Builder(String name, int chatId, String userName) {
            this.mChatId = chatId;
            this.mName = name;
            this.mUserName = userName;

        }


        public ChatThread build() {
            return new ChatThread(this);
        }

    }

    private ChatThread(final ChatThread.Builder builder) {
        this.mChatId= builder.mChatId;
        this.mName = builder.mName;
        this.mUserName = builder.mUserName;


    }

    public int getChatId() {
        return mChatId;
    }

    public String getName() {

        return mName;
    }




}
