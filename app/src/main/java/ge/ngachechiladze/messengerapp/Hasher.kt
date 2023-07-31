package ge.ngachechiladze.messengerapp

import java.security.MessageDigest

const val CACHE_NICKNAME = "n1"
const val CACHE_ID = "n2"
const val CACHE_JOB = "n3"
const val SENDING_ID = 2
const val RECEIVING_ID = 1

object Hasher {
    public fun hashString(input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }
}