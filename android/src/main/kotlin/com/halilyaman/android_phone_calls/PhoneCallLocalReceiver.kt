package com.halilyaman.android_phone_calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import io.flutter.plugin.common.EventChannel
import java.util.HashMap

class PhoneCallLocalReceiver(private val eventSink: EventChannel.EventSink?) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val map = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getSerializableExtra(AndroidPhoneCallsPlugin.LOCAL_MESSAGE, HashMap::class.java)!! as HashMap<String, String>
            else
                intent.getSerializableExtra(AndroidPhoneCallsPlugin.LOCAL_MESSAGE) as HashMap<String, String>

            eventSink?.success(map);
        }
    }

}