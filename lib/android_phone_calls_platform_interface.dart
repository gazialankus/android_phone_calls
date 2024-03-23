import 'package:android_phone_calls/phone_call_event.dart';
import 'package:flutter/services.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'android_phone_calls_method_channel.dart';

abstract class AndroidPhoneCallsPlatform extends PlatformInterface {
  /// Constructs a AndroidPhoneCallsPlatform.
  AndroidPhoneCallsPlatform() : super(token: _token);

  static final Object _token = Object();

  static AndroidPhoneCallsPlatform _instance = MethodChannelAndroidPhoneCalls();

  /// The default instance of [AndroidPhoneCallsPlatform] to use.
  ///
  /// Defaults to [MethodChannelAndroidPhoneCalls].
  static AndroidPhoneCallsPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AndroidPhoneCallsPlatform] when
  /// they register themselves.
  static set instance(AndroidPhoneCallsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool?> requestPermissions() {
    throw UnimplementedError('requestPermissions() has not been implemented.');
  }

  Future<bool?> checkPermissions() {
    throw UnimplementedError('checkPermissions() has not been implemented.');
  }

  Future<String?> getDialerPackageName() {
    throw UnimplementedError('getDialerAppPackages() has not been implemented.');
  }

  Stream<PhoneCallEvent> get phoneCallStream {
    throw UnimplementedError('phoneCallStream has not been implemented.');
  }

}
