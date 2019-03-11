package edu.uw.team02tcss450.tasks;

import android.net.Uri;

import edu.uw.team02tcss450.utils.DelAsyncTask;
import edu.uw.team02tcss450.HomeActivity;
import edu.uw.team02tcss450.R;
import edu.uw.team02tcss450.model.Credentials;
import edu.uw.team02tcss450.utils.GetAsyncTask;
import edu.uw.team02tcss450.utils.PutAsyncTask;

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

    public static void sendInvitiationEmail(HomeActivity homeActivity, String mJwToken, String to_email){

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendPath(homeActivity.getString(R.string.ep_invite))
                .appendQueryParameter("from_username", getUsernameFromCreds(homeActivity))
                .appendQueryParameter("to_email", to_email)
                .build();

        new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(homeActivity::handleSendEmailInviteOnPostWithToast)
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }

    public static void sendFriendRequestTo(HomeActivity homeActivity, String mJwToken, String to_username){

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", getUsernameFromCreds(homeActivity))
                .appendQueryParameter("sent_to", to_username)
                .build();

        new PutAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(homeActivity::handleFriendRequestOnPostWithToast)
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }


    public static void loadRequestsSentFragment(HomeActivity homeActivity, String mJwToken){

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_from", getUsernameFromCreds(homeActivity))
                .build();


        new edu.uw.team02tcss450.utils.GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(homeActivity::handleRequestSentOnPostExecute)
                .addHeaderField("authorization", mJwToken)
                .build().execute();

    }

    private void loadRequestsReceivedFragment(HomeActivity homeActivity, String mJwToken){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(homeActivity.getString(R.string.ep_base_url))
                .appendPath(homeActivity.getString(R.string.ep_connections))
                .appendQueryParameter("sent_to", getUsernameFromCreds(homeActivity))
                .build();

        edu.uw.team02tcss450.utils.GetAsyncTask task = new GetAsyncTask.Builder(uri.toString())
                .onPreExecute(homeActivity::onWaitFragmentInteractionShow)
                .onPostExecute(homeActivity::handleRequestReceivedOnPostExecute)
                .addHeaderField("authorization", mJwToken)
                .build();
        task.execute();


    }


    private static String getUsernameFromCreds(HomeActivity homeActivity) {
        Credentials credentials = (Credentials) homeActivity.getIntent()
                .getExtras().getSerializable(homeActivity.getString(R.string.keys_intent_credentials));
        return credentials.getUsername();
    }

}
