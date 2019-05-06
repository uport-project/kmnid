# KMNID
A kotlin implementation of [MNID](https://github.com/uport-project/mnid)

# Multi Network Identifier (MNID)

Ethereum, and uPort, is entering a multi-chain world.
As end users increasingly interact with multiple chains, on Ethereum or elsewhere, 
the risk of users/servers inadvertently transferring value from an address on network X 
to an address on network Y is growing. This could result in monetary loss. 
Since uPort is switching to a new test network, we need to solve this issue urgently.

The Bitcoin protocol uses [Base58Check encoding](https://en.bitcoin.it/wiki/Base58Check_encoding) 
to prevent users from sending value off-network, but the ethereum ecosystem has used a raw hex 
version of the address instead.

## Encoding scheme

The [original proposal](https://github.com/uport-project/mnid) is inspired by the 
Base58Check encoding as well as [EIP77](https://github.com/ethereum/EIPs/issues/77)
but also specifies a network identifier, which allows us to programmatically extract the network
used by an address as well as provide a visual indicator of the network used.

The following items are encoded:

* 1 byte version number currently `1`
* network id or four bytes of genesis block hash (or both)
* actual address data
* Four bytes (32 bits) of SHA3-based error checking code (digest of the version, network and payload)

Then base58 encoding is applied to the end result.
The end result is fairly complete but still extendible in the future.
We could start by simply using the network id and replace it with the genesis block hash
and other meta data in the future.

### Examples

The following Ethereum hex encoded address `0x00521965e7bd230323c423d96c657db5b79d099f`
could be encoded as follows:

* main-net: `2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqX`
* ropsten: `2oDZvNUgn77w2BKTkd9qKpMeUo8EL94QL5V`
* kovan: `34ukSmiK1oA1C5Du8aWpkjFGALoH7nsHeDX`
* infuranet: `9Xy8yQpdeCNSPGQ9jwTha9MRSb2QJ8HYzf1u`

## Usage

#### Import:

```groovy
repositories {
    //...
    maven { url 'https://jitpack.io' }
}

dependencies {
    //...
    compile "com.github.uport-project:kmnid:0.3.1"
}

```

#### Encode
```kotlin
val mnid = MNID.encode(
  network = '0x1', // the hex encoded network id or for private chains the hex encoded first 4 bytes of the genesis hash
  address = '0x00521965e7bd230323c423d96c657db5b79d099f'
)


assertEquals('2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqX', mnid)
```
#### Decode

```kotlin

val account = MNID.decode('2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqX')
assertEquals('0x1', account.network) 
assertEquals('0x00521965e7bd230323c423d96c657db5b79d099f', account.address)
```

### Check

```kotlin
// Check if string is a valid MNID

assertTrue( MNID.isMNID('2nQtiQG6Cgm1GYTBaaKAgr76uY7iSexUkqX') )


//bad encoding (ethereum address)
assertFalse( MNID.isMNID('0x00521965e7bd230323c423d96c657db5b79d099f') )


//bad encoding (bitcoin address)
assertFalse( MNID.isMNID('1GbVUSW5WJmRCpaCJ4hanUny77oDaWW4to') )


//bad encoding (ipfs hash)
assertFalse( MNID.isMNID('QmXuNqXmrkxs4WhTDC2GCnXEep4LUD87bu97LQMn1rkxmQ') )
```

## Changelog

* 0.3.1 - maintenance release
    * updated build scripts

* 0.3
    * removed spongycastle dependency for hashing,
    using [KHash](https://github.com/komputing/KHash) instead
    * targeting java 1.8
    * updated [KEthereum](https://github.com/komputing/KEthereum) base58 dependency to 0.75.0
* 0.2.1
    * rebuild
* 0.2
    * reduce library footprint by only using the base58 lib from kethereum
* 0.1
    * initial release