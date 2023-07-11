package com.pearldrift.handsets.util

import java.io.*
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object Gzip {

    public fun Compress(data: String): String? {
        return try {

            // Create an output stream, and a gzip stream to wrap over.
            val bos = ByteArrayOutputStream(data.length)
            val gzip = GZIPOutputStream(bos)

            // Compress the input string
            gzip.write(data.toByteArray())
            gzip.close()
            var compressed = bos.toByteArray()
            bos.close()

            // Convert to base64
            compressed = Base64.getEncoder().encode(compressed)

            // return the newly created string
            String(compressed)
        } catch (e: IOException) {
            null
        }
    }


    @Throws(IOException::class)
    public fun Decompress(compressedText: String): String? {

        // get the bytes for the compressed string
        var compressed = compressedText.toByteArray(charset("UTF8"))

        // convert the bytes from base64 to normal string
        val d: Base64.Decoder = Base64.getDecoder()
        compressed = d.decode(compressed)

        // decode.
        val BUFFER_SIZE = 32
        val `is` = ByteArrayInputStream(compressed)
        val gis = GZIPInputStream(`is`, BUFFER_SIZE)
        val string = java.lang.StringBuilder()
        val data = ByteArray(BUFFER_SIZE)
        var bytesRead: Int
        while (gis.read(data).also { bytesRead = it } != -1) {
            string.append(String(data, 0, bytesRead))
        }
        gis.close()
        `is`.close()
        return string.toString()
    }


}