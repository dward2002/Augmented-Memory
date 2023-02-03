package uk.ac.wlv.augmentedmemory;

import static uk.ac.wlv.augmentedmemory.MainActivity.MESSAGES_CHILD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReminderFragment extends Fragment {
    private Reminder mReminder = new Reminder();
    private EditText mTitleField;
    private String reminderId;
    private DatabaseReference mFirebaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        reminderId = (String) getActivity().getIntent().getSerializableExtra(ReminderActivity.EXTRA_REMINDER_ID);

        mFirebaseReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(reminderId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);
        mTitleField = (EditText) v.findViewById(R.id.reminder_title);
        Log.d("www","hiiii");
        mFirebaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.d("www","error");
                }
                else{
                    mReminder = task.getResult().getValue(Reminder.class);
                    Log.d("www", mReminder.getmTitle());
                    mTitleField.setText(mReminder.getmTitle());
                }
            }
        });
        Log.d("www","lol");

        return v;
    }
}
