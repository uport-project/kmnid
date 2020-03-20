package me.uport.mnid.demo

import me.uport.mnid.Account
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test

class MnidActivityTest {

    @Test
    fun `can run test`() {
        assertTrue(true)
    }

    @Test
    fun `can use library`() {
        val expected = Account.from("0x01", "0x0000000000000000000000000000000000001234")
        val acc = Account.from("2nQs23uc3UN6BBPqGHpbudDxBkeNVX7YRBb")
        Assert.assertEquals(expected, acc)
    }

}
