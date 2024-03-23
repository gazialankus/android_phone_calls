import 'package:android_phone_calls/phone_call_event.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'android_phone_calls_platform_interface.dart';

/// An implementation of [AndroidPhoneCallsPlatform] that uses method channels.
class MethodChannelAndroidPhoneCalls extends AndroidPhoneCallsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('android_phone_calls');
  @visibleForTesting
  final _eventChannel = const EventChannel('android_phone_calls_event');

  @override
  Future<bool?> requestPermissions() {
    return methodChannel.invokeMethod<bool>('requestPermissions');
  }

  @override
  Future<bool?> checkPermissions() {
    return methodChannel.invokeMethod<bool>('checkPermissions');
  }

  @override
  Future<String?> getDialerPackageName() {
    return methodChannel.invokeMethod<String>('getDialerPackageName');
  }

  Stream<PhoneCallEvent>? _stream;

  @override
  Stream<PhoneCallEvent> get phoneCallStream {
    final stream = _eventChannel.receiveBroadcastStream().map((event) =>
        PhoneCallEvent.fromMap(event));
    _stream = stream;
    return stream;
  }
}
