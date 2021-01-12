package com.example.lab5;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageList extends ArrayAdapter<Message> {
    Context context;
    int resource;
    ArrayList<Message> posts;

    public MessageList(Context context, int resource, ArrayList<Message> posts) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Message post = posts.get(position);
       if(convertView == null){
           convertView = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);
       }

        TextView textViewVotes = (TextView) convertView.findViewById(R.id.voteCount);
        TextView textViewMessages = (TextView) convertView.findViewById(R.id.postedMessage);

        textViewVotes.setText(post.getVotes());
        textViewMessages.setText(post.getPost());

        return convertView;
    }
    /*
    private Activity context;
    private List<Message> postsList;

    public MessageList(Activity context, List<Message> postsList) {
        super(context, R.layout.list_layout, postsList);
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewVotes = (TextView) listViewItem.findViewById(R.id.voteCount);
        TextView textViewMessages = (TextView) listViewItem.findViewById(R.id.postedMessage);

        Message message = postsList.get(position);

        textViewVotes.setText(message.getVotes());
        textViewMessages.setText(message.getPost());

        return listViewItem;
    }
     */
}
