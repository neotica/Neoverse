package id.neotica.network.utils

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object LongCryptoUtil {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val IV_SIZE = 16

    // Convert a byte array to a SecretKey
    private fun getKeyFromBytes(keyBytes: ByteArray): Key {
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    // Generate a random IV
    private fun generateIv(): ByteArray {
        return ByteArray(IV_SIZE).apply {
            java.security.SecureRandom().nextBytes(this)
        }
    }

    // Encrypt a Long value
    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(value: Long): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = generateIv()
        val key = ByteArray(16) { 0x32 }
        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromBytes(key), IvParameterSpec(iv))

        val encryptedBytes = cipher.doFinal(value.toString().toByteArray())
        val ivAndCipher = iv + encryptedBytes // Prepend IV to the ciphertext
        return Base64.encode(ivAndCipher) //.getEncoder().encodeToString(ivAndCipher)
    }

    // Decrypt an encrypted string back to a Long
    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(encryptedValue: String): Long {
        val ivAndCipher = Base64.decode(encryptedValue) //.getDecoder().decode(encryptedValue)
        val iv = ivAndCipher.copyOfRange(0, IV_SIZE) // Extract IV
        val cipherBytes = ivAndCipher.copyOfRange(IV_SIZE, ivAndCipher.size)
        val key = ByteArray(16) { 0x32 }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getKeyFromBytes(key), IvParameterSpec(iv))

        val decryptedBytes = cipher.doFinal(cipherBytes)
        return String(decryptedBytes).toLong()
    }
}