package uk.ac.wlv.augmentedmemory;

import static uk.ac.wlv.augmentedmemory.MainActivity.MESSAGES_CHILD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private static final String ARG_REMINDER= "reminder";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private Reminder mReminder;
    private Reminder mReminder1;
    private EditText mTitleField;
    Button mDateButton;
    CheckBox mReadCheckBox;
    Button mDeleteButton;
    private String reminderId;
    private DatabaseReference mFirebaseReference;

    public static ReminderFragment newInstance(String reminderId, Reminder rem){
        Bundle args = new Bundle();
        args.putSerializable(ARG_REMINDER_ID, reminderId);
        args.putSerializable(ARG_REMINDER,rem);
        ReminderFragment fragment = new ReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        reminderId = (String) getArguments().getSerializable(ARG_REMINDER_ID);
        mReminder1 = (Reminder) getArguments().getSerializable(ARG_REMINDER);

        mFirebaseReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(reminderId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);
        mTitleField = (EditText) v.findViewById(R.id.reminder_title);
        mTitleField.setText(mReminder1.getmTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mReminder1.setmTitle(s.toString());
                mFirebaseReference.child("mTitle")
                        .setValue(mReminder1.getmTitle());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*mDateButton = (Button) v.findViewById(R.id.reminder_date);

        SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy");
        Date date = new Date();
        try {
            date = fm.parse("28, FEB 2003");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(mReminder1.getDate() != null) {
            mDateButton.setText(mReminder1.getDate());
        }
        else {
            String myString = fm.format(date);
            mDateButton.setText(myString);
        }*/
        mDateButton = (Button) v.findViewById(R.id.reminder_date);
        Date date = new Date();
        if(mReminder1.getDate() != null) {
            mDateButton.setText(mReminder1.getDate());
            SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy");
            try {
                date = fm.parse(mReminder1.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Date finalDate = date;
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(finalDate);
                dialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mReadCheckBox = (CheckBox) v.findViewById(R.id.reminder_read);
        mReadCheckBox.setChecked(mReminder1.isRead());
        mReadCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mReminder1.setRead(isChecked);
                //Log.d("www",Reminder.getmTitle());
                mFirebaseReference.child("read")
                        .setValue(mReminder1.isRead());
            }
        });

        mDeleteButton = (Button) v.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseReference
                        .removeValue();
                getActivity().finish();
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy");
            String myString = fm.format(date);
            mReminder1.setDate(myString);
            mDateButton.setText(myString);
            mFirebaseReference.child("date")
                    .setValue(mReminder1.getDate());
        }
    }

}
