package org.gagauz.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Collection;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CryptoUtils {

	private static final String JOIN_STR = "\0";

	private static final String ALGORITHM = "AES";

	private static final Cipher encryptAES;

	private static final Cipher decryptAES;

	private static final String CH = "latin1";

	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	static {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec("password123456".toCharArray(), "sdfadsfds".getBytes(CH), 65536, 128);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
			encryptAES = Cipher.getInstance(ALGORITHM);
			encryptAES.init(Cipher.ENCRYPT_MODE, key);

			decryptAES = Cipher.getInstance(ALGORITHM);
			decryptAES.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] createSHA512(final String password) {
		if (null != password) {
			try {
				final MessageDigest md = MessageDigest.getInstance("SHA-512");
				md.update(password.getBytes());
				return md.digest();
			} catch (final NoSuchAlgorithmException e) {
				throw new IllegalStateException("JDK misses SHA-512?", e);
			}
		}
		return new byte[0];
	}

	public static String createSHA512String(final String password) {
		return bytesToHex(createSHA512(password));
	}

	public static String encryptAES(String valueToEnc) {
		try {
			byte[] encValue = encryptAES.doFinal(valueToEnc.getBytes(CH));
			return Base64.encodeBase64String(encValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decryptAES(String encryptedValue) {
		try {
			byte[] decordedValue = Base64.decodeBase64(encryptedValue.getBytes(CH));
			byte[] decValue = decryptAES.doFinal(decordedValue);
			return new String(decValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encryptArrayAES(Collection<String> strings) {
		return encryptArrayAES(strings.toArray());
	}

	public static String encryptArrayAES(Object... strings) {
		if (strings.length == 0) {
			return encryptAES("");
		}
		StringBuilder sb = new StringBuilder(String.valueOf(strings[0]));
		for (int i = 1; i < strings.length; i++) {
			sb.append(JOIN_STR).append(String.valueOf(strings[i]));
		}
		return encryptAES(sb.toString());
	}

	public static String[] decryptArrayAES(String string) {
		string = decryptAES(string);
		if (null != string) {
			return string.split(JOIN_STR);
		}
		return null;
	}
}
