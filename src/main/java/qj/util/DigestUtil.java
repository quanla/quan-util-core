package qj.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtil {
	public static String digest(String message) {
		return digest(message, "MD5");
	}

	public static String digest(String message, String algo) {
		if (message==null) {
			return null;
		}
		try {
			byte[] bytesOfMessage = message.getBytes("UTF-8");

			MessageDigest md = MessageDigest.getInstance(algo);
			byte[] thedigest = md.digest(bytesOfMessage);
			return StringUtil.bytesToHexString(thedigest);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

	}

	public static String digestAscii(String message, String algo) {
		if (message==null) {
			return null;
		}
		byte[] bytesOfMessage = message.getBytes();
		
		return digest(bytesOfMessage, algo);

	}

	private static String digest(byte[] bytesOfMessage, String algo) {
		try {

			MessageDigest md = MessageDigest.getInstance(algo);
			byte[] thedigest = md.digest(bytesOfMessage);
			
			StringBuilder hexString = new StringBuilder(thedigest.length);
			for (int i=0;i<thedigest.length;i++) {
				hexString.append(StringUtil.ensureLength(Integer.toHexString(0xFF & thedigest[i]), 2, false, '0'));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String digest(File file, String algo) {
		return digest(FileUtil.readFileToBytes(file), algo);
	}
	
	public static void main(String[] args) {
//		System.out.println(digestAscii("U2999272:U2563505:GunnyTool_beta:0.02:83830188:LRUSD:rt349 fej98w 30413r","SHA-256").toUpperCase());
		//F9F0364A8BBC7DE6B88D3527F5D293C6F344154232225209C2287F8F637CEC89
		//F9F0364A8BBC7DE6B88D3527F5D293C6F344154232225209C2287F8F637CEC89
		
//		System.out.println(digest(new File("/Users/quanle/Documents/Workon/qj-svn_tkqn/commercial-apps/gunny-tool/todo.txt"), "SHA-1"));
//		System.out.println(digest("123!@#qwe"));
		System.out.println(digest("q")); // f6147426e39665bc0c5d1d19d43de19d
	}

}
