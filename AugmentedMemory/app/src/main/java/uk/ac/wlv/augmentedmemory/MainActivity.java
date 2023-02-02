package uk.ac.wlv.augmentedmemory;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
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
    private Button mViewButton;
    private Button mSaveButton;
    private SpeechRecognizer speechRecognizer;
    boolean clicked = true;
    private String mResults;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private ArrayList<ChatMessage> pracList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        mButton = (ImageView) findViewById(R.id.button);
        mViewButton = (Button) findViewById(R.id.view);
        mSaveButton = (Button) findViewById(R.id.save);

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

        /*mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/

        /*mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send messages on click
                Log.d("www","y0");
                Log.d("www", String.valueOf(pracList.size()));
                for(ChatMessage chat : pracList){
                    Log.d("www",chat.getText());
                }
                ChatMessage chatMessage = new ChatMessage(mMessageEditText.getText().toString(),
                        mUserName);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage);
                mMessageEditText.setText("");
            }
        });*/

        /*mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pracList.clear();
                //select image for image on click
                DatabaseReference mFirebaseDeleteReference = FirebaseDatabase.getInstance().getReference()
                        .child(MESSAGES_CHILD);
                mFirebaseDeleteReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot data : snapshot.getChildren()) {
                            ChatMessage post = data.getValue(ChatMessage.class);
                            Log.d("www","inside = "+post.getmTitle());
                            pracList.add(post);
                            //Log.d("www", String.valueOf(pracList.size()));
                            //String post = data.getValue(String.class);
                            //String post = data.getKey();
                            //Log.d("www", post);
                            //Log.d("www", post.getName());
                            //Log.d("www", post.getText());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(MainActivity.this, ReminderListActivity.class);
                startActivity(intent);
            }
        });*/

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessage chatMessage = new ChatMessage(mResults,
                        mUserName);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage);
            }
        });

        mViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReminderListActivity.class);
                startActivity(intent);
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
}