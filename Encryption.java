package chat;

public class Encryption {

    // Method to calculate Hamming code for a given data
    public static String encodeHamming(String data) {
        int r = 0;
        int m = data.length();

        // Calculate number of parity bits needed
        while ((1 << r) < (m + r + 1)) {
            r++;
        }

        // Create an array to hold the data and parity bits
        char[] encoded = new char[m + r];
        int j = 0, k = 0;

        // Place data bits in the array and initialize parity bits
        for (int i = 0; i < encoded.length; i++) {
            if (Math.pow(2, j) - 1 == i) {
                encoded[i] = '0'; // Initialize parity bit as 0
                j++;
            } else {
                encoded[i] = data.charAt(k);
                k++;
            }
        }

        // Calculate parity bits
        for (int i = 0; i < r; i++) {
            int parityPos = (1 << i) - 1;
            int count = 0;
            for (int j1 = parityPos; j1 < encoded.length; j1 += (parityPos + 1) * 2) {
                for (int j2 = j1; j2 < j1 + parityPos + 1 && j2 < encoded.length; j2++) {
                    if (encoded[j2] == '1') {
                        count++;
                    }
                }
            }
            encoded[parityPos] = (count % 2 == 0) ? '0' : '1';
        }

        return new String(encoded);
    }

    // Method to detect and correct errors in Hamming code
    public static String decodeHamming(String encoded) {
        int r = 0;
        int m = encoded.length();

        // Calculate number of parity bits needed
        while ((1 << r) < (m + 1)) {
            r++;
        }

        // Detect and correct errors
        int errorPos = 0;
        for (int i = 0; i < r; i++) {
            int parityPos = (1 << i) - 1;
            int count = 0;
            for (int j1 = parityPos; j1 < m; j1 += (parityPos + 1) * 2) {
                for (int j2 = j1; j2 < j1 + parityPos + 1 && j2 < m; j2++) {
                    if (encoded.charAt(j2) == '1') {
                        count++;
                    }
                }
            }
            if (count % 2 != 0) {
                errorPos += parityPos + 1;
            }
        }

        // Correct error if any
        if (errorPos != 0) {
            char[] encodedArray = encoded.toCharArray();
            encodedArray[errorPos - 1] = (encodedArray[errorPos - 1] == '1') ? '0' : '1';
            encoded = new String(encodedArray);
        }

        // Extract original data
        StringBuilder data = new StringBuilder();
        int j = 0;
        for (int i = 0; i < m; i++) {
            if (Math.pow(2, j) - 1 != i) {
                data.append(encoded.charAt(i));
            } else {
                j++;
            }
        }

        return data.toString();
    }

    // Caesar cipher encryption
    public static String caesarEncrypt(String data, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                result.append((char) ((c - base + shift) % 26 + base));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // Caesar cipher decryption
    public static String caesarDecrypt(String data, int shift) {
        return caesarEncrypt(data, 26 - shift);
    }

    // Combined encoding method
    public static String encode(String data) {
        String caesarEncrypted = caesarEncrypt(data, 3);
        StringBuilder binary = new StringBuilder();
        for (char c : caesarEncrypted.toCharArray()) {
            binary.append(String.format("%7s", Integer.toBinaryString(c)).replaceAll(" ", "0"));
        }
        return encodeHamming(binary.toString());
    }

    // Combined decoding method
    public static String decode(String data) {
        String binaryString = decodeHamming(data);
        StringBuilder decodedMessage = new StringBuilder();
        for (int i = 0; i < binaryString.length(); i += 7) {
            String byteString = binaryString.substring(i, i + 7);
            char c = (char) Integer.parseInt(byteString, 2);
            decodedMessage.append(c);
        }
        return caesarDecrypt(decodedMessage.toString(), 3);
    }
}
