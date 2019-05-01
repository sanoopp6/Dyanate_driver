package com.fast_prog.dynate.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.fast_prog.dynate.R
import kotlinx.android.synthetic.main.content_support.*

class SupportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this@SupportActivity, LoginActivity::class.java))
            finish()
        }

        textview_supportno.setOnClickListener {
            val phone = "+966 5577 69868"
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
            startActivity(intent)
        }

        textview_supportmail.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "message/rfc822"
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@fast-prog.com"))
            i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.NewRegistration))
            i.putExtra(Intent.EXTRA_TEXT, "")
            try {
                startActivity(Intent.createChooser(i, resources.getString(R.string.SendMail)))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(this@SupportActivity, resources.getString(R.string.ThereAreNoEmailClients), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SupportActivity, LoginActivity::class.java))
        finish()
    }
}
