package ricardo.com.fmuinovae;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;

import android.util.Log;
import android.view.View;

import android.widget.ListView;


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

public class RegisteringDataParticipation extends AppCompatActivity implements LocationListener{

    //private Button mButton;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 42;
    private static double latitude;
    private static double longitude;
    private static LocationManager lm;
    private GoogleApiClient client;
    private static  boolean isQuiz;
    private ListView listView1;

    static int isUpLoadQuestion;

    static  int idQuestionFirst;
    static  int idQuestionSecond;
    static  int idQuestionThird;
    static  int idQuestionFourth;

    static  int notaQuestionFirst;
    static  int notaQuestionSecond;
    static  int notaQuestionThird;
    static  int notaQuestionFourth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering_data_participation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundColor(Color.BLUE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Snackbar snackBar= null;
                //isQuiz=true;
                if(isUpLoadQuestion > 3){
                    isQuiz = true;
                }
                Log.e("============================ VALOR ",String.valueOf(isUpLoadQuestion));
                SpannableStringBuilder builder = new SpannableStringBuilder();
                if(!isQuiz) {
                    builder.append(" Responda a pesquisa  depois click para ler o \n QR CODE para marcar a presença.");
                    snackBar = Snackbar.make(view, builder, Snackbar.LENGTH_LONG);
                }else if(isQuiz){
                    snackBar = Snackbar.make(view, builder, Snackbar.LENGTH_LONG);
                    snackBar.setAction("LER QR CODE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { //Retry Code here
                            IntentIntegrator integrator = new IntentIntegrator(RegisteringDataParticipation.this);
                            integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
                        }
                    });
                }
                snackBar.show();

            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Option questions[] = new Option[] {
                        new Option("O que você achou do tema da Palestra ?"),
                        new Option("O que você achou da apresentação/palestrante ?"),
                        new Option("O que você achou do tempo da apresentação ?"),
                        new Option("O que você achou da organização da palestra ?"),

                };
        RadioGroupAdapter adapter = new RadioGroupAdapter(this,R.layout.list_item, questions);
        listView1 = (ListView)findViewById(R.id.listView1);
        listView1.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // GPS está ligado
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
        } else {
            // Solicita ao usuário para ligar o GPS
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("O GPS está desligado, deseja ligar agora?")
                    .setCancelable(false).setPositiveButton(
                    "Sim",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Intent para entrar nas configurações de localização
                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        }
                    });
            alertDialogBuilder.setNegativeButton("Não",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Desliga o GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.d("code", re);
        }

        new AsyncTaskGravaDados().execute("http://ws-fmu.sa-east-1.elasticbeanstalk.com/v1/avaliacao");

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "OpenReadQrCode Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ricardo.com.fmuinovae/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "OpenReadQrCode Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://ricardo.com.fmuinovae/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
            dialog = ProgressDialog.show(RegisteringDataParticipation.this, "Aguarde",
                    "Consultando cadastro, Por Favor Aguarde...");

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
            super.onPostExecute(json);
            dialog.dismiss();



        }

    }




}


