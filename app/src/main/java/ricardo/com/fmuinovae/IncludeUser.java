package ricardo.com.fmuinovae;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.provider.Settings.System;
import android.view.WindowManager;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONObject;


import java.net.HttpURLConnection;
import java.net.URL;



public class IncludeUser extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String androidID;
    public static final int REQUEST_CODE = 1;

    private boolean isSave;

    private EditText txtNome;
    private EditText txtRa;
    private EditText txtCurso;
    private EditText txtSemestre;
    private String nome;
    private String ra;
    private String curso;
    private String semestre;


    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

       androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        /*new AsyncTaskParseJson().execute("https://api.github.com/users/dmnugent80/repos");*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

         txtNome = (EditText)findViewById(R.id.name);
         txtRa = (EditText)findViewById(R.id.ra);
         txtCurso = (EditText)findViewById(R.id.curso);
         txtSemestre = (EditText)findViewById(R.id.semestre);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

       /* txtNome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(txtNome.length() == 0 || txtNome.equals("")) {
                        txtNome.requestFocus();
                        return;
                    }
                }
            }
        });*/

        txtSemestre.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || actionId == EditorInfo.IME_ACTION_DONE) {
                    register(v);
                }

                return false;
            }
        });




    }


    public class AsyncTaskParseJson extends AsyncTask<String, String, JSONObject> {
                                                  //String, Void, List<Trend>
        final String TAG = getLocalClassName();
        ProgressDialog dialog;
        HttpURLConnection connection;

        StringBuilder sb = new StringBuilder();
        JSONObject  jsonRootObject = null;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(IncludeUser.this, "Aguarde",
                    "enviando dados para cadastrar , Por Favor Aguarde...");

            nome = txtNome.getText().toString().trim();
            ra =   txtRa.getText().toString().trim();
            curso =txtCurso.getText().toString().trim();
            semestre = txtSemestre.getText().toString().trim();

        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String urlString = arg0[0];
            HttpResponse response = null;

            try {

                HttpClient  client = new DefaultHttpClient();

                URL url = new URL(urlString);
                HttpPost httpPost = new HttpPost(url.toURI());


                JSONObject jsonParam = new JSONObject();
                jsonParam.put("nome",nome);
                jsonParam.put("ra",ra);
                jsonParam.put("curso",curso);
                jsonParam.put("semestre",semestre);
                jsonParam.put("imei",androidID);

                httpPost.setEntity(new StringEntity(jsonParam.toString(), "UTF-8"));

                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Accept-Encoding", "application/json");
                httpPost.setHeader("Accept-Language", "en-US");
                response =client.execute(httpPost);

                if(response != null){
                    isSave = true;

                }



            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
               // connection.disconnect();

            }
            return jsonRootObject;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            dialog.dismiss();
            if(isSave) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        IncludeUser.this).setTitle("Atenção")
                        .setMessage("Cadastro realizado com sucesso ...")
                        .setPositiveButton("OK", null);
                builder.create().show();

            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        IncludeUser.this).setTitle("Atenção")
                        .setMessage("Não foi possivel salvar as informções...")
                        .setPositiveButton("OK", null);
                builder.create().show();
            }
         }

        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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

    public void register(View view) {
        new AsyncTaskParseJson().execute("http://ws-fmu.sa-east-1.elasticbeanstalk.com/v1/usuarios");
        finish();
        Intent register = new Intent(this,RegisteringDataParticipation.class);
        startActivity(register);
    }

    public void  testeQrCode(View view) {
        //androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //Log.i("IMEI >>>>>>>>>>>>>>>>>>>>>>>>>>> ",androidID);
        Intent intent = new Intent(this,OpenReadQrCode.class);
        startActivity(intent);



    }
}
