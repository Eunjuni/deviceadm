package com.bob.deviceadm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class SyncService
    extends IntentService
{
    private static final int ALARM_TYPE = 1;

    private static final int DELAY_SYNC_SECONDS = 2;

    private static final long INTERVAL_MILLIS = 1 * 60 * 60 * 1000;// 1 hour

    private static final String URL_CONF = "";

    public static void startSync( Context context )
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar time = Calendar.getInstance();
        time.add(Calendar.SECOND, DELAY_SYNC_SECONDS);
        alarmManager.setRepeating(ALARM_TYPE, time.getTimeInMillis(), INTERVAL_MILLIS, PendingIntent.getService(context, 0, new Intent(context, SyncService.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public SyncService()
    {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent( Intent intent )
    {
        if (intent != null)
        {
            NetworkInfo networkInfo = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                String json = downloadUrl(URL_CONF);
                if (!TextUtils.isEmpty(json))
                {
                    Configuration.updateConfigration(getApplicationContext(), json);
                }
            }
        }
    }

    private static String downloadUrl( String myurl )
    {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try
        {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;
        }
        catch (IOException e)
        {
        }
        finally
        {

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return null;
    }

    private static String readIt( InputStream stream, int len )
        throws IOException, UnsupportedEncodingException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
