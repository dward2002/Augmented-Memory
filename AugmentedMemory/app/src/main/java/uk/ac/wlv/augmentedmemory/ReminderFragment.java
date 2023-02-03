package uk.ac.wlv.augmentedmemory;

import static uk.ac.wlv.augmentedmemory.MainActivity.MESSAGES_CHILD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReminderFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        String reminderId = (String) getActivity().getIntent().getSerializableExtra(ReminderActivity.EXTRA_REMINDER_ID);
        DatabaseReference mFirebaseDeleteReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(reminderId);
        mFirebaseDeleteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    Reminder post = data.getValue(Reminder.class);
                    Log.d("www",post.getmTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);
        return v;
    }
}
