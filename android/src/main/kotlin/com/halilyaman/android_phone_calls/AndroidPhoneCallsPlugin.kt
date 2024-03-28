package com.halilyaman.android_phone_calls

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.StandardMethodCodec


/** AndroidPhoneCallsPlugin */
class AndroidPhoneCallsPlugin: FlutterPlugin, MethodCallHandler, ActivityAware,
  EventChannel.StreamHandler {
  private lateinit var context: Context
  private var phoneCallLocalReceiver: PhoneCallLocalReceiver? = null
  private lateinit var activity: Activity

  companion object {
    const val TAG = "AndroidPhoneCallsPlugin"
    const val CHANNEL_NAME = "android_phone_calls"
    const val EVENT_CHANNEL_NAME = "android_phone_calls_event"
    const val LOCAL_INTENT = "com.halilyaman.android_phone_calls.localintent"
    const val LOCAL_MESSAGE = "local_message"
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    lateinit var channel : MethodChannel
    // Event channel lets people subscribe to it
    lateinit var eventChannel : EventChannel
  }

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG, "onAttachedToEngine")
    val taskQueue =
        flutterPluginBinding.binaryMessenger.makeBackgroundTaskQueue()
    Log.d(TAG, "All channels use taskqueue")

    context = flutterPluginBinding.applicationContext

    channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME,
      StandardMethodCodec.INSTANCE, taskQueue)
    channel.setMethodCallHandler(this)
    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, EVENT_CHANNEL_NAME,
      StandardMethodCodec.INSTANCE, taskQueue)
    eventChannel.setStreamHandler(this)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onMethodCall(call: MethodCall, result: Result) {
    Log.d("AndroidPhone", "onMethodCall ${call.method}")
    when (call.method) {
      "requestPermissions" -> {
        PermissionHelper.requestPhoneCallPermissions(activity)
        result.success(true)
      }
      "checkPermissions" -> {
        val permissionsGranted = PermissionHelper.checkPhoneCallPermissions(activity)
        result.success(permissionsGranted)
      }
      "getDialerPackageName" -> {
        val packageName = getDialerAppPackageName(activity as Context)
        Log.d(TAG, "Dialer App: $packageName")
        result.success(packageName)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    Log.d(TAG, "onDetachedFromEngine")
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    Log.d(TAG, "onAttachedToActivity")
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    Log.d(TAG, "onDetachedFromActivityForConfigChanges (blank)")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    Log.d(TAG, "onReattachedToActivityForConfigChanges (blank)")
  }

  override fun onDetachedFromActivity() {
    Log.d(TAG, "onDetachedFromActivity (blank)")
  }


  @RequiresApi(Build.VERSION_CODES.M)
  private fun getDialerAppPackageName(context: Context) : String? {
    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    return telecomManager.defaultDialerPackage
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    val intentFilter = IntentFilter()
    intentFilter.addAction(LOCAL_INTENT)
    phoneCallLocalReceiver = PhoneCallLocalReceiver(events)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      context.registerReceiver(phoneCallLocalReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
    } else {
      context.registerReceiver(phoneCallLocalReceiver, intentFilter)
    }
    val listenerIntent: Intent = Intent(
      context,
      PhoneCallLocalReceiver::class.java
    )
    context.startService(listenerIntent)
    Log.i(TAG, "Started the phone call tracking service.")

  }

  override fun onCancel(arguments: Any?) {
    context.unregisterReceiver(phoneCallLocalReceiver);
    phoneCallLocalReceiver = null;
  }
}
