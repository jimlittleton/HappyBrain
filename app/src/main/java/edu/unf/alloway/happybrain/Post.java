package edu.unf.alloway.happybrain;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.unf.alloway.utils.NetworkUtils;

/**
 * Created by rojas on 2/27/2018.
 */

public class Post extends Fragment {

    private final String TAG = Post.class.getSimpleName();
    private SharedPreferences mobileIdPref;
    private String mobileId;

    @BindView(R.id.bullet_point_one) TextView tvPointOne;
    @BindView(R.id.bullet_point_two) TextView tvPointTwo;
    @BindView(R.id.bullet_point_three) TextView tvPointThree;
    @BindView(R.id.web_link) TextView tvArticleLink;
    @BindView(R.id.why_message) TextView tvReflectMessage;
    @BindView(R.id.table_row_one) TableRow tableRowOne;
    @BindView(R.id.table_row_two) TableRow tableRowTwo;
    @BindView(R.id.table_row_three) TableRow tableRowThree;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_post, container, false);
        ButterKnife.bind(this, view);
        // Load the device's mobile id from shared preferences
        mobileIdPref = getActivity().getSharedPreferences(
                getString(R.string.pref_credential),
                Context.MODE_PRIVATE);
        mobileId = mobileIdPref.getString(getString(R.string.pref_mobile_id), null);

        // The mobileId will be null if this is the first time the user
        // has opened this app. If it is, we'll have to get one from the server
        if (mobileId == null) {
            getMobileId();
        } else {
            loadPageFromServer();
        }
        return view;
    }

    /**
     * AsyncTask to get a new mobile id from the Happy Brain server
     */
    @SuppressLint("StaticFieldLeak")
    private void getMobileId() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return NetworkUtils.makeHttpRequest(NetworkUtils.ADD_MOBILE_DEVICE_URL);
            }

            @Override
            protected void onPostExecute(String result) {
                // The String returned contains quotation marks so we have to remove those
                mobileId = result.replace("\"", "");
                mobileIdPref.edit().putString(getString(R.string.pref_mobile_id), mobileId).apply();
                // After we've successfully loaded a device's mobile ID,
                // we can fetch a web page from the server
                loadPageFromServer();
            }
        }.execute();
    }

    /**
     * AsyncTask to load a new page from the server
     * */
    @SuppressLint("StaticFieldLeak")
    private void loadPageFromServer() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String pageUrl = NetworkUtils.buildPageRequestUrl(mobileId);
                return NetworkUtils.makeHttpRequest(pageUrl);
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    // Convert the returned result to a JSONObject so we can
                    // extract the data from it
                    JSONObject baseJsonObject = new JSONObject(result);
                    populateViews(baseJsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * Uses a {@link JSONObject} returned from the server to populate the
     * given text views.
     *
     * @param baseJsonObject The {@link JSONObject} to extract the data from
     */
    private void populateViews(JSONObject baseJsonObject) {
        try {
            // Extract the text from the json object
            String pointOne = baseJsonObject.getString(JsonConstants.POINT_ONE);
            String pointTwo = baseJsonObject.getString(JsonConstants.POINT_TWO);
            String pointThree = baseJsonObject.getString(JsonConstants.POINT_THREE);
            String link = baseJsonObject.getString(JsonConstants.URL);
            String articleTitle = baseJsonObject.getString(JsonConstants.TITLE);
            String message = baseJsonObject.getString(JsonConstants.MESSAGE);

            // Before we set the text views, we have to check if there
            //is actually a bullet point to show in the JSONObject
            if (!isBulletEmpty(pointOne)) tvPointOne.setText(pointOne);
            else tableRowOne.setVisibility(View.GONE);

            if (!isBulletEmpty(pointTwo)) tvPointTwo.setText(pointTwo);
            else tableRowTwo.setVisibility(View.GONE);

            if (!isBulletEmpty(pointThree)) tvPointThree.setText(pointThree);
            else tableRowThree.setVisibility(View.GONE);

            tvReflectMessage.setText(message);
            tvArticleLink.setText(getString(R.string.place_holder_link, link));
            // Makes the article link clickable
            tvArticleLink.setMovementMethod(LinkMovementMethod.getInstance());
            getActivity().setTitle(articleTitle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if there is no text to show for the given bullet point.
     * The bullet point will be empty if the text is "0"
     *
     * @param bullet The bullet point to check
     * */
    private boolean isBulletEmpty(String bullet) {
        return TextUtils.isEmpty(bullet) || bullet.equals("0");
    }
}
