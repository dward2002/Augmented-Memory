package uk.ac.wlv.augmentedmemory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class ReminderPagerActivity extends AppCompatActivity{
    private static final String EXTRA_MESSAGE_ID = "uk.ac.wlv.messagesapp.message_id";
    private ViewPager mViewPager;
    private List<Reminder> mReminders;

    public static Intent newIntent(Context packageContext, String crimeId){
        Intent intent = new Intent(packageContext, ReminderPagerActivity.class);
        intent.putExtra(EXTRA_MESSAGE_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_pager);
        String ReminderId = (String) getIntent().getSerializableExtra((EXTRA_MESSAGE_ID));
        mViewPager = (ViewPager) findViewById(R.id.activity_message_pager_view_pager);
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
