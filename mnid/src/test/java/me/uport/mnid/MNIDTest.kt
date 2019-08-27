package me.uport.mnid

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MNIDTest {

    @Test
    fun mainNetEncode() {
        val encoded = MNID.encode("0x1", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqX", encoded)
    }

    @Test
    fun genesisHashEncode() {
        val encoded = MNID.encode("0x94365e3a", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals("5A8bRWU3F7j3REx3vkJWxdjQPp4tqmxFPmab1Tr", encoded)
    }

    @Test
    fun ropstenEncode() {
        val encoded = MNID.encode("0x3", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals("2oDZvNUgn77w2BKTkd9qKpMeUo8EL94QL5V", encoded)
    }

    @Test
    fun kovanEncode() {
        val encoded = MNID.encode("0x2a", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals("34ukSmiK1oA1C5Du8aWpkjFGALoH7nsHeDX", encoded)
    }

    @Test
    fun infuranetEncode() {
        val encoded = MNID.encode("0x16b2", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals("9Xy8yQpdeCNSPGQ9jwTha9MRSb2QJ8HYzf1u", encoded)
    }

    @Test
    fun decodeMainNet() {
        val decoded = MNID.decode("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqX")
        val expected = Account.from("0x01", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals(expected, decoded)
    }

    @Test
    fun encodeShortAddress() {
        val encoded = MNID.encode("0x1", "0x1234")
        assertEquals(Account.from("0x1", "0x1234"), MNID.decode(encoded))
    }

    @Test
    fun compareOddEncoding() {
        val athos = MNID.encode("0x1", "0x1234")
        val porthos = MNID.encode("0x01", "0x1234")
        val aramis = MNID.encode("0x1", "0x01234")
        val dartagnan = MNID.encode("0x01", "0x01234")
        assertEquals(athos, porthos)
        assertEquals(porthos, aramis)
        assertEquals(aramis, dartagnan)
    }

    @Test
    fun compareDifferentNetworkEncodings() {
        // 1 byte network
        val blofeld = MNID.encode("0x7", "0x1234")

        // 2 byte network
        val bond = MNID.encode("0x007", "0x1234")
        assertNotEquals(blofeld, bond)
    }

    @Test
    fun compareDirectEncodingVsAccountEncoding() {
        val direct = MNID.encode("0x7", "0x1234")
        val acc = MNID.encode(Account.from("0x7", "0x1234"))

        assertEquals(direct, acc)
    }

    @Test
    fun decodeRopsten() {
        val decoded = MNID.decode("2oDZvNUgn77w2BKTkd9qKpMeUo8EL94QL5V")
        val expected = Account.from("0x03", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals(expected, decoded)
    }

    @Test
    fun decodeKovan() {
        val decoded = MNID.decode("34ukSmiK1oA1C5Du8aWpkjFGALoH7nsHeDX")
        val expected = Account.from("0x2a", "0x00521965e7bd230323c423d96c657db5b79d099f")
        assertEquals(expected, decoded)
    }

    @Test
    fun badChecksum() {
        try {
            MNID.decode("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqU")
        } catch (e: Exception) {

            assert(e is MnidEncodingException)
            assertEquals("The checksum does not match the payload", e.message)
        }

    }

    @Test(expected = NumberFormatException::class)
    fun badCharacter() {
        MNID.decode("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexlIO0")
    }

    @Test
    fun badVersion() {
        try {
            MNID.decode("4nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqU")
        } catch (e: Exception) {
            assert(e is MnidEncodingException)
            assertEquals(
                "Version mismatch.\nCan't decode a future version of MNID. Expecting 1 and got 2",
                e.message
            )
        }

    }

    @Test
    fun badLength() {
        try {
            MNID.decode("2nQtiQG6CYTBaaKAgr76uY7iSexUkqU")
        } catch (e: Exception) {
            assert(e is MnidEncodingException)
            assertEquals(
                "Buffer size mismatch.\nThere are not enough bytes in this mnid to encode an address",
                e.message
            )
        }

    }

    @Test
    fun addressTooLong() {
        try {
            MNID.encode("0x1", "0xaa00521965e7bd230323c423d96c657db5b79d099f")
        } catch (e: Exception) {
            assert(e is MnidEncodingException)
            assertEquals(
                "Address is too long.\nAn Ethereum address must be 20 bytes long.",
                e.message
            )
        }

    }

    @Test
    fun isMnidValid() {
        assertTrue(MNID.isMNID("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqX"))
        assertTrue(MNID.isMNID("5A8bRWU3F7j3REx3vkJWxdjQPp4tqmxFPmab1Tr"))
        assertTrue(MNID.isMNID("2oDZvNUgn77w2BKTkd9qKpMeUo8EL94QL5V"))
        assertTrue(MNID.isMNID("34ukSmiK1oA1C5Du8aWpkjFGALoH7nsHeDX"))
        // bad checksum but still MNID
        assertTrue(MNID.isMNID("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqU"))
    }

    @Test
    fun isMnidInValid() {
        // Ethereum Hex
        assertFalse(MNID.isMNID("0x00521965e7bd230323c423d96c657db5b79d099f"))
        // Bitcoin
        assertFalse(MNID.isMNID("1GbVUSW5WJmRCpaCJ4hanUny77oDaWW4to"))

        // IPFS
        assertFalse(MNID.isMNID("QmXuNqXmrkxs4WhTDC2GCnXEep4LUD87bu97LQMn1rkxmQ"))

        // Cut off
        assertFalse(MNID.isMNID("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkq"))

        // bad char
        assertFalse(MNID.isMNID("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexlIO0"))

        assertFalse(MNID.isMNID(""))
        assertFalse(MNID.isMNID(null))
    }

    @Test(expected = MnidEncodingException::class)
    fun `throws on null input`() {
        MNID.decode(null)
    }

    @Test(expected = MnidEncodingException::class)
    fun `throws on empty input`() {
        MNID.decode("")
    }

    @Test(expected = MnidEncodingException::class)
    fun `throws on blank input`() {
        MNID.decode(" ")
    }

    @Test
    fun `encodes blank mnid when given null input`() {
        assertEquals("2n1XR4oJkmBdJMxhBGQGb96gQ88xV6zpStY", MNID.encode(null))
    }

}
