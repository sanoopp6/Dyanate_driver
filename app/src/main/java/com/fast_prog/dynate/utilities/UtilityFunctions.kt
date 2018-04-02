package com.fast_prog.dynate.utilities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.fast_prog.dynate.R
import com.fast_prog.dynate.R.id.circular_progress_bar
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

/**
 * Created by sarathk on 8/13/17.
 */

class UtilityFunctions {

    companion object {
        private var dialog: Dialog? = null
        private var anim: ObjectAnimator? = null

        fun showProgressDialog(context: Context) {
            dialog = Dialog(context)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.layout_circular_progress_dialog)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            anim = ObjectAnimator.ofInt(circular_progress_bar, "progress", 0, 100)
            anim!!.interpolator = DecelerateInterpolator()
            anim!!.repeatCount = ValueAnimator.INFINITE
            anim!!.duration = 2000
            anim!!.start()
            dialog!!.setCancelable(false)
            dialog!!.show()
        }

        fun dismissProgressDialog() {
            if (dialog != null && dialog!!.isShowing) { dialog!!.dismiss() }
            if (anim != null) { anim!!.end() }
        }

        fun showAlertOnActivity(context: Context, message: String, okButtonMsg: String, cancelButtonMsg: String,
                                showCancelButton: Boolean, setCancelable: Boolean, actionOk: () -> Unit, actionCancel: () -> Unit): AlertDialog? {
            val builder = AlertDialog.Builder(context)
            val inflaterAlert = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewDialog = inflaterAlert.inflate(R.layout.alert_dialog, null)
            builder.setView(viewDialog)
            val dialog = builder.create()

            val buttonCancel = viewDialog.findViewById<Button>(R.id.btn_cancel)
            val buttonOk = viewDialog.findViewById<Button>(R.id.btn_ok)
            val textViewAlert = viewDialog.findViewById<TextView>(R.id.txt_alert)

            textViewAlert.text = message
            buttonOk.text = okButtonMsg
            buttonCancel.text = cancelButtonMsg

            buttonOk.setOnClickListener {
                dialog.dismiss()
                actionOk()
            }

            if (showCancelButton) {
                buttonCancel.visibility = View.VISIBLE
            } else {
                buttonCancel.visibility = View.GONE
            }
            
            buttonCancel.setOnClickListener {
                dialog.dismiss()
                actionCancel()
            }

            dialog.setCancelable(setCancelable)
            dialog.show()

            return dialog
        }

        fun fromHtml(html: String): Spanned {
            val result: Spanned
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                result = Html.fromHtml(html)
            }
            return result
        }

        fun scaleDownBitmap(photo: Bitmap?, newHeight: Int, context: Context): Bitmap? {
            var photo = photo
            val densityMultiplier = context.resources.displayMetrics.density
            val h = (newHeight * densityMultiplier).toInt()
            val w = (h * photo!!.width / photo.height.toDouble()).toInt()
            photo = Bitmap.createScaledBitmap(photo, w, h, true)
            return photo
        }

        fun decodeFile(f: File): Bitmap? {
            try {
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                BitmapFactory.decodeStream(FileInputStream(f), null, o)

                val REQUIRED_SIZE = 500
                var width_tmp = o.outWidth
                var height_tmp = o.outHeight
                var scale = 1
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                        break
                    width_tmp /= 2
                    height_tmp /= 2
                    scale *= 2
                }

                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                return BitmapFactory.decodeStream(FileInputStream(f), null, o2)

            } catch (ignored: FileNotFoundException) {
            }

            return null
        }

        fun createImageFile(): File {
            val root = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Dynate" + File.separator)
            if(!root.exists() || !root.isDirectory) {
                root.mkdirs()
            }

            val image = File.createTempFile("IMG-", "DYNATE.jpg", root)

            return image
        }

        fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
            val spannableString = SpannableString(textView.text)
            for (i in links.indices) {
                val clickableSpan = clickableSpans[i]
                val link = links[i]

                val startIndexOfLink = textView.text.toString().indexOf(link)
                spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        }

        val blinkAnimation: Animation
            get() {
                val animation = AlphaAnimation(1f, 0f)
                animation.duration = 300
                animation.interpolator = LinearInterpolator()
                animation.repeatCount = -1
                animation.repeatMode = Animation.REVERSE

                return animation
            }

        fun convertLayoutToImage(mContext: Context, count: Int, drawableId: Int): Drawable {
            val inflater = LayoutInflater.from(mContext)
            val view = inflater.inflate(R.layout.badge_icon_layout, null)
            (view.findViewById<View>(R.id.icon_badge) as ImageView).setImageResource(drawableId)

            if (count == 0) {
                val counterTextPanel = view.findViewById<View>(R.id.counterValuePanel)
                counterTextPanel.visibility = View.GONE
            } else {
                val textView = view.findViewById<View>(R.id.count) as TextView
                textView.text = String.format(Locale.getDefault(), "%d", count)
            }

            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            view.isDrawingCacheEnabled = true
            view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            return BitmapDrawable(mContext.resources, bitmap)
        }
    }
}
