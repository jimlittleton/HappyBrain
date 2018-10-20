package edu.unf.alloway.happybrain.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.unf.alloway.happybrain.R;
import edu.unf.alloway.happybrain.models.Page;
import edu.unf.alloway.happybrain.utils.NetworkUtils;

/**
 * Created by rojas on 2/27/2018.
 */

public class PostFragment extends Fragment {
    private SharedPreferences mobileIdPref;
    private String mobileId;

    @BindView(R.id.post_image) ImageView ivPageImage;
    @BindView(R.id.read_the_study_header) TextView readStudyHeader;
    @BindView(R.id.bullet_point_one) TextView tvPointOne;
    @BindView(R.id.bullet_point_two) TextView tvPointTwo;
    @BindView(R.id.bullet_point_three) TextView tvPointThree;
    @BindView(R.id.web_link) TextView tvArticleLink;
    @BindView(R.id.why_message) TextView tvReflectMessage;
    @BindView(R.id.tv_error_message) TextView tvError;
    @BindView(R.id.table_row_one) TableRow tableRowOne;
    @BindView(R.id.table_row_two) TableRow tableRowTwo;
    @BindView(R.id.table_row_three) TableRow tableRowThree;
    @BindView(R.id.post_container) LinearLayout layoutContainer;
    @BindView(R.id.post_progress_bar) ProgressBar progressBar;
    @BindView(R.id.fab_share) FloatingActionButton fabShare;
    @BindView(R.id.et_reflect_container) TextInputLayout etContainer;
    @BindView(R.id.et_reflect) EditText etReflect;
    @BindView(R.id.bt_submit_reflection) Button btSubmitReflection;
    @BindView(R.id.post_root) RelativeLayout root;

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
        if (mobileId == null || mobileId == "") getMobileIdFromServer();
        else loadPageFromServer();

