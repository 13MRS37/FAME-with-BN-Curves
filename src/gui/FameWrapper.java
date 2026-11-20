package gui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import hk.chriz.FAME;
import hk.chriz.FAMEMasterKey;
import hk.chriz.FAMESecretKey;
import hk.chriz.FAMECipherText;

public class FameWrapper {
    
    /** Simple container for encryption results */
    public static class EncryptionResult {
        public final String ciphertext;
        public final String decryptedMessage;
        public EncryptionResult(String ciphertext, String decryptedMessage) {
            this.ciphertext = ciphertext;
            this.decryptedMessage = decryptedMessage;
        }
    }
    
    /**
     * Performs the FAME encryption and decryption using the given inputs.
     * @param propertiesFile Name of the .properties parameter file to load (from project root).
     * @param attributes List of attribute strings (in "ATTRIBUTE:VALUE" format).
     * @param plaintext The plaintext message to encrypt.
     * @return EncryptionResult containing the ciphertext and decrypted message.
     */
    public static EncryptionResult encryptAndDecrypt(String propertiesFile, List<String> attributes, String plaintext) throws Exception {
        // Feed the properties file name into FAME's console input
        InputStream originalIn = System.in;
        ByteArrayInputStream bis = new ByteArrayInputStream((propertiesFile + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        System.setIn(bis);
        // Instantiate FAME (this will read from the provided InputStream)
        FAME cpabe = new FAME();
        // Restore original System.in
        System.setIn(originalIn);
        
        // 1. Setup phase: generate public key and master key
        FAMEMasterKey msk = cpabe.setup();
        // 2. Key generation for the provided attributes
        String[] attrArray = attributes.toArray(new String[0]);
        FAMESecretKey sk = cpabe.keygen(msk, attrArray);
        // 3. Encryption: build policy from attributes and encrypt the plaintext bytes
        String policy = String.join(" and ", attributes);
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        FAMECipherText ct = cpabe.encrypt(policy, plaintextBytes);
        // 4. Decryption: decrypt the ciphertext back to plaintext
        byte[] decryptedBytes = cpabe.decrypt(sk, ct);
        String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);
        
        // Format the ciphertext for display (using Base64 for the AES-encrypted data portion)
        String ciphertextDisplay;
        if (ct.aesBuf != null) {
            String encryptedDataB64 = Base64.getEncoder().encodeToString(ct.aesBuf);
            ciphertextDisplay = "Ciphertext: \n" + encryptedDataB64;
        } else {
            // Fallback if aesBuf is not available
            ciphertextDisplay = "Ciphertext generated successfully.";
        }
        return new EncryptionResult(ciphertextDisplay, decryptedMessage);
    }
}
