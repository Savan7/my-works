package com.valdas.mag2;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;

public class RegistrationActivity extends AppCompatActivity {
    private static SecureRandom random = new SecureRandom();

    String mainpasswordbase64;
    byte[] mainpassword;
    String username;
    String userPassword;
    String imei_nr;
    String saltbase64;
    String response;
    String pattern= "^[a-zA-Z0-9]*$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_main);


        final Button button4 = (Button) findViewById(R.id.button4);
        final EditText usernameEditText = (EditText) findViewById(R.id.editText);
        final EditText pass = (EditText) findViewById(R.id.editText2);
        final EditText confirmpass = (EditText) findViewById(R.id.editText3);
        TelephonyManager imei = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        System.out.println("IMEI: " + imei.getDeviceId());
        imei_nr = imei.getDeviceId();

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassword = pass.getText().toString();
                username = usernameEditText.getText().toString();

                if (isNetworkConnected()) {

                if (username.length() > 0 && pass.getText().toString().length() > 0 && confirmpass.getText().toString().length() > 0) {

                    if(username.matches(pattern)) {



                        if (pass.getText().toString().equals(confirmpass.getText().toString())) {

                            //Cryptography crypto = new Cryptography();
                            //  mainpassword = crypto.deriveKeyPbkdf2(pass.getText().toString());

                            //mainpasswordbase64 = Base64.encodeToString(mainpassword, Base64.DEFAULT);
                            //   saltbase64 = Base64.encodeToString( Base64.DEFAULT);


                            new InitRegistrationTask().execute(username, imei_nr);

                        } else {
                            Toast.makeText(RegistrationActivity.this, "Nesutampa slaptažodžiai", Toast.LENGTH_SHORT).show();
                            //System.out.println("passes: " + pass.getText().toString() + " " + confirmpass.getText().toString());
                        }
                    }
                    else {
                        Toast.makeText(RegistrationActivity.this, "Blogas slapyvardis", Toast.LENGTH_SHORT).show();
                        //System.out.println("passes: " + pass.getText().toString() + " " + confirmpass.getText().toString());
                    }




                } else {
                    Toast.makeText(RegistrationActivity.this, "Nepalikite neužpildytų laukelių", Toast.LENGTH_SHORT).show();
                }
            } else {
                    Toast.makeText(RegistrationActivity.this, "Prisijunkite prie interneto tinklo", Toast.LENGTH_SHORT).show();
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

    public static String toHex(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (byte b : bytes) {
            buff.append(String.format("%02X", b));
        }

        return buff.toString();
    }

    //check username
    public class InitRegistrationTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(RegistrationActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("Tikrinamas vartotojo vardas... Prašome palaukti.");
            Dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String paramValue1 = "regCheckUser";
            String paramValue2 = params[0]; //username
            //String paramValue3 = params[1]; //imei
            String paramValue4; // salt
            String paramValue5; //pass

            try {
                URL url = new URL("https://www.websprendimai.eu/registration.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("firstParamR", paramValue1)
                        .appendQueryParameter("secondParamR", paramValue2);
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

            Dialog.dismiss();

            if (response.equals("0")) {

                Toast.makeText(RegistrationActivity.this, "Vartotojo vardas užimtas", Toast.LENGTH_SHORT).show();
            }
            else if (response.equals("1")){
                //Toast.makeText(RegistrationActivity.this, "Registracija sėkminga", Toast.LENGTH_SHORT).show();
                new RegistrationTask().execute(username, imei_nr);
            }
            else {
                Toast.makeText(RegistrationActivity.this, "Klaida", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class RegistrationTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(RegistrationActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Dialog.setMessage("Kuriama paskyra... Prašome palaukti.");
            Dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String paramValue1 = "reg";
            String paramValue2 = params[0]; //username
            String paramValue3 = params[1]; //imei
            String paramValue4; // salt
            String paramValue5; //pass

          // mainpassword = Cryptography.deriveKeyPbkdf2(paramValue2);

            byte[] salt = new byte[20];
            random.nextBytes(salt);



            byte[] keybytes = Cryptography.deriveKeyPbkdf2(salt, userPassword);

            //mainpasswordbase64 = Base64.encodeToString(mainpassword, Base64.DEFAULT);
            //String[] fields = saltandkeybytes.split("]");
           // paramValue4 = fields[0]; //salt
            //paramValue5 = fields[1]; //pass
            paramValue4 = Base64.encodeToString(salt, Base64.DEFAULT); //salt
            paramValue5 = Base64.encodeToString(keybytes, Base64.DEFAULT); //pass
            System.out.println("salt: "+paramValue4);

            try {
                URL url = new URL("https://www.websprendimai.eu/registration.php");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("firstParamR", paramValue1)
                        .appendQueryParameter("secondParamR", paramValue2)
                        .appendQueryParameter("thirdParamR", paramValue3)
                        .appendQueryParameter("fourthParamR", paramValue4)
                        .appendQueryParameter("fifthParamR", paramValue5);
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

            Dialog.dismiss();
             if (response.equals("1")){
                Toast.makeText(RegistrationActivity.this, "Registracija sėkminga", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(RegistrationActivity.this, "Klaida", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
