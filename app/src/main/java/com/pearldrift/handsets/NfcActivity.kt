package com.pearldrift.handsets

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.compose.*
import com.fgtit.fpcore.ActivityList
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import okhttp3.internal.and
import java.util.*


open class NfcActivity : ComponentActivity() {

    private lateinit var nfcAdapter: NfcAdapter
    private var mPendingIntent: PendingIntent? = null
    private lateinit var mFilters: Array<IntentFilter>
    private var cardData = MutableLiveData<String>("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InitReadCard()

        setContent {

            var nfcStatus by remember {
                mutableStateOf(false)
            }

            cardData.observeForever{
                if(it.isNotEmpty()){
                    nfcStatus = true
                }

            }

            BASHandsetsCompatiblektTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                   Scaffold(topBar = {
                       TopBarWithBack(title = "Card Capture"){
                           finish()
                       }
                   }, content = ({
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(1f)) {

                            Spacer(modifier = Modifier.height(40.dp))
                            Text(text = "Card Entry", style = MaterialTheme.typography.h4)
                            var statusText = "Waiting for NFC card to contact device."
                            if (nfcStatus){

                                statusText = "Captured completed, go back to form."
                                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lf30_editor_apobxi2y))
                                val progress by animateLottieCompositionAsState(
                                    composition
                                )

                                LottieAnimation(
                                    composition,
                                    progress,
                                    modifier = Modifier
                                        .height(400.dp)
                                        .width(400.dp)
                                )
                            }
                            else{

                                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.nfc_scan))
                                val progress by animateLottieCompositionAsState(
                                    composition,
                                    iterations = LottieConstants.IterateForever
                                )

                                LottieAnimation(
                                    composition,
                                    progress,
                                    modifier = Modifier
                                        .height(400.dp)
                                        .width(400.dp)
                                )
                            }
                            Text(text = statusText, style = MaterialTheme.typography.h6)

                        }
                   }), bottomBar = {

                   })
                }
            }
        }
    }

    fun InitReadCard() {

            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
            if (nfcAdapter == null) {
                Toast.makeText(this, "Device does not support NFC!", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(
                    this,
                    "Enable the NFC function in the system settings!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
            mPendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
            )
            mFilters = arrayOf(
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            )

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED){
            Toast.makeText(this, "Change", Toast.LENGTH_SHORT).show()
        }

        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
            val cardstr =  /*Integer.toString(count)+":"+*/Integer.toHexString(
                sn!![0] and 0xFF
            ).uppercase(Locale.getDefault()) + Integer.toHexString(
                sn!![1] and
                        0xFF
            ).uppercase(Locale.getDefault()) + Integer.toHexString(sn!![2] and 0xFF)
                .uppercase(Locale.getDefault()) +
                    Integer.toHexString(sn!![3] and 0xFF).uppercase(Locale.getDefault())
            cardData.value = cardstr

            val data = Intent()
            data.putExtra("cardstr", cardstr)
            setResult(RESULT_OK, data)

        }

    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        var pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_MUTABLE)

        val ndef = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED).apply {
            try {
                addDataType("*/*")    /* Handles all MIME based dispatches.
                                 You should specify only the ones that you need. */
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }

        var intentFiltersArray = arrayOf(ndef)
        var techListsArray = arrayOf(arrayOf<String>(NfcA::class.java.name))


        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
        nfcAdapter?.enableReaderMode(this, {

        }, 0, null)
    }

    public override fun onPause() {
        if (nfcAdapter != null) nfcAdapter!!.disableForegroundDispatch(this)
        super.onPause()
    }




}