        layoutContainer.setVisibility(View.INVISIBLE);
        btSubmitReflection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etReflect.getText())) {
                    etContainer.setError("Please enter a reflection");
                    return;
                }
                String comment = etReflect.getText().toString().trim();
                submitCommentToServer(comment);
            }
        });
        return view;
    }

    /**
     * AsyncTask to get a new mobile id from the Happy Brain server
     */
    @SuppressLint("StaticFieldLeak")
    private void getMobileIdFromServer() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtils.hasInternetConnection(getActivity())) {
                    cancel(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    showConnectionSnackbar(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMobileIdFromServer();
                        }
                    });
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                return NetworkUtils.makeHttpRequest(NetworkUtils.ADD_MOBILE_DEVICE_URL);
            }

            @Override
            protected void onPostExecute(String result) {
                // The String returned contains quotation marks so we have to remove those
                mobileId = result.replace("\"", "");
                mobileIdPref.edit().putString(getString(R.string.pref_mobile_id), mobileId).apply();
                layoutContainer.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                // After we've successfully loaded a device's mobile ID,
                // we can fetch a web page from the server
                loadPageFromServer();
            }
        }.execute();
    }

    /**
     * AsyncTask to load a new page from the server
     */
    @SuppressLint("StaticFieldLeak")
    private void loadPageFromServer() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtils.hasInternetConnection(getActivity())) {
                    cancel(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    showConnectionSnackbar(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadPageFromServer();
                        }
                    });
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                String pageUrl = NetworkUtils.buildPageRequestUrl(mobileId);
                return NetworkUtils.makeHttpRequest(pageUrl);
            }

            @Override
            protected void onPostExecute(String result) {
               if (!result.contains("HBA")) {
                   populateViews(Page.fromJson(result));
               } else {
                   handleError(result);
               }
            }
        }.execute();
    }

    /**
     * AsyncTask to submit a comment to the server
     */
    @SuppressLint("StaticFieldLeak")
    private void submitCommentToServer(final String comment) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // We need to disable the button to make sure the
                // user doesn't submit multiple requests if the button
                // is tapped multiple times
                btSubmitReflection.setEnabled(false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                String commentUrl = NetworkUtils.buildServerCommentUrl(mobileId, comment);
                return NetworkUtils.makeHttpRequest(commentUrl);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                btSubmitReflection.setEnabled(true);
                // Comment was submitted successfully
                if (result.equals(NetworkUtils.RESPONSE_HBA00005)) {
                    Snackbar.make(root, R.string.reflection_submit_success, Snackbar.LENGTH_SHORT).show();
                } else { // Error submitting comment
                    Snackbar.make(root, R.string.reflection_submit_error, Snackbar.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * Uses a {@link Page} object returned from the server to populate the
     * given text views. This method also sets a listener for the
     * FloatingActionButton to share the text of the post.
     *
     * @param page The {@link Page} object to extract the data from
     */
    private void populateViews(final Page page) {
        Picasso.get().load(page.getImageUrl()).into(ivPageImage);

        String linkText = String.format("<a href='%s'>Click Here to View the Study</a>", page.getStudyLink());

        // Before we set the text views, we have to check if there
        // is actually a bullet point to show
        if (!TextUtils.isEmpty(page.getFirstBullet())) tvPointOne.setText(page.getFirstBullet());
        else tableRowOne.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(page.getSecondBullet())) tvPointTwo.setText(page.getSecondBullet());
        else tableRowTwo.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(page.getThirdBullet())) tvPointThree.setText(page.getThirdBullet());
        else tableRowThree.setVisibility(View.GONE);

        tvReflectMessage.setText(page.getMessage());





        // If there isn't a link to show, we don't want
        // to waste space in the layout so we'll hide it
        if (!TextUtils.isEmpty(page.getStudyLink())) {
            //tvArticleLink.setText(page.getStudyLink());

            tvArticleLink.setClickable(true);
            tvArticleLink.setMovementMethod(LinkMovementMethod.getInstance());
            tvArticleLink.setAutoLinkMask(0);
            tvArticleLink.setText(Html.fromHtml(linkText));


            // Makes the article link clickable
            //tvArticleLink.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            readStudyHeader.setVisibility(View.GONE);
            tvArticleLink.setVisibility(View.GONE);
        }
        // Makes sure to set the title of the action bar to
        // the title of this post
        getActivity().setTitle(page.getTitle());
        layoutContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        // The listener for the fab is set here so that a post can only
        // be shared if it was loaded successfully
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Now that we have the data from the server, we can share the
                // title of the post along with the message when the fab is clicked
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, page.getTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, page.getTeaser());
                sharingIntent.putExtra(Intent.EXTRA_HTML_TEXT, String.format("<a href='%s'>Click Here to View the Page</a>", page.getPageUrl()));
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });
        // We only want to show the views to submit a reflection
        // if the page from the server is a reflection page
        if (page.isReflect()) {
            etContainer.setVisibility(View.VISIBLE);
            btSubmitReflection.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows any error that may have occurred.
     */
    private void handleError(String error) {
        progressBar.setVisibility(View.INVISIBLE);
        switch (error) {
            case NetworkUtils.RESPONSE_HBA00000: // Unknown error
                Snackbar.make(root, getString(R.string.ERROR_HBA00000), Snackbar.LENGTH_LONG).show();
                break;
            case NetworkUtils.RESPONSE_HBA00001: // Unknown request
            case NetworkUtils.RESPONSE_HBA00002: // Unable to generate mobile id
            case NetworkUtils.RESPONSE_HBA00003: // Mobile id doesn't exist on server
                break;
            case NetworkUtils.RESPONSE_HBA00004: // Last page reached
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.ERROR_HBA00004));
                break;
        }
    }

    /**
     * Shows a SnackBar with the text "No internet connection"
     * @param listener The action to be performed when the RETRY
     *                 button is clicked.
     * */
    private void showConnectionSnackbar(View.OnClickListener listener) {
        View rootView = getActivity().findViewById(R.id.main_activity_root);
        Snackbar.make(rootView, R.string.error_no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_action_retry, listener)
                .show();
    }
}
