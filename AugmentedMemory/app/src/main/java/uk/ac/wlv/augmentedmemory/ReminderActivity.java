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
    public static final String EXTRA_MESSAGE_ID = "uk.ac.wlv.messagesapp.crime_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            UUID messageId = (UUID) getIntent().getSerializableExtra(EXTRA_MESSAGE_ID);
            fragment = ReminderFragment.newInstance(messageId);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
