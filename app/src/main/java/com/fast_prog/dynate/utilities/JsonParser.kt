package com.fast_prog.dynate.utilities

import android.util.Log

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.HashMap

class JsonParser {

    private var conn: HttpURLConnection? = null
    private val result = StringBuilder()
    private var jObj: JSONObject? = null

    fun makeHttpRequest(url: String, method: String,
                        params: HashMap<String, String>): JSONObject? {
        var url = url

        Log.e("url", url)
        val sbParams = StringBuilder()
        var i = 0
        val charset = "UTF-8"
        for (key in params.keys) {
            try {
                if (i != 0) {
                    sbParams.append("&")
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params[key], charset))

            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            i++
        }
        Log.e("params", sbParams.toString())
        val urlObj: URL
        if (method == "POST") {
            //Log.d("request method is POST", "PSPRT");
            try {
                urlObj = URL(url)

                conn = urlObj.openConnection() as HttpURLConnection

                conn!!.doOutput = true

                conn!!.requestMethod = "POST"

                conn!!.setRequestProperty("Accept-Charset", charset)

                conn!!.readTimeout = 30000
                conn!!.connectTimeout = 35000

                conn!!.connect()

                val paramsString = sbParams.toString()

                val wr = DataOutputStream(conn!!.outputStream)
                wr.writeBytes(paramsString)
                wr.flush()
                wr.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else if (method == "GET") {
            //Log.d("request method is GETY", "GET");
            if (sbParams.length != 0) {
                url += "?" + sbParams.toString()
            }

            try {
                urlObj = URL(url)

                conn = urlObj.openConnection() as HttpURLConnection

                conn!!.doOutput = false

                conn!!.requestMethod = "GET"

                conn!!.setRequestProperty("Accept-Charset", charset)

                conn!!.connectTimeout = 15000

                conn!!.connect()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        try {
            //Receive the response from the server
            val `in` = BufferedInputStream(conn!!.inputStream)
            val reader = BufferedReader(InputStreamReader(`in`))

            var line: String? = null
            while ({ line = reader.readLine(); line }() != null) {
                result.append(line)
            }

            Log.e("JSON_Parser", "result: " + result.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        conn!!.disconnect()

        // try parse the string to a JSON object
        try {
            jObj = JSONObject(result.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON Object
        return jObj
    }
}