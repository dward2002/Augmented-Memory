package uk.ac.wlv.augmentedmemory;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static uk.ac.wlv.augmentedmemory.MainActivity.MESSAGES_CHILD;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
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
    Button mTimeButton;
    CheckBox mReadCheckBox;
    Button mDeleteButton;
    private String reminderId;
    private DatabaseReference mFirebaseReference;
    private Date mDate;

    private Button mSelectTimeBtn;
    private Button mSetAlarmBtn;
    private Button mCancelAlarmBtn;
    private TextView mSelectedTime;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;

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

        createNotificationChannel();

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

        /*SimpleDateFormat fm1 = new SimpleDateFormat("dd, MMM yyyy HH:mm");
        Date doodle = new Date();
        try {
            doodle = fm1.parse("23, MAR 2010 16:32");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("www", String.valueOf(doodle));*/
        mDateButton = (Button) v.findViewById(R.id.reminder_date);
        mDate = new Date();
        if(mReminder1.getDate() != null) {
            mDateButton.setText(mReminder1.getDate());
            SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy, HH mm");
            try {
                mDate = fm.parse(mReminder1.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Date finalDate = mDate;
        Log.d("www","this "+ String.valueOf(mDate));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mDate);
                dialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.reminder_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mDate);
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

        mSelectTimeBtn = (Button) v.findViewById(R.id.selectTimeBtn);
        mSelectTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("www","mSelectTimeBtn");
                showTimePicker();
            }
        });

        mSetAlarmBtn = (Button) v.findViewById(R.id.setAlarmBtn);
        mSetAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("www","mSetAlarmBtn");
                setAlarm();
            }
        });

        mCancelAlarmBtn = (Button) v.findViewById(R.id.cancelAlarmBtn);
        mCancelAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("www","mCancelAlarmBtn");
                cancelAlarm();
            }
        });
        mSelectedTime = (TextView) v.findViewById(R.id.selectedTime);

        return v;
    }

    private void showTimePicker(){
        calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        //calendar.set(Calendar.HOUR_OF_DAY,06);
        //calendar.set(Calendar.MINUTE,18);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy");
        Log.d("www",dateFormat.format(calendar.getTimeInMillis()));
        mSelectedTime.setText(dateFormat.format(calendar.getTimeInMillis()));
    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        Bundle bundle1 = new Bundle();
        String title = mReminder1.getmTitle();
        int requestCode = mReminder1.getRequestCode();
        Log.d("www",title);
        bundle1.putString(ARG_REMINDER,title);
        bundle1.putInt("reminder1",requestCode);
        intent.putExtras(bundle1);


        pendingIntent = PendingIntent.getBroadcast(getActivity(),requestCode,intent, FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);

        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
        //       AlarmManager.INTERVAL_DAY,pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        //Calendar cal = calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
        Log.d("www",dateFormat.format(calendar.getTimeInMillis()));



        Toast.makeText(getActivity(),"Alarm set Successfully",Toast.LENGTH_LONG).show();

    }

    private void cancelAlarm() {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        int requestCode = mReminder1.getRequestCode();
        pendingIntent = PendingIntent.getBroadcast(getActivity(),requestCode,intent,FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);

        if(alarmManager == null){
            alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);

        Toast.makeText(getActivity(), "Alarm Cancelled", Toast.LENGTH_LONG).show();

    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy, HH mm");
            SimpleDateFormat fm1 = new SimpleDateFormat("HH, mm");
            String myString = fm.format(date);
            String myString1 = fm1.format(date);
            mDate = date;
            Log.d("www","return "+mDate);
            mReminder1.setDate(myString);
            mDateButton.setText(myString);
            mTimeButton.setText(myString1);
            mFirebaseReference.child("date")
                    .setValue(mReminder1.getDate());
        }
    }

}
