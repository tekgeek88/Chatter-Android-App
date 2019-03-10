package edu.uw.team02tcss450;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import edu.uw.team02tcss450.RecentChatFragment.OnRecentChatListFragmentInteractionListener;
import edu.uw.team02tcss450.dummy.DummyContent.DummyItem;
import edu.uw.team02tcss450.model.ChatThread;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnRecentChatListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRecentChatRecyclerViewAdapter extends RecyclerView.Adapter<MyRecentChatRecyclerViewAdapter.ViewHolder> {


private final List<ChatThread> mValues;
    private final RecentChatFragment.OnRecentChatListFragmentInteractionListener mListener;


    public MyRecentChatRecyclerViewAdapter(List<ChatThread> items, RecentChatFragment.OnRecentChatListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

    }

    @Override
    public MyRecentChatRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_requestlist_item, parent, false);


        return new MyRecentChatRecyclerViewAdapter.ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(final MyRecentChatRecyclerViewAdapter.ViewHolder holder, int position) {


        holder.mItem = mValues.get(position);

        holder.mFirstName.setText(mValues.get(position).getName());


        holder.mLastName.setVisibility(View.GONE);
        holder.mUserName.setVisibility(View.GONE);
        holder.mBtnCancel.setVisibility(View.GONE);
        holder.mCheckBox.setVisibility(View.GONE);
        holder.mBtnAccept.setVisibility(View.GONE);



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRecentChatListFragmentInteraction(holder.mItem);
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


        void onListFragmentInteraction(ChatThread mItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFirstName;
        public final TextView mLastName;
        public final TextView mUserName;
        public final TextView mBtnAccept;
        public final TextView mBtnCancel;
        public final CheckBox mCheckBox;
        public ChatThread mItem;

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
