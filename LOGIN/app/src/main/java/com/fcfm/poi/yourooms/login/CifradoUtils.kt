package com.fcfm.poi.yourooms.login

import android.util.Base64
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CifradoUtils {

    private const val CIPHER_TRANSFORM = "AES/CBC/PKCS5PADDING"

    fun cifrar(textoPlano: String, llaveSecreta: String): String {

        val cipher = Cipher.getInstance(CIPHER_TRANSFORM)

        val llaveBytesFinal = ByteArray(16)
        val llaveBytesOriginal = llaveSecreta.toByteArray(charset("UTF-8"))

        System.arraycopy(
            llaveBytesOriginal, 0, llaveBytesFinal, 0,
            Math.min(llaveBytesOriginal.size, llaveBytesFinal.size)
        )

        val secretKeySpec = SecretKeySpec(llaveBytesFinal, "AES")

        val initVector = IvParameterSpec(llaveBytesFinal)

        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKeySpec,
            initVector
        )

        val textoCifrado = cipher.doFinal(textoPlano.toByteArray(charset("UTF-8")))

        val resultadoBase64 = String(Base64.encode(textoCifrado, Base64.NO_PADDING))

        return resultadoBase64
    }

    fun descifrar(textoCifrado: String, llaveSecreta: String): String {

        val textoCifradoBytes = Base64.decode(textoCifrado, Base64.NO_PADDING)

        val cipher = Cipher.getInstance(CIPHER_TRANSFORM)

        val llaveBytesFinal = ByteArray(16)
        val llaveBytesOriginal = llaveSecreta.toByteArray(charset("UTF-8"))

        System.arraycopy(
            llaveBytesOriginal, 0, llaveBytesFinal, 0,
            Math.min(llaveBytesOriginal.size, llaveBytesFinal.size)
        )

        val secretKeySpec = SecretKeySpec(llaveBytesFinal, "AES")
        val initVector = IvParameterSpec(llaveBytesFinal)

        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKeySpec,
            initVector
        )

        val textoPlanoRecuperado = try {
            String(cipher.doFinal(textoCifradoBytes))
        } catch (ex: BadPaddingException) {
            "Contraseña incorrecta"
        }

        return textoPlanoRecuperado
    }
}