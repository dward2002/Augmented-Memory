package uk.ac.wlv.augmentedmemory;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.UUID;

public class ReminderActivity extends FragmentActivity {
    public static final String EXTRA_REMINDER_ID = "uk.ac.wlv.augmentedmemory.reminder_id";

    public static Intent newIntent(Context packageContext, String reminderId){
        Intent intent = new Intent(packageContext, ReminderActivity.class);
        intent.putExtra(EXTRA_REMINDER_ID, reminderId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            UUID messageId = (UUID) getIntent().getSerializableExtra(EXTRA_REMINDER_ID);
            fragment = ReminderFragment.newInstance(messageId);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
