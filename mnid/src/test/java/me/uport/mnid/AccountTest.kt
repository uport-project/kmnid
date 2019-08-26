package me.uport.mnid

import org.junit.Assert.assertEquals
import org.junit.Test

class AccountTest {

    @Test
    fun compareOddAccountEncoding() {
        val athos = Account.from("0x1", "0x1234")
        val porthos = Account.from("0x01", "0x1234")
        val aramis = Account.from("0x1", "0x01234")
        val dartagnan = Account.from("0x01", "0x01234")
        assertEquals(athos, porthos)
        assertEquals(porthos, aramis)
        assertEquals(aramis, dartagnan)
    }

    @Test(expected = NumberFormatException::class)
    fun throwsOnWeirdCharacters() {
        Account.from("2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexlIO0")
    }

    @Test
    fun `can create account from mnid`() {
        val expected = Account("0x01", "0x0000000000000000000000000000000000001234")
        val acc = Account.from("2nQs23uc3UN6BBPqGHpbudDxBkeNVX7YRBb")
        assertEquals(expected, acc)
    }

    @Test(expected = MnidEncodingException::class)
    fun `throws when given invalid address`() {
        Account.from("0x01", "0x00000000000000000000000000000000000012345678")
    }

    @Test(expected = NullPointerException::class)
    fun `throws when given null network`() {
        Account.from(null, "0x0000000000000000000000000000000000001234")
    }

    @Test(expected = NullPointerException::class)
    fun `throws when given null address`() {
        Account.from("0x1", null)
    }
}