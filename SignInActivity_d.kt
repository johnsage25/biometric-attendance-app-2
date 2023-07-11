package com.pearldrift.handsets

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.airbnb.lottie.compose.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fgtit.data.FingerEvent
import com.fgtit.data.FingerState
import com.fgtit.device.Constants
import com.fgtit.device.FPModule
import com.fgtit.fpcore.ObjectBox
import com.fgtit.model.Attendance
import com.fgtit.model.EnrolmentModel
import com.fgtit.model.EnrolmentModel_
import com.google.gson.Gson
import com.parse.ParseObject
import com.parse.ParseUser
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import com.skydoves.landscapist.glide.GlideImage
import io.objectbox.Box
import kotlinx.coroutines.launch
import okhttp3.internal.and
import sm.euzee.github.com.servicemanager.ServiceManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SignInActivityd : ComponentActivity() {
    private val fpm = FPModule()
    private val bmpdata = ByteArray(Constants.RESBMP_SIZE)
    private var bmpsize = 0
    private var refsize = 0
    private val matdata = ByteArray(Constants.TEMPLATESIZE * 2)
    private var matsize = 0
    private var worktype = 0
    private val refdata = ByteArray(Constants.TEMPLATESIZE * 2)
    var fingerImage = MutableLiveData<Bitmap?>()
    private var stateChange = MutableLiveData("")
    private var textValue = MutableLiveData("")
    private var textLottie = MutableLiveData("true")
    //dynamic setting of the permission for writing the data into phone memory
    private  var attendanceBox: Box<Attendance>? = null
    private val REQUEST_PERMISSION_CODE = 1
    private lateinit var mFilters: Array<IntentFilter>
    private lateinit var service: serviceReceiver
    private var is_nfc_card = false
    private var mNfcAdapter: NfcAdapter? = null
    var attendanceObserver = MutableLiveData(Attendance())

    private val model: FingerEvent by viewModels()


    var innet_action = ""


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
        ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Running android service here
        Intent(this, FingerPrintService::class.java).also {
            startService(it)
        }


        val nameObserver = Observer<FingerState> { it ->
            // Update the UI, in this case, a TextView.
//            textLottie.value = it.animate
        }

        model.getData().observe(this, nameObserver)

        service = serviceReceiver()
        registerReceiver(service, IntentFilter().apply {
            addAction("SIGNED_IN")
            addAction("FPM_NEWIMAGE")
            addAction("FPM_PLACE")
            addAction("FPM_LIFT")
            addAction("DEV_FAIL")
        })

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        setContent {

            var fingerPlaced by remember {
                mutableStateOf("false")
            }
            var textva by remember {
                mutableStateOf("")
            }


            stateChange.observeForever { observer ->
                fingerPlaced = observer.toString()
            }

            textValue.observeForever{
                textva = it.toString()
            }

            var staff_enrol_list = remember {
                mutableStateListOf<Attendance>()
            }

            val coroutineScope = rememberCoroutineScope()

            attendanceBox =
                ObjectBox.store.boxFor(Attendance::class.java)


            val listState = rememberLazyListState()

            attendanceObserver.observeForever {
                staff_enrol_list.add(it)

                coroutineScope.launch {
                    listState.animateScrollToItem(staff_enrol_list.size)
                }
            }



            BASHandsetsCompatiblektTheme() {

                Scaffold(

                    containerColor = colorResource(id = R.color.gray),
                    topBar = {
                        TopBarWithBack(title = "Sign In"){
                            finish()
                        }
                    },
                    content = ({
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .nestedScroll(
                                rememberNestedScrollInteropConnection()
                            )) {

                            val enrolBox =
                                ObjectBox.store.boxFor(EnrolmentModel::class.java)

                            if(staff_enrol_list.size.equals(1)){

                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .fillMaxHeight(), contentAlignment = Alignment.Center) {

                                    Column(modifier = Modifier
                                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Image(painter = painterResource(id = R.drawable.ic_history__1), contentDescription = "history",
                                            modifier = Modifier.size(height = 200.dp, width = 200.dp))
                                        androidx.compose.material3.Text(
                                            text = "No attendance",
                                            fontSize = 25.sp,
                                            color = colorResource(id = R.color.transparent_db)
                                        )
                                    }
                                }

                            }else
                            {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(vertical = 10.dp, horizontal = 10.dp),
                                userScrollEnabled = true,
                                state = listState
                                ,
                                verticalArrangement = Arrangement.Bottom) {



                                items(staff_enrol_list.size, key = {
                                    staff_enrol_list[it].id
                                }, itemContent = { index ->
                                    var item = staff_enrol_list.get(index)


                                    var enrolled_user = enrolBox.query(EnrolmentModel_.id.equal(item.enrol_id)).build().findFirst()

                                    enrolled_user.let {

                                        if(it?.id != null){

                                            val density = LocalDensity.current

                                            AnimatedVisibility(
                                                visible = it?.id != null,
                                                enter = slideInVertically {
                                                    // Slide in from 40 dp from the top.
                                                    with(density) { -40.dp.roundToPx() }
                                                } + expandVertically(
                                                    // Expand from the top.
                                                    expandFrom = Alignment.Top
                                                ) + fadeIn(
                                                    // Fade in with the initial alpha of 0.3f.
                                                    initialAlpha = 0.3f
                                                ),
                                                exit = slideOutVertically() + shrinkVertically() + fadeOut()

                                            ) {

                                            ListItem(
                                                icon = {
                                                    GlideImage(
                                                        imageModel = it?.image,
                                                        // Crop, Fit, Inside, FillHeight, FillWidth, None
                                                        Modifier
                                                            .width(70.dp)
                                                            .height(70.dp)
                                                            .clip(RoundedCornerShape(50)),
                                                        requestOptions = {
                                                            RequestOptions()
                                                                .override(70, 70)
                                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .centerCrop()
                                                        },
                                                        contentScale = ContentScale.Crop,
                                                        // shows a placeholder while loading the image.
                                                        placeHolder = ImageBitmap.imageResource(R.drawable.avatar),
                                                        // shows an error ImageBitmap when the request failed.
                                                        error = ImageBitmap.imageResource(R.drawable.avatar)
                                                    )
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        color = colorResource(
                                                            id = R.color.transparent_db
                                                        )
                                                    ),
                                                text = {
                                                   Column() {

                                                       Text(
                                                           text = it?.fullname.toString(),
                                                           fontSize = 20.sp,
                                                           fontWeight = FontWeight.Bold
                                                       )
                                                       Spacer(modifier = Modifier.padding(bottom = 5.dp))
                                                       Row() {
                                                           Text(text = "Entrance type: ")
                                                           Text(text = "${item.capture_type?.toUpperCase()}")
                                                       }

                                                   }

                                                },
                                                trailing = {

//                                                    val localDateTime =
//                                                        LocalDateTime.parse(item.timestamp_date)
//                                                    val formatter =
//                                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                                                    val output = formatter.format(localDateTime).toLocalDateTime()

                                                    val parsedDate = LocalDateTime.parse(item.timestamp_date, DateTimeFormatter.ISO_DATE_TIME)
                                                    val time = parsedDate.format(DateTimeFormatter.ofPattern("hh:mm a"))

                                                    val date = parsedDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy"))

                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Text(text = time, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                                        Text(text = date, fontSize = 15.sp)
                                                    }

                                                    },

                                                )

                                            Divider(modifier = Modifier.fillMaxWidth(), color = colorResource(
                                                id = R.color.gray
                                            ))
                                            }

                                    }
                                    }

                                })
                             }
                            }
                            Card(modifier = Modifier
                                .background(color = Color.White)
                                .fillMaxWidth(), elevation = 4.dp, shape = RoundedCornerShape(0.dp)) {
                                Column() {
                                    Divider(Modifier.fillMaxWidth(1f))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                                        .fillMaxWidth()) {
                                        var lottieImage by remember {
                                            mutableStateOf(R.raw.scanner_loading)
                                        }
                                        Spacer(modifier = Modifier.height(20.dp))

                                        textLottie.observeForever{
                                            Log.d("user", "live ${it}")

                                            lottieImage = R.raw.scanner_loading

                                            if(it.toBoolean() != true){
                                                lottieImage = R.raw.red_scanner
                                            }

                                        }

                                        val composition by rememberLottieComposition(
                                            LottieCompositionSpec.RawRes(lottieImage))
                                        val progress by animateLottieCompositionAsState(composition = composition,
                                            iterations = LottieConstants.IterateForever,
                                            restartOnPlay= true,
                                            isPlaying = fingerPlaced.toBoolean() )

                                        Box(
                                            modifier = Modifier.size(
                                                height = 200.dp,
                                                width = 180.dp
                                            ),
                                            contentAlignment = Alignment.Center
                                        ){

                                            var mapBitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }

                                            fingerImage.observeForever{

                                                mapBitmap.value = it
                                            }

                                            GlideImage(
                                                imageModel = mapBitmap.value,
                                                // Crop, Fit, Inside, FillHeight, FillWidth, None
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .size(
                                                        height = 130.dp,
                                                        width = 130.dp
                                                    )
                                                    .rotate(180f),
                                                // shows a placeholder while loading the image.
                                                placeHolder = ImageVector.vectorResource(
                                                    id = R.drawable.ic_fingerprint__2
                                                ),
                                                // shows an error ImageBitmap when the request failed.
                                                error = ImageVector.vectorResource(
                                                    id = R.drawable.ic_fingerprint__2
                                                ),
                                            )

                                            LottieAnimation(
                                                composition,
                                                progress,
                                                modifier = Modifier
                                                    .fillMaxWidth(1f)
                                                    .fillMaxHeight(1f),
                                            )
                                        }
                                        Box(modifier = Modifier
                                            .fillMaxWidth(1f)
                                            .padding(bottom = 15.dp), contentAlignment = Alignment.Center){
                                            Text(text = textva, fontSize = 17.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }),
                    bottomBar = {

                    }
                )

            }
        }

    }



    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        innet_action = intent.action.toString()

        var tagFromIntent: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        val nfc = NfcA.get(tagFromIntent)

        nfc.connect()
        val isConnected= nfc.isConnected()

        if(isConnected){
            textValue.value = "Remove Card"

            val sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
            val cardstr =  /*Integer.toString(count)+":"+*/Integer.toHexString(
                sn!![0] and 0xFF
            ).uppercase(Locale.getDefault()) + Integer.toHexString(
                sn!![1] and
                        0xFF
            ).uppercase(Locale.getDefault()) + Integer.toHexString(sn!![2] and 0xFF)
                .uppercase(Locale.getDefault()) +
                    Integer.toHexString(sn!![3] and 0xFF).uppercase(Locale.getDefault())

            val enrolBox = ObjectBox.store.boxFor(EnrolmentModel::class.java)

            var enrol_entity = enrolBox.query(EnrolmentModel_.nfc_card_code.equal(cardstr)).build().findFirst()

            Log.d("user", "${cardstr}")
            enrol_entity.let {
                var dataAttendance  = Attendance(
                    staff_objectId = it?.uuid,
                    enrol_id = it?.id ?: 0,
                    timestamp_date = LocalDateTime.now().toString(),
                    capture_type = "card",
                    location = "",
                    attns_type = "signed_in",
                    device_admin = ParseUser.getCurrentUser().objectId
                )

                attendanceBox =
                    ObjectBox.store.boxFor(Attendance::class.java)
                attendanceBox?.put(dataAttendance)



                var staff = ParseObject("StaffEnrolment")
                staff.objectId = it?.uuid.toString()

                var attendance = ParseObject("Attendance")
                attendance.put("staff_objectId", it?.uuid.toString())
                attendance.put("enrol_id", staff)
                attendance.put("timestamp_date", LocalDateTime.now().toString())
                attendance.put("capture_type", "finger")
                attendance.put("location", "")
                attendance.put("attns_type", "signed_in")
                attendance.put("device_admin", ParseUser.getCurrentUser())
                attendance.saveEventually()

                attendanceObserver.value = dataAttendance

            }





            Log.d("user", "${enrol_entity?.fullname}")

//            Log.d("user", "connected")
        }
        else{
//            Log.e("ans", "Not connected")
        }
        mNfcAdapter?.ignore(tagFromIntent, 1000, NfcAdapter.OnTagRemovedListener {
            Log.d("user", "ignored")

            textValue.value = "Place Finger"

        }, Handler(Looper.getMainLooper()))


    }

    override fun onStart() {
        Log.d("user", "activity_started")

        super.onStart()
    }
    override fun onResume() {
        Log.d("user", "activity_resume")
//        FingerPrintService.startAction(this)
        super.onResume()

        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

         try {

             Log.d("user", "loading nfc .....")

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


             mNfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
             mNfcAdapter?.enableReaderMode(this@SignInActivityd, {

             }, 0, null)

         }
         catch (e: Exception){
//             Log.d("user", "error: ${e.message}")
         }
    }


    override fun onPause() {

        mNfcAdapter?.disableForegroundDispatch(this)
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        stopService(Intent(this@SignInActivity, FingerPrintService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this@SignInActivity, FingerPrintService::class.java))
        unregisterReceiver(service);
    }

    companion object {
        private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }

    inner class serviceReceiver : BroadcastReceiver() {


        override fun onReceive(context: Context?, intent: Intent) {

            if(intent.action =="FPM_PLACE"){
                val message: String? = intent.getStringExtra("MESSAGE")
                val animate: String? = intent.getStringExtra("ANIMATION")
                textValue.value = message
                textLottie.value = "true"
            }

            if (intent.action == "FPM_LIFT"){
                val message: String? = intent.getStringExtra("MESSAGE")
                val animate: String? = intent.getStringExtra("ANIMATION")
                stateChange.value = "true"
                textValue.value = message
            }


            if (intent.action == "SIGNED_IN") {
                val message: String? = intent.getStringExtra("MESSAGE")
                val animate: String? = intent.getStringExtra("ANIMATION")
                val data: String? = intent.getStringExtra("DATA")

//                model.setData(animate.toString(),message.toString(),"")
                if(message.toString() == "Place Finger"){
                    textLottie.value = "true"

                }

                if(data!!.isNotEmpty()){
                    var jsonData = Gson()
                    var jsonObject = jsonData.fromJson<Attendance>(data, Attendance::class.java)
                    attendanceObserver.value = jsonObject

                    Log.d("user", "jsonObject: ${jsonObject.staff_objectId}")
                }


                stateChange.value = animate

                Log.d("user", "message: ${message}")
                Log.d("user", "animate: ${animate}")
                // Show it in activity
            }

            if(intent.action == "FPM_NEWIMAGE"){


                val inpsize: Int = intent.getIntExtra("IMAGE_SIZE", 0)
                val inpdata: ByteArray? = intent.getByteArrayExtra("IMAGE")
//                var gson = Gson()
//                var decompressed = Base64Util.decompress(image)
//                var image_data = gson.fromJson<FingerImage>(decompressed, FingerImage::class.java)


                Log.d("user", "image: ${inpdata}")

                var bm1 = BitmapFactory.decodeByteArray(
                    inpdata,
                    0,
                    inpsize
                )


                fingerImage.value = bm1

            }
        }
    }

}