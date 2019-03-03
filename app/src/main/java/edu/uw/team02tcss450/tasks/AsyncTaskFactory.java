package edu.uw.team02tcss450.tasks;

import android.net.Uri;
import android.support.annotation.NonNull;

import edu.uw.team02tcss450.DelAsyncTask;
import edu.uw.team02tcss450.HomeActivity;
import edu.uw.team02tcss450.R;
import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.utils.GetAsyncTask;

public class AsyncTaskFactory {

    // No need to instantiate a static helper class
    private AsyncTaskFactory() {}

    public static void confirmConnection(HomeActivity homeActivity, String mJwToken, String sent_from_username){

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendPath(homeActivity.getString(R.string.ep_confirm))
                .appendQueryParameter("sent_from", sent_from_username)
                .appendQueryParameter("sent_to", getUsernameFromCreds(homeActivity))
                .build();

        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(homeActivity::handleRequestOnPostWithToast)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    public static void removeConnectionRequestSentFrom(HomeActivity homeActivity, String mJwToken, String sent_from_username){

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", sent_from_username)
                .appendQueryParameter("sent_to", getUsernameFromCreds(homeActivity))
                .build();

        new DelAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(homeActivity::handleRequestOnPostWithToast)
                .addHeaderField("authorization", mJwToken)
                .build().execute();
    }

    public static void removeConnectionRequestSentTo(HomeActivity homeActivity, String mJwToken, String sent_to_username){

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", getUsernameFromCreds(homeActivity))
                .appendQueryParameter("sent_to", sent_to_username)
                .build();

        new DelAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(homeActivity::handleRequestOnPostWithToast)
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }

    private static String getUsernameFromCreds(HomeActivity homeActivity) {
        Credentials credentials = (Credentials) homeActivity.getIntent()
                .getExtras().getSerializable(homeActivity.getString(R.string.keys_intent_credentials));
        return credentials.getUsername();
    }

}
