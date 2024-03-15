import 'package:flutter/material.dart';
import 'package:android_phone_calls/android_phone_calls.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  String callState = "No Call";
  String callState2 = "No Call";

  Object object2 = Object();

  @override
  void initState() {
    super.initState();
    AndroidPhoneCalls.setPhoneCallListenerFor(
      forObject: this,
      onIncomingCall: (phone, name) {
        setState(() {
          callState = "phone: $phone, name: $name";
        });
      },
      onCallAnswered: () {
        setState(() {
          callState = "Call answered";
        });
      },
      onCallEnded: () {
        setState(() {
          callState = "Call ended";
        });
      },
      onMissedCall: () {
        setState(() {
          callState = "Missed call";
        });
      },
    );
    AndroidPhoneCalls.setPhoneCallListenerFor(
      forObject: object2,
      onIncomingCall: (phone, name) {
        setState(() {
          callState2 = "phone: $phone, name: $name";
        });
      },
      onCallAnswered: () {
        setState(() {
          callState2 = "Call answered";
        });
      },
      onCallEnded: () {
        setState(() {
          callState2 = "Call ended";
        });
      },
      onMissedCall: () {
        setState(() {
          callState2 = "Missed call";
        });
      },
    );
    AndroidPhoneCalls.getDialerPackageName().then((value) => print('Dialer app: $value'));
  }

  @override
  void dispose() {
    AndroidPhoneCalls.clearPhoneCallListenerFor(forObject: object2);
    AndroidPhoneCalls.clearPhoneCallListenerFor(forObject: this);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              TextButton(
                onPressed: () {
                  AndroidPhoneCalls.requestPermissions();
                },
                child: const Text("Request Permissions"),
              ),
              const Spacer(),
              Text(callState),
              const Spacer(),
              Text(callState2),
              const Spacer(),
            ],
          ),
        ),
      ),
    );
  }
}
