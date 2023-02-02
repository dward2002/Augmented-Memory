package uk.ac.wlv.augmentedmemory;

import static uk.ac.wlv.augmentedmemory.MainActivity.MESSAGES_CHILD;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReminderListActivity extends AppCompatActivity {
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        mProgressBar = (ProgressBar) findViewById(R.id.progreesBar1);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView1);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        loadFirebaseMessages();
        DatabaseReference mFirebaseDeleteReference = FirebaseDatabase.getInstance().getReference()
                .child(MESSAGES_CHILD);
        mFirebaseDeleteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    ChatMessage post = data.getValue(ChatMessage.class);
                    //Log.d("www","inside = "+post.getmTitle());
                    //Log.d("www", String.valueOf(pracList.size()));
                    //String post = data.getValue(String.class);
                    //String post = data.getKey();
                    //Log.d("www", post);
                    //Log.d("www", post.getName());
                    //Log.d("www", post.getText());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onPause(){
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mFirebaseAdapter.notifyDataSetChanged();
        mFirebaseAdapter.startListening();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView messageTextView;
        TextView messengerTextView;
        CheckBox readCheckbox;
        //Button messengerDeleteButton;

        public MessageViewHolder(View v){
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.list_item_message_title_text_view);
            messengerTextView = (TextView) itemView.findViewById(R.id.list_item_message_date_text_view);
            readCheckbox = (CheckBox) itemView.findViewById(R.id.list_item_message_read_check_box);
        }
    }

    private void loadFirebaseMessages() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @Override
            public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
                ChatMessage ChatMessage = dataSnapshot.getValue(ChatMessage.class);
                if (ChatMessage != null)
                    ChatMessage.setId(dataSnapshot.getKey());
                return ChatMessage;
            }
        };
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>().setQuery(messagesRef, parser).build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder, int position, ChatMessage ChatMessage) {
                /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("www",ChatMessage.getText());
                    }
                });*/

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (ChatMessage.getmTitle() != null) {
                    viewHolder.messageTextView.setText(ChatMessage.getmTitle());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                }
                viewHolder.readCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ChatMessage.setRead(isChecked);
                        //Log.d("www",ChatMessage.getmTitle());
                        FirebaseDatabase.getInstance().getReference()
                                .child(MESSAGES_CHILD).child(ChatMessage.getId()).child("read")
                                .setValue(ChatMessage.isRead());
                    }
                });
                viewHolder.messengerTextView.setText(ChatMessage.getName());
                viewHolder.readCheckbox.setChecked(ChatMessage.isRead());
            }
        };
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int ChatMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (ChatMessageCount - 1)
                        && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
