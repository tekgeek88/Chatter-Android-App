package edu.uw.team02tcss450.model;

public class ChatThread {
    private final int mChatId;
    private final String mName;


    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final int mChatId;
        private final String mName;


        /**
         * Constructs a new Builder.
         *
         * @param chatId the id of the user
         */
        public Builder(String name, int chatId) {
            this.mChatId = chatId;
            this.mName = name;


        }


        public ChatThread build() {
            return new ChatThread(this);
        }

    }

    private ChatThread(final ChatThread.Builder builder) {
        this.mChatId= builder.mChatId;
        this.mName = builder.mName;


    }

    public int getChatId() {
        return mChatId;
    }

    public String getName() {
        return mName;
    }




}
