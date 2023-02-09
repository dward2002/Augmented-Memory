package uk.ac.wlv.augmentedmemory;

import static uk.ac.wlv.augmentedmemory.MainActivity.MESSAGES_CHILD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ReminderFragment extends Fragment {
    private static final String ARG_REMINDER_ID= "reminder_id";
    private Reminder mReminder;
    private EditText mTitleField;
    Button mDateButton;
    private String reminderId;
    private DatabaseReference mFirebaseReference;

    public static ReminderFragment newInstance(String reminderId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_REMINDER_ID, reminderId);
        ReminderFragment fragment = new ReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        reminderId = (String) getArguments().getSerializable(ARG_REMINDER_ID);

        mFirebaseReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(reminderId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);
        mTitleField = (EditText) v.findViewById(R.id.reminder_title);
        mDateButton = (Button) v.findViewById(R.id.reminder_date);
        //Log.d("www","hiiii");
        mFirebaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.d("www","error");
                }
                else{
                    mReminder = task.getResult().getValue(Reminder.class);
                    mTitleField.setText(mReminder.getmTitle());
                    SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy");
                    Date date = new Date();
                    try {
                        date = fm.parse("26, MAR 2002");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String myString = fm.format(date);
                    mDateButton.setText(myString);
                }
            }
        });
        return v;
    }
}
