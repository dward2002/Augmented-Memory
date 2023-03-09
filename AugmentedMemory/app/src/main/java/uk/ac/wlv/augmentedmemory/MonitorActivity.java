package uk.ac.wlv.augmentedmemory;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MonitorActivity extends AppCompatActivity {

    public static final String MESSAGES_CHILD = "messages";

    private String mUsername;

    private TextView mTitleTextView;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Reminder, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mReminderRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        mUsername = getIntent().getStringExtra("username");
        mTitleTextView = (TextView) findViewById(R.id.Title);
        mTitleTextView.setText(mUsername);

        int dotIndex = mUsername.indexOf(".");
        emailId = mUsername.substring(0,dotIndex);
        Log.d("www", emailId);

        mProgressBar = (ProgressBar) findViewById(R.id.progreesBar1);
        mReminderRecyclerView = (RecyclerView) findViewById(R.id.reminderRecyclerView1);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mReminderRecyclerView.setLayoutManager(mLinearLayoutManager);
        loadFirebaseMessages();



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
        TextView reminderTextView;
        //Button messengerDeleteButton;

        public MessageViewHolder(View v){
            super(v);
            reminderTextView = (TextView) itemView.findViewById(R.id.list_item_reminder_title_text_view);
        }
    }


    private void loadFirebaseMessages() {
        Log.d("www", "loaded");
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<Reminder> parser = new SnapshotParser<Reminder>() {
            @Override
            public Reminder parseSnapshot(DataSnapshot dataSnapshot) {
                Reminder Reminder = dataSnapshot.getValue(Reminder.class);
                if (Reminder != null) {
                    //mReminders.add(Reminder);
                    Reminder.setId(dataSnapshot.getKey());
                }
                return Reminder;
            }
        };
        DatabaseReference remindersRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(emailId);
        FirebaseRecyclerOptions<Reminder> options =
                new FirebaseRecyclerOptions.Builder<Reminder>().setQuery(remindersRef, parser).build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Reminder, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.monitor_reminder, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder, int position, Reminder Reminder) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (Reminder.getmTitle() != null) {
                    viewHolder.reminderTextView.setText(Reminder.getmTitle());
                    viewHolder.reminderTextView.setVisibility(TextView.VISIBLE);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int ReminderCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (ReminderCount - 1)
                        && lastVisiblePosition == (positionStart - 1))) {
                    mReminderRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mReminderRecyclerView.setAdapter(mFirebaseAdapter);
    }


}
