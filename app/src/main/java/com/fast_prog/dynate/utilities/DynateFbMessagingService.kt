package com.ralhawameer.fastprogramming.dyna.utilities

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by sarathk on 12/13/16.
 */

class DynateFbMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        if (remoteMessage!!.data.isNotEmpty()) {

        }
    }

}
