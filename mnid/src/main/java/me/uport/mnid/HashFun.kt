package me.uport.mnid

import org.spongycastle.jcajce.provider.digest.SHA3

fun ByteArray.sha3() = SHA3.Digest256().let {
    it.update(this)
    it.digest()
}!!