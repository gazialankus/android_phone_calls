
import 'android_phone_calls_platform_interface.dart';

class AndroidPhoneCalls {
  const AndroidPhoneCalls._();

  static Future<void> requestPermissions() {
    return AndroidPhoneCallsPlatform.instance.requestPermissions();
  }

  static Future<bool> checkPermissions() async {
    final result = await AndroidPhoneCallsPlatform.instance.checkPermissions();
    return result == true;
  }
}
