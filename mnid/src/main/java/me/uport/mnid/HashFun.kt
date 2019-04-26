package me.uport.mnid

import org.walleth.sha3.SHA3Parameter
import org.walleth.sha3.calculateSHA3

fun ByteArray.sha3() = this.calculateSHA3(SHA3Parameter.SHA3_256)