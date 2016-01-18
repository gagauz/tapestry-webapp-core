package org.gagauz.utils.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final MessageDigest MD5;
    private static Object mutex = new Object();
    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
    }

    public static String calculate(String source) {
        synchronized (mutex) {
            MD5.reset();

            byte[] md5 = MD5.digest(source.getBytes());

            BigInteger bigInt = new BigInteger(1, md5);
            String md5Hex = bigInt.toString(16);
            while (md5Hex.length() < 32) {
                md5Hex = "0" + md5Hex;
            }
            return md5Hex;
        }
    }

}
