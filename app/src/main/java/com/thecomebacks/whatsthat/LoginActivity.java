package com.thecomebacks.whatsthat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thecomebacks.whatsthat.beans.User;
import com.thecomebacks.whatsthat.commons.Constants;
import com.thecomebacks.whatsthat.commons.Utils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "hello:hello", "world:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserRegisterTask mRegisterTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private boolean sharedPreferences = false;

    private boolean registerView = false;

    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create Shared Preferences
        sp = getSharedPreferences( getApplicationInfo().name, MODE_PRIVATE);
        String userUsername = sp.getString(Constants.USER_USERNAME,null);
        String userPassword = sp.getString(Constants.USER_PASSWORD, null);

        if (userUsername != null && !"".equals(userUsername) &&
                userPassword != null && !"".equals(userPassword)) {
            checkCurrentUser(userUsername, userPassword);
        } else {

            // Set up the login form.
            mUsernameView = (EditText) findViewById(R.id.user);

            mPasswordView = (EditText) findViewById(R.id.hash);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            final Button mUsernameSignInButton = (Button) findViewById(R.id.username_sign_in_button);
            mUsernameSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);

            final Button btnRegister = (Button) findViewById(R.id.username_register_button);
            btnRegister.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (registerView) {
                        mUsernameSignInButton.setText(R.string.action_sign_in);
                        btnRegister.setText(R.string.action_register);
                        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptLogin();
                            }
                        });
                    } else {
                        mUsernameSignInButton.setText(R.string.create);
                        btnRegister.setText(R.string.cancel);
                        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptRegister();
                            }
                        });
                    }
                    registerView = !registerView;
                }
            });
        }
    }


    private void checkCurrentUser (String username, String password) {
        if (username != null && !"".equals(username) &&
                password != null && !"".equals(password)) {
            sharedPreferences = true;
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mRegisterTask = new UserRegisterTask(username, password);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, User> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected User doInBackground(Void... params) {

            User returnUser = new User();
            returnUser.setUser(mUsername);
            returnUser.setHash(Utils.md5(mPassword));

            String response = utils.generatePostOrPutRequest(Constants.METHOD_PUT, Constants.LOGIN_URL, returnUser);
            if (!Boolean.valueOf(response)) {
                returnUser = null;
            }

            return returnUser;
        }

        @Override
        protected void onPostExecute(final User user) {
            mAuthTask = null;
            if (!sharedPreferences) {
                showProgress(false);
            }

            if (user != null) {
                spEditor = sp.edit();
                spEditor.putString(Constants.USER_USERNAME, user.getUser());
                spEditor.putString(Constants.USER_PASSWORD, user.getHash());
                spEditor.apply();

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ShowImage.class);
                intent.putExtra(Constants.USER_USERNAME, user.getUser());
                intent.putExtra(Constants.USER_PASSWORD, user.getHash());
                startActivity(intent);
                finish();
            } else {

                spEditor = sp.edit();
                if (sp.contains(Constants.USER_USERNAME)) {
                    spEditor.remove(Constants.USER_USERNAME);
                }
                if (sp.contains(Constants.USER_PASSWORD)) {
                    spEditor.remove(Constants.USER_PASSWORD);
                }
                spEditor.apply();
                sharedPreferences = false;

                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, User> {

        private final String mUsername;
        private final String mPassword;

        UserRegisterTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected User doInBackground(Void... params) {

            User returnUser = new User();
            returnUser.setUser(mUsername);
            returnUser.setHash(Utils.md5(mPassword));

            String response = utils.generatePostOrPutRequest(Constants.METHOD_POST, Constants.LOGIN_URL, returnUser);
            if (!Boolean.valueOf(response)) {
                returnUser = null;
            }

            return returnUser;
        }

        @Override
        protected void onPostExecute(final User user) {
            mAuthTask = null;
            if (!sharedPreferences) {
                showProgress(false);
            }

            if (user != null) {
                spEditor = sp.edit();
                spEditor.putString(Constants.USER_USERNAME, user.getUser());
                spEditor.putString(Constants.USER_PASSWORD, user.getHash());
                spEditor.apply();

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ShowImage.class);
                intent.putExtra(Constants.USER_USERNAME, user.getUser());
                intent.putExtra(Constants.USER_PASSWORD, user.getHash());
                startActivity(intent);
                finish();
            } else {

                spEditor = sp.edit();
                if (sp.contains(Constants.USER_USERNAME)) {
                    spEditor.remove(Constants.USER_USERNAME);
                }
                if (sp.contains(Constants.USER_PASSWORD)) {
                    spEditor.remove(Constants.USER_PASSWORD);
                }
                spEditor.apply();
                sharedPreferences = false;

                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

