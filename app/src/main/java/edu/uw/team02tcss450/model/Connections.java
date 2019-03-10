package edu.uw.team02tcss450.model;

import java.io.Serializable;

public class Connections implements Serializable {
    private final int mMemberId;
    private final String mFirstName;
    private final String mLastName;
    private final String mUserName;
    private final int mVerified;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final int mMemberId;
        private final String mFirstName;
        private final String mLastName;
        private final String mUserName;
        private final int mVerified;

        /**
         * Constructs a new Builder.
         *
         * @param id the id of the user
         */
        public Builder(int id, String firstName, String lastName, String userName, int verified) {
            this.mMemberId = id;
            this.mFirstName = firstName;
            this.mLastName = lastName;
            this.mUserName = userName;
            this.mVerified = verified;

        }

        public Connections build() {
            return new Connections(this);
        }

    }

    private Connections(final Connections.Builder builder) {
        this.mFirstName = builder.mFirstName;
        this.mLastName = builder.mLastName;
        this.mMemberId = builder.mMemberId;
        this.mUserName = builder.mUserName;
        this.mVerified = builder.mVerified;

    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getUserName() {
        return mUserName;
    }

    public int getIsVerified() {return mVerified; }

    public int getMemberId() {
        return mMemberId;
    }

    public String getVerified() {
        if(mVerified==1){
            return "ADDED";
        }else{
            return "NOT ADDED";
        }

    }



}

