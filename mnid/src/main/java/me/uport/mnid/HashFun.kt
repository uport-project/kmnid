package me.uport.mnid

import org.komputing.khash.keccak.Keccak
import org.komputing.khash.keccak.KeccakParameter

fun ByteArray.sha3() = Keccak.digest(this, KeccakParameter.SHA3_256)