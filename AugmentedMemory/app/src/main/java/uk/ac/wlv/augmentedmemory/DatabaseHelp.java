package uk.ac.wlv.augmentedmemory;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DatabaseHelp {
    private DatabaseReference mFirebaseReference;
    private List<Reminder> mReminders;

    public DatabaseHelp(){
        mFirebaseReference = FirebaseDatabase.getInstance().getReference()
                .child("messages");
    }

    public void getmReminders(){
        mFirebaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.d("www","error");
                }
                else{

                    for(DataSnapshot data : task.getResult().getChildren()) {
                        Reminder mReminder = task.getResult().getValue(Reminder.class);
                        Log.d("www","inside = "+mReminder.getmTitle());
                    }
                }
            }
        });
    }
}
