package com.halilyaman.android_phone_calls

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log


var isRinging = false
var isAnswered = false

class PhoneCallHandler : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(AndroidPhoneCallsPlugin.TAG, "onReceive")
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: return
        Log.d(AndroidPhoneCallsPlugin.TAG, "will get caller name")
        val callerName = getContactDisplayNameByNumber(context, phoneNumber) ?: ""
        Log.d(AndroidPhoneCallsPlugin.TAG, "phoneNumber: $phoneNumber")
        val msg = when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                // Incoming call
                Log.d(AndroidPhoneCallsPlugin.TAG, "Incoming call...")
                isRinging = true;
                hashMapOf("eventType" to "incomingCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                // Call answered
                if (isRinging) {
                    Log.d(AndroidPhoneCallsPlugin.TAG, "Answered call.")
                    isAnswered = true
                    isRinging = false
                    hashMapOf("eventType" to "answeredCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
                } else if (!isAnswered) {
                    Log.d(AndroidPhoneCallsPlugin.TAG, "Placing call.")
                    hashMapOf(
                        "eventType" to "placingCall",
                        "phoneNumber" to phoneNumber,
                        "callerName" to callerName
                    )
                } else {
                    hashMapOf(
                        "eventType" to "callOnHold",
                        "phoneNumber" to phoneNumber,
                        "callerName" to callerName
                    )
                }
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                if (isAnswered) {
                    // Call ended
                    Log.d(AndroidPhoneCallsPlugin.TAG, "Ended call.")
                    isAnswered = false
                    hashMapOf("eventType" to "endedCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
                } else {
                    // Call missed or rejected
                    Log.d(AndroidPhoneCallsPlugin.TAG, "Missed/Rejected call.")
                    isRinging = false
                    hashMapOf("eventType" to "missedCall", "phoneNumber" to phoneNumber, "callerName" to callerName)
                }
            }
            else -> hashMapOf("eventType" to "unknown", "phoneNumber" to phoneNumber, "callerName" to callerName)
        }

        val intent = Intent(AndroidPhoneCallsPlugin.LOCAL_INTENT)
        intent.putExtra(AndroidPhoneCallsPlugin.LOCAL_MESSAGE, msg)
        context.sendBroadcast(intent)
    }

    // https://stackoverflow.com/a/16462895/679553
    private fun getContactDisplayNameByNumber(context: Context, number: String?): String {
        Log.d(AndroidPhoneCallsPlugin.TAG, "Caller: will try $number")
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        var name = "?"
        val contentResolver: ContentResolver = context.contentResolver
        val contactLookup = contentResolver.query(
            uri, arrayOf(
                BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME
            ),
            null, null, null
        )
        try {
            if (contactLookup != null && contactLookup.count > 0) {
                contactLookup.moveToNext()
                val columnIndex = contactLookup
                    .getColumnIndex(ContactsContract.Data.DISPLAY_NAME)
                name = if (columnIndex >= 0) {
                    contactLookup.getString(
                        columnIndex
                    )
                } else {
                    Log.d(AndroidPhoneCallsPlugin.TAG, "No such column")
                    ""
                }
            }
        } finally {
            contactLookup?.close()
        }
        return name
    }

}