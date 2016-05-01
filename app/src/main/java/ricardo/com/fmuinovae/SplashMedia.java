package ricardo.com.fmuinovae;

/**
 * Created by root on 27/04/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AlertDialog;
import android.util.Log;

import android.provider.Settings;
import android.provider.Settings.System;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;


public class SplashMedia extends Activity {
    // Timer da splash screen
    private static int SPLASH_TIME_OUT = 4000;
    private boolean isIncludeUser;
    private static String ra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new AsyncTaskParseJson().execute("http://ws-fmu.sa-east-1.elasticbeanstalk.com/v1/usuarios/imei/"+System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
        //new AsyncTaskParseJson().execute("http://ws-fmu.sa-east-1.elasticbeanstalk.com/v1/usuarios/ra/299999999");

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                if(isIncludeUser) {
                    Intent i = new Intent(SplashMedia.this, RegisteringDataParticipation.class);
                   // Intent i = new Intent(SplashMedia.this, OpenReadQrCode.class);
                    Bundle params = new Bundle();
                    params.putString("ra", ra);
                    i.putExtras(params);
                    startActivity(i);


                }else{
                    Intent i = new Intent(SplashMedia.this, IncludeUser.class);
                    startActivity(i);
                }

               // finish();
            }
        }, SPLASH_TIME_OUT);

    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public class AsyncTaskParseJson extends AsyncTask<String, String, JSONObject> {
        //String, Void, List<Trend>
        final String TAG = getLocalClassName();
        ProgressDialog dialog;
        HttpURLConnection urlConnection;

        StringBuilder sb = new StringBuilder();
        JSONObject  jsonRootObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(SplashMedia.this, "Aguarde",
                    "Validando cadastro do User, Por Favor Aguarde...");



        }


        @Override
        protected JSONObject doInBackground(String... arg0) {
            String urlString = arg0[0];

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                //Log.e(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>   ",String.valueOf(urlConnection.getResponseCode()));
                if(urlConnection.getResponseCode() == 200) {
                    InputStream is = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader in = new BufferedReader(new InputStreamReader(is));

                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        // Log.d(TAG, (String) sb.toString());
                    }

                    jsonRootObject = new JSONObject(sb.toString());
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            SplashMedia.this).setTitle("Atenção")
                            .setMessage("Falha com a internet ...")
                            .setPositiveButton("OK", null);
                    builder.create().show();

                }
            }catch( Exception e) {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(
                        SplashMedia.this).setTitle("Atenção")
                        .setMessage("Falha na consulta da conexão ...")
                        .setPositiveButton("OK", null);
                builder.create().show();*/

                java.lang.System.out.println(e.getMessage());
            }
            finally {
                urlConnection.disconnect();

            }
            return jsonRootObject;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            dialog.dismiss();
            if(json!=null) {
                try {
                    JSONArray jsonArray = jsonRootObject.optJSONArray("request");
                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.i("============================================== ",jsonObject.optString("ra").toString());
                    if(Integer.parseInt(jsonObject.optString("ra").toString())!=0 ){
                        ra = jsonObject.optString("ra").toString();
                        isIncludeUser = true;
                         }
                    }

                } catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            SplashMedia.this).setTitle("Atenção")
                            .setMessage("Falha na consulta na conexão ...")
                            .setPositiveButton("OK", null);
                    builder.create().show();
                }
            }
        }

    }

}
