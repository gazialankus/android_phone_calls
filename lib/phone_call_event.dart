enum PhoneCallEventType {
  incomingCall,
  answeredCall,
  endedCall,
  missedCall,
  unknown;

  static fromString(String str) {
    return switch (str) {
      'incomingCall' => PhoneCallEventType.incomingCall,
      'answeredCall' => PhoneCallEventType.answeredCall,
      'endedCall' => PhoneCallEventType.endedCall,
      'missedCall' => PhoneCallEventType.missedCall,
      _ => PhoneCallEventType.unknown,
    };
  }
}

class PhoneCallEvent {
  PhoneCallEvent({
    required this.eventType,
    required this.phoneNumber,
    required this.callerName,
  });

  PhoneCallEventType eventType;
  String phoneNumber;
  String callerName;

  PhoneCallEvent.fromMap(Map<dynamic, dynamic> map) :
    eventType = PhoneCallEventType.fromString(map['eventType']),
    phoneNumber = map['phoneNumber'],
    callerName = map['callerName'];

  @override
  String toString() {
    return '''PhoneCallEvent
    eventType: $eventType
    phoneNumber: $phoneNumber
    callerName: $callerName 
    ''';
  }
}
