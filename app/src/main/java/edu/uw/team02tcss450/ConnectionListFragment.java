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
import android.widget.ArrayAdapter;
import android.widget.Button;

import edu.uw.team02tcss450.model.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ConnectionListFragment extends Fragment {


   // private static ArrayAdapter<Object> mConnectionAdapter;
    /**
     * A simple {@link android.support.v4.app.Fragment} subclass.
     */
    private MyConnectionListRecyclerViewAdapter mConnectionAdapter;
    public Button mChatButton;
    public Button mRemoveButton;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public static final String ARG_CONNECTION_LIST = "connection lists";
    private  List<Connection> mConnections;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionListFragment() {
    }

    // TODO: Customize parameter initialization


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatButton = getActivity().findViewById(R.id.button_activity_home_chat);
        mChatButton.setVisibility(View.VISIBLE);
        mRemoveButton = getActivity().findViewById(R.id.button_activity_home_remove);
        mRemoveButton.setVisibility(View.VISIBLE);
        if (getArguments() != null) {
            Log.e("List", getArguments().toString());

            mConnections = new ArrayList<Connection>(
                    Arrays.asList((Connection[]) getArguments().getSerializable(ARG_CONNECTION_LIST)));

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requestlist, container, false);
        getActivity().setTitle("Friends");
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new MyConnectionListRecyclerViewAdapter(mConnections,mListener));
            mConnectionAdapter = new MyConnectionListRecyclerViewAdapter(mConnections, mListener);
            recyclerView.setAdapter(mConnectionAdapter);
        }

        return view;
    }

    public void removeItems(ArrayList<Connection> c) {
        if (null != c && null != mConnections) {
            for(int i = 0; i<c.size();i++) {

                mConnectionAdapter.removeItem(c.get(i));
                mConnectionAdapter.notifyDataSetChanged();
            }

        }
    }






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onPause() {
        super.onPause();
        mChatButton.setVisibility(View.GONE);
        mRemoveButton.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {

        super.onResume();
        mChatButton.setVisibility(View.VISIBLE);
        mRemoveButton.setVisibility(View.VISIBLE);

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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name



        void onCheckBoxListInteraction(View v, Connection mItem);

        void onUncheckBoxListInteraction(View v, Connection mItem);
    }







}
