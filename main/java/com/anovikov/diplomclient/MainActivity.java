package com.anovikov.diplomclient;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.anovikov.diplomclient.task_schedule.ScheduleConfig;

public class MainActivity extends AppCompatActivity implements ConnectFragment.OnConnectFragment,
        MainMenuFragment.OnMainMenuListener, WaitFragment.OnWaitFragmentListener, ResultFragment.OnResultFragmentListener {

    enum Screen { CONNECT, MENU, WAIT, RESULTS}

    /**
     * TODO: delete PORT and address
     */
    public static final String address = "10.0.2.2";
    public static final int PORT = 2222;
    public static final int MSG_COORD = 0;
    public static final String MESSENGER = "messenger";
    public static final String PORT_ID = "port";
    public static final String ADDRESS_ID = "address";

    private EditText mEditJobs;
    private EditText mEditProcs;

    private ServerCommunication mCom;
    private Messenger messenger;
    private Screen mState;

    private String mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            try {
                setScreen(Screen.CONNECT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mEditJobs = null;
        mEditProcs = null;

        mLogin = null;
        messenger = new Messenger(new UpdateCoord());
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()) {
            stopService(new Intent(this, ServerCommunication.class));
        }
    }

    private void setScreen(Screen newScreen) throws Exception {
        mState = newScreen;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = null;

        switch(mState) {
            case CONNECT:
                fragment = new ConnectFragment();
                break;
            case MENU:
                fragment = new MainMenuFragment();
                break;
            case WAIT:
                fragment = new WaitFragment();
                break;
            case RESULTS:
                fragment = new ResultFragment();
                break;
            default:
                throw new Exception("Error on set new screen. New screen is not define.");
        }

        transaction.replace(R.id.start_layout, fragment);
        transaction.commit();
    }

    //ConnectFragment
    @Override
    public void connect() {
        Intent serviceIntent = new Intent(this, ServerCommunication.class);

        serviceIntent.putExtra(MESSENGER, messenger);
        serviceIntent.putExtra(PORT_ID, PORT);
        serviceIntent.putExtra(ADDRESS_ID, address);
        startService(serviceIntent);

        try {
            setScreen(Screen.MENU);
        } catch (Exception e) {
            System.out.println("connect() in MainActivity!");
            e.printStackTrace();
        }

        //mLogin = login;
    }
    //MainMenuFragment
    @Override
    public void bindViews() {
        mEditJobs = (EditText)findViewById(R.id.edit_count_j);
        mEditProcs = (EditText)findViewById(R.id.edit_count_pr);
    }

    @Override
    public void unbindViews() {
        mEditJobs = null;
        mEditProcs = null;
    }

    @Override
    public void solve() {
        ScheduleConfig conf = new ScheduleConfig();
        conf.mCountJobs = Integer.parseInt(mEditJobs.getText().toString());
        conf.mCountProcs = Integer.parseInt(mEditProcs.getText().toString());
        conf.mMethod = 2;
        //TODO: request to SERVER!!!
        try {
            setScreen(Screen.WAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //WaitFragment
    @Override
    public void cancel() {
        try {
            setScreen(Screen.MENU);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ResultScreen

    @Override
    public void newTask() {

    }

    @Override
    public void exit() {
        finish();
    }

    class UpdateCoord extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_COORD:
                    Bundle data = msg.getData();
                    //mCoordText.setText("x = " + data.getDouble(ServerCommunication.X) + "; y = " + data.getDouble(ServerCommunication.Y));
                    break;
                default:
                super.handleMessage(msg);
            }
        }
    }
}
