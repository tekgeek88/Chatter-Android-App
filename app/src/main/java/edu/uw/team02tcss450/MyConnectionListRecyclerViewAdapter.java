package edu.uw.team02tcss450;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.team02tcss450.ConnectionListFragment.OnListFragmentInteractionListener;
import edu.uw.team02tcss450.dummy.DummyContent;
import edu.uw.team02tcss450.dummy.DummyContent.DummyItem;
import edu.uw.team02tcss450.model.Connections;
import edu.uw.team02tcss450.utils.GetAsyncTask;

import java.util.ArrayList;
import java.util.List;

import static android.provider.Settings.System.getString;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyConnectionListRecyclerViewAdapter extends RecyclerView.Adapter<MyConnectionListRecyclerViewAdapter.ViewHolder> {

    private final List<Connections> mValues;
    private final OnListFragmentInteractionListener mListener;


    public MyConnectionListRecyclerViewAdapter(List<Connections> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
       
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_connectionlist, parent, false);


        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TextView status = (TextView) holder.mView.findViewById(R.id.textview_fragment_connection_status);
        status.setText(mValues.get(position).getVerified());

        holder.mItem = mValues.get(position);
        holder.mFirstName.setText(mValues.get(position).getFirstName());
        holder.mLastName.setText(mValues.get(position).getLastName());
        holder.mUserName.setText(mValues.get(position).getUserName());



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public interface OnButtonFragmentInteractionListener {
        // TODO: Update argument type and name


        void onListFragmentInteraction(Connections mItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mUserName;
        public Connections mItem;
        public String mButtonAction;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFirstName = (TextView) view.findViewById(R.id.textview_fragment_connection_firstName);
            mLastName = (TextView) view.findViewById(R.id.textview_fragment_connection_lastName);
            mUserName = (TextView) view.findViewById(R.id.textview_fragment_connection_userName);



        }



        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }
}
