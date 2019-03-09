package edu.uw.team02tcss450;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.uw.team02tcss450.model.EveryMessage;
import edu.uw.team02tcss450.utils.PushReceiver;
import edu.uw.team02tcss450.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {



    private static final String TAG = "CHAT_FRAG";

    private PushMessageReceiver mPushMessageReciever;

    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;

    //Zebin add
    private ArrayList<EveryMessage> mChatList;
    private ListView mMessageOutputListView;
    private ArrayAdapter<EveryMessage> mChatListAdapter;

    private String mEmail;
    private String mJwToken;
    private String mSendUrl;
    private int mChatId;




    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReciever == null) {
            mPushMessageReciever = new PushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushMessageReciever, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReciever != null){
            getActivity().unregisterReceiver(mPushMessageReciever);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);

        mMessageOutputTextView = rootLayout.findViewById(R.id.textview_fragment_chat_my_chat_bubble_message_output);
        mMessageInputEditText = rootLayout.findViewById(R.id.edittext_fragment_chat_message_input);
        mMessageInputEditText.setImeActionLabel("Send", KeyEvent.KEYCODE_ENTER);
        mMessageInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    if (v.getText().toString().isEmpty()) {
                        Toast toast = Toast.makeText(getActivity(), "Please enter a message to send!", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        handleSendClick(v);
//                        return true;
                    }
                }
                return false;
            }
        });

            // Zebin add
            mMessageOutputListView = rootLayout.findViewById(R.id.listview_fragment_chat_list);

            mChatList = new ArrayList<EveryMessage>();

                if (getArguments() != null) {
                mChatList = (ArrayList<EveryMessage>) getArguments().getSerializable(getString(R.string.keys_intent_messages));
            }
                rootLayout.findViewById(R.id.btn_fragment_chat_send).setOnClickListener(this::handleSendClick);
            getActivity().setTitle("Chat");
                return rootLayout;
        }


        @Override
        public void onStart() {
            super.onStart();

            // Lets check and see if we have a package
            if (getArguments() != null) {
                //get the email and JWT from the Activity. Make sure the Keys match what you used
                mEmail = getArguments().getString(getString(R.string.key_email));
                mJwToken = getArguments().getString(getString(R.string.keys_intent_jwt));
                mChatId = getArguments().getInt(getString(R.string.key_chat_id));
            }
            mChatList = new ArrayList<EveryMessage>();
            // Open the bundle and stock all the messages in the order they were sent.
            for (int i = mChatList.size() - 1; i >= 0; i--) {
                String sender = mChatList.get(i).getSenderName();
                String messageText = mChatList.get(i).getSenderMessageContent();
                mChatList.add(new EveryMessage(sender, messageText, mEmail));
                mChatListAdapter = new ChatListAdapter(getContext(), mChatList, messageText, sender);
                mMessageOutputListView.setAdapter(mChatListAdapter);
//            mMessageOutputListView.setSelection(mMessageOutputListView.getCount() - 1);
            }
            mMessageOutputListView.setSelection(mMessageOutputListView.getCount() - 1);


            //We will use this url every time the user hits send. Let's only build it once, ya?
            mSendUrl = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_messaging_base))
                    .appendPath(getString(R.string.ep_messaging_send))
                    .build()
                    .toString();
        }


        private void handleSendClick(final View theButton) {
            String msg = mMessageInputEditText.getText().toString();

            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("email", mEmail);
                messageJson.put("message", msg);
                messageJson.put("chat_id", mChatId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                    .onPostExecute(this::endOfSendMsgTask)
                    .onCancelled(error -> Log.e(TAG, error))
                    .addHeaderField("authorization", mJwToken)
                    .build().execute();
        }

        private void endOfSendMsgTask(final String result) {
            try {
                //This is the result from the web service
                JSONObject res = new JSONObject(result);

                if(res.has("success")  && res.getBoolean("success")) {
                    //The web service got our message. Time to clear out the input EditText
                    mMessageInputEditText.setText("");

                    //its up to you to decide if you want to send the message to the output here
                    //or wait for the message to come back from the web service.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static String getTAG() {
            return TAG;
        }

        /**
         * A BroadcastReceiver that listens for messages sent from PushReceiver
         */
        private class PushMessageReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.hasExtra("SENDER") && intent.hasExtra("MESSAGE")) {

                    String sender = intent.getStringExtra("SENDER");
                    String messageText = intent.getStringExtra("MESSAGE");
                    mChatList.add(new EveryMessage(sender, messageText, mEmail));
                    mChatListAdapter = new ChatListAdapter(getContext(), mChatList, messageText, sender);
                    mMessageOutputListView.setAdapter(mChatListAdapter);
                    mMessageOutputListView.setSelection(mMessageOutputListView.getCount() - 1);
                }
            }
        }
    }
