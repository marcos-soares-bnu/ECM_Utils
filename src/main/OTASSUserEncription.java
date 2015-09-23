package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import database.DBUtil;
import rsa.Encryption;
import rsa.KeyGenerator;

public class OTASSUserEncription {
    public static void main(String[] args) {
        final String originalMsg = "soxi0401";
        ObjectInputStream inputStream = null;
        final PublicKey chavePublica;
        byte[] textoCriptografado = null;
        
        final PrivateKey chavePrivada;
        final String textoPuro;
        
     // Criptografa a Mensagem usando a Chave Pública
        try {
            inputStream = new ObjectInputStream(new FileInputStream(KeyGenerator.PATH_PUBLIC_KEY));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            chavePublica = (PublicKey) inputStream.readObject();
            textoCriptografado = Encryption.encrypt(originalMsg, chavePublica);
            DBUtil db = new DBUtil();
            db.doINSERT(textoCriptografado);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
//     textoCriptografado = new DBUtil().doSelect();
////      Decriptografa a Mensagem usando a Chave privada
//        try {
//            inputStream = new ObjectInputStream(new FileInputStream(KeyGenerator.PATH_PRIVATE_KEY));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            chavePrivada = (PrivateKey) inputStream.readObject();
//            textoPuro = new Encryption().decrypt(textoCriptografado);
//            System.out.println(textoPuro);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
    }
}
