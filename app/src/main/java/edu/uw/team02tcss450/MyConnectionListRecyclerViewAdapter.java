package edu.uw.team02tcss450;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.uw.team02tcss450.ConnectionListFragment.OnListFragmentInteractionListener;
import edu.uw.team02tcss450.model.Connection;

import java.util.ArrayList;
import java.util.List;

public class MyConnectionListRecyclerViewAdapter extends RecyclerView.Adapter<MyConnectionListRecyclerViewAdapter.ViewHolder> {

    private final List<Connection> mValues;
    private final OnListFragmentInteractionListener mListener;


    public MyConnectionListRecyclerViewAdapter(List<Connection> items, OnListFragmentInteractionListener listener) {
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
        holder.mBtnCancel.setVisibility(View.GONE);

        //Listener for checkbox
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()) {
                    mListener.onCheckBoxListInteraction(v, holder.mItem);
                }else{
                   mListener.onUncheckBoxListInteraction(v,holder.mItem);
                }
            }
        });

        holder.mBtnAccept.setVisibility(View.GONE);

    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //Removes the connections from the list
    public void removeItem(Connection c){
        mValues.remove(c);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mUserName;
        public final TextView mBtnAccept;
        public final TextView mBtnCancel;
        public final CheckBox mCheckBox;
        public Connection mItem;

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
