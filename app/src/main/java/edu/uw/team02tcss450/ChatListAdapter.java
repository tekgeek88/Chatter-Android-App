package edu.uw.team02tcss450;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import edu.uw.team02tcss450.model.EveryMessage;

public class ChatListAdapter extends ArrayAdapter<EveryMessage> {
    private Context mContext;
    private String mChatContent;
    private String mSender;
    private ArrayList<EveryMessage> mChatList;

    public ChatListAdapter(Context context, ArrayList<EveryMessage> chatList, String chatContent, String sender) {
        super(context, 0, 0, chatList);
        mContext = context;
        mChatList = chatList;
        mChatContent = chatContent;
        mSender = sender;

    }

    public String getChatContent() {
        return mChatContent;
    }

    public String getSender() {
        return mSender;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater messageInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        EveryMessage msg = getItem(position);

        if (!msg.getSenderEmail().equals(msg.getSenderName())) {
            convertView = messageInflater.inflate(R.layout.fragment_chat_sent_by_others_chat_bubble, null);
            TextView chatContent = (TextView) convertView.findViewById(R.id.textview_fragment_chat_sent_by_others_chat_bubble_message_output);
            TextView senderName = (TextView) convertView.findViewById(R.id.textview_fragment_chat_sent_by_others_chat_bubble_name);
            chatContent.setText(msg.getSenderMessageContent());
            senderName.setText(msg.getSenderName());

            Button removeButton = (Button) convertView.findViewById(R.id.btn_fragment_chat_sent_by_others_chat_bubble_remove);
            Button cancelButton = (Button) convertView.findViewById(R.id.btn_fragment_chat_sent_by_others_chat_bubble_cancel);
            convertView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chatContent.setVisibility(View.INVISIBLE);
                            senderName.setVisibility(View.INVISIBLE);
                            removeButton.setVisibility(View.VISIBLE);
                            removeButton.setTag(position);
                            removeButton.setOnClickListener(
                                    new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Integer index = (Integer) v.getTag();
                                            mChatList.remove(index.intValue());
                                            notifyDataSetChanged();
                                        }
                                    });
                            cancelButton.setVisibility(View.VISIBLE);
                            cancelButton.setOnClickListener(
                                    new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            removeButton.setVisibility(View.INVISIBLE);
                                            cancelButton.setVisibility(View.INVISIBLE);
                                            chatContent.setVisibility(View.VISIBLE);
                                            senderName.setVisibility(View.VISIBLE);
                                        }
                                    }
                            );
                        }
                    }
            );

        } else {
            convertView = messageInflater.inflate(R.layout.fragment_chat_my_chat_bubble, null);
            TextView chatContent = (TextView) convertView.findViewById(R.id.textview_fragment_chat_my_chat_bubble_message_output);
            chatContent.setText(msg.getSenderMessageContent());

            Button removeButton = (Button) convertView.findViewById(R.id.btn_fragment_chat_my_chat_bubble_remove);
            Button cancelButton = (Button) convertView.findViewById(R.id.btn_fragment_chat_my_chat_bubble_cancel);
            convertView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chatContent.setVisibility(View.INVISIBLE);
                            removeButton.setVisibility(View.VISIBLE);
                            removeButton.setTag(position);
                            removeButton.setOnClickListener(
                                    new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Integer index = (Integer) v.getTag();
                                            mChatList.remove(index.intValue());
                                            notifyDataSetChanged();
                                        }
                                    });
                            cancelButton.setVisibility(View.VISIBLE);
                            cancelButton.setOnClickListener(
                                    new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            removeButton.setVisibility(View.INVISIBLE);
                                            cancelButton.setVisibility(View.INVISIBLE);
                                            chatContent.setVisibility(View.VISIBLE);
                                        }
                                    }
                            );
                        }
                    }
            );


        }

        return convertView;
    }
}
