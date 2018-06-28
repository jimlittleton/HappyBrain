package edu.unf.alloway.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Utility class used to make connections to the Happy Brain server.
 * Here is a list of the following possible error responses returned by the server:
 * <br>
 * <li><b>HBA00001</b>
 * <p>
 * <b>Meaning</b>: An unrecognized request was received by the Server.<br>
 * <b>Cause</b>: An improperly formatted or incorrect request submitted to the Server.<br>
 * <b>Corrective Action</b>: Check the request and try again.
 * <br><br>
 * <li><b>HBA00002</b>
 * <p>
 * <b>Meaning</b>: The Server was not able to generate a unique Mobile ID for the requesting mobile device.<br>
 * <b>Cause</b>:Two or more mobile devices simultaneously attempted to add a new device to the Server.  This is a rare condition even in a busy environment.<br>
 * <b>Corrective Action</b>: Simply resubmit the request to add new device.
 * <br><br>
 * <li><b>HBA00003</b>
 * <p>
 * <b>Meaning</b>: The specified mobile device Mobile ID does not exist on the Server.<br>
 * <b>Cause</b>: The specified mobile device
 * <br>1) is invalid or
 * <br>2) was deleted from the Server.<br>
 * <b>Corrective Action</b>:
 * <br>1) Verify the mobile device Mobile ID is accurate or
 * <br>2) request to add the mobile device to the Server.
 * <br><br>
 * <li><b>HBA00004</b>
 * <p>
 * <b>Meaning</b>: The last page of the Server has been viewed.<br>
 * <b>Cause</b>: The mobile device has displayed all of the pages available on the Server.<br>
 * <b>Corrective Action</b>: None.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    /**
     * The base URL for the Happy Brain website
     */
    private static final String BASE_WEBSITE_URL = "happybrainapp.com";
    /**
     * Server key used to make requests
     */
    private static final String SERVER_SECRET_KEY = "ADD KEY HERE";
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
    public static final String ADD_MOBILE_DEVICE_URL =
            String.format("https://%s/service.php?secret=%s&action=add",
                    BASE_WEBSITE_URL,
                    SERVER_SECRET_KEY);

    /**
     * This is a static class and shouldn't be instantiated
     */
    private NetworkUtils() {}

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
     */
    @Nullable
    public static String buildPageRequestUrl(@NonNull String mobileId) {
        // We can't request the next page if the mobileId doesn't exist
        if (TextUtils.isEmpty(mobileId)) {
            Log.e(TAG, "buildPageRequestUrl: couldn't build url, mobileId was null");
            return null;
        }
        return String.format("https://%s/service.php?secret=%s&action=request&mobileid=%s",
                BASE_WEBSITE_URL,
                SERVER_SECRET_KEY,
                mobileId);
    }
}
