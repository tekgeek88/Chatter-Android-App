package edu.uw.team02tcss450.model;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import edu.uw.team02tcss450.WaitFragment;
import edu.uw.team02tcss450.WeatherFragment;

public class Favorite {
    private TextView mName;
    private ImageButton mDelete;
    private ImageButton mLoad;
    private double mZipcode;
    private double mLat;
    private double mLong;
    private String mNickname;
    OnFavoriteInteractionListener mListener;

    public static class Builder {
        //Required
        private TextView mName;
        private ImageButton mDelete;
        private ImageButton mLoad;
        private String mNickname;

        //Optional
        private double mZipcode = 0;
        private double mLat = 0;
        private double mLong = 0;

        public Builder (String nick, TextView name, ImageButton delete, ImageButton load) {
            mNickname = nick;
            mName = name;
            mDelete = delete;
            mLoad = load;
        }

        public Builder LatLng (double lat, double lng) {
            mLat = lat;
            mLong = lng;
            return this;
        }
        public Builder LatLng (LatLng latLng) {
            mLat = latLng.latitude;
            mLong = latLng.longitude;
            return this;
        }
        public Builder zipcode (double zipcode) {
            mZipcode = zipcode;
            return this;
        }
        public Builder zipcode (String zipcode) {
            mZipcode = Double.parseDouble(zipcode);
            return this;
        }
        public Favorite build () {
            return new Favorite(this);
        }
    }

    private Favorite (Builder builder) {
        mNickname = builder.mNickname;
        mLoad = builder.mLoad;
        mDelete = builder.mDelete;
        mName = builder.mName;

        mLat = builder.mLat;
        mLong = builder.mLong;
        mZipcode = builder.mZipcode;
        onStart();
    }

    private void onStart () {
        mLoad.setOnClickListener(this::loadFavorite);
        mDelete.setOnClickListener(this::removeFavorite);
        mName.setText(mNickname);
    }

    private void removeFavorite (View v) {
        //DELETE async call to delete this favorite then reload
        reloadFavorites();
    }

    private void loadFavorite (View v) {
        if (getZip() == 0) {
            mListener.onLoadFavorite(getLatlng());
        } else {
            mListener.onLoadFavorite(Double.toString(getZip()));
        }
    }

    private void reloadFavorites () {
        mListener.onReloadFavorites();
    }

    public double getZip () {
        return mZipcode;
    }
    public LatLng getLatlng () {
        return new LatLng(mLat,mLong);
    }


    public interface OnFavoriteInteractionListener {
        void onReloadFavorites();
        void onLoadFavorite(LatLng latLng);
        void onLoadFavorite(String zipcode);
    }
}
