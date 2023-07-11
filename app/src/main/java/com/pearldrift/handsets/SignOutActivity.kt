package com.pearldrift.handsets

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.airbnb.lottie.compose.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.datetimeutils.DateTimeStyle
import com.datetimeutils.DateTimeUtils
import com.fgtit.device.Constants
import com.fgtit.device.FPModule
import com.fgtit.fpcore.ObjectBox
import com.fgtit.fpcore.StaffAuthClass
import com.fgtit.model.Attendance
import com.fgtit.model.Attendance_
import com.fgtit.model.EnrolmentModel
import com.fgtit.model.EnrolmentModel_
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.kswafx.iamtoast.IamToast
import com.parse.ParseObject
import com.parse.ParseUser
import com.pearldrift.handsets.ui.nav.TopBarWithBack
import com.pearldrift.handsets.ui.theme.BASHandsetsCompatiblektTheme
import com.pearldrift.handsets.util.DataHttpHandler
import com.pearldrift.handsets.util.ExtApi
import com.skydoves.landscapist.glide.GlideImage
import io.objectbox.Box
import io.objectbox.query.QueryBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.and
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SignOutActivity : ComponentActivity() {
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
    private var textLottie = MutableLiveData("false")
    //dynamic setting of the permission for writing the data into phone memory
    private  var attendanceBox: Box<Attendance>? = null
    private val REQUEST_PERMISSION_CODE = 1
    private var mNfcAdapter: NfcAdapter? = null
    private var threadStarted = false;
    private lateinit var mFilters: Array<IntentFilter>
    var innet_action = ""

    var attendanceObserver = MutableLiveData(Attendance())

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
        ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startIntent = intent

        Log.d("user", "context intent ${startIntent.action}")


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        fpm.InitMatch()
        fpm.SetTimeOut(Constants.TIMEOUT_LONG)

        fpm.SetContextHandler(this@SignOutActivity, mHandler)
        fpm.SetLastCheckLift(true)

        TimeStart()

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
                        TopBarWithBack(title = "Sign Out"){
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
                                            modifier = Modifier.size(height = 160.dp, width = 160.dp))
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


                                                            val sdf = SimpleDateFormat("H:mm")
                                                            val dateObj = sdf.parse(item.time)

                                                            val time =
                                                                SimpleDateFormat("K:mm a").format(dateObj)

                                                            val date =
                                                                DateTimeUtils.formatWithStyle(item.timestamp_date,  DateTimeStyle.MEDIUM)

//
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
//                                            textLottie.value = "true"
                                            if(it.toBoolean() == true){
                                                lottieImage = R.raw.scanner_loading
                                            }
                                            else{
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
                                                height = 150.dp,
                                                width = 150.dp
                                            ),
                                            contentAlignment = Alignment.Center
                                        ){

                                            var mapBitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }

                                            fingerImage.observeForever{
                                                mapBitmap.value = it
                                            }


                                            if(!threadStarted){
                                                val composition by rememberLottieComposition(
                                                    LottieCompositionSpec.RawRes(R.raw.loading))
                                                val progress by animateLottieCompositionAsState(composition = composition,
                                                    iterations = LottieConstants.IterateForever,
                                                    restartOnPlay= true,
                                                    isPlaying = true )
                                                BoxWithConstraints( modifier = Modifier
                                                    .height(150.dp)
                                                    .width(150.dp)
                                                    .border(border = BorderStroke(1.dp,
                                                        colorResource(
                                                            id = R.color.white)))
                                                    .background(color = colorResource(
                                                        id = R.color.white))){

//                                                    if (maxHeight > 150.dp) {
                                                    LottieAnimation(
                                                        composition,
                                                        progress,
                                                        contentScale = ContentScale.FillHeight,
                                                        modifier = Modifier
                                                            .fillMaxWidth(1f)
                                                            .fillMaxHeight(1f),
                                                    )
//                                                    }

                                                }
                                            }
                                            else{
                                                GlideImage(
                                                    imageModel = mapBitmap.value,
                                                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier
                                                        .size(
                                                            height = 110.dp,
                                                            width = 110.dp
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


    @SuppressLint("HandlerLeak")
    private var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        val sn: IntArray = IntArray(8)
        override fun handleMessage(msg: Message) {

            val cardsn: String = (Integer.toHexString(sn.get(0) and 0xFF).uppercase(
                Locale.getDefault()
            ) +
                    Integer.toHexString(sn.get(1) and 0xFF)
                        .uppercase(Locale.getDefault()) +
                    Integer.toHexString(sn.get(2) and 0xFF)
                        .uppercase(Locale.getDefault()) +
                    Integer.toHexString(sn.get(3) and 0xFF)
                        .uppercase(Locale.getDefault()))

            Log.d("user", "finger_h ${cardsn}")

            when (msg.what) {

                Constants.FPM_DEVICE -> when (msg.arg1) {

                }

                Constants.FPM_PLACE -> {
                    stateChange.value = "false"
                    textValue.value = "Place Finger"
                    textLottie.value = "true";
                }

                Constants.FPM_LIFT -> {
                    stateChange.value = "true"
                    textValue.value = "Lift Finger"
                }

                Constants.FPM_GENCHAR -> {
                    Log.d("user", "char")
                    fpm.RestartTask(true)
                    stateChange.value = "false"


                    if (msg.arg1 == 1) {



                        val enrolBox = ObjectBox.store.boxFor(EnrolmentModel::class.java)

                        var enrolList =  enrolBox.all


                        try {

                            var counter = 0



                            for (it in enrolList){
                                counter ++
                                matsize = fpm.GetTemplateByGen(matdata)

                                var tempR = ExtApi.Base64ToBytes(
                                    it.right_fingerprint,
                                )


                                var tempL = ExtApi.Base64ToBytes(
                                    it.left_fingerprint,
                                )



                                val tmpSizeL = it.left_inpsize as Int
                                val tmpSizeR = it.right_inpsize as Int

                                if (fpm.MatchTemplate(
                                        tempR,
                                        tmpSizeR,
                                        matdata,
                                        matsize,
                                        60
                                    ) || fpm.MatchTemplate(
                                        tempL,
                                        tmpSizeL,
                                        matdata,
                                        matsize,
                                        60
                                    )){


                                    var current = DateTimeUtils.formatWithPattern(Date(), "yyyy-MM-dd")

                                    var findData =
                                        attendanceBox?.query(Attendance_.staff_uuid.equal(it?.uuid).and(Attendance_.timestamp.equal(current)))?.contains(Attendance_.attns_type,"signed_out", QueryBuilder.StringOrder.CASE_INSENSITIVE)

                                    var find = findData?.orderDesc(Attendance_.timestamp)?.build()?.findFirst()

                                    if(find?.timestamp.equals(current)){

                                        var mediaPlayer = MediaPlayer.create(this@SignOutActivity, R.raw.invalid_selection)
                                        mediaPlayer.start()

                                        textLottie.value = "false";
                                        textValue.value = "Duplicated"
                                        IamToast.error(this@SignOutActivity, "You can't repeat this action.", IamToast.GRAVITY_BOTTOM,
                                            IamToast.LONG_DURATION)

                                        break
                                    }

                                    val dataAttendance = addAttendance(type = "finger", it= it)
                                    attendanceObserver.value = dataAttendance


                                    var mediaPlayer = MediaPlayer.create(this@SignOutActivity, R.raw.beep_08b)
                                    mediaPlayer.start()

                                    textLottie.value = "true";
                                    textValue.value = "Match OK"
                                    break
                                }


                                if(counter >= enrolList.size){
                                    textLottie.value = "false";
                                    textValue.value = "Match Fail"
                                }

                            }


                        }
                        catch (e: Exception){
                            Log.d("user", "Error: ${e.message}")
                            Log.d("user", "Code: ${e.cause}")
                            textLottie.value = "false";
                            textValue.value = "Match Fail"
                        }

                        refsize = fpm.GetTemplateByGen(refdata)

                    }



                }

                Constants.FPM_NEWIMAGE -> {

                    bmpsize = fpm.GetBmpImage(bmpdata)
                    val inpdata = ByteArray(73728)
                    val inpsize = 73728
                    System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize)

                    var bm1 = BitmapFactory.decodeByteArray(
                        bmpdata,
                        0,
                        bmpsize
                    )

                    fingerImage.value = bm1

                }

                Constants.FPM_TIMEOUT -> {
                    textValue.value = "Time Out"
                }

            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)


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

            var enrol_entity = enrolBox.query(EnrolmentModel_.nfc_card_code.equal(cardstr)).build()

            if(enrol_entity.count().equals(0)){
                Toast.makeText(this@SignOutActivity, "Invalid user", Toast.LENGTH_SHORT).show()
            }else


                enrol_entity.findFirst().let {

                    var current = DateTimeUtils.formatWithPattern(Date(), "yyyy-MM-dd")

                    var findData =
                        attendanceBox?.query(Attendance_.staff_uuid.equal(it?.uuid).and(Attendance_.timestamp.equal(current)))?.contains(Attendance_.attns_type,"signed_out", QueryBuilder.StringOrder.CASE_INSENSITIVE)

                    var find = findData?.orderDesc(Attendance_.timestamp)?.build()?.findFirst()


                    if(find != null){


                        if(find?.timestamp.equals(current)){

                            var mediaPlayer = MediaPlayer.create(this@SignOutActivity, R.raw.invalid_selection)
                            mediaPlayer.start()

                            textLottie.value = "false";
                            textValue.value = "Duplicated"
                            IamToast.error(this@SignOutActivity, "You can't repeat this action.", IamToast.GRAVITY_BOTTOM,
                                IamToast.LONG_DURATION)

                            Handler(Looper.getMainLooper()).postDelayed({
                                textLottie.value = "true";
                                textValue.value = "Place Finger"
                            }, 2000)

                            return
                        }

                    }



                    val dataAttendance = addAttendance(type = "card", it= it)

                    attendanceObserver.value = dataAttendance

                }

        }
        else{

        }
        mNfcAdapter?.ignore(tagFromIntent, 1000, NfcAdapter.OnTagRemovedListener {
            Log.d("user", "ignored")

            textValue.value = "Place Finger"

        }, Handler(Looper.getMainLooper()))


    }

    fun TimeStart(){
        textLottie.value = "true"
        if(!threadStarted){
            Handler(Looper.getMainLooper()).postDelayed({

                fpm.StartThread(1)
                threadStarted = true
            }, 3000)
        }


    }

    fun addAttendance(type: String, it: EnrolmentModel?): Attendance {

        DateTimeUtils.setTimeZone("Africa/Lagos");

        val date_time = DateTimeUtils.formatWithPattern(Date(), "yyyy-MM-dd")
        val time = DateTimeUtils.formatWithPattern(Date(), "HH:mm")

        var current = DateTimeUtils.formatWithPattern(Date(), "yyyy-MM-dd")

        val userid = StaffAuthClass().readFromSharedPreferences("userId", this@SignOutActivity)

        val staff =  StaffAuthClass().getStaffDetailBox(userid)

        val deviceUUID = DataHttpHandler().getDeviceUUID(applicationContext)
        val uidString = DataHttpHandler().createUniqueMd4Value("${it?.uuid}")



        var dataAttendance  = Attendance(
            staff_objectId =  it?.objectId,
            enrol_id = it?.id ?: 0,
            timestamp_date =  date_time,
            capture_type = type,
            timestamp = current,
            time = time,
            staff_uuid = it?.uuid,
            location = staff[0].department,
            attns_type = "signed_out",
            device_admin = deviceUUID,
            uid = "out-${uidString}"
        )

        attendanceBox =
            ObjectBox.store.boxFor(Attendance::class.java)
        attendanceBox?.put(dataAttendance)



        dataAttendance.let { att ->

            var gson = Gson()
            Log.d("user-log", "${gson.toJson(att)}")

            GlobalScope.launch(Dispatchers.IO) {

                try {

                    Fuel.post("${getString(R.string.basApi)}/attendance",
                        listOf(
                            "staff_objectId" to att.staff_objectId,
                            "staff_uuid" to att?.staff_uuid.toString(),
                            "uid" to att?.uid.toString(),
                            "enrol_id" to att?.id.toString(),
                            "timestamp_date" to date_time,
                            "capture_type" to  att?.capture_type.toString(),
                            "location" to att?.location.toString(),
                            "timestamp" to att.timestamp.toString(),
                            "time" to att?.time.toString(),
                            "attns_type" to "signed_out",
                            "device_admin" to deviceUUID
                        )
                    )
                        .also {
                            Log.d("user-log", "${it.awaitString()}")
                        }

                }catch(e: Exception){

                }

            }


        }

        return dataAttendance

    }



    override fun onResume() {
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
            mNfcAdapter?.enableReaderMode(this@SignOutActivity, {

            }, 0, null)

        }
        catch (e: Exception){
//             Log.d("user", "error: ${e.message}")
        }

        if(!threadStarted){
            fpm.ResumeRegister()
            fpm.OpenDevice()
        }
    }

    override fun onStop() {

        if(!threadStarted){
            fpm.PauseUnRegister()
            fpm.CloseDevice()
        }

        super.onStop()

    }

    override fun onDestroy() {
        threadStarted = false
        super.onDestroy()
    }

}