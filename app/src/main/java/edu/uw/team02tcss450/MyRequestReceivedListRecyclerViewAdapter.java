package edu.uw.team02tcss450;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.RequestReceivedListFragment.OnRequestReceivedListFragmentInteractionListener;
import edu.uw.team02tcss450.model.Connections;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Connections} and makes a call to the
 * specified {@link OnRequestReceivedListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRequestReceivedListRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestReceivedListRecyclerViewAdapter.ViewHolder> {

    private final List<Connections> mValues;
    private final OnRequestReceivedListFragmentInteractionListener mListener;

    public MyRequestReceivedListRecyclerViewAdapter(List<Connections> items, OnRequestReceivedListFragmentInteractionListener listener) {
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
//        TextView status = (TextView) holder.mView.findViewById(R.id.textview_fragment_connection_status);
//        status.setText(mValues.get(position).getVerified());

        holder.mItem = mValues.get(position);
        holder.mFirstName.setText(mValues.get(position).getFirstName());
        holder.mLastName.setText(mValues.get(position).getLastName());
        holder.mUserName.setText(mValues.get(position).getUserName());
        holder.mCheckBox.setVisibility(View.GONE);
        holder.mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRequestReceivedListButtonInteraction(v, holder.mItem);
                mValues.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValues.remove(position);
                notifyDataSetChanged();
                mListener.onRequestReceivedListButtonInteraction(v, holder.mItem);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRequestReceivedListFragmentInteraction(holder.mItem);
                }
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
        public final CheckBox mCheckBox;
        public Connections mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFirstName = (TextView) view.findViewById(R.id.textview_requests_namefirst);
            mLastName = (TextView) view.findViewById(R.id.textview_requests_namelast);
            mUserName = (TextView) view.findViewById(R.id.textview_requests_nickname);
            mBtnAccept = (TextView) view.findViewById(R.id.textview_requests_accept);
            mBtnCancel = (TextView) view.findViewById(R.id.textview_requests_cancel);
            mCheckBox = (CheckBox) view.findViewById(R.id.checkBox_request_check);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUserName.getText() + "'";
        }
    }
}
