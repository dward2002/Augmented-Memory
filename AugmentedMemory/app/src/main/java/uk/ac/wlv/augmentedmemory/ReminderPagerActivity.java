package uk.ac.wlv.augmentedmemory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReminderPagerActivity extends AppCompatActivity{
    private static final String EXTRA_REMINDER_ID = "uk.ac.wlv.augmentedmemory.reminder_id";
    private static final String EXTRA_REMINDER_LIST = "uk.ac.wlv.augmentedmemory.reminder_list";
    private ViewPager mViewPager;
    private List<Reminder> mReminders;
    private Reminder mReminder;

    public static Intent newIntent(Context packageContext, String reminderId, List<Reminder> mReminders){
        Intent intent = new Intent(packageContext, ReminderPagerActivity.class);
        Bundle args = new Bundle();
        args.putString(EXTRA_REMINDER_ID,reminderId);
        args.putSerializable(EXTRA_REMINDER_LIST, (Serializable) mReminders);
        intent.putExtra(EXTRA_REMINDER_ID,args);
        //intent.putExtra(EXTRA_REMINDER_ID, reminderId);
        //intent.putExtra(EXTRA_REMINDER_LIST, (Serializable) mReminders);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_pager);
        //String ReminderId = (String) getIntent().getSerializableExtra((EXTRA_REMINDER_ID));
        Bundle args = getIntent().getBundleExtra(EXTRA_REMINDER_ID);
        String ReminderId = args.getString(EXTRA_REMINDER_ID);
        mReminders = (ArrayList<Reminder>) args.getSerializable(EXTRA_REMINDER_LIST);
        mViewPager = (ViewPager) findViewById(R.id.activity_reminder_pager_view_pager);

        //mReminders = MessageLab.get(this).getMessages();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Reminder reminder = mReminders.get(position);
                return ReminderFragment.newInstance(reminder.getId());
            }

            @Override
            public int getCount() {
                return mReminders.size();
            }
        });
        for(int i = 0; i < mReminders.size(); i++){
            if(mReminders.get(i).getId().equals(ReminderId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
