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
import android.provider.Settings;
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


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisteringDataParticipation extends AppCompatActivity implements LocationListener{

    //private Button mButton;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 42;
    private static String latitude;
    private static String longitude;
    private static LocationManager lm;
    private GoogleApiClient client;
    static  boolean isQuiz;
    private ListView listView1;




    static int isUpLoadQuestion;
    /*static int isUpLoadQuestion2;
    static boolean isUpLoadQuestion3;
    static boolean isUpLoadQuestion4;
*/
    private static String imei;
    private static String codPalestra;
    private static String periodoPalestra;
    private static String diaPalestra;
    private static String tipoEvento;
    private static String dataEnvio;
    private static String horaEnvio;

    static  int idQuestionFirst;
    static  int idQuestionSecond;
    static  int idQuestionThird;
    static  int idQuestionFourth;

    static  int notaQuestionFirst;
    static  int notaQuestionSecond;
    static  int notaQuestionThird;
    static  int notaQuestionFourth;

    private static String ra;

    private  static  GPSTracker gps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering_data_participation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();


        gps = new GPSTracker(this);

        if(params!=null) {
            ra = params.getString("ra");
        }



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
               // Log.e("============================ VALOR ",String.valueOf(isUpLoadQuestion));
                SpannableStringBuilder builder = new SpannableStringBuilder();
                if(!isQuiz) {
                    builder.append(" Responda a pesquisa  depois click para ler o QR CODE para marcar presença.");
                    //builder.append(isQuiz);
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

        imei = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

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
            alertDialogBuilder.setMessage("O GPS está desligado, é preciso a localização para marcar a prese" +
                    "nça no curso, liqar o GPS?")
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
            String s = scanResult.getContents();
//            Log.e(" ================  a=1, b='N',c=3, d='P'   ================================ ", re);
            //s.concat(s.trim());
            String notSpace =s.replaceAll(" ","");
            char[] values = notSpace.toCharArray();
            //String[] fields = values=s.split(",");
           /* for (int i =0 ; i < values.length; i ++){
                Log.i(" ================  a=1, b='N',c=3, d='P'   ================================ " +
                        "", String.valueOf(String.valueOf(values[i])));

            }*/

            codPalestra=String.valueOf(values[2]);//1
            periodoPalestra=String.valueOf(values[7]);//n
            diaPalestra=String.valueOf(values[12]);//3
            tipoEvento=String.valueOf(values[17]);//p
        }


        new AsyncTaskGravaDados().execute("http://ws-fmu.sa-east-1.elasticbeanstalk.com/v1/pesquisa");

        AlertDialog.Builder builder = new AlertDialog.Builder(
                RegisteringDataParticipation.this).setTitle("Atenção")
                .setMessage("Cadastro realizado com sucesso ...")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               finish();
                            }
                        });
        builder.create().show();

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

        latitude= String.valueOf(location.getLongitude());
        latitude =  String.valueOf(location.getLatitude());


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
            HttpResponse response = null;
            //double latitude = gps.getLatitude();
            //double longitude = gps.getLongitude();

            try {
                HttpClient client = new DefaultHttpClient();
                URL url = new URL(urlString);
                HttpPost httpPost = new HttpPost(url.toURI());


                JSONObject jsonParam = new JSONObject();
                jsonParam.put("ra",ra);
                jsonParam.put("imei",imei);

                jsonParam.put("longitude",String.valueOf(gps.getLongitude()));
                jsonParam.put("latitude",String.valueOf(gps.getLatitude()));

                /*jsonParam.put("longitude",longitude);
                jsonParam.put("latitude",latitude);*/

                jsonParam.put("codPalestra",codPalestra); // a
                jsonParam.put("periodoPalestra",periodoPalestra); //b
                jsonParam.put("diaPalestra",diaPalestra); //c
                jsonParam.put("tipoEvento",tipoEvento); //d

                jsonParam.put("idPergunta1",idQuestionFirst);
                jsonParam.put("notaPergunta1",notaQuestionFirst);

                jsonParam.put("idPergunta2",idQuestionSecond);
                jsonParam.put("notaPergunta2",notaQuestionSecond);

                jsonParam.put("idPergunta3",idQuestionThird);
                jsonParam.put("notaPergunta3",notaQuestionThird);

                jsonParam.put("idPergunta4",idQuestionFourth);
                jsonParam.put("notaPergunta4",notaQuestionFourth);






                httpPost.setEntity(new StringEntity(jsonParam.toString(), "UTF-8"));

                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Accept-Encoding", "application/json");
                httpPost.setHeader("Accept-Language", "en-US");


                response =client.execute(httpPost);

              //  Log.e("===========================",jsonParam.toString());


            } catch (IOException e) {
                e.printStackTrace();

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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


