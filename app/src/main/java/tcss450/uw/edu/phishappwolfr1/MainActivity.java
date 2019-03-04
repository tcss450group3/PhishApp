package tcss450.uw.edu.phishappwolfr1;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import me.pushy.sdk.Pushy;
import tcss450.uw.edu.phishappwolfr1.Model.Credentials;

public class MainActivity extends AppCompatActivity

        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener {

    private boolean mLoadFromChatNotification = false;
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pushy.listen(this);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("type")) {
                mLoadFromChatNotification = getIntent().getExtras().getString("type").equals("msg");
            }
        }

        if(savedInstanceState == null) {
            if (findViewById(R.id.frameMain) != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameMain, new LoginFragment());
                transaction.commit();
            }
        }
    }


    @Override
    public void onLoginSuccess(Credentials theC, String jwt) {
        Log.i(TAG, "onLoginSuccess: JWT = "+jwt);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(getString(R.string.keys_intent_credentials), theC);
        intent.putExtra(getString(R.string.keys_intent_jwt), jwt);
        intent.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterClicked() {
        RegisterFragment register = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameMain,register).addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRegisterSuccess(Credentials c) {
        LoginFragment login = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameMain, login);
        Bundle args = new Bundle();
        args.putSerializable("credentials", c);
        login.setArguments(args);
        transaction.commit();
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameMain, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }
}
