package rsa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGenerator {
    public static final String ALGORITHM = "RSA";
    public static final String PATH_PRIVATE_KEY = "C:\\Temp\\script result\\privateOTASS.key";
    public static final String PATH_PUBLIC_KEY = "D:\\IAS_Monitoring\\APP_Dev\\SCHEDScripts\\publicOTASS.key";
    
    public static void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();
            
            File privateKeyFile = new File(PATH_PRIVATE_KEY);
            File publicKeyFile = new File(PATH_PUBLIC_KEY);
            
            if (privateKeyFile.getParentFile() != null)
                privateKeyFile.getParentFile().mkdirs();
            
            privateKeyFile.createNewFile();
            
            if (publicKeyFile.getParentFile() != null)
                publicKeyFile.getParentFile().mkdirs();
            
            publicKeyFile.createNewFile();
            
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();
            
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            publicKeyOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
