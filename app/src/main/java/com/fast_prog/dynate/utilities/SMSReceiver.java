package com.fast_prog.dynate.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.fast_prog.dynate.views.ResetPasswordActivity;
import com.fast_prog.dynate.views.VerifyOTPActivity;

/**
 * Created by Ravi on 09/07/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    //private static final String TAG = SMSReceiver.class.getSimpleName();
    private String classname;

    public SMSReceiver(String classname) {
        this.classname = classname;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(Constants.SMS_ORIGIN.toLowerCase())) {
                        return;
                    }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);

                    if (classname.equalsIgnoreCase("verify")) {
                        VerifyOTPActivity.updateData(verificationCode);
                    } else{
                        ResetPasswordActivity.updateData(verificationCode);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Constants.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return null;
    }
}
