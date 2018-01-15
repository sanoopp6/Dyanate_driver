package com.fast_prog.dynate.models

import android.graphics.Bitmap

import java.io.Serializable

/**
 * Created by sarathk on 4/22/17.
 */

class UploadFiles : Serializable {

    var bm1: Bitmap? = null
    var base64Encoded1: String? = null
    var imageName1: String? = null

    var bm2: Bitmap? = null
    var base64Encoded2: String? = null
    var imageName2: String? = null

    var bm3: Bitmap? = null
    var base64Encoded3: String? = null
    var imageName3: String? = null

    var bm4: Bitmap? = null
    var base64Encoded4: String? = null
    var imageName4: String? = null
}
