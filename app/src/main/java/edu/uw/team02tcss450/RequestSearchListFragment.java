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

import edu.uw.team02tcss450.model.Connections;

//import edu.uw.team02tcss450.dummy.DummyContent;
//import edu.uw.team02tcss450.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRequestSearchListFragmentInteractionListener}
 * interface.
 */
public class RequestSearchListFragment extends Fragment {

    MyRequestSearchListRecyclerViewAdapter mAdapter;

    public static final String TAG = "REQUEST_SENT_FRAG";

    /**
     * A simple {@link Fragment} subclass.
     */

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnRequestSearchListFragmentInteractionListener mListener;
    private List<Connections> mConnections;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RequestSearchListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            Log.e("List", getArguments().toString());
            mConnections = new ArrayList<Connections>(
                    Arrays.asList((Connections[]) getArguments()
                            .getSerializable(getString(R.string.keys_intent_connections_sent))));
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
            mAdapter = new MyRequestSearchListRecyclerViewAdapter(mConnections, mListener);
            recyclerView.setAdapter(mAdapter);
//            recyclerView.setAdapter(new MyRequestSearchListRecyclerViewAdapter(mConnections, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRequestSearchListFragmentInteractionListener) {
            mListener = (OnRequestSearchListFragmentInteractionListener) context;
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
    public interface OnRequestSearchListFragmentInteractionListener {
        void onRequestSearchListFragmentInteraction(Connections item);
        void onRequestSearchListButtonInteraction(View v, Connections item);
    }

    public void updateConnections(Connections[] connections) {
        if (null != connections) {
            if (null != connections && null != mAdapter) {
                mConnections = new ArrayList<Connections>(
                        Arrays.asList((Connections[]) connections));
                mAdapter.notifyDataSetChanged();
            }
        }

    }
}
