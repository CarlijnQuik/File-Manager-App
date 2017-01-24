package com.example.carlijnquik.nlmprogblifi;

// copyright 2013 Google Inc. All Rights Reserved

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Query;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.ConnectionEventListener;

import static com.google.android.gms.drive.DriveApi.*;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile, which also adds a request dialog to access the user's Google Drive.
 */
public class DriveFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SIGN_IN = 9001;

    public GoogleApiClient googleApiClient;
    public GoogleSignInOptions googleSignInOptions;
    public GoogleApiClient driveGoogleApiClient;
    GoogleAccountCredential driveCredential;
    SharedPreferences prefs;

    private TextView tvStatus;
    private Button bSignIn;
    private Button bSignOut;
    Button bCreate;
    String accountName;
    private ProgressDialog progressDialog;
    private Bitmap bitmapToSave;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int RESULT_OK = -1;
    private static final String[] SCOPES = {DriveScopes.DRIVE};

    ArrayList<FileObject> driveFiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the global array list with dive files
        driveFiles = AllDriveFiles.getInstance().getFileList();

        if (googleSignInOptions == null) {
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(Scopes.DRIVE_FILE))
                    .requestEmail()
                    .build();
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(this.getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();

        }
        googleApiClient.connect();

        if (driveGoogleApiClient == null) {
            driveGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
        }
        driveGoogleApiClient.connect();

        if (driveCredential == null) {
            driveCredential = GoogleAccountCredential.usingOAuth2(
                    this.getActivity(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
        }

        prefs = this.getActivity().getPreferences(getContext().MODE_PRIVATE);

        // get signed in account if there
        accountName = prefs.getString("accountName", null);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drive, container, false);

        // Views
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        bSignIn = (Button) view.findViewById(R.id.bSignIn);
        bSignOut = (Button) view.findViewById(R.id.bSignOut);
        bCreate = (Button) view.findViewById(R.id.bCreate);

        // Button listeners
        bSignIn.setOnClickListener(this);
        bSignOut.setOnClickListener(this);
        bCreate.setOnClickListener(this);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        googleApiClient.connect();
        driveGoogleApiClient.connect();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void onStop() {
        super.onStop();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
        driveGoogleApiClient.disconnect();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        }
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            if (accountName != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("accountName", accountName);
                editor.apply();
                driveCredential.setSelectedAccountName(accountName);

                new MakeRequestTask(driveCredential).execute();
            }


        }


    }
    // [END onActivityResult]



    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
// [END handleSignInResult]

    // [START signIn]
    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        startActivityForResult(driveCredential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);


    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.

    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            bSignIn.setVisibility(View.GONE);
            bSignOut.setVisibility(View.VISIBLE);

            tvStatus.setText("Signed In");
        } else {
            tvStatus.setText(R.string.signed_out);

            bSignIn.setVisibility(View.VISIBLE);
            bSignOut.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSignIn:
                signIn();
                break;
            case R.id.bSignOut:
                signOut();
                break;
            case R.id.bCreate:
                createFile();
                Query query = new Query.Builder().build();
                Drive.DriveApi.query(driveGoogleApiClient, query).setResultCallback(metadataBufferCallback);
                break;
        }
    }

    public void createFile(){
        Drive.DriveApi.newDriveContents(driveGoogleApiClient).setResultCallback(driveContentsCallback);
    }

    /**
     * Appends the retrieved results to the result buffer.
     */
    private final ResultCallback<MetadataBufferResult> metadataBufferCallback = new
            ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    MetadataBuffer mdb = null;
                    try {
                        mdb = result.getMetadataBuffer();
                        if (mdb != null) for (Metadata md : mdb) {
                            if (md == null || !md.isDataValid()) continue;
                            md.getDriveId();
                            Log.d("string driveid buffer", md.getDriveId().toString());
                        }
                    } finally {
                        if (mdb != null) mdb.close();
                    }


                }
            };



    final private ResultCallback<DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write("Hello World!");
                                writer.close();
                            } catch (IOException e) {
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("New file")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(driveGoogleApiClient)
                                    .createFile(driveGoogleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
                @Override
                public void onResult(DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    tvStatus.setText(result.getDriveFile().getDriveId().toString());
                }
            };

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(transport, jsonFactory, credential)
                    .setApplicationName("BliFi")
                    .build();
        }

        /**
         * Background task to call Drive API, no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Retrieve the files from Google Drive.
         */
        public List<String> getDataFromApi() throws IOException {
            List<String> fileInfo = new ArrayList<>();

            FileList result = mService.files().list()
                    .setFields("nextPageToken, files")
                    .execute();
            List<File> files = result.getFiles();

            if (files != null) {


                for (File file : files) {
                    fileInfo.add(String.format("%s (%s)\n", file.getName(), file.getId()));
                    Log.d("string driveFile", file.getId());
                    Log.d("string driveFile", file.getName());

                    driveFiles.add(new FileObject(file, null, "DRIVE", "file"));

                }


            }

            return fileInfo;
        }

        @Override
        protected void onPostExecute(List<String> output) {
            Log.d("string yes", "yes");


        }


    }
}




