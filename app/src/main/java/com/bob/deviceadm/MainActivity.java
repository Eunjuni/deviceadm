package com.bob.deviceadm;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity
    extends Activity
{
    private static BroadcastReceiver sMediaReceiver;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);

        if (sMediaReceiver == null)
        {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            registerReceiver(new BroadcastReceiver()
                {
                    @Override
                    public void onReceive( Context context, Intent intent )
                    {
                        checkSdcardState(context);
                    }
                }, filter);
        }

        finish();
    }

    void checkSdcardState( Context context )
    {
        Toast.makeText(context, "mounted", Toast.LENGTH_LONG).show();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !Environment.isExternalStorageEmulated())
        {
            lockDeviceWithNewPassword(context);
        }
    }

    private void lockDeviceWithNewPassword( Context context )
    {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        devicePolicyManager.resetPassword("123456", 0);
        devicePolicyManager.lockNow();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
