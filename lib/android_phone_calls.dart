
import 'android_phone_calls_platform_interface.dart';

class AndroidPhoneCalls {
  const AndroidPhoneCalls._();

  static Future<void> requestPermissions() {
    return AndroidPhoneCallsPlatform.instance.requestPermissions();
  }

  static Future<bool?> checkPermissions() async {
    return await AndroidPhoneCallsPlatform.instance.checkPermissions();
  }

  static void setPhoneCallListenerFor({
    void Function(String? phoneNumber, String? callerName)? onIncomingCall,
    void Function()? onCallAnswered,
    void Function()? onCallEnded,
    void Function()? onMissedCall,
    required Object forObject,
  }) {
    AndroidPhoneCallsPlatform.instance.setPhoneCallListenerFor(
      onIncomingCall: onIncomingCall,
      onCallAnswered: onCallAnswered,
      onCallEnded: onCallEnded,
      onMissedCall: onMissedCall,
      forObject: forObject,
    );
  }

  static void clearPhoneCallListenerFor({required Object forObject}) {
    AndroidPhoneCallsPlatform.instance.clearPhoneCallListenerFor(
      forObject: forObject,
    );
  }


  static Future<String?> getDialerPackageName() {
    return AndroidPhoneCallsPlatform.instance.getDialerPackageName();
  }
}
