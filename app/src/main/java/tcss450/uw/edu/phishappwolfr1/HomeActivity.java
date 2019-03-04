package tcss450.uw.edu.phishappwolfr1;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.pushy.sdk.Pushy;
import tcss450.uw.edu.phishappwolfr1.Model.Credentials;
import tcss450.uw.edu.phishappwolfr1.Content.BlogPost;
import tcss450.uw.edu.phishappwolfr1.Content.SetList;
import tcss450.uw.edu.phishappwolfr1.utils.GetAsyncTask;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BlogFragment.OnListFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener,
        SetListFragment.OnListFragmentInteractionListener {

    private String mJwToken;
    private Credentials mCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the Intent that started this activity and extract the string.
        Intent intent = getIntent();
        Bundle args = new Bundle();

        if (intent.getExtras().containsKey(getString(R.string.keys_intent_jwt))) {
            mJwToken = getIntent().getStringExtra(getString(R.string.keys_intent_jwt));
            args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
        }

        if (intent.getExtras().containsKey(getString(R.string.keys_intent_credentials))) {
            mCredentials = (Credentials) intent.getExtras().getSerializable(getString(R.string.keys_intent_credentials));
            args.putSerializable(getString(R.string.keys_intent_credentials), mCredentials);
        }

        Fragment fragment;
        if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
            fragment = new ChatFragment();
        } else {
            fragment = new SuccessFragment();
            fragment.setArguments(args);
        }


        loadFragment(fragment);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // nothing implimented for settings
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.id_setlist_post_select) {
//            Uri uri = new Uri.Builder().encodedPath("https://cfb3-lab4-backend.herokuapp.com/phish/setlists/recent").build();
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_phish))
                    .appendPath(getString(R.string.ep_setlists))
                    .appendPath(getString(R.string.ep_recent))
                    .build();

            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleSetListGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();
        }

        if (id == R.id.id_global_chat_select) {

            Bundle args = new Bundle();
            args.putSerializable(getString(R.string.keys_intent_jwt), mJwToken);
            args.putSerializable(getString(R.string.keys_intent_credentials), mCredentials.getEmail());
            Fragment frag = new ChatFragment();
            frag.setArguments(args);

            loadFragment(frag);

        }
        if (id == R.id.id_blog_post_select) {
            // Handle the blog post navigation action

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_phish))
                    .appendPath(getString(R.string.ep_blog))
                    .appendPath(getString(R.string.ep_get))
                    .build();

            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleBlogGetOnPostExecute)
                    .addHeaderField("authorization", mJwToken) //add the JWT as a header
                    .build().execute();


        } else if (id == R.id.id_drawer_home_select) {
            //go to success fragment
            // Get the Intent that started this activity
            Intent intent = getIntent();

            // add logic to check if the key is there
            Credentials c;
            if (intent.getExtras().containsKey(getString(R.string.keys_intent_credentials))) {
                c = (Credentials) intent.getExtras().getSerializable(getString(R.string.keys_intent_credentials));
                SuccessFragment success = new SuccessFragment();
                Bundle args = new Bundle();
                args.putSerializable(getString(R.string.keys_intent_credentials), c);
                success.setArguments(args);
                loadFragment(success);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleSetListGetOnPostExecute(String result) {
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
//Log.e("setlist", "handleSetListGetOnPostExecute: " + root );
            if (root.has(getString(R.string.keys_json_setlist_response))) {
                JSONObject response = root.getJSONObject(
                        getString(R.string.keys_json_setlist_response));
                if (response.has(getString(R.string.keys_json_setlist_data))) {
                    JSONArray data = response.getJSONArray(
                            getString(R.string.keys_json_setlist_data));

                    List<SetList> setLists = new ArrayList<>();

                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonBlog = data.getJSONObject(i);

                        setLists.add(new SetList.Builder(jsonBlog.getString(getString(R.string.keys_json_setlist_date)),
                                jsonBlog.getString(getString(R.string.keys_json_setlist_location)),
                                (jsonBlog.getString(getString(R.string.keys_json_setlist_venue))))
                                .addUrl(jsonBlog.getString(getString(R.string.keys_json_setlist_url)))
                                .addNotes(jsonBlog.getString(getString(R.string.keys_json_setlist_notes)))
                                .addSetListData(jsonBlog.getString(getString(R.string.keys_json_setlist_setlistdata)))
                                .build());
                    }

                    SetList[] selistsAsArray = new SetList[setLists.size()];
                    selistsAsArray = setLists.toArray(selistsAsArray);


                    Bundle args = new Bundle();
                    args.putSerializable(SetListFragment.ARG_SET_LIST, selistsAsArray);
                    Fragment frag = new SetListFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();
                    loadFragment(frag);
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }

    @Override
    public void onListFragmentInteraction(BlogPost item) {
        BlogPostFragment frag = new BlogPostFragment();
        Bundle args = new Bundle();
        args.putSerializable("", item);
        frag.setArguments(args);
        loadFragment(frag);
    }

    @Override
    public void onListFragmentInteraction(SetList item) {
        OneSetList frag = new OneSetList();
        Bundle args = new Bundle();
        args.putSerializable("", item);
        frag.setArguments(args);
        loadFragment(frag);
    }


    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_home, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("WAIT")))
                .commit();
    }

    private void handleBlogGetOnPostExecute(final String result) {
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has(getString(R.string.keys_json_blogs_response))) {
                JSONObject response = root.getJSONObject(
                        getString(R.string.keys_json_blogs_response));
                if (response.has(getString(R.string.keys_json_blogs_data))) {
                    JSONArray data = response.getJSONArray(
                            getString(R.string.keys_json_blogs_data));

                    List<BlogPost> blogs = new ArrayList<>();

                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonBlog = data.getJSONObject(i);

                        blogs.add(new BlogPost.Builder(
                                jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_pubdate)),
                                jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_title)))
                                .addTeaser(jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_teaser)))
                                .addUrl(jsonBlog.getString(
                                        getString(R.string.keys_json_blogs_url)))
                                .build());
                    }

                    BlogPost[] blogsAsArray = new BlogPost[blogs.size()];
                    blogsAsArray = blogs.toArray(blogsAsArray);


                    Bundle args = new Bundle();
                    args.putSerializable(BlogFragment.ARG_BLOG_LIST, blogsAsArray);
                    Fragment frag = new BlogFragment();
                    frag.setArguments(args);

                    onWaitFragmentInteractionHide();
                    loadFragment(frag);
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_home, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    private void logout() {

        new DeleteTokenAsyncTask().execute();
//        finishAndRemoveTask();

        //or close this activity and bring back the Login
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
//        End this Activity and remove it from the Activity back stack.
        finish();
    }



    // Deleting the Pushy device token must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();

            //unregister the device from the Pushy servers
            Pushy.unregister(HomeActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            finishAndRemoveTask();

            //or close this activity and bring back the Login
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//            //Ends this Activity and removes it from the Activity back stack.
//            finish();
        }
    }


}
