package com.pearldrift.handsets.util

import okhttp3.internal.and
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.experimental.or

class DataUtils {
    private fun getChar(position: Int): CharArray {
        var str = position.toString()
        if (str.length == 1) {
            str = "0$str"
        }
        return charArrayOf(str[0], str[1])
    }

    companion object {
        /**
         *
         *
         * @param hex
         * @return
         */
        fun hexStringTobyte(hex: String): ByteArray {
            val len = hex.length / 2
            val result = ByteArray(len)
            val achar = hex.toCharArray()
            var temp = ""
            for (i in 0 until len) {
                val pos = i * 2
                result[i] = (toByte(achar[pos]) shl 4 or toByte(
                    achar[pos + 1]
                )).toByte()
                temp += result[i].toString() + ","
            }
            // uiHandler.obtainMessage(206, hex + "=read=" + new String(result))
            // .sendToTarget();
            return result
        }

        fun toByte(c: Char): Int {
            val b = "0123456789ABCDEF".indexOf(c).toByte()
            return b.toInt()
        }

        /**
         *
         *
         * @param b
         * @return
         */
        fun toHexString(b: ByteArray): String {
            val buffer = StringBuffer()
            for (i in b.indices) {
                buffer.append(toHexString1(b[i]))
            }
            return buffer.toString()
        }

        fun toHexString1(b: Byte): String {
            val s = Integer.toHexString(b and 0xFF)
            return if (s.length == 1) {
                "0$s"
            } else {
                s
            }
        }

        /**
         *
         */
        fun hexStr2Str(hexStr: String): String {
            val str = "0123456789ABCDEF"
            val hexs = hexStr.toCharArray()
            val bytes = ByteArray(hexStr.length / 2)
            var n: Int
            for (i in bytes.indices) {
                n = str.indexOf(hexs[2 * i]) * 16
                n += str.indexOf(hexs[2 * i + 1])
                bytes[i] = (n and 0xff).toByte()
            }
            return String(bytes)
        }

        /**
         * OַOOOתOOOOʮOOOOOOOַOOO
         */
        fun str2Hexstr(str: String): String {
            val chars = "0123456789ABCDEF".toCharArray()
            val sb = StringBuilder("")
            val bs = str.toByteArray()
            var bit: Int
            for (i in bs.indices) {
                bit = bs[i] and 0x0f0 shr 4
                sb.append(chars[bit])
                bit = bs[i] and 0x0f
                sb.append(chars[bit])
            }
            return sb.toString()
        }

        fun byte2Hexstr(b: Byte): String {
            var temp = Integer.toHexString(0xFF and b.toInt())
            if (temp.length < 2) {
                temp = "0$temp"
            }
            temp = temp.uppercase(Locale.getDefault())
            return temp
        }

        fun str2Hexstr(str: String, size: Int): String {
            val byteStr = str.toByteArray()
            val temp = ByteArray(size)
            System.arraycopy(byteStr, 0, temp, 0, byteStr.size)
            temp[size - 1] = byteStr.size.toByte()
            return toHexString(temp)
        }

        /**
         * 16OOOOOַOOOOָOOOOOɿ飬ÿOO32OO16OOOOOַOOOOO16OֽO
         *
         * @param str
         * @return
         */
        fun hexStr2StrArray(str: String): Array<String?> {
            // 32OOʮOOOOOOOַOOOOOʾ16OֽO
            val len = 32
            val size = if (str.length % len == 0) str.length / len else str.length / len + 1
            val strs = arrayOfNulls<String>(size)
            for (i in 0 until size) {
                if (i == size - 1) {
                    var temp = str.substring(i * len)
                    for (j in 0 until len - temp.length) {
                        temp = temp + "0"
                    }
                    strs[i] = temp
                } else {
                    strs[i] = str.substring(i * len, (i + 1) * len)
                }
            }
            return strs
        }

        /**
         * OO16OOOOOַOOOѹOOOOOֽOOOO飬OڰOOֽOOOOOתOOOO16OOOOOַOOO
         *
         * @param hexstr
         * @return
         * @throws IOException
         */
        @Throws(IOException::class)
        fun compress(data: ByteArray?): ByteArray {
            val out = ByteArrayOutputStream()
            val gzip = GZIPOutputStream(out)
            gzip.write(data)
            gzip.close()
            return out.toByteArray()
        }

        /**
         * OO16OOOOOַOOOOOѹOOѹOOOOOֽOOOO飬OڰOOֽOOOOOתOOOO16OOOOOַOOO
         *
         * @param hexstr
         * @return
         * @throws IOException
         */
        @Throws(IOException::class)
        fun uncompress(data: ByteArray?): ByteArray {
            val out = ByteArrayOutputStream()
            val `in` = ByteArrayInputStream(data)
            val gunzip = GZIPInputStream(`in`)
            val buffer = ByteArray(256)
            var n: Int
            while (gunzip.read(buffer).also { n = it } >= 0) {
                out.write(buffer, 0, n)
            }
            return out.toByteArray()
        }



        fun bytesEquals(d1: ByteArray?, d2: ByteArray?): Boolean {
            if (d1 == null && d2 == null) return true
            if (d1 == null || d2 == null) return false
            if (d1.size != d2.size) return false
            for (i in d1.indices) if (d1[i] != d2[i]) return false
            return true
        }

        fun bytestochars(data: ByteArray): CharArray {
            val cdata = CharArray(data.size)
            for (i in cdata.indices) cdata[i] = (data[i] and 0xff).toChar()
            return cdata
        }

        fun getRandomByteArray(nlength: Int): ByteArray {
            val data = ByteArray(nlength)
            val rmByte = Random(System.currentTimeMillis())
            for (i in 0 until nlength) data[i] = rmByte.nextInt(256).toByte()
            return data
        }

        fun blackWhiteReverse(data: ByteArray) {
            for (i in data.indices) data[i] = (data[i] and 0xff).inv().toByte()
        }

        fun getSubBytes(org: ByteArray, start: Int, length: Int): ByteArray {
            val ret = ByteArray(length)
            for (i in 0 until length) ret[i] = org[i + start]
            return ret
        }

        fun byteToStr(rc: Byte): String {
            var tmp = Integer.toHexString(rc and 0xff)
            tmp = tmp.uppercase(Locale.getDefault())
            val rec: String
            rec =
                if (tmp.length == 1) StringBuilder("0x0").append(tmp).toString() else StringBuilder(
                    "0x"
                )
                    .append(tmp).toString()
            return rec
        }

        fun bytesToStr(rcs: ByteArray): String {
            val stringBuilder = StringBuilder()
            for (i in rcs.indices) {
                var tmp = Integer.toHexString(rcs[i] and 0xff)
                tmp = tmp.uppercase(Locale.getDefault())
                if (tmp.length == 1) stringBuilder.append(
                    StringBuilder("0x0").append(tmp).toString()
                ) else stringBuilder.append(
                    StringBuilder("0x").append(tmp).toString()
                )
                if (i % 16 != 15) stringBuilder.append(" ") else stringBuilder.append("\n")
            }
            return stringBuilder.toString()
        }

        fun cloneBytes(data: ByteArray): ByteArray {
            val ret = ByteArray(data.size)
            for (i in data.indices) ret[i] = data[i]
            return ret
        }


        fun byteArraysToBytes(data: Array<ByteArray>): ByteArray {
            var length = 0
            for (i in data.indices) length += data[i].size
            val send = ByteArray(length)
            var k = 0
            for (i in data.indices) {
                for (j in 0 until data[i].size) send[k++] = data[i][j]
            }
            return send
        }

        fun copyBytes(
            orgdata: ByteArray,
            orgstart: Int,
            desdata: ByteArray,
            desstart: Int,
            copylen: Int
        ) {
            for (i in 0 until copylen) desdata[desstart + i] = orgdata[orgstart + i]
        }
    }
}
