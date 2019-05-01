package com.fast_prog.dynate.models

import java.io.Serializable

/**
 * Created by sarathk on 11/9/16.
 */

class RegisterUser : Serializable {

    var name: String? = null
    var nameArabic: String? = null
    var mobile: String? = null
    var mail: String? = null
    var address: String? = null
    //var username: String? = null
    //var password: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var loginMethod: String? = null
    var vModelId: String? = null
    var vModelName: String? = null
    var vCompId: String? = null
    var vCompName: String? = null
    var licenseNo: String? = null
    var licenseNoArabic: String? = null
    var withGlass: Boolean = false
}
