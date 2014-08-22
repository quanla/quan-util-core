package qj.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;

public class EncryptUtil4 {

	private static final String ALGO = "AES"; // DES
	private static final SecretKey key = secretKey("PlsD0n'tHackMe0k");
	
	public static final byte[] encrypt(byte[] data,String key) {
		return encrypt(data, secretKey(key));
	}

	public static SecretKey secretKey(final String key) {
		return new SecretKey() {
			private static final long serialVersionUID = -829558999158937420L;

			public String getAlgorithm() {
				return ALGO;
			}

			public byte[] getEncoded() {
				return key.getBytes();
			}

			public String getFormat() {
				return "RAW";
			}

			@Override
			public void destroy() throws DestroyFailedException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isDestroyed() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}

	public static final byte[] encrypt(byte[] data) {
		return encrypt(data, key);
	}

	public static byte[] encrypt(byte[] data, Key key) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, key);

			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	public static final byte[] decrypt(byte[] data) {
		return decrypt(data, key);
	}
	public static byte[] decrypt(byte[] data, String key) {
		return decrypt(data,secretKey(key));
	}

	public static byte[] decrypt(byte[] data, Key key) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key);

			return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void encryptL(byte[] bytes, int length) {
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (bytes[i] + 29);
		}
	}
	public static void decryptL(byte[] bytes, int length) {
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (bytes[i] - 29);
		}
	}

	public static void encryptL(InputStream in, OutputStream out) throws IOException {
        int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];
        for (int read;(read=in.read(buffer, 0, bufferSize)) > -1;) {
        	encryptL(buffer, read);
            out.write(buffer, 0, read);
        }
	}

	public static String encrypt(String msg) {
		return StringUtil.bytesToHexString(encrypt(msg.getBytes()));
	}
	public static String decrypt(String name) {
		return new String(decrypt(StringUtil.hexToBytes(name)));
	}

	public static byte[] sign(byte[] bytes, PrivateKey privateKey) {
		try {
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initSign(privateKey);
			dsa.update(bytes,0,bytes.length);
			byte[] sign = dsa.sign();
			return sign;
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean verify(byte[] sign, byte[] bytes, PublicKey publicKey) {
		try {
			Signature dsa1 = Signature.getInstance("SHA1withDSA", "SUN");
			dsa1.initVerify(publicKey);
//		byte[] signatureBytes = StringUtil.hexToBytes(signature);
			dsa1.update(bytes,0,bytes.length);
			boolean verify = dsa1.verify(sign);
			return verify;
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}
	
//	public static void main(String[] args) {
//		decryptStream(FileUtil.fileInputStream(null))
//		
//		
//		SecretKey secretKey = secretKey("PlsD0n'tFackMe0k");
//		long start = System.currentTimeMillis();
//		for (int i = 0; i < 1000; i++) {
//			byte[] bytes = FileUtil.readFileToBytes("/Users/quanle/Documents/Workon/qj-svn/commercial-apps/gunny-tool/gt-support/target/client_update/data/img/wind.pack");
////			decrypt(encrypt, secretKey);
//		}
////		byte[] encrypt = encrypt(bytes, secretKey);
//		System.out.println("Time=" + (System.currentTimeMillis() - start));
//	}
	
	public static void main1(String[] args) {

//		KeyPair pair = keyPair();
//		PrivateKey priv = pair.getPrivate();
//		PublicKey pub = pair.getPublic();
//		System.out.println("public :" + IOUtil.serialize_s(pub));
//		System.out.println("private :" + IOUtil.serialize_s(priv));
		

		
//		byte[] encrypt = encrypt("ha ha".getBytes(),publicKey);
//		System.out.println(new String(decrypt(encrypt,privateKey)));
		
		String password = StringUtil.randomString(16);
		System.out.println(new String(decrypt(encrypt("he he".getBytes(), password),password)));
		
	}

	public static KeyPair keyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			SecureRandom random =
				    SecureRandom.getInstance("SHA1PRNG");
			keyGen.initialize(1024, random);
			KeyPair pair = keyGen.generateKeyPair();
			return pair;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static EncryptOutputStream encryptStream(
			final String key) {
		return new EncryptOutputStream() {public byte[] getEncryptedBytes() {
			return encrypt(toByteArray(), key);
		}};
	}
	
	public static abstract class EncryptOutputStream extends ByteArrayOutputStream {
		public abstract byte[] getEncryptedBytes();
	}

	public static void encryptFile(File fromFile, File toFile, String key) {
		EncryptOutputStream encryptStream = encryptStream(key);
		FileUtil.readFileOut(fromFile, encryptStream);
		FileUtil.writeToFile(encryptStream.getEncryptedBytes(), toFile);
	}

	public static InputStream decryptStream(File file, String key) {
		return new ByteArrayInputStream(decrypt(FileUtil.readFileToBytes(file), key));
	}
}
