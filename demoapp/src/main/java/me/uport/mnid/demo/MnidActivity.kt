package me.uport.mnid.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import me.uport.mnid.MNID

class MnidActivity : AppCompatActivity() {

    private lateinit var mnidField: EditText
    private lateinit var networkField: EditText
    private lateinit var addressField: EditText
    private lateinit var statusField: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mnid)

        mnidField = findViewById(R.id.mnid)
        networkField = findViewById(R.id.network)
        addressField = findViewById(R.id.address)
        statusField = findViewById(R.id.status_box)

        mnidField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onMnidChanged(p0!!)
            }
        })

        val fieldWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onFieldChanged()
            }
        }

        networkField.addTextChangedListener(fieldWatcher)

        addressField.addTextChangedListener(fieldWatcher)
    }


    private var disableOtherUpdates: Boolean = false

    @SuppressLint("SetTextI18n")
    fun onMnidChanged(newValue: CharSequence) {
        if (disableOtherUpdates) {
            return
        }
        disableOtherUpdates = true
        val mnid = newValue.toString()

        if (MNID.isMNID(mnid)) {
            var status = "it looks like MNID\n"
            try {
                val account = MNID.decode(mnid)
                networkField.setText(account.network)
                addressField.setText(account.address)
            } catch (e: Exception) {
                status += "but I get this error when decoding:\n" + e.message
                networkField.setText("0x00")
                addressField.setText("0x0000000000000000000000000000000000000000")
            }

            statusField.text = status

        } else {
            statusField.text = "it doesn't look like MNID"
            networkField.setText("0x00")
            addressField.setText("0x0000000000000000000000000000000000000000")
        }
        disableOtherUpdates = false
    }

    fun onFieldChanged() {
        if (disableOtherUpdates) {
            return
        }
        disableOtherUpdates = true
        val address = addressField.text.toString()
        val network = networkField.text.toString()

        try {
            val mnid = MNID.encode(network, address)
            mnidField.setText(mnid)
            statusField.text = ""
        } catch (e: Exception) {
            statusField.text = e.message
        }

        disableOtherUpdates = false
    }
}
