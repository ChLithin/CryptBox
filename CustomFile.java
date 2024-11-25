import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CustomFile extends StorageNode{
    private final String file_type;
    
    public CustomFile(String name,String path){
        super(name,path);
        this.file_type = name.contains(".") ? name.substring(name.lastIndexOf(".")) : "";
        this.command_touch();
    }
    public String getFile_type(){
        return this.file_type;
    }
    public native void command_touch();
    public native void command_cat();
    public native void command_echo(String text, String option);
    public native void command_cp(String path);
    public native void command_mv(String path);
    public native void command_rm();
    public native void command_rmWithOption(String option);
    public boolean command_encrypt(String passKey){
        try {
            SecureFile.encryptFile(actualPath, passKey);
            this.is_encrypted = true;
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }
    public boolean command_decrypt(String passKey){
        try {
            SecureFile.decryptFile(actualPath, passKey);
            this.is_encrypted = false;
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }
}

class SecureFile {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256; // Bits
    private static final int GCM_IV_LENGTH = 12; // Bytes
    private static final int GCM_TAG_LENGTH = 16; // Bytes

    public static void encryptFile(String filePath, String passkey) throws Exception {
        // Generate a 256-bit key from the passkey
        SecretKey secretKey = getKeyFromPasskey(passkey);

        // Read file content
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        // Generate IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // Initialize cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        // Encrypt file content
        byte[] encryptedContent = cipher.doFinal(fileContent);

        // Write the IV and encrypted content back to the file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(iv); // Write IV first
            fos.write(encryptedContent); // Write encrypted content
        }
    }
    public static void decryptFile(String filePath, String passkey) throws Exception {
        // Generate the AES key from the passkey
        SecretKey secretKey = getKeyFromPasskey(passkey);

        // Read the file's content
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        // Extract the IV and encrypted content
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(fileContent, 0, iv, 0, GCM_IV_LENGTH); // Extract IV
        byte[] encryptedContent = new byte[fileContent.length - GCM_IV_LENGTH];
        System.arraycopy(fileContent, GCM_IV_LENGTH, encryptedContent, 0, encryptedContent.length); // Extract encrypted data

        // Initialize cipher for decryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        // Decrypt the content
        byte[] decryptedContent = cipher.doFinal(encryptedContent);

        // Write the decrypted content back to the file (overwriting)
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(decryptedContent);
        }
    }
    private static SecretKey getKeyFromPasskey(String passkey) throws Exception {
        // Use the first 32 bytes of the passkey (padded or trimmed) to create a key
        byte[] keyBytes = new byte[AES_KEY_SIZE / 8];
        byte[] passkeyBytes = passkey.getBytes("UTF-8");
        int length = Math.min(passkeyBytes.length, keyBytes.length);
        System.arraycopy(passkeyBytes, 0, keyBytes, 0, length);

        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

}