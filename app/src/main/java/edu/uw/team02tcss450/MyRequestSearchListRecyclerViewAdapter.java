package edu.uw.team02tcss450;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.team02tcss450.RequestSearchListFragment.OnRequestSearchListFragmentInteractionListener;
import edu.uw.team02tcss450.model.Connections;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Connections} and makes a call to the
 * specified {@link OnRequestSearchListFragmentInteractionListener}.
 */
public class MyRequestSearchListRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestSearchListRecyclerViewAdapter.ViewHolder> {

    private final List<Connections> mValues;
    private final OnRequestSearchListFragmentInteractionListener mListener;
    private MyRequestSearchListRecyclerViewAdapter mAdapter;

    public MyRequestSearchListRecyclerViewAdapter(List<Connections> items, OnRequestSearchListFragmentInteractionListener listener) {
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
        MyRequestSearchListRecyclerViewAdapter adapter = this;
        holder.mItem = mValues.get(position);
        holder.mFirstName.setText(mValues.get(position).getFirstName());
        holder.mLastName.setText(mValues.get(position).getLastName());
        holder.mUserName.setText(mValues.get(position).getUserName());
        holder.mCheckBox.setVisibility(View.GONE);
        holder.mBtnAccept.setVisibility(View.GONE);
        holder.mBtnCancel.setText("SEND REQUEST");
        holder.mBtnAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.onRequestSearchListButtonInteraction(v, holder.mItem);
            }
        });
        holder.mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValues.remove(position);
                notifyDataSetChanged();
                mListener.onRequestSearchListButtonInteraction(v, holder.mItem);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRequestSearchListFragmentInteraction(holder.mItem);
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

    public void removeItem(int position) {
        mValues.remove(position);
        notifyDataSetChanged();
    }

    public void removeItem(Connections connection) {
        mValues.remove(connection);
        notifyDataSetChanged();
    }

    public void addItem(Connections connection) {
        mValues.add(connection);
        notifyDataSetChanged();
    }

    public void setItems(List<Connections> items) {
        mValues.clear();
        for(Connections c: items) {
            mValues.add(c);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Connections mItem;
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mUserName;
        public final TextView mBtnAccept;
        public final TextView mBtnCancel;
        public final CheckBox mCheckBox;

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