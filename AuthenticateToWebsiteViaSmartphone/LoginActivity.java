package com.valdas.mag2;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {
    String imei_nr;
    String passLogin;
    String tokenString;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        final EditText passLoginEditText = (EditText) findViewById(R.id.editText4);
        final EditText token = (EditText) findViewById(R.id.editText5);
        final Button loginButton = (Button) findViewById(R.id.button5);
        final TextView status = (TextView) findViewById(R.id.textView6);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        TelephonyManager imei = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei_nr = imei.getDeviceId();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String x =  Cryptography.deriveKeyPbkdf2(passLogin.getText().toString());
                //System.out.println("String x: " + x);
                status.setText("");
                passLogin = passLoginEditText.getText().toString();
                tokenString = token.getText().toString();
                if (isNetworkConnected()) {
                    if (passLogin.length() > 0) {

                        if (tokenString.length() > 0) {
                             new LoginTask().execute(tokenString, imei_nr);
                             //Dialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Įveskite autentifikacijos kodą", Toast.LENGTH_SHORT).show();
                            token.requestFocus();
                            imm.showSoftInput(token, InputMethodManager.SHOW_IMPLICIT);
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Įveskite slaptažodį", Toast.LENGTH_SHORT).show();
                        passLoginEditText.requestFocus();
                        imm.showSoftInput(passLoginEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Prisijunkite prie interneto tinklo", Toast.LENGTH_SHORT).show();
                }
            }


        });


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

    //get salt
    public class LoginTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);
        String saltbase64;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("Gaunami duomenys... Prašome palaukti...");
            Dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String paramValue1 = "log";
            String paramValue2 = params[0];
            String paramValue3 = params[1];


            try {
                URL url = new URL("https://www.websprendimai.eu/login.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("firstParamL", paramValue1)
                        .appendQueryParameter("secondParamL", paramValue2)
                        .appendQueryParameter("thirdParamL", paramValue3);

                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                System.out.println(response);
                conn.disconnect();
                System.out.println("disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // L.s(Main2Activity.this,"All done");
//String res = response;
            Dialog.dismiss();
            //Toast.makeText(LoginActivity.this, "All done", Toast.LENGTH_SHORT).show();
            //System.out.println("res: " + response);
           // String tom = response;
            if (response.equals("0")) {
                Toast.makeText(LoginActivity.this, "Neteisingas arba nebegaliojantis autentifikacijos kodas", Toast.LENGTH_SHORT).show();
            }
            else if (response.equals("3")){
                Toast.makeText(LoginActivity.this, "Nesutampa IMEI", Toast.LENGTH_SHORT).show();
            }
            else {
                    new AuthTask().execute(passLogin, imei_nr, response, tokenString);
            }
        }

    }
    public class AuthTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);
        String generatedpasswordbase64;
        byte [] generatedpasswordkeybytes;
       // String response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("Vykdoma autentifikacija... Prašome palaukti...");
            Dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String paramValue1 = "auth";
            String paramValue2 = params[0]; //passLogin
            String paramValue3 = params[1]; //imei
            String paramValue4 = params[2]; //salt
            String paramValue5 = params[3]; //token

           byte [] paramValue4Base64 = Base64.decode(paramValue4, Base64.DEFAULT);

            System.out.println("Auth: passLogin:"+paramValue2+"salt:"+paramValue4);

            generatedpasswordkeybytes = Cryptography.deriveKeyPbkdf2(paramValue4Base64, paramValue2);
            generatedpasswordbase64 = Base64.encodeToString(generatedpasswordkeybytes, Base64.DEFAULT);
            long start = System.currentTimeMillis();
            try {
                URL url = new URL("https://www.websprendimai.eu/auth.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("firstParamA", paramValue1) //auth
                        .appendQueryParameter("secondParamA", generatedpasswordbase64) //generatedpassword
                        .appendQueryParameter("thirdParamA", paramValue3) //imei

                .appendQueryParameter("fourthParamA", paramValue5); //token

                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                long elapsed = System.currentTimeMillis() - start;
                //Dialog.dismiss();
                Log.d("bcrypt", String.format("bcrypt serverijy užtruko %d [ms].",
                        elapsed));
                System.out.println(response);
                conn.disconnect();
                System.out.println("disconnected");
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // L.s(Main2Activity.this,"All done");

            Dialog.dismiss();

            if (response.equals("0")) {

                Toast.makeText(LoginActivity.this, "Slapažodis neteisingas", Toast.LENGTH_SHORT).show();
            }
            else if (response.equals("1")){
                Toast.makeText(LoginActivity.this, "Autentifikacija sėkminga", Toast.LENGTH_SHORT).show();
                        }
            else {
                Toast.makeText(LoginActivity.this, "Klaida", Toast.LENGTH_SHORT).show();
                System.out.println("Bcrypt took: " + response);
            }

            //System.out.println("res authentication: "+res);

        }

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
