import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.FileOutputStream

fun downloadImageWithVolleyAndSave(context: Context, link: String, imageName:String) {
    val requestQueue = Volley.newRequestQueue(context)
    val imageLoader = ImageLoader(requestQueue, object : ImageLoader.ImageCache {
        private val cache = LruCache<String, Bitmap>(20)

        override fun getBitmap(url: String): Bitmap? {
            return cache.get(url)
        }

        override fun putBitmap(url: String, bitmap: Bitmap) {
            cache.put(url, bitmap)
        }
    })

    val imageRequest = ImageRequest(
        link,
        Response.Listener { response ->
            Log.d("user-log", "Download Successful")
            saveImage(response, imageName, context)
        },
        0,
        0,
        ImageView.ScaleType.CENTER_CROP,
        Bitmap.Config.RGB_565,
        Response.ErrorListener { error ->
            Log.d("user-log", "Download Failed")
        }
    )

    requestQueue.add(imageRequest)
}

fun saveImage(bitmap: Bitmap, imageName: String, context:Context) {
    val folder = File(context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DCIM), imageName)
    if (!folder.exists()) {
        folder.mkdirs()
    }
    val file = File(folder, imageName)
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.flush()
    stream.close()
}