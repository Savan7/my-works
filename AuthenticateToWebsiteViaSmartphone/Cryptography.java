package com.valdas.mag2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Valdas on 2016-04-17.
 */
public class Cryptography {
    private static final String TAG = Cryptography.class.getSimpleName();
    public static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int PKCS5_SALT_LENGTH = 160/8;
    private static String DELIMITER = "]";

    private static int KEY_LENGTH = 160;
    // minimum values recommended by PKCS#5, increase as necessary
    private static int ITERATION_COUNT = 100000;


    private static SecureRandom random = new SecureRandom();


    public static byte [] deriveKeyPbkdf2(byte[] salt, String password) {

        try {

            //Dialog.setMessage("Stiprinimas slaptažodis... Prašome palaukti...");
            //Dialog.show();
            long start = System.currentTimeMillis();
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            long elapsed = System.currentTimeMillis() - start;
            Log.d(TAG, "key bytes to hex: " + toHex(keyBytes));
            Log.d(TAG, "key bytes to base64: " + toBase64(keyBytes));

            //SecretKey result = new SecretKeySpec(keyBytes, "AES");
            //Log.d(TAG, "key bytes SecretKeyResult: " + result);

            //Dialog.dismiss();
            Log.d(TAG, String.format("PBKDF2 key derivation užtruko %d [ms].",
                    elapsed));


            //return String.format("%s%s%s", toBase64(salt), DELIMITER, toBase64(keyBytes));

            //return toBase64(keyBytes);
            return keyBytes;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }


       public static String toHex(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (byte b : bytes) {
            buff.append(String.format("%02X", b));
        }

        return buff.toString();
    }

    public static String toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static byte[] fromBase64(String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }


}
