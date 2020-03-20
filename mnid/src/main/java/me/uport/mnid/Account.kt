package me.uport.mnid

import org.komputing.khex.extensions.clean0xPrefix
import org.komputing.khex.extensions.toHexString
import org.komputing.khex.model.HexString

/**
 * A class that encapsulates an ethereum address and the network it corresponds to
 */

data class Account internal constructor(val network: String, val address: String) {

    companion object {

        fun from(mnid: String): Account {
            return MNID.decode(mnid)
        }

        fun from(network: String?, address: String?): Account {
            if (address == null || network == null) {
                throw NullPointerException("can't create an Account object using null network or address")
            }

            var strippedAddress = HexString(address).clean0xPrefix().string
            val numZeros = 40 - strippedAddress.length
            if (numZeros > 0) {
                strippedAddress = String.format("%0" + numZeros + "d", 0) + strippedAddress
            } else if (numZeros < 0) {
                throw MnidEncodingException("Address is too long.\nAn Ethereum address must be 20 bytes long.")
            }

            val formattedNetwork = network.hexToByteArrayLenient().toHexString()

            return Account(formattedNetwork, "0x$strippedAddress")
        }
    }

    internal constructor(network: ByteArray, address: ByteArray) : this(
        network.toHexString(),
        address.toHexString()
    )
}
