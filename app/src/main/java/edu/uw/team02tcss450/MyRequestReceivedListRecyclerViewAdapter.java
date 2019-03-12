package edu.uw.team02tcss450;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.RequestReceivedListFragment.OnRequestReceivedInteractionListener;
import edu.uw.team02tcss450.model.Connection;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Connection} and makes a call to the
 * specified {@link OnRequestReceivedInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRequestReceivedListRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestReceivedListRecyclerViewAdapter.ViewHolder> {

    private final List<Connection> mValues;
    private final OnRequestReceivedInteractionListener mListener;

    public MyRequestReceivedListRecyclerViewAdapter(List<Connection> items, OnRequestReceivedInteractionListener listener) {
        if (null == items) {
            items = new ArrayList<>();
        }
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_requestlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mFirstName.setText(mValues.get(position).getFirstName());
        holder.mLastName.setText(mValues.get(position).getLastName());
        holder.mUserName.setText(mValues.get(position).getUserName());
        // Request Received ACCEPT Interaction
        holder.mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRequestReceivedAcceptInteraction(holder.mItem);
            }
        });
        // Request Received CANCEL Interaction
        holder.mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRequestReceivedAcceptInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mValues) {
                return 0;
        } else {
            return mValues.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mUserName;
        public final TextView mBtnAccept;
        public final TextView mBtnCancel;
        public Connection mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFirstName = (TextView) view.findViewById(R.id.textview_requests_namefirst);
            mLastName = (TextView) view.findViewById(R.id.textview_requests_namelast);
            mUserName = (TextView) view.findViewById(R.id.textview_requests_nickname);
            mBtnAccept = (TextView) view.findViewById(R.id.textview_requests_accept);
            mBtnCancel = (TextView) view.findViewById(R.id.textview_requests_cancel);
            ((CheckBox) view.findViewById(R.id.checkBox_request_check)).setVisibility(View.GONE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }
}
