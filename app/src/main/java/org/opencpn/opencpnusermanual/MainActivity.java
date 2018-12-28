package org.opencpn.opencpnusermanual;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.ReadPosition;
import com.folioreader.model.ReadPositionImpl;
import com.folioreader.util.ReadPositionListener;


import java.util.List;

public class MainActivity extends Activity {

    public String m_position = "";
    public Button readButton;
    public TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readButton = findViewById(R.id.buttonRead);
        statusText = findViewById(R.id.statusText);

        addListenerOnButtonRead();

        // Show license if not seen before
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean licenseShown = sharedPref.getBoolean("LicenseShown", false);
        if(!licenseShown) {
            showLicense();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("LicenseShown", true);
            editor.commit();
        }
        else {
            readBook();
        }
    }

    public void addListenerOnButtonRead() {
        readButton = findViewById(R.id.buttonRead);

        readButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                readBook();
            }

        });

    }

    public void showLicense(){
        String title = getResources().getString(R.string.app_name);
        title += "\n";
        title += getResources().getString(R.string.license_title);

        String message = getResources().getString(R.string.license);
        showAlertDialog( title, message);

    }


    public void readBook(){
        FolioReader folioReader = FolioReader.get();

        folioReader.setReadPositionListener(new ReadPositionListener() {
            @Override
            public void saveReadPosition(ReadPosition readPosition) {

                String position = readPosition.toJson();

                m_position = position;

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Position", position);
                editor.commit();
            }
        });


        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String defaultValue = "";
        String startPosition = sharedPref.getString("Position", defaultValue);
        ReadPosition readPosition = ReadPositionImpl.createInstance(startPosition);

        folioReader.setReadPosition(readPosition);
        Config config = new Config().setDirection(Config.Direction.HORIZONTAL)
                .setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL);
        folioReader.setConfig(config, true);

        folioReader.openBook("file:///android_asset/OpenCPNUserManual-4-8-6-Sept-18-18-compress-lossy-2-70pc-R1.epub");

    }

    public String showHTMLAlertDialog( String title, String htmlString) {

        WebView wv = new WebView(getApplicationContext());

        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setMinimumFontSize(50);
        wv.loadData(htmlString, "text/html; charset=UTF-8", null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setView(wv)
                .setCancelable(false)
                .setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        //                     alertDialogBuilder.setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
        //                             public void onClick(DialogInterface dialog,int id) {
        // if this button is clicked, close
        // current activity
        //                                     QtActivity.this.finish();
        //                             }
        //                       });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        return ("OK");

    }

    public String showAlertDialog( String title, String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        //                     alertDialogBuilder.setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
        //                             public void onClick(DialogInterface dialog,int id) {
        // if this button is clicked, close
        // current activity
        //                                     QtActivity.this.finish();
        //                             }
        //                       });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        return ("OK");

    }

    public boolean canDisplayEpub() {
        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/epub+zip");
        return packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


    public void readBookUsingInstalledReader(){
        Uri URI0 = Uri.parse("/storage/emulated/legacy/Charts/OpenCPNUserManual-4-8-6-Sept-18-18-compress-lossy-2-70pc.epub");
        Intent intent0 = new Intent(Intent.ACTION_VIEW, URI0);
        intent0.setType("application/epub+zip");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent0,  PackageManager.MATCH_ALL);



        List<PackageInfo> packList = getPackageManager().getInstalledPackages(PackageManager.GET_INTENT_FILTERS);
        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            if (  (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                if(appName.equals("Aldiko")){
                    if(packInfo.versionCode >= 18){

                        //break;
                    }
                }
                //Log.i("OpenCPN", appName);
            }
        }

        if(canDisplayEpub()){
            Log.i("OpenCPN", "Can do epub");

/*
            String url = "/storage/emulated/0/Download/book.epub";

            Uri URI = null;
            try{
                // Build fileprovider URI
                File helpFile = new File(url);
                URI = FileProvider.getUriForFile(this,
                        getString(R.string.file_provider_authority),
                        helpFile);
                URI = URI.normalizeScheme();
            } catch (Exception ex) {
                Log.i("OpenCPN", ex.getMessage());
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, URI);
            intent.setType("application/epub+zip");

            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (activities.size() > 0) {
                intent.setDataAndType(URI, "application/epub+zip");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                finish();
            } else {
                // Do something else here. Maybe pop up a Dialog or Toast
            }
*/
        }

    }
}
