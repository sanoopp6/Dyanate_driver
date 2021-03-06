package com.fast_prog.dynate.utilities

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

/**
 * Created by sarathk on 1/19/18.
 */

class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) : InputFilter {

    private var mPattern: Pattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {

        val matcher = mPattern.matcher(dest)

        return if (!matcher.matches()) "" else null

    }

}
