package com.messages.messagesbroadcastreceiver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Header;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.messages.messagesbroadcastreceiver.networkUtils.Connector;
import com.messages.messagesbroadcastreceiver.utils.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SmsListener extends BroadcastReceiver {

    String mMessageFrom;
    String formattedDate;
    SmsMessage[] mMessageArray;
    Bundle mBundle;
    Object[] mMessageBytes;
    String mMessageBody;
    Date mDate;
    Connector mConnector;
    HashMap<String,String> mHashMap;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                mBundle = intent.getExtras();
                if (mBundle != null) {
                    mMessageBytes = (Object[]) mBundle.get("pdus");
                    if (mMessageBytes != null) {
                        mMessageArray = new SmsMessage[mMessageBytes.length];
                        for (int i = 0; i < mMessageArray.length; i++) {
                            mMessageArray[i] = SmsMessage.createFromPdu((byte[]) mMessageBytes[i]);
                            mMessageFrom = mMessageArray[i].getOriginatingAddress();
                            mMessageBody = mMessageArray[i].getMessageBody();
                        }
                        if (mMessageBody.matches("[0-9]+") && mMessageBody.length() > 2) {
                            mDate = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            formattedDate = df.format(mDate);
                            mHashMap = new HashMap<>();
                            mHashMap.put("Serial", mMessageBody);
                            mHashMap.put("Date", formattedDate);
                            mHashMap.put("Mobile", mMessageFrom);
                            Helper.writeToLog(mMessageBody);
                            Helper.writeToLog(formattedDate);
                            Helper.writeToLog(mMessageFrom);
                            mConnector = new Connector(context, new Connector.LoadCallback() {
                                @Override
                                public void onComplete(String tag, String response) {
                                    mConnector.cancelAllRequests("SmsListener");
                                    Helper.writeToLog(response);
                                    if (response.contains("Valid")){
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                        int numberOfMessages = preferences.getInt("messagesCount",0);
                                        numberOfMessages++;
                                        preferences.edit().putInt("messagesCount", numberOfMessages).apply();
                                    } else {
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                        int numberOfNotValidMessages = preferences.getInt("messagesNotValidCount",0);
                                        numberOfNotValidMessages++;
                                        preferences.edit().putInt("messagesNotValidCount", numberOfNotValidMessages).apply();
                                    }
                                }
                            }, new Connector.ErrorCallback() {
                                @Override
                                public void onError(VolleyError error) {
                                    if (error instanceof NoConnectionError) {
                                        Toast.makeText(context, "No Connection", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            String URL = "http://namaafeed.info:3000/checksticker";
                            mConnector.setMap(mHashMap);
                            mConnector.getRequest("SmsListener", URL);
                        }
                    }
                }
            }
        }
    }
}
