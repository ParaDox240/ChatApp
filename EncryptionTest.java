package chat;

public class EncryptionTest {
    public static void main(String[] args) {
        // Test message
        String message = "Hello, World!";
        
        // Encrypt the message using the Encryption class
        String encryptedMessage = Encryption.encode(message);
        
        // Print the encrypted message
        System.out.println("Original Message: " + message);
        System.out.println("Encrypted Message: " + encryptedMessage);
        
        // Decrypt the message using the Encryption class
        String decryptedMessage = Encryption.decode(encryptedMessage);
        
        // Print the decrypted message
        System.out.println("Decrypted Message: " + decryptedMessage);
        
        // Verify if the decrypted message matches the original message
        if (message.equals(decryptedMessage)) {
            System.out.println("Success: The decrypted message matches the original message.");
        } else {
            System.out.println("Error: The decrypted message does not match the original message.");
        }
    }
}
