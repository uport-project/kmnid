package me.uport.mnid

import org.kethereum.encodings.decodeBase58
import org.kethereum.encodings.encodeToBase58String
import java.nio.ByteBuffer

/**
 * Multi Network Identifier
 *
 *
 * A safer way of expressing ethereum addresses on multiple networks
 *
 *
 * A Kotlin implementation ported from: https://github.com/uport-project/mnid
 *
 *
 * MNID is inspired by the Base58Check encoding as well as EIP77 but also specifies a network
 * identifier, which allows us to programmatically extract the network used by an address
 * as well as provide a visual indicator of the network used.
 *
 *
 * The following items are encoded:
 *
 *  * 1 byte version number currently 1
 *  * network id or four bytes of genesis block hash (or both)
 *  * actual address data
 *  * Four bytes (32 bits) of SHA3-based error checking code (digest of the version, network and payload)
 *
 * Then base58 encoding is applied to the end result.
 *
 *
 * TODO: document exceptions
 */
object MNID {

    private const val VERSION_WIDTH = 1
    private const val ADDRESS_WIDTH = 20
    private const val CHECKSUM_WIDTH = 4
    private const val VERSION: Byte = 1

    /**
     * Decodes a MNID encoded address into an address and network.
     *
     * @param mnid the mnid encoded account
     * @return an Account object encapsulating the address and network as hex strings prefixed with `0x`
     * @throws MnidEncodingException if there is an error within the mnid string
     */
    @Throws(MnidEncodingException::class)
    fun decode(mnid: String?): Account {
        if (mnid == null || mnid.isEmpty()) {
            throw MnidEncodingException("Can't decode a null or empty mnid")
        }

        val rawMnid = mnid.decodeBase58()
        val buff = ByteBuffer.wrap(rawMnid)

        val version = buff.get()

        if (version > VERSION) {
            throw MnidEncodingException("Version mismatch.\nCan't decode a future version of MNID. Expecting $VERSION and got $version")
        }

        val networkLen = rawMnid.size - VERSION_WIDTH - CHECKSUM_WIDTH - ADDRESS_WIDTH
        if (networkLen <= 0) {
            throw MnidEncodingException("Buffer size mismatch.\nThere are not enough bytes in this mnid to encode an address")
        }

        // read the raw network and address
        val networkBytes = ByteArray(networkLen)
        buff.get(networkBytes)
        val addressBytes = ByteArray(ADDRESS_WIDTH)
        buff.get(addressBytes)

        // check for checksum match
        buff.rewind()
        val payloadBytes = ByteArray(rawMnid.size - CHECKSUM_WIDTH)
        buff.get(payloadBytes)
        val checksumBytes = ByteArray(CHECKSUM_WIDTH)
        buff.get(checksumBytes)

        val payloadCheck = payloadBytes.sha3().sliceArray(0 until CHECKSUM_WIDTH)
        if (!checksumBytes.contentEquals(payloadCheck)) {
            throw MnidEncodingException("The checksum does not match the payload")
        }

        return Account(networkBytes, addressBytes)
    }

    /**
     * Encodes an Account into a MNID string.
     *
     * @param account an Account encapsulating an address and network
     * @return the MNID encoding representing that address and network
     */
    fun encode(account: Account?): String {
        return if (account == null) {
            encode("00", "00")
        } else encode(account.network, account.address)
    }

    /**
     * Encodes an address and a network into a MNID string.
     *
     * @param network A hex string possibly prefixed by `0x`
     * @param address A hex string possibly prefixed by `0x`
     * @return the MNID encoding representing that address and network
     */
    fun encode(network: String, address: String): String {
        val addressBytes = address.hexToByteArrayLenient()
        val networkBytes = network.hexToByteArrayLenient()

        if (addressBytes.size > ADDRESS_WIDTH) {
            throw MnidEncodingException("Address is too long.\nAn Ethereum address must be 20 bytes long.")
        }

        val buff = ByteBuffer.allocate(VERSION_WIDTH
                + networkBytes.size
                + ADDRESS_WIDTH
                + CHECKSUM_WIDTH)
        //version
        buff.put(VERSION)

        //network
        buff.put(networkBytes)

        //address
        if (addressBytes.size < ADDRESS_WIDTH) {
            val numZeros = ADDRESS_WIDTH - addressBytes.size
            buff.put(ByteArray(numZeros))
        }
        buff.put(addressBytes)

        //checksum
        val payload = buff.array()
                .sliceArray(0 until (buff.capacity() - CHECKSUM_WIDTH))
        val hash = payload.sha3()
        val checksumBytes = hash.sliceArray(0 until CHECKSUM_WIDTH)
        buff.put(checksumBytes)

        //that's a wrap
        return buff.array().encodeToBase58String()
    }

    /**
     * Checks if a string is a MNID encoding of something
     *
     * @param candidate a string to be checked
     * @return `true` if it looks like MNID (enough bytes and proper version),
     * `false` otherwise or if there's an exception
     */
    fun isMNID(candidate: String?): Boolean {
        val input = candidate ?: return false
        return try {
            val raw = input.decodeBase58()
            raw.size > VERSION_WIDTH + CHECKSUM_WIDTH + ADDRESS_WIDTH && raw[0] == VERSION
        } catch (e: Exception) {
            false
        }

    }
}
