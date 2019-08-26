package me.uport.mnid

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

fun String.clean0xPrefix(): String =
    if (this.startsWith("0x")) this.substring(2) else this
