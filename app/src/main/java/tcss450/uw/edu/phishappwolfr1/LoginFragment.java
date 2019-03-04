package tcss450.uw.edu.phishappwolfr1;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import me.pushy.sdk.Pushy;
import tcss450.uw.edu.phishappwolfr1.Model.Credentials;
import tcss450.uw.edu.phishappwolfr1.utils.SendPostAsyncTask;


/**
 */
public class LoginFragment extends Fragment {

    private EditText mEmailEntryEditText;
    private EditText mPassEntryEditText;
    private OnFragmentInteractionListener mListener;
    private Credentials mCredentials;
    private String mJwt;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v =  inflater.inflate(R.layout.fragment_login_fragment, container, false);
       mEmailEntryEditText = (EditText) v.findViewById(R.id.edit_email_login_screen);
       mPassEntryEditText = (EditText) v.findViewById(R.id.password_login_screen);

       Button b = (Button) v.findViewById(R.id.register_button_loginsc);
       b.setOnClickListener(this::setRegister);

       b = (Button) v.findViewById(R.id.login_butt_loginsc);
       b.setOnClickListener(this::setLogin);
       if (getArguments() != null && getArguments().containsKey("credentials")){
           Credentials c = (Credentials) getArguments().get("credentials");
           mEmailEntryEditText.setText(c.getEmail());
           mPassEntryEditText.setText(c.getPassword());
       }
       return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //retrieve the stored credentials from SharedPrefs
        if (prefs.contains(getString(R.string.keys_prefs_email)) &&
                prefs.contains(getString(R.string.keys_prefs_password))) {

            final String email = prefs.getString(getString(R.string.keys_prefs_email), "");
            final String password = prefs.getString(getString(R.string.keys_prefs_password), "");
            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById(R.id.edit_email_login_screen);
            emailEdit.setText(email);
            EditText passwordEdit = getActivity().findViewById(R.id.password_login_screen);
            passwordEdit.setText(password);
            doLogin(new Credentials.Builder(
                    emailEdit.getText().toString(),
                    passwordEdit.getText().toString())
                    .build());
        }
    }


    private void setLogin(View view) {
        boolean hasError = false;
        if (mEmailEntryEditText.getText().length() == 0) {
            hasError = true;
            mEmailEntryEditText.setError("Field must not be empty.");
        }  else if (mEmailEntryEditText.getText().toString().chars().filter(ch -> ch == '@').count() != 1) {
            hasError = true;
            mEmailEntryEditText.setError("Field must contain a valid email address.");
        }
        if (mPassEntryEditText.getText().length() == 0) {
            hasError = true;
            mPassEntryEditText.setError("Field must not be empty.");
        }

        if (!hasError) {
            doLogin(new Credentials.Builder(
                    mEmailEntryEditText.getText().toString(),
                    mPassEntryEditText.getText().toString())
                    .build());

        }

    }

    private void setRegister(View view){
        mListener.onRegisterClicked();
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNC_TASK_ERROR",  result);
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));

            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                mJwt = resultsJSON.getString(getString(R.string.keys_json_login_jwt));
                new RegisterForPushNotificationsAsync().execute();
                return;
            } else {
                //Login was unsuccessful. Don’t switch fragments and
                // inform the user
                ((TextView) getView().findViewById(R.id.edit_email_login_screen))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.edit_email_login_screen))
                    .setError("Login Unsuccessful");
        }
    }

    private void saveCredentials(final Credentials credentials) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //Store the credentials in SharedPrefs
        prefs.edit().putString(getString(R.string.keys_prefs_email), credentials.getEmail()).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_password), credentials.getPassword()).apply();
    }

    private void doLogin(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();

        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();

        mCredentials = credentials;

        Log.d("JSON Credentials", msg.toString());

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleLoginOnPre)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }


    private void handlePushyTokenOnPost(String result) {
        try {

            Log.d("JSON result",result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                saveCredentials(mCredentials);
                mListener.onLoginSuccess(mCredentials, mJwt);
                return;
            } else {
                //Saving the token wrong. Don’t switch fragments and inform the user
                ((TextView) getView().findViewById(R.id.edit_email_login_screen))
                        .setError("Login Unsuccessful");
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.edit_email_login_screen))
                    .setError("Login Unsuccessful");
        }
    }



    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, String, String> {

        protected String doInBackground(Void... params) {
            String deviceToken = "";

            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getActivity().getApplicationContext());

                //subscribe to a topic (this is a Blocking call)
                Pushy.subscribe("all", getActivity().getApplicationContext());
            }
            catch (Exception exc) {

                cancel(true);
                // Return exc to onCancelled
                return exc.getMessage();
            }

            // Success
            return deviceToken;
        }

        @Override
        protected void onCancelled(String errorMsg) {
            super.onCancelled(errorMsg);
            Log.d("PhishApp", "Error getting Pushy Token: " + errorMsg);
        }

        @Override
        protected void onPostExecute(String deviceToken) {
            // Log it for debugging purposes
            Log.d("PhishApp", "Pushy device token: " + deviceToken);

            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_pushy))
                    .appendPath(getString(R.string.ep_token))
                    .build();

            //build the JSONObject
            JSONObject msg = mCredentials.asJSONObject();

            try {
                msg.put("token", deviceToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //instantiate and execute the AsyncTask.
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(LoginFragment.this::handlePushyTokenOnPost)
                    .onCancelled(LoginFragment.this::handleErrorsInTask)
                    .addHeaderField("authorization", mJwt)
                    .build().execute();

        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
            extends WaitFragment.OnFragmentInteractionListener {
        void onLoginSuccess(Credentials c, String s) ;
        void onRegisterClicked();
    }



}
