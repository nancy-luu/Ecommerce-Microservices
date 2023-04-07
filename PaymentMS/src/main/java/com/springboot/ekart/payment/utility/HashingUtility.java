package com.springboot.ekart.payment.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This is a utility class used to convert a string into its corresponding hashed format.
 * We are storing the hashed data and not actual data for security
 * Secured information includes password, pin, cvv.
 */

public class HashingUtility {
	
	public static String getHashedValue(String data) throws NoSuchAlgorithmException {
		
		/**
		 * MessageDigest provides applications the functionality of a message digest algorithm, such as SHA-1 or SHA-256.
		 * MessageDigest is a secure one-way hash function that take arbritrary-sized data and output a fixed-length hash value.
		 */
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		// Updates the digest using the specified array of bytes.
		md.update(data.getBytes());
		
		// md.digest() returns an array of:
		// 32 bytes if SHA-256 algorithm is used
		// 20 bytes if SHA-1 algorithm is used
		byte[] byteData = md.digest();
		
		// convert the byte to hex format
		StringBuffer hexString = new StringBuffer();
		for (int i=0; i<byteData.length;i++) {
			
			// converting each byte data to its corresponding positive number and storing the hexadecimal format of the same
			String hex = Integer.toHexString(0xff & byteData[i]);
			
			if(hex.length()==1) {
				hexString.append('0');
			}
			
			// appending the hexadecimal value to a string buffer
			hexString.append(hex);
		}
		
		return hexString.toString();
		
	}

}
