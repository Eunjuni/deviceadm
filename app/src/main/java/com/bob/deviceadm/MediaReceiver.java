package com.bob.deviceadm;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class MediaReceiver
    extends BroadcastReceiver
{
    public MediaReceiver()
    {
    }

    @Override
    public void onReceive( final Context context, Intent intent )
    {
        checkSdcard(context);
    }

    void checkSdcard( final Context context )
    {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_CHECKING))
        {
            new Thread()
                {
                    @Override
                    public void run()
                    {
                        while (true)
                            try
                            {
                                Thread.sleep(20 * 1000);
                                checkSdcard(context);
                                break;
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                    }
                }.start();
            return;
        }
        if (state.equals(Environment.MEDIA_MOUNTED) && !Environment.isExternalStorageEmulated())
        {
            lockDeviceWithNewPassword(context);
        }
    }

    private void lockDeviceWithNewPassword( Context context )
    {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.resetPassword("123456", 0);
        devicePolicyManager.lockNow();
    }
}
