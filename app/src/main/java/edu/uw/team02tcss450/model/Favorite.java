package edu.uw.team02tcss450.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import edu.uw.team02tcss450.HomeActivity;
import edu.uw.team02tcss450.R;
import edu.uw.team02tcss450.WaitFragment;
import edu.uw.team02tcss450.WeatherFragment;
import edu.uw.team02tcss450.utils.GetAsyncTask;

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


    public void onAttach(Fragment fragment) {
        if (fragment instanceof Favorite.OnFavoriteInteractionListener) {
            mListener = (Favorite.OnFavoriteInteractionListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString()
                    + " must implement OnFavoriteInteractionListener");
        }
    }


    public void onDetach() {
        mListener = null;
    }

    private void removeFavorite (View v) {
        //DELETE async call to delete this favorite then reload
        mListener.onDeleteFavorite(mNickname);
        reloadFavorites();
    }

    private void loadFavorite (View v) {
        Log.d("FAVORITES", "In favorite load favorite");
        Log.d("FAVORITES", getLatlng().toString());
        Log.d("FAVORITES", Integer.toString((int)getZip()));
        if ((int)getZip() == 0) {
            mListener.onLoadFavorite(getLatlng());
        } else {
            Log.d("FAVORITES", Integer.toString((int)getZip()));
            mListener.onLoadFavorite(Integer.toString((int)getZip()));
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
        void onDeleteFavorite (String nickname);
    }
}
