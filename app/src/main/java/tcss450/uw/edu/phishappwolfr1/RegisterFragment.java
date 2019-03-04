package tcss450.uw.edu.phishappwolfr1;

import android.content.Context;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import tcss450.uw.edu.phishappwolfr1.Model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private EditText emailEntry;
    private EditText passEntry;
    private EditText passEntryConfirm;
    private EditText firstNameEntry;
    private EditText lastNameEntry;
    private EditText userNameEntry;
    private OnRegisterFragmentInteractionListener mListener;
    private Credentials mCredentials;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        emailEntry = (EditText) v.findViewById(R.id.register_email_entry_field);
        passEntry = (EditText) v.findViewById(R.id.register_pass_entry_field);
        passEntryConfirm = (EditText) v.findViewById(R.id.register_pass_confirm_field);
        firstNameEntry = (EditText) v.findViewById(R.id.register_first_name_entry);
        lastNameEntry = (EditText) v.findViewById(R.id.register_last_name_entry);
        userNameEntry = (EditText) v.findViewById(R.id.register_username_entry);
        Button b = (Button) v.findViewById(R.id.send_registration_info);
        b.setOnClickListener(this::setRegisterSend);
        return v;
    }

    private void setRegisterSend(View view) {
        // perform data validation here
        String emailAddress = emailEntry.getText().toString();
        String password = passEntry.getText().toString();
        String passConfirm = passEntryConfirm.getText().toString();
        String userName = userNameEntry.getText().toString();
        String firstName = firstNameEntry.getText().toString();
        String lastName = lastNameEntry.getText().toString();


        boolean valid = true;
        if (emailAddress.length() == 0){
            //must enter username and password
            emailEntry.setError("Must enter email");
            valid = false;
        }
        if (userName.length() == 0){
            //must enter username and password
            userNameEntry.setError("Must enter EserName");
            valid = false;
        }
        if (!emailAddress.contains("@")){
            emailEntry.setError("Not a valid email");
            valid = false;
        }

        if (password.length() < 6){
            //must enter username and password
            passEntry.setError("Password Must be at least 6 characters");
            valid = false;
        }
        if (!password.equals(passConfirm)){
            //must enter username and password
            passEntryConfirm.setError("Passwords must Match");
            valid = false;
        }
        if (firstName.length() == 0){
            firstNameEntry.setError("Must Enter a First Name");
        }
        if (lastName.length() == 0){
            lastNameEntry.setError("Must Enter a Last Name");
        }

        if (valid) {
            Credentials c = new Credentials.Builder(emailAddress,password).
                    addFirstName(firstName).addLastName(lastName).addUsername(userName).build();
            mCredentials = c;

            //build the JSONObject
            JSONObject msg = c.asJSONObject();

            AsyncTask<String, Void, String> task = null;
            task = new PostWebServiceTask();
            task.execute(getString(R.string.ep_base_url), getString(R.string.ep_register),
                    msg.toString());
            }


    }

    private void handleErrorsInTask(String s) {
        Log.e("ASYNC_TASK_ERROR RegisterFragment",  s);
    }

    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success =
                    resultsJSON.getBoolean(
                            getString(R.string.keys_json_login_success));

            if (success) {
                //Register was successful. Switch to the loadSuccessFragment.
                mListener.onWaitFragmentInteractionHide();
                mListener.onRegisterSuccess(mCredentials);
                return;
            } else {
                //Register  was unsuccessful. Don’t switch fragments and
                // inform the user need more robust error
                ((TextView) getView().findViewById(R.id.register_username_entry))
                        .setError(resultsJSON.
                                getString("detail"));
            }
            mListener.onWaitFragmentInteractionHide();
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            Log.e("JSON_PARSE_ERROR",  result
                    + System.lineSeparator()
                    + e.getMessage());

            mListener.onWaitFragmentInteractionHide();
            ((TextView) getView().findViewById(R.id.register_username_entry))
                    .setError("Login Unsuccessful ");
        }


//        JSONObject resultsJSON = new JSONObject(result);
//        boolean success =
//                resultsJSON.getBoolean(
//                        getString(R.string.keys_json_login_success);
//        mListener.onRegisterSuccess(mCredentials);
//        mListener.onWaitFragmentInteractionHide();

    }

    private void handleRegisterOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentInteractionListener) {
            mListener = (OnRegisterFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnRegisterFragmentInteractionListener {
        void onRegisterSuccess(Credentials c);
        void onWaitFragmentInteractionShow();
        void onWaitFragmentInteractionHide();
    }


    private class PostWebServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            handleRegisterOnPre();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];
            String endPoint = strings[1];
            String msg = strings[2];
            //build the url
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(url)
                    .appendPath(endPoint)
                    .build();

            try {
                URL urlObject = new URL(uri.toString());
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);
                OutputStreamWriter wr =
                        new OutputStreamWriter(urlConnection.getOutputStream());

                wr.write(msg);
                wr.flush();
                wr.close();

                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }


            } catch (Exception e) {
                //cancel will result in onCanceled not onPostExecute
                cancel(true);
                return "Unable to connect, Reason: "  + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }
        @Override
        protected void onCancelled(String result) {
            super.onCancelled(result);
            mListener.onWaitFragmentInteractionHide();
            Log.e("register", "onCancelled: " +result );
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            handleLoginOnPost(result);
        }
    }
}
