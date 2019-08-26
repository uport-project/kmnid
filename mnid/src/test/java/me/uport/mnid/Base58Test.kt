package me.uport.mnid

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.kethereum.encodings.decodeBase58
import org.kethereum.encodings.encodeToBase58String
import org.komputing.khex.extensions.hexToByteArray
import java.util.Random

class Base58Test {

    private val invalids = arrayOf("invalid", "invaOid", "inva0id", "invaIid", " 1111", "1111 ")

    //hex string and corresponding base58 encoding
    private val validPairs = arrayOf(
        TestPair("ef41b9ce7e830af7", "h26E62FyLQN"),
        TestPair("606cbc791036d2e9", "H8Sa62HVULG"),
        TestPair("bdcb0ea69c2c8ec8", "YkESUPpnfoD"),
        TestPair("1a2358ba67fb71d5", "5NaBN89ajtQ"),
        TestPair("e6173f0f4d5fb5d7", "fVAoezT1ZkS"),
        TestPair("91c81cbfdd58bbd2", "RPGNSU3bqTX"),
        TestPair("329e0bf0e388dbfe", "9U41ZkwwysT"),
        TestPair("30b10393210fa65b", "99NMW3WHjjY"),
        TestPair("ab3bdd18e3623654", "VeBbqBb4rCT"),
        TestPair("fe29d1751ec4af8a", "jWhmYLN9dUm"),
        TestPair("c1273ab5488769807d", "3Tbh4kL3WKW6g"),
        TestPair("6c7907904de934f852", "2P5jNYhfpTJxy"),
        TestPair("05f0be055db47a0dc9", "5PN768Kr5oEp"),
        TestPair("3511e6206829b35b12", "gBREojGaJ6DF"),
        TestPair("d1c7c2ddc4a459d503", "3fsekq5Esq2KC"),
        TestPair("1f88efd17ab073e9a1", "QHJbmW9ZY7jn"),
        TestPair("0f45dadf4e64c5d5c2", "CGyVUMmCKLRf"),
        TestPair("de1e5c5f718bb7fafa", "3pyy8U7w3KUa5"),
        TestPair("123190b93e9a49a46c", "ES3DeFrG1zbd"),
        TestPair("8bee94a543e7242e5a", "2nJnuWyLpGf6y"),
        TestPair("9fd5f2285362f5cfd834", "9yqFhqeewcW3pF"),
        TestPair("6987bac63ad23828bb31", "6vskE5Y1LhS3U4"),
        TestPair("19d4a0f9d459cc2a08b0", "2TAsHPuaLhh5Aw"),
        TestPair("a1e47ffdbea5a807ab26", "A6XzPgSUJDf1W5"),
        TestPair("35c231e5b3a86a9b83db", "42B8reRwPAAoAa"),
        TestPair("b2351012a48b8347c351", "B1hPyomGx4Vhqa"),
        TestPair("71d402694dd9517ea653", "7Pv2SyAQx2Upu8"),
        TestPair("55227c0ec7955c2bd6e8", "5nR64BkskyjHMq"),
        TestPair("17b3d8ee7907c1be34df", "2LEg7TxosoxTGS"),
        TestPair("7e7bba7b68bb8e95827f", "879o2ATGnmYyAW"),
        TestPair("db9c13f5ba7654b01407fb", "wTYfxjDVbiks874"),
        TestPair("6186449d20f5fd1e6c4393", "RBeiWhzZNL6VtMG"),
        TestPair("5248751cebf4ad1c1a83c3", "MQSVNnc8ehFCqtW"),
        TestPair("32090ef18cd479fc376a74", "DQdu351ExDaeYeX"),
        TestPair("7cfa5d6ed1e467d986c426", "XzW67T5qfEnFcaZ"),
        TestPair("9d8707723c7ede51103b6d", "g4eTCg6QJnB1UU4"),
        TestPair("6f4d1e392d6a9b4ed8b223", "Ubo7kZY5aDpAJp2"),
        TestPair("38057d98797cd39f80a0c9", "EtjQ2feamJvuqse"),
        TestPair("de7e59903177e20880e915", "xB2N7yRBnDYEoT2"),
        TestPair("b2ea24a28bc4a60b5c4b8d", "mNFMpJ2P3TGYqhv"),
        TestPair("cf84938958589b6ffba6114d", "4v8ZbsGh2ePz5sipt"),
        TestPair("dee13be7b8d8a08c94a3c02a", "5CwmE9jQqwtHkTF45"),
        TestPair("14cb9c6b3f8cd2e02710f569", "Pm85JHVAAdeUdxtp"),
        TestPair("ca3f2d558266bdcc44c79cb5", "4pMwomBAQHuUnoLUC"),
        TestPair("c031215be44cbad745f38982", "4dMeTrcxiVw9RWvj3"),
        TestPair("1435ab1dbc403111946270a5", "P7wX3sCWNrbqhBEC"),
        TestPair("d8c6e4d775e7a66a0d0f9f41", "56GLoRDGWGuGJJwPN"),
        TestPair("dcee35e74f0fd74176fce2f4", "5Ap1zyuYiJJFwWcMR"),
        TestPair("bfcc0ca4b4855d1cf8993fc0", "4cvafQW4PEhARKv9D"),
        TestPair("e02a3ac25ece7b54584b670a", "5EMM28xkpxZ1kkVUM"),
        TestPair("fe4d938fc3719f064cabb4bfff", "NBXKkbHwrAsiWTLAk6"),
        TestPair("9289cb4f6b15c57e6086b87ea5", "DCvDpjEXEbHjZqskKv"),
        TestPair("fc266f35626b3612bfe978537b", "N186PVoBWrNre35BGE"),
        TestPair("33ff08c06d92502bf258c07166", "5LC4SoW6jmTtbkbePw"),
        TestPair("6a81cac1f3666bc59dc67b1c3c", "9sXgUySUzwiqDU5WHy"),
        TestPair("9dfb8e7e744c544c0f323ea729", "EACsmGmkgcwsrPFzLg"),
        TestPair("1e7a1e284f70838b38442b682b", "3YEVk9bE7rw5qExMkv"),
        TestPair("2a862ad57901a8235f5dc74eaf", "4YS259nuTLfeXa5Wuc"),
        TestPair("74c82096baef21f9d3089e5462", "AjAcKEhUfrqm8smvM7"),
        TestPair("7a3edbc23d7b600263920261cc", "BBZXyRgey5S5DDZkcK"),
        TestPair("20435664c357d25a9c8df751cf4f", "CrwNL6Fbv4pbRx1zd9g"),
        TestPair("51a7aa87cf5cb1c12d045ec3422d", "X27NHGgKXmGzzQvDtpC"),
        TestPair("344d2e116aa26f1062a2cb6ebbef", "LEDLDvL1Hg4qt1efVXt"),
        TestPair("6941add7be4c0b5c7163e4928f8e", "fhMyN6gwoxE3uYraVzV"),
        TestPair("10938fcbb7c4ab991649734a14bf", "76TPrSDxzGQfSzMu974"),
        TestPair("eafe04d944ba504e9af9117b07de", "2VPgov563ryfe4L2Bj6M"),
        TestPair("58d0aeed4d35da20b6f052127edf", "ZenZhXF9YwP8nQvNtNz"),
        TestPair("d734984e2f5aecf25f7a3e353f8a", "2N7n3jFsTdyN49Faoq6h"),
        TestPair("57d873fdb405b7daf4bafa62068a", "ZJ7NwoP4wHvwyZg3Wjs"),
        TestPair("bda4ec7b40d0d65ca95dec4c4d3b", "2CijxjsNyvqTwPCfDcpA"),
        TestPair("826c4abdceb1b91f0d4ad665f86d2e", "4edfvuDQu9KzVxLuXHfMo"),
        TestPair("e7ecb35d07e65b960cb10574a4f51a", "7VLRYdB4cToipp2J2p3v9"),
        TestPair("4f2d72ead87b31d6869fba39eac6dc", "3DUjqJRcfdWhpsrLrGcQs"),
        TestPair("8b4f5788d60030950d5dfbf94c585d", "4u44JSRH5jP5X39YhPsmE"),
        TestPair("ee4c0a0025d1a74ace9fe349355cc5", "7fgACjABRQUGUEpN6VBBA"),
        TestPair("58ac05b9a0b4b66083ff1d489b8d84", "3UtJPyTwGXapcxHx8Rom5"),
        TestPair("1aa35c05e1132e8e049aafaef035d8", "kE2eSU7gM2619pT82iGP"),
        TestPair("771b0c28608484562a292e5d5d2b30", "4LGYeWhyfrjUByibUqdVR")
    )

