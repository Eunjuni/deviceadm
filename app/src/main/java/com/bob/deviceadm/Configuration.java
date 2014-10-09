package com.bob.deviceadm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

public class Configuration
{
    public static class WifiItem
    {
        public WifiItem()
        {

        }

        public WifiItem( String ssid )
        {
            this.ssid = ssid;
        }

        public String ssid;

        public String bssid;
    }

    public boolean mobileDataEnable;

    public String lockPasword;

    public List<WifiItem> wifiWhiteList;

    private static final String KEY_CONF = "configuration";

    private static Configuration sConfiguration;

    public static Configuration getConfiguration( Context context )
    {
        if (sConfiguration == null)
        {
            String json = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_CONF, null);
            if (TextUtils.isEmpty(json))
            {
                sConfiguration = new Configuration();
                sConfiguration.mobileDataEnable = true;
                sConfiguration.lockPasword = "1!2@3#4$abc";
                sConfiguration.wifiWhiteList = new ArrayList<WifiItem>();
                sConfiguration.wifiWhiteList.add(new WifiItem("DCOLCONON"));
                sConfiguration.wifiWhiteList.add(new WifiItem("JTOSTDAC"));
            }
            else
            {
                sConfiguration = new Gson().fromJson(json, Configuration.class);
            }
        }
        return sConfiguration;
    }

    public static void updateConfigration( Context context, String configurationJson )
    {
        sConfiguration = null;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_CONF, configurationJson).apply();
    }
}
