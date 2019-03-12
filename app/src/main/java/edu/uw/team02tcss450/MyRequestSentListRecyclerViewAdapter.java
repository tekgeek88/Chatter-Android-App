package edu.uw.team02tcss450;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.uw.team02tcss450.RequestSentListFragment.OnRequestSentInteractionListener;
import edu.uw.team02tcss450.model.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Connection} and makes a call to the
 * specified {@link OnRequestSentInteractionListener}.
 */
public class MyRequestSentListRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestSentListRecyclerViewAdapter.ViewHolder> {

    private final List<Connection> mValues;
    private final OnRequestSentInteractionListener mListener;

    public MyRequestSentListRecyclerViewAdapter(List<Connection> items, OnRequestSentInteractionListener listener) {
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
        MyRequestSentListRecyclerViewAdapter adapter = this;
        holder.mItem = mValues.get(position);
        holder.mFirstName.setText(mValues.get(position).getFirstName());
        holder.mLastName.setText(mValues.get(position).getLastName());
        holder.mUserName.setText(mValues.get(position).getUserName());

        // Cancel is used to cancel a request that we have sent to another user
        holder.mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This listener will communicate back to main activity and allow us to
                // use the adapter to remove an item.
                mListener.onRequestSentCancelInteraction(holder.mItem);
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
        public Connection mItem;
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mUserName;
        public final TextView mBtnCancel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFirstName = (TextView) view.findViewById(R.id.textview_requests_namefirst);
            mLastName = (TextView) view.findViewById(R.id.textview_requests_namelast);
            mUserName = (TextView) view.findViewById(R.id.textview_requests_nickname);
            mBtnCancel = (TextView) view.findViewById(R.id.textview_requests_cancel);

            // Theses views are unused in this context
            ((TextView) view.findViewById(R.id.textview_requests_accept)).setVisibility(View.GONE);
            ((CheckBox) view.findViewById(R.id.checkBox_request_check)).setVisibility(View.GONE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }
}
