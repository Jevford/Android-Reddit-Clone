package com.example.lab5;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private EditText editMessage;
    private Button postButton;
    private ListView lvPosts;
    private ArrayList<Message> posts;
    private ArrayList<String> str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Database Variables
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Messages");

        //Input
        editMessage = (EditText) findViewById(R.id.inputMsg);

        //Post Button
        postButton = findViewById(R.id.postMsgButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMessage(v);
            }
        });

        //Posts List
        posts = new ArrayList<Message>();

        //Posts' Message List synced with Posts List
        str = new ArrayList<>();

        //List View Adapter for Posts List
        lvPosts = (ListView)findViewById(R.id.listViewPosts);

    }

    @Override
    protected void onStart() {
        super.onStart();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Add array clear
                if(posts.size() > 0){
                    posts.clear();
                    str.clear();
                }
                for(DataSnapshot messageSnapshot : dataSnapshot.getChildren()){
                    Message message = messageSnapshot.getValue(Message.class);
                    posts.add(message);
                    sortReplies();
                    //System.out.println(message.getPost() + " - " + message.getVotes());
                }

                for(Message message : posts){
                    str.add(message.getVotes() + " - " + message.getPost());
                }

                //MessageList adapter = new MessageList(getApplicationContext(), R.layout.list_layout, posts);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, str);

                lvPosts.setAdapter(adapter);

                lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String postSelection = "Selected: " + parent.getItemAtPosition(position);

                        Toast.makeText(MainActivity.this, postSelection, Toast.LENGTH_SHORT).show();
                    }
                });

                lvPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        Message post = posts.get(position);
                        showPostFunctionsDialog(post.getPostID(), post.getPost(), post.getVotes());
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Database Error Handling
            }
        });
    }

    public void postMessage(View view){
        String input = editMessage.getText().toString();

        if(input.length() > 0){
            String id = myRef.push().getKey();
            Message message = new Message(id, input, 0);
            myRef.child(id).setValue(message);
            Toast.makeText(this, "Message: '" + message.getPost() + "' has been posted." ,
                    Toast.LENGTH_LONG).show();
        }
        editMessage.setText("");

    }

    private void showPostFunctionsDialog(final String postId, final String post, final int votes){
        AlertDialog.Builder dialogueBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogueView = inflater.inflate(R.layout.post_functions, null);

        dialogueBuilder.setView(dialogueView);

        final Button buttonUpvote = (Button) dialogueView.findViewById(R.id.buttonUpvote);
        final Button buttonDownvote = (Button) dialogueView.findViewById(R.id.buttonDownvote);
        final Button buttonReply = (Button) dialogueView.findViewById(R.id.buttonReply);
        final Button buttonDelete = (Button) dialogueView.findViewById(R.id.buttonDelete);

        dialogueBuilder.setTitle("Upvote, Downvote, Reply, or Delete the selected Post.");
        final AlertDialog alertDialog = dialogueBuilder.create();
        alertDialog.show();

        buttonUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upVotePost(postId, post, votes);
                alertDialog.dismiss();
            }
        });

        buttonDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downVotePost(postId, post, votes);
                alertDialog.dismiss();
            }
        });

        buttonReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyToPost(postId, post, votes);
                alertDialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(postId);
                alertDialog.dismiss();
            }
        });

    }

    private void upVotePost(String id, String msg, int votes){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages").child(id);

        Message post = new Message(id, msg, votes);
        post.upVote();
        databaseReference.setValue(post);
        Toast.makeText(this, "Post has been UPVOTED.", Toast.LENGTH_LONG).show();
    }

    private void downVotePost(String id, String msg, int votes){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages").child(id);

        Message post = new Message(id, msg, votes);
        post.downVote();
        databaseReference.setValue(post);
        Toast.makeText(this, "Post has been DOWNVOTED.", Toast.LENGTH_LONG).show();
    }

    private void replyToPost(String id, String msg, int votes){
        //Last Feature  to Implement
    }

    private void deletePost(String postID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages").child(postID);

        databaseReference.removeValue();
        Toast.makeText(this, "Post has been DELETED.", Toast.LENGTH_LONG).show();
    }

    public void sortReplies(){
        Comparator c = Collections.reverseOrder(new SortByVotes());
        Collections.sort(posts, c);
    }

};



class SortByVotes implements Comparator<Message> {
    @Override
    public int compare(Message a, Message b){
        return a.getVotes() -  b.getVotes();
    }
}