    data class TestPair(val hex: String, val base58: String)

    @Test
    fun checkEncodeDecode() {

        val r = Random()
        r.setSeed(0)

        for (i in 0..99999) {

            var numBytes = r.nextInt(128)
            numBytes = Math.max(numBytes, 1) //avoid zero length
            val buff = ByteArray(numBytes)

            r.nextBytes(buff)

            val encoded = buff.encodeToBase58String()
            val decoded = encoded.decodeBase58()

            assertArrayEquals(
                "The decoded byte array does not match what was encoded",
                decoded,
                buff
            )
        }
    }

    @Test
    fun checkEncodeEmpty() {
        val encoded = ByteArray(0).encodeToBase58String()
        assertEquals(encoded, "")
    }

    @Test
    fun checkDecodeEmpty() {
        val decoded = "".decodeBase58()
        assertEquals(decoded.size.toLong(), 0)
    }

    @Test
    fun checkEncodeText() {
        val encoded = "hello".toByteArray().encodeToBase58String()
        assertEquals(encoded, "Cn8eVZg")
    }

    @Test
    fun checkThrowsOnInvalidChar() {
        for (invalid in invalids) {
            try {
                invalid.decodeBase58()
            } catch (ex: Exception) {
                assert(ex is NumberFormatException)
            }

        }
    }

    @Test
    fun checkDecodeToByteArray() {
        validPairs.forEach {
            val decoded = it.base58.decodeBase58()
            val actual = it.hex.hexToByteArray()
            assertArrayEquals("invalid pair at: ${it.hex}:${it.base58}", decoded, actual)
        }
    }

    @Test
    fun checkEncodeByteArray() {

        validPairs.forEach {
            val input = it.hex.hexToByteArray()
            val encoded = input.encodeToBase58String()
            assertEquals("invalid pair at: ${it.hex}:${it.base58}", it.base58, encoded)
        }
    }
}