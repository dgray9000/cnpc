package com.graysoda.cnpc.Database;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.graysoda.cnpc.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

class PopulateDatabase extends AsyncTask<String,Void,ArrayList>{
    @Override
    protected ArrayList doInBackground(String... strings) {
        try{

            URL url = new URL(Resources.getSystem().getString(R.string.baseCryptowatchApiUrl));

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpsURLConnection.HTTP_OK){
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
