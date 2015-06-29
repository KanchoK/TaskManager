package taskManager.utils;

import java.security.MessageDigest;

import org.apache.commons.lang3.RandomStringUtils;


public class SecurityUtils {

	/**
	 * generates random password for new users using apache library
	 * @return generated password
	 */
	public static String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(20).toUpperCase();
	}
	
	/**
	 * encodes a given password
	 * @param password password to be encoded
	 * @return encoded password
	 */
    public static String getHashedPassword(String password) {
        try {
            MessageDigest mda = MessageDigest.getInstance("SHA-512");
            password = new String(mda.digest(password.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }
	
}
