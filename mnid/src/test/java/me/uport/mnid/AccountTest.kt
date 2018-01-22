package me.uport.mnid

import org.junit.Test

import org.junit.Assert.assertEquals

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
}