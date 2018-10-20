package edu.unf.alloway.happybrain.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Utility class used to make connections to the Happy Brain server.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    /**
     * Server key used to make requests
     */
    private static final String SERVER_SECRET_KEY = "UnFkFzLqOwNtXkS7KpFa2tTzSuFaUuNf";
    /**
     * The base URL for the Happy Brain website
     */
    public static final String BASE_SERVER_URL = "happybrainapp.com";
    /**
     * The URL used to add a new mobile device to the server.
     * Requesting this URL returns a random String that can be
     * used later in different server requests.
     * <p>
     * This URL should be called only once when a mobile
     * device connects to the Server for the first time.
     * <p>
     * The Mobile ID is a 17-character alphanumeric JSON-encoded string
     * consisting of the prefix UNF_ and 13 digits.
     */
    public static final String ADD_MOBILE_DEVICE_URL = String.format("https://%s/service.php?secret=%s&action=add",
                    BASE_SERVER_URL,
                    SERVER_SECRET_KEY);

    // Various responses that could be sent back from the server
    /**
     * <b>HBA00000</b>
     * <b>Meaning</b>: An unexpected error occurred during the last operation.<br>
     * <b>Cause</b>: Unknown.<br>
     * <b>Corrective Action</b>: None.
     */
    public static final String RESPONSE_HBA00000 = "\"Error: HBA00000\"";
    /**
     * <b>HBA00001</b>
     * <b>Meaning</b>: An unrecognized request was received by the Server.<br>
     * <b>Cause</b>: An improperly formatted or incorrect request submitted to the Server.<br>
     * <b>Corrective Action</b>: Check the request and try again.
     */
    public static final String RESPONSE_HBA00001 = "\"Error: HBA00001\"";
    /**
     * <b>HBA00002</b>
     * <b>Meaning</b>: The Server was not able to generate a unique Mobile ID for the requesting mobile device.<br>
     * <b>Cause</b>:Two or more mobile devices simultaneously attempted to add a new device to the Server.  This is a rare condition even in a busy environment.<br>
     * <b>Corrective Action</b>: Simply resubmit the request to add new device.
     */
    public static final String RESPONSE_HBA00002 = "\"Error: HBA00002\"";
    /**
     * <b>HBA00003</b>
     * <b>Meaning</b>: The specified mobile device Mobile ID does not exist on the Server.<br>
     * <b>Cause</b>: The specified mobile device
     * <br>1) is invalid or
     * <br>2) was deleted from the Server.<br>
     * <b>Corrective Action</b>:
     * <br>1) Verify the mobile device Mobile ID is accurate or
     * <br>2) request to add the mobile device to the Server.
     */
    public static final String RESPONSE_HBA00003 = "\"Error: HBA00003\"";
    /**
     * <b>HBA00004</b>
     * <b>Meaning</b>: The last page of the Server has been viewed.<br>
     * <b>Cause</b>: The mobile device has displayed all of the pages available on the Server.<br>
     * <b>Corrective Action</b>: None.
     */
    public static final String RESPONSE_HBA00004 = "\"Error: HBA00004\"";
    /**
     * <b>HBA00005</b><br>
     * <b>Meaning</b>: The data submitted by the mobile device was stored successfully.<br>
     */
    public static final String RESPONSE_HBA00005 = "\"Status: HBA00005\"";

    /**
     * This is a static class and shouldn't be instantiated
     */
    private NetworkUtils() {
    }

    /**
     * Used to make a request to the Happy Brain server.
     *
     * @param url The URL to make the request from
     * @return The response from the url requested
     */
    public static String makeHttpRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            return body != null ? body.string() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Used to build the URL that requests the page from the Happy Brain server.
     * Note that this request requires that we already have the mobile id
     * returned from the {@link #ADD_MOBILE_DEVICE_URL} link.
     *
     * @param mobileId The unique String used to identify this device.
     * @return The url for the query or {@code null} if there mobileId was missing
     * @see #ADD_MOBILE_DEVICE_URL
     */
    public static String buildPageRequestUrl(String mobileId) {
        // We can't request the next page if the mobileId doesn't exist
        if (TextUtils.isEmpty(mobileId)) {
            Log.e(TAG, "buildPageRequestUrl: couldn't build url, mobileId was missing");
            return null;
        }
        return String.format("https://%s/service.php?secret=%s&action=request&mobileid=%s",
                BASE_SERVER_URL,
                SERVER_SECRET_KEY,
                mobileId);
    }

    /**
     * Used to build the URL used to submit a comment to the server.
     * Note that this request requires that we already have the mobile id
     * returned from the {@link #ADD_MOBILE_DEVICE_URL} link.
     *
     * @param mobileId The unique String used to identify this device.
     * @param comment  The comment to be submitted
     * @return The formatted URL or {@code null} if there was an error
     * @see #ADD_MOBILE_DEVICE_URL
     */
    public static String buildServerCommentUrl(String mobileId, String comment) {
        if (TextUtils.isEmpty(mobileId)) {
            Log.e(TAG, "buildServerCommentUrl: couldn't build URL, mobileId was missing");
            return null;
        }
        if (TextUtils.isEmpty(comment)) {
            Log.e(TAG, "buildServerCommentUrl: cannot submit a null or empty comment");
            return null;
        }
        return String.format("https://%s/service.php?secret=%s&action=post&mobileid=%s&comment=%s",
                BASE_SERVER_URL,
                SERVER_SECRET_KEY,
                mobileId,
                comment);
    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
