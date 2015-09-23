package rsa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class Encryption {
    public static byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(KeyGenerator.ALGORITHM);
            //Encrypt plain text using a public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }
    
    public String decrypt(byte[] text) {
        final String PATH_PRIVATE_KEY = "C:\\Temp\\script result\\privateOTASS.key";
        byte[] decryptedPass = null;
        PrivateKey privateKey = null;
        ObjectInputStream inputStream = null;
        
        //Retrieve private key file
        try {
            inputStream = new ObjectInputStream(new FileInputStream(PATH_PRIVATE_KEY));
        } catch (FileNotFoundException e) {
            System.out.println("OTASS private key file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error while opening OTASS private key file.");
            e.printStackTrace();
        }
        
        
        //Read the private key file
        try {
            privateKey = (PrivateKey) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error while reading OTASS private key file.");
            e.printStackTrace();
        }
        
        //Decrypt the password
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedPass = cipher.doFinal(text);
        } catch (Exception e) {
            System.out.println("Error while decrypting OTASS password.");
            e.printStackTrace();
        }
        
        return new String(decryptedPass);
    }
    
}
