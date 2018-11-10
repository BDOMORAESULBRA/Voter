package br.edu.ulbra.election.voter.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String md5(String pass) {

		StringBuffer sb = new StringBuffer();

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(pass.getBytes());
			byte[] digest = messageDigest.digest();
			for (byte b : digest) {
				sb.append(Integer.toHexString((int) (b & 0xff)));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
