package com.bob.deviceadm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

public class NetworkReceiver
    extends BroadcastReceiver
{
    @Override
    public void onReceive( Context context, Intent intent )
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting())
        {
            Configuration configuration = Configuration.getConfiguration(context);
            if (!configuration.mobileDataEnable)
            {
                disableMobileData(context);
            }
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                if (!disableWifiNotInWhitelist(context, configuration.wifiWhiteList))
                {
                    SyncService.startSync(context);
                }
            }
        }
    }

    private boolean disableWifiNotInWhitelist( Context context, List<Configuration.WifiItem> whitelist )
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        boolean needToDisable = info != null && !inWhiteList(info, whitelist);
        if (needToDisable)
        {
            Toast.makeText(context, info.getSSID() + " is Not an authorized wireless network, disconnectiong... ", Toast.LENGTH_LONG).show();
            wifiManager.disconnect();

            wifiManager.disableNetwork(info.getNetworkId());
            wifiManager.removeNetwork(info.getNetworkId());
            wifiManager.saveConfiguration();
        }
        return needToDisable;
    }

    private boolean inWhiteList( WifiInfo info, List<Configuration.WifiItem> whitelist )
    {
        if (whitelist != null && whitelist.size() > 0)
        {
            String ssid = info.getSSID().replaceAll("\"", "");
            String bssid = info.getBSSID();
            for (Configuration.WifiItem item : whitelist)
            {
                if (!TextUtils.isEmpty(item.ssid) && !item.ssid.equals(ssid))
                {
                    continue;
                }
                if (!TextUtils.isEmpty(item.bssid) && !item.bssid.equals(bssid))
                {
                    continue;
                }

                if (TextUtils.isEmpty(item.ssid) && TextUtils.isEmpty(item.bssid))
                {
                    continue;
                }

                return true;
            }
        }

        return false;
    }

    private void disableMobileData( Context context )
    {
        if (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDataState() == TelephonyManager.DATA_CONNECTED)
        {
            setMobileDataEnabled(context, false);
        }
    }

    private void setMobileDataEnabled( Context context, boolean enabled )
    {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass;
        try
        {
            conmanClass = Class.forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField.get(conman);
            final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }
}
