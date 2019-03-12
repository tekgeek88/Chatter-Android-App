package edu.uw.team02tcss450;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uw.team02tcss450.model.Connection;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRequestReceivedInteractionListener}
 * interface.
 */
public class RequestReceivedListFragment extends Fragment {

    public static final String TAG = "REQUEST_RECEIVED_FRAG";
    MyRequestReceivedListRecyclerViewAdapter mAdapter;


    /**
     * A simple {@link Fragment} subclass.
     */

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnRequestReceivedInteractionListener mListener;
    private List<Connection> mConnections;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestReceivedListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Log.e("List", getArguments().toString());
            Connection[] connects = (Connection[]) getArguments()
                    .getSerializable(getString(R.string.keys_intent_connections_received));
            if (null == connects) {
                mConnections = new ArrayList<>();
            } else {
                mConnections = new ArrayList<Connection>(
                        Arrays.asList(connects));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requestlist, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mAdapter = new MyRequestReceivedListRecyclerViewAdapter(mConnections, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    public void addItem(Connection c) {
        if (null != c && null != mConnections) {
            mConnections.add(c);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void removeItem(Connection c) {
        if (null != c && null != mConnections) {
            mConnections.remove(c);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateConnections(Connection[] connections) {
        if (null != connections) {
            mConnections = new ArrayList<Connection>(
                    Arrays.asList((Connection[]) connections));
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRequestReceivedInteractionListener) {
            mListener = (OnRequestReceivedInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRequestSentListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRequestReceivedInteractionListener {
        void onRequestReceivedAcceptInteraction(Connection connection);
        void onRequestReceivedCancelInteraction(Connection connection);
    }
}
