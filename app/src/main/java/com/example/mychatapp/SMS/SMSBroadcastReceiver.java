package com.example.mychatapp.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import androidx.annotation.NonNull;

import com.example.mychatapp.Model.Chat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//https://github.com/google-developer-training/android-fundamentals-phone-sms/blob/master/SmsMessaging/app/src/main/java/com/example/android/smsmessaging/MySmsReceiver.java

public class SMSBroadcastReceiver extends BroadcastReceiver{
    private static final String pdu_type = "pdus";

    DatabaseReference reference;

    //function to send SMS to specific number
    public void sendSMS(String phonenumber, String message){
        try{
            SmsManager smsManager = SmsManager.getDefault();
            //if the message's length is out of the limits, using sendMultipartTextMessage()
            if (message.length() > 160) {
                ArrayList<String> multi_text = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phonenumber, null, multi_text, null, null);
            }else{
                smsManager.sendTextMessage(phonenumber, null, message, null, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //this function called when the BroadcastReceiver is receiving an Intent broadcast
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(pdu_type);
                assert sms != null;
                final SmsMessage[] messages = new SmsMessage[sms.length];

                String smsBody = "";
                String address = "";
                //the purpose of this is to collect received message for official user, since the digital signature is too long and out of the limit(160 characters)
                for (int i = 0; i < sms.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) sms[i]);
                    //the incoming message's content
                    smsBody = smsBody + messages[i].getMessageBody();
                    //the Sender Id of the incoming message
                    address = messages[i].getOriginatingAddress();
                }

                //check if the message is from an Individual user
                reference = FirebaseDatabase.getInstance().getReference("Chats");
                final String finalAddress = address;
                final String finalSmsBody = smsBody;
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            assert chat != null;
                            //if the Sender Id exists in the sent_message table(in firebase realtime database), and nobody used it for the authentication.
                            if (chat.getSender().equals(finalAddress)){
                                if (chat.getStatus().equals("un_used") || chat.getStatus().equals("from_Official")) {
                                    if (chat.getStatus().equals("un_used")){
                                        reference.child(finalAddress).child("status").setValue("used");
                                    }
                                    //send the information(which belongs to this Sender ID) to the Main activity for authentication
                                    Intent intent1 = new Intent();
                                    intent1.setAction("SMS_RECEIVED");
                                    intent1.putExtra("sender", finalAddress);
                                    intent1.putExtra("message", finalSmsBody);
                                    intent1.putExtra("seed", chat.getSecret());
                                    context.sendBroadcast(intent1);
                                    reference.removeEventListener(this);
                                }
                            }else{
                                //if can not find the same Sender ID in the database, still send the information of the incoming message to the Main activity, but set the secret as empty.
                                Intent intent1 = new Intent();
                                intent1.setAction("SMS_RECEIVED");
                                intent1.putExtra("sender", finalAddress);
                                intent1.putExtra("message", finalSmsBody);
                                intent1.putExtra("seed", "");
                                context.sendBroadcast(intent1);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }
    }
}
