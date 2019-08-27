package me.uport.mnid

import org.komputing.khex.extensions.clean0xPrefix

fun String.hexToByteArrayLenient(): ByteArray {
    val cleanInput = this.clean0xPrefix()
    val evenInput = if (cleanInput.length % 2 != 0) "0$cleanInput" else cleanInput

    return ByteArray(evenInput.length / 2).apply {
        var i = 0
        while (i < evenInput.length) {
            this[i / 2] = ((Character.digit(evenInput[i], 16) shl 4) + Character.digit(
                evenInput[i + 1],
                16
            )).toByte()
            i += 2
        }
    }
}
