package uk.ac.wlv.augmentedmemory;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    public static final String USERS_CHILD = "users";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUserName;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    private TextView mTextView;
    private ImageView mButton;
    private Button mSaveButton;
    private Button mNotifyButton;
    private Button mMonitorButton;
    private SpeechRecognizer speechRecognizer;
    boolean clicked = true;
    private String mResults;
    private String emailId;
    private BottomNavigationView mBottomNav;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private ArrayList<Reminder> pracList = new ArrayList<>();
    private List<Reminder> mReminders = new ArrayList<>();

    private static final String ARG_REMINDER= "reminder";
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    private Reminder mReminder1;
    private String emailFound;
    private String account;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        mButton = (ImageView) findViewById(R.id.button);
        mSaveButton = (Button) findViewById(R.id.save);
        mMonitorButton = (Button) findViewById(R.id.monitorButton);
        mBottomNav = findViewById(R.id.bottom_nav);
        mBottomNav.setOnNavigationItemSelectedListener(this);
        mBottomNav.setSelectedItemId(R.id.menu);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Set default username is anonymous.
        mUserName = ANONYMOUS;
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser == null){
            //Not signed in launch the Sign In activity
            startActivity(new Intent(this,SignInActivity.class));
            finish();
            return;
        }else{
            mUserName = mFirebaseUser.getDisplayName();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        email = mFirebaseUser.getEmail();
        int dotIndex = email.indexOf(".");
        emailId = email.substring(0,dotIndex);
        String monitoredAccount = null;
        if (email.equals("doogieboy111@gmail.com")){
            account = "user";
        }
        else{
            account = "monitor";
            monitoredAccount = "doogieboy111@gmail.com";
        }
        final String monitoredAccount1 = monitoredAccount;

        Log.d("www",email);

        emailFound = "false";
        DatabaseReference mFirebaseUserReference = FirebaseDatabase.getInstance().getReference()
                .child(USERS_CHILD);

        mFirebaseUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    //user.setId(data.getKey());
                    if(user.getEmail().equals(email)){
                        //User user1 = new User(email,account);
                        user.setEmail(email);
                        user.setAccount(account);
                        user.setMonitoredAccount(monitoredAccount1);
                        mFirebaseUserReference.child(emailId).setValue(user);
                        emailFound = "true";
                    }
                }
                if(emailFound.equals("false")){
                    User user1 = new User(email,account);
                    user1.setMonitoredAccount(monitoredAccount1);
                    mFirebaseUserReference.child(emailId).setValue(user1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference mFirebaseDeleteReference = FirebaseDatabase.getInstance().getReference()
                .child(MESSAGES_CHILD).child(emailId);
        mFirebaseDeleteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mReminders.clear();
                for(DataSnapshot data : snapshot.getChildren()) {
                    Reminder post = data.getValue(Reminder.class);
                    post.setId(data.getKey());
                    mReminders.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        createNotificationChannel();


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mResults == null){
                    Toast.makeText(MainActivity.this, "Click the red button to create a reminder", Toast.LENGTH_LONG).show();
                }
                else{
                    NewReminderProcessor process;
                    process = new NewReminderProcessor(mResults);
                    //String prac = "walk the dog at 16:02 at location gornal tyres";
                    //String prac1 = "walk the dog at location gornal tyres at 16:02";
                    //String prac2 = "at location gornal tyres at 16:02 walk the dog ";
                    //String prac3 = "go to gornal tyres at 16:02 to walk the dog ";
                    //process = new NewReminderProcessor(prac2);
                    String location = process.getLocationProcess();
                    String dateTime = process.dateTimeprocess();
                    int requestCode = requestCheck();
                    Date date = new Date();
                    SimpleDateFormat fm = new SimpleDateFormat("dd, MMM yyyy, HH mm");
                    try {
                        date = fm.parse(dateTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mReminder1 = new Reminder(mResults, dateTime,requestCode, location, email);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(emailId).push().setValue(mReminder1);
                    showTimePicker(date);
                    setAlarm();
                }
            }
        });


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)!=
                PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clicked){
                    clicked = true;
                    mButton.setImageResource(R.drawable.ic_mics);
                    speechRecognizer.cancel();
                    speechRecognizer.stopListening();
                }
                else{
                    clicked = false;
                    mButton.setImageResource(R.drawable.ic_mic_offs);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "start speaking");
                    speechRecognizer.setRecognitionListener(new RecognitionListener() {
                        @Override
                        public void onReadyForSpeech(Bundle bundle) {

                        }

                        @Override
                        public void onBeginningOfSpeech() {
                    /*ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.alertcustom,
                            viewGroup, false);

                    alertSpeechDialog = new AlertDialog.Builder(MainActivity.this);
                    alertSpeechDialog.setMessage("Listening....");
                    alertSpeechDialog.setView(dialogView);
                    alertDialog = alertSpeechDialog.create();
                    alertDialog.show();*/
                            mTextView.setText("Listening....");
                        }

                        @Override
                        public void onRmsChanged(float v) {

                        }

                        @Override
                        public void onBufferReceived(byte[] bytes) {

                        }

                        @Override
                        public void onEndOfSpeech() {
                            mButton.setImageResource(R.drawable.ic_mics);
                            mTextView.setText("Tap to Speak");
                        }

                        @Override
                        public void onError(int i) {

                        }

                        @Override
                        public void onResults(Bundle bundle) {
                            mButton.setImageResource(R.drawable.ic_mics);
                            ArrayList<String> arrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            mResults = arrayList.get(0);
                            mTextView.setText(arrayList.get(0));
                            //alertDialog.dismiss();
                        }

                        @Override
                        public void onPartialResults(Bundle bundle) {

                        }

                        @Override
                        public void onEvent(int i, Bundle bundle) {

                        }
                    });
                    speechRecognizer.startListening(intent);
                    //startActivityForResult(intent, 100);
                }
            }
        });

        if(account.equals("monitor")){
            mTextView.setVisibility(View.GONE);
            mButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.GONE);
            mBottomNav.setVisibility(View.GONE);


            mMonitorButton.setVisibility(View.VISIBLE);

            mMonitorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, MonitorActivity.class);
                    intent.putExtra("username", "doogieboy111@gmail.com");
                    startActivity(intent);
                }
            });
        }

    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private int requestCheck(){
        int highest = 0;
        for(Reminder reminder : mReminders){
            if(reminder.getRequestCode() > highest){
                highest = reminder.getRequestCode();
            }
        }
        return highest + 1;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "AugmemoryReminderChannel";
            String description = "Channel For Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Augmemory",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = MainActivity.this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showTimePicker(Date date){
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        //calendar.set(Calendar.HOUR_OF_DAY,06);
        //calendar.set(Calendar.MINUTE,18);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy");
    }

    private void setAlarm() {

        alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        Bundle bundle1 = new Bundle();
        String title = mReminder1.getmTitle();
        int requestCode = mReminder1.getRequestCode();
        Log.d("www",title);
        bundle1.putString(ARG_REMINDER,title);
        bundle1.putInt("reminder1",requestCode);
        intent.putExtras(bundle1);


        pendingIntent = PendingIntent.getBroadcast(MainActivity.this,requestCode,intent, FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);

        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
        //       AlarmManager.INTERVAL_DAY,pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        //Calendar cal = calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
        //Log.d("www",dateFormat.format(calendar.getTimeInMillis()));



        Toast.makeText(MainActivity.this,"Alarm set Successfully",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mBottomNav.setSelectedItemId(R.id.menu);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUserName = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        Log.d(TAG,"onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu:
                break;
            case R.id.view:
                Intent intent = new Intent(MainActivity.this, ReminderListActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.map:
                Intent intent1 = MapsActivity.newIntent(MainActivity.this, mReminders);
                startActivity(intent1);
                break;
        }
        return true;
    }
}