
import 'package:android_phone_calls/phone_call_event.dart';

import 'android_phone_calls_platform_interface.dart';

class AndroidPhoneCalls {
  const AndroidPhoneCalls._();

  static Future<void> requestPermissions() {
    return AndroidPhoneCallsPlatform.instance.requestPermissions();
  }

  static Future<bool?> checkPermissions() async {
    return await AndroidPhoneCallsPlatform.instance.checkPermissions();
  }

  static Future<String?> getDialerPackageName() {
    return AndroidPhoneCallsPlatform.instance.getDialerPackageName();
  }

  static Stream<PhoneCallEvent> get phoneCallStream {
    return AndroidPhoneCallsPlatform.instance.phoneCallStream;
  }
}
