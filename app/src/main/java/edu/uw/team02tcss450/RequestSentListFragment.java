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
 * Activities containing this fragment MUST implement the {@link OnRequestSentInteractionListener}
 * interface.
 */
public class RequestSentListFragment extends Fragment {

    public static final String TAG = "REQUEST_SENT_FRAG";

    MyRequestSentListRecyclerViewAdapter mAdapter;

    /**
     * A simple {@link android.support.v4.app.Fragment} subclass.
     */

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnRequestSentInteractionListener mListener;
    private List<Connection> mConnections;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestSentListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            Log.e("List", getArguments().toString());
            mConnections = new ArrayList<Connection>(
                    Arrays.asList((Connection[]) getArguments()
                            .getSerializable(getString(R.string.keys_intent_connections_sent))));
        } else {
            mConnections = new ArrayList<>();
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
            mAdapter = new MyRequestSentListRecyclerViewAdapter(mConnections, mListener);

            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRequestSentInteractionListener) {
            mListener = (OnRequestSentInteractionListener) context;
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

    public void addItem(Connection c) {
        if (null != c) {
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

    public void updateConnections(Connection[] connection) {
        if (null != connection) {
            mConnections = new ArrayList<Connection>(
                    Arrays.asList((Connection[]) connection));
        }
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
    public interface OnRequestSentInteractionListener {

        // This interface is used to allow MainActivity to be in control
        // of the removal of items from the data model
        void onRequestSentCancelInteraction(Connection item);
    }
}
