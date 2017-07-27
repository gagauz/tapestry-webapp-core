package org.apache.tapestry5.web.services.security;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CookieEncryptorDecryptor {

	private static final String JOIN_STR = "\0";

	private static final String ALGORITHM = "AES";

	private final Cipher encrypt;

	private final Cipher decrypt;

	private static final String CHARSET = "latin1";

	public CookieEncryptorDecryptor(String passphrase, String salt) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt.getBytes(CHARSET), 65536, 128);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
			encrypt = Cipher.getInstance(ALGORITHM);
			encrypt.init(Cipher.ENCRYPT_MODE, key);

			decrypt = Cipher.getInstance(ALGORITHM);
			decrypt.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String encrypt(String valueToEnc) {
		try {
			byte[] encValue = encrypt.doFinal(valueToEnc.getBytes(CHARSET));
			return new String(Base64.encodeBase64URLSafe(encValue), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String decrypt(String encryptedValue) {
		try {
			byte[] decordedValue = Base64.decodeBase64(encryptedValue);
			byte[] decValue = decrypt.doFinal(decordedValue);
			return new String(decValue, CHARSET);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String encryptArray(String... strings) {
		if (strings.length == 0) {
			return encrypt("");
		}
		StringBuilder sb = new StringBuilder(strings[0]);
		for (int i = 1; i < strings.length; i++) {
			sb.append(JOIN_STR).append(strings[i]);
		}
		return encrypt(sb.toString());
	}

	public String[] decryptArray(String string) {
		string = decrypt(string);
		return string.split(JOIN_STR);
	}
}
