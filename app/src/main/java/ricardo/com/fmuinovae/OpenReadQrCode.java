package ricardo.com.fmuinovae;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class OpenReadQrCode extends AppCompatActivity implements LocationListener {


    private Button mButton;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 42;
    private static double latitude;
    private static double longitude;

    private static LocationManager lm;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);


        mButton = (Button) findViewById(R.id.assistant_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(OpenReadQrCode.this);
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        IntentIntegrator integrator = new IntentIntegrator(OpenReadQrCode.this);
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);


    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.d("code", re);
        }
       finish();

    }


    @Override
    public void onLocationChanged(Location location) {

        latitude= location.getLongitude();
        latitude =  location.getLatitude();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class AsyncTaskGravaDados extends AsyncTask<String, String, JSONObject> {
        //String, Void, List<Trend>
        final String TAG = "AsyncTaskParseJson.java";
        ProgressDialog dialog;
        HttpURLConnection urlConnection;

        StringBuilder sb = new StringBuilder();
        String json;
        JSONObject jsonResponse;
        JSONObject jsonObj = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(OpenReadQrCode.this, "Aguarde",
                    "Enviando Dados do QR CODE lido, Por Favor Aguarde...");

        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String urlString = arg0[0];
            URL url = null;
            try {
                url = new URL(urlString);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            // super.onPostExecute(s);
            dialog.dismiss();

        }

    }




}


