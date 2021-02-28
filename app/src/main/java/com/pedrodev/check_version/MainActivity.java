package com.pedrodev.check_version;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pedrodev.check_version.provider.VersionProvider;

public class MainActivity extends AppCompatActivity {

   /* DownloadManager mDowloadManager;
    DownloadManager.Request request;
    long enqueue;
    final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    AlertDialog mDialogDownload;*/

    AlertDialog dialog;
    String DOWNLOAD_URL = "";

    // This provider is used to connect to Firebase cloud realtime to get the data
    VersionProvider mVersionProvider;
    TextView versionApp;
    TextView checkVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instanciate the Provider
        mVersionProvider = new VersionProvider();

        versionApp = findViewById(R.id.versionApp);
        checkVersion = findViewById(R.id.textViewCheckVersion);
        checkVersionApp();

        checkVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowserToDownload(DOWNLOAD_URL);
            }
        });
    }

    public void checkVersionApp() {
        mVersionProvider.getVersion().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {

                        /* These variables are for get version code and name of the app */
                        int versionCode = 0;
                        String versionName = "";

                        /*Here get the data to verify the version*/
                        String _versionCodeFB = snapshot.child("Version").child("versionApp").getValue(String.class);
                        String _versionNameFB = snapshot.child("Version").child("versionName").getValue(String.class);
                        String _urlApkFB = snapshot.child("Version").child("urlApk").getValue(String.class);

                        /* Get android:versionName */
                        versionName = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
                        /* Get android:versionCode*/
                        versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                        /* Set Version to Login */
                        String nameApp = getNameApp();
                        /* This method is used to show name and version of the app in the login layout */
                        versionApp.setText(nameApp + ": " + versionName);

                        /* Test */
                        Log.e("VersionNAME:",versionName);
                        Log.e("versionCode:", String.valueOf(versionCode));

                        /* Conditional to compare the versions of the app with the ones I get from firebase */
                        if (!(String.valueOf(versionCode).equals(_versionCodeFB)
                                && (versionName.equals(_versionNameFB)))){

                            /* Get URL From Firebase */
                            DOWNLOAD_URL = _urlApkFB;

                            /* Alert dialog Options */
                            dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("New version available")
                                    .setMessage("Please, update app to new version.")
                                    .setPositiveButton("Update",
                                            new DialogInterface.OnClickListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.M)
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    /* The user goes to app.sysnotes.net */
                                                    openBrowserToDownload(DOWNLOAD_URL);
                                                }
                                            }).setNegativeButton("No, thanks",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(MainActivity.this,
                                                            "You have a pending update", Toast.LENGTH_LONG).show();
                                                }
                                            }).create();
                            dialog.show();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error"
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openBrowserToDownload(String download_url) {
        // Intent
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(download_url));
        startActivity(browserIntent);
    }

    private String getNameApp() {
        PackageManager packageManager = MainActivity.this.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(MainActivity.this.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }

    //    private void checkPermissionsUsers(final String DOWNLOAD_URL) {
//        dialog = new AlertDialog.Builder(MainActivity.this)
//                .setTitle("New version available")
//                .setMessage("Please, update app to new version.")
//                .setPositiveButton("Update",
//                        new DialogInterface.OnClickListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.M)
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Check if the write permission is already available
//                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//                                    // Permission granted
//                                    DownloadApk(DOWNLOAD_URL);
//                                } else {
//                                    // Permissions requested when needed
//                                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                                        DownloadApk(DOWNLOAD_URL);
//                                    } else {
//                                        // Permissions for Write external storage
//                                        // Provide an aditional rationale to the user if the permission was not granted
//                                        // and the user would benefit from additional context for the use of the permission
//                                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                                            Toast.makeText(MainActivity.this, "Write permissions is needed to download apk",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//                                    }
//                                }
//
//                            }
//                        }).setNegativeButton("No, thanks",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).create();
//        dialog.show();
//    }

//    private void DownloadApk(String DOWNLOAD_URL) {
//        mDialogDownload.show();
//        mDowloadManager = (DownloadManager) MainActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
//        request = new DownloadManager.Request(Uri.parse(DOWNLOAD_URL));
//        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(DOWNLOAD_URL);
//        Log.e("File extension", fileExtension);
//        final String name = URLUtil.guessFileName(DOWNLOAD_URL, null, fileExtension);
//        Log.e("File name", fileExtension);
//        request.setDestinationInExternalPublicDir("/Download", name);
//        String h = request.setDestinationInExternalPublicDir("/Download", name).toString();
//        Log.e("Destino del Archivo: ", h);
//        enqueue = mDowloadManager.enqueue(request);
//        Log.e("Enqueue: ", String.valueOf(enqueue));
//
//        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                assert action != null;
//                Log.e("Action: ", action);
//                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//                    intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//
//                    Log.e("Intent: ", String.valueOf(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)));
//                    if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) == -1) {
//                        finish();
//                    }
//
//                    DownloadManager.Query query = new DownloadManager.Query();
//                    query.setFilterById(enqueue);
//                    Cursor cursor = mDowloadManager.query(query);
//                    Log.e("cursor: ", String.valueOf(mDowloadManager.query(query)));
//                    if (cursor.moveToFirst()) {
//                        int columIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//
//                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columIndex)) {
//                            String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                            Log.e("uriString",uriString);
//                            Toast.makeText(MainActivity.this, "Termino la descarga",Toast.LENGTH_LONG).show();
//                            mDialogDownload.dismiss();
//                            //startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
//                                FileProvider file = new FileProvider();
//
//                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
//                                intent2.setDataAndType(Uri.parse(uriString),
//                                        "application/vnd.android.package-archive");
//                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                        }
//                    }
//                }
//            }
//
//        };
//        MainActivity.this.registerReceiver(mBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//    }

    // Handle the response
    /*
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            // received permission result for write permissions

            //Check if the only required permission has been granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted
                UpdateApp();
            } else {
                Toast.makeText(MainActivity.this, "Permission was not granted", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

*/

}