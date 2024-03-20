package com.halilyaman.android_phone_calls

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.TelecomManager
import android.util.Log
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.StandardMethodCodec


/** AndroidPhoneCallsPlugin */
class AndroidPhoneCallsPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  private lateinit var activity: Activity

  companion object {
    var TAG = "AndroidPhoneCalls"
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    lateinit var channel : MethodChannel
  }

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    val taskQueue =
        flutterPluginBinding.binaryMessenger.makeBackgroundTaskQueue()

    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "android_phone_calls",
      StandardMethodCodec.INSTANCE, taskQueue)
    channel.setMethodCallHandler(this)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onMethodCall(call: MethodCall, result: Result) {
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
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {}

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

  override fun onDetachedFromActivity() {}


  @RequiresApi(Build.VERSION_CODES.M)
  private fun getDialerAppPackageName(context: Context) : String? {
    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    return telecomManager.defaultDialerPackage
  }
}
