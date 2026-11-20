package hk.chriz;

import java.util.Scanner;
import java.time.Duration;
import java.time.Instant;


public class Main {
  
    public static void main(String[] args) throws Exception {
    	
    	Scanner scanner = new Scanner(System.in);
    	System.out.print("Enter number of attributes: ");
    	int numAttrs = Integer.parseInt(scanner.nextLine());

    	String[] attrs = new String[numAttrs];
    	for (int i = 0; i < numAttrs; i++) {
    	    while (true) {
    	        System.out.print("Enter attribute " + (i + 1) + " in format ATTRIBUTE:VALUE: ");
    	        String input = scanner.nextLine().trim();

    	        // Simple rule : include:
    	        if (!input.contains(":")) {
    	            System.out.println("Format invalid. Use ATTRIBUTE:VALUE (missing ':' character). Try again.");
    	            continue;
    	        }

    	        // Strict rule : split and check for NULLs
    	        String[] parts = input.split(":", 2);
    	        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
    	            System.out.println("Invalid format. Make sure both ATTRIBUTE and VALUE are non-empty. Try again.");
    	            continue;
    	        }

    	        // Attribute passed
    	        attrs[i] = input;
    	        break;
    	    }
    	}

        // Get message to encrypt
        System.out.print("Enter a message to encrypt: ");
        String fileContent = scanner.nextLine();

        byte[] whatToEncrypt = fileContent.getBytes();
        System.out.println("Input message size in bytes: " + whatToEncrypt.length);

        // Begin FAME operations
        FAME cpabe = new FAME(scanner);

        // Initialization :
        Instant start_init = Instant.now();
        FAMEMasterKey msk = cpabe.setup();
        Instant end_init = Instant.now();
        printDuration("Setup", start_init, end_init);

        // Private Key Extraction / Generation:
        Instant start_keygen = Instant.now();
        FAMESecretKey skey = cpabe.keygen(msk, attrs);
        Instant end_keygen = Instant.now();
        printDuration("Keygen", start_keygen, end_keygen);
        
        // Adjust attributes
        String encryptionPolicy = String.join(" and ", attrs);
        
        // Encryption
        Instant start_enc = Instant.now();
        FAMECipherText cpt = cpabe.encrypt(encryptionPolicy,whatToEncrypt);
        Instant end_enc = Instant.now();
        printDuration("Encryption", start_enc, end_enc);

        // Decryption
        Instant start_dec = Instant.now();
        byte[] decrypted = cpabe.decrypt(skey, cpt);
        Instant end_dec = Instant.now();
        printDuration("Decryption", start_dec, end_dec);

        // Verification of decryption
        String dec_message = new String(decrypted);
        if (dec_message.equals(fileContent)) {
            System.out.println("~~~ Verification of Decryption Success ~~~ \n");
        } else {
            System.err.println("=== Error: Decrypted text is not the same! ===");
            System.err.println("Original:"+whatToEncrypt);
            System.err.println("Decrypted:"+dec_message);
        }
        scanner.close();
}// Main-Bracket
    // Method to print 
    private static void printDuration(String operation, Instant start, Instant end) {
    		System.out.println("~~~~ " + operation + " Complete ~~~~");
    		System.out.println("Elapsed time: \n" + Duration.between(start, end) + "\n");
    }
}//Class - bracket