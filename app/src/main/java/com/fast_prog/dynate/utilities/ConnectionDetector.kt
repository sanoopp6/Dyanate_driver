package com.fast_prog.dynate.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.View

import com.fast_prog.dynate.R

object ConnectionDetector {

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnected
    }

    fun isConnectedOrConnecting(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun errorSnackbar(coordinatorLayout: CoordinatorLayout) {
        val snackbar = Snackbar.make(coordinatorLayout, R.string.UnableToConnect, Snackbar.LENGTH_LONG).setAction(R.string.Ok) { }
        snackbar.show()
    }

}
