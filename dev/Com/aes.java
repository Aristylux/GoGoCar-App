import java.util.Random;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class aes {
    
    public static void main(String[] args) {

        AES aes = new AES();
        AES.AESKey key = aes.generateAESKey(aes.KEY_256_BITS);
        System.out.println(key.print());

        String text = "hello";

        byte[] ciphertext = aes.aesEncrypt(text, key);

        System.out.println("Plaintext     : " + text);
        System.out.print("Ciphertext    : ");
        for (int i = 0; i < 16; i++) {
            System.out.print(String.format("%02x ", ciphertext[i]));
        }
        System.out.print("\n");

        String plaintext = aes.aesDecrypt(ciphertext, key);
        System.out.print("Decrypted data: ");
        for (int i = 0; i < 16; i++) {
            System.out.print(String.format("%02x ", plaintext.getBytes()[i]));
        }
        System.out.print("\n");
        System.out.println("Decrypted text: " + plaintext);
    }

    public static class AES {

        public class AESKey {

            private int keySize;
            private byte[] key;

            public AESKey(int keySize){
                this.keySize = keySize;
                this.key = new byte[keySize];
            }

            public byte[] getKey() {
                return key;
            }

            public String print(){
                StringBuilder sb = new StringBuilder("AES key: ");
                for (int i = 0; i < this.keySize; i++) {
                    sb.append(String.format("%02x", this.key[i]));
                }
                return sb.toString();
            }

        }

        public final int BLOCK_SIZE_128_BITS = 16;

        public final int KEY_128_BITS = 16;
        public final int KEY_192_BITS = 24;
        public final int KEY_256_BITS = 32;

        /**
         * @brief S-box values (Substitution box)
         */
        final private byte[] sbox = {
            (byte) 0x63, (byte) 0x7c, (byte) 0x77, (byte) 0x7b, (byte) 0xf2, (byte) 0x6b, (byte) 0x6f, (byte) 0xc5, (byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2b, (byte) 0xfe, (byte) 0xd7, (byte) 0xab, (byte) 0x76,
            (byte) 0xca, (byte) 0x82, (byte) 0xc9, (byte) 0x7d, (byte) 0xfa, (byte) 0x59, (byte) 0x47, (byte) 0xf0, (byte) 0xad, (byte) 0xd4, (byte) 0xa2, (byte) 0xaf, (byte) 0x9c, (byte) 0xa4, (byte) 0x72, (byte) 0xc0,
            (byte) 0xb7, (byte) 0xfd, (byte) 0x93, (byte) 0x26, (byte) 0x36, (byte) 0x3f, (byte) 0xf7, (byte) 0xcc, (byte) 0x34, (byte) 0xa5, (byte) 0xe5, (byte) 0xf1, (byte) 0x71, (byte) 0xd8, (byte) 0x31, (byte) 0x15,
            (byte) 0x04, (byte) 0xc7, (byte) 0x23, (byte) 0xc3, (byte) 0x18, (byte) 0x96, (byte) 0x05, (byte) 0x9a, (byte) 0x07, (byte) 0x12, (byte) 0x80, (byte) 0xe2, (byte) 0xeb, (byte) 0x27, (byte) 0xb2, (byte) 0x75,
            (byte) 0x09, (byte) 0x83, (byte) 0x2c, (byte) 0x1a, (byte) 0x1b, (byte) 0x6e, (byte) 0x5a, (byte) 0xa0, (byte) 0x52, (byte) 0x3b, (byte) 0xd6, (byte) 0xb3, (byte) 0x29, (byte) 0xe3, (byte) 0x2f, (byte) 0x84,
            (byte) 0x53, (byte) 0xd1, (byte) 0x00, (byte) 0xed, (byte) 0x20, (byte) 0xfc, (byte) 0xb1, (byte) 0x5b, (byte) 0x6a, (byte) 0xcb, (byte) 0xbe, (byte) 0x39, (byte) 0x4a, (byte) 0x4c, (byte) 0x58, (byte) 0xcf,
            (byte) 0xd0, (byte) 0xef, (byte) 0xaa, (byte) 0xfb, (byte) 0x43, (byte) 0x4d, (byte) 0x33, (byte) 0x85, (byte) 0x45, (byte) 0xf9, (byte) 0x02, (byte) 0x7f, (byte) 0x50, (byte) 0x3c, (byte) 0x9f, (byte) 0xa8,
            (byte) 0x51, (byte) 0xa3, (byte) 0x40, (byte) 0x8f, (byte) 0x92, (byte) 0x9d, (byte) 0x38, (byte) 0xf5, (byte) 0xbc, (byte) 0xb6, (byte) 0xda, (byte) 0x21, (byte) 0x10, (byte) 0xff, (byte) 0xf3, (byte) 0xd2,
            (byte) 0xcd, (byte) 0x0c, (byte) 0x13, (byte) 0xec, (byte) 0x5f, (byte) 0x97, (byte) 0x44, (byte) 0x17, (byte) 0xc4, (byte) 0xa7, (byte) 0x7e, (byte) 0x3d, (byte) 0x64, (byte) 0x5d, (byte) 0x19, (byte) 0x73,
            (byte) 0x60, (byte) 0x81, (byte) 0x4f, (byte) 0xdc, (byte) 0x22, (byte) 0x2a, (byte) 0x90, (byte) 0x88, (byte) 0x46, (byte) 0xee, (byte) 0xb8, (byte) 0x14, (byte) 0xde, (byte) 0x5e, (byte) 0x0b, (byte) 0xdb,
            (byte) 0xe0, (byte) 0x32, (byte) 0x3a, (byte) 0x0a, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5c, (byte) 0xc2, (byte) 0xd3, (byte) 0xac, (byte) 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xe4, (byte) 0x79,
            (byte) 0xe7, (byte) 0xc8, (byte) 0x37, (byte) 0x6d, (byte) 0x8d, (byte) 0xd5, (byte) 0x4e, (byte) 0xa9, (byte) 0x6c, (byte) 0x56, (byte) 0xf4, (byte) 0xea, (byte) 0x65, (byte) 0x7a, (byte) 0xae, (byte) 0x08,
            (byte) 0xba, (byte) 0x78, (byte) 0x25, (byte) 0x2e, (byte) 0x1c, (byte) 0xa6, (byte) 0xb4, (byte) 0xc6, (byte) 0xe8, (byte) 0xdd, (byte) 0x74, (byte) 0x1f, (byte) 0x4b, (byte) 0xbd, (byte) 0x8b, (byte) 0x8a,
            (byte) 0x70, (byte) 0x3e, (byte) 0xb5, (byte) 0x66, (byte) 0x48, (byte) 0x03, (byte) 0xf6, (byte) 0x0e, (byte) 0x61, (byte) 0x35, (byte) 0x57, (byte) 0xb9, (byte) 0x86, (byte) 0xc1, (byte) 0x1d, (byte) 0x9e,
            (byte) 0xe1, (byte) 0xf8, (byte) 0x98, (byte) 0x11, (byte) 0x69, (byte) 0xd9, (byte) 0x8e, (byte) 0x94, (byte) 0x9b, (byte) 0x1e, (byte) 0x87, (byte) 0xe9, (byte) 0xce, (byte) 0x55, (byte) 0x28, (byte) 0xdf,
            (byte) 0x8c, (byte) 0xa1, (byte) 0x89, (byte) 0x0d, (byte) 0xbf, (byte) 0xe6, (byte) 0x42, (byte) 0x68, (byte) 0x41, (byte) 0x99, (byte) 0x2d, (byte) 0x0f, (byte) 0xb0, (byte) 0x54, (byte) 0xbb, (byte) 0x16  
        };

        /**
         * @brief Inverted S-box values
         * @note Generate with generateSboxInv();
         */
        private byte[] sboxInv;

        /**
         * @brief Round constant values
         */
        final private byte[] rcon = {
            (byte) 0x8d, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1b, (byte) 0x36, (byte) 0x6c, (byte) 0xd8,
            (byte) 0xab, (byte) 0x4d, (byte) 0x9a, (byte) 0x2f, (byte) 0x5e, (byte) 0xbc, (byte) 0x63, (byte) 0xc6, (byte) 0x97, (byte) 0x35, (byte) 0x6a, (byte) 0xd4, (byte) 0xb3,
            (byte) 0x7d, (byte) 0xfa, (byte) 0xef, (byte) 0xc5, (byte) 0x91, (byte) 0x39, (byte) 0x72, (byte) 0xe4, (byte) 0xd3, (byte) 0xbd, (byte) 0x61, (byte) 0xc2, (byte) 0x9f,
            (byte) 0x25, (byte) 0x4a, (byte) 0x94, (byte) 0x33, (byte) 0x66, (byte) 0xcc, (byte) 0x83, (byte) 0x1d, (byte) 0x3a, (byte) 0x74, (byte) 0xe8, (byte) 0xcb, (byte) 0x8d,
            (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1b, (byte) 0x36, (byte) 0x6c, (byte) 0xd8, (byte) 0xab,
            (byte) 0x4d, (byte) 0x9a, (byte) 0x2f, (byte) 0x5e, (byte) 0xbc, (byte) 0x63, (byte) 0xc6, (byte) 0x97, (byte) 0x35, (byte) 0x6a, (byte) 0xd4, (byte) 0xb3, (byte) 0x7d,
            (byte) 0xfa, (byte) 0xef, (byte) 0xc5, (byte) 0x91, (byte) 0x39, (byte) 0x72, (byte) 0xe4, (byte) 0xd3, (byte) 0xbd, (byte) 0x61, (byte) 0xc2, (byte) 0x9f, (byte) 0x25,
            (byte) 0x4a, (byte) 0x94, (byte) 0x33, (byte) 0x66, (byte) 0xcc, (byte) 0x83, (byte) 0x1d, (byte) 0x3a, (byte) 0x74, (byte) 0xe8, (byte) 0xcb, (byte) 0x8d, (byte) 0x01,
            (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1b, (byte) 0x36, (byte) 0x6c, (byte) 0xd8, (byte) 0xab, (byte) 0x4d,
            (byte) 0x9a, (byte) 0x2f, (byte) 0x5e, (byte) 0xbc, (byte) 0x63, (byte) 0xc6, (byte) 0x97, (byte) 0x35, (byte) 0x6a, (byte) 0xd4, (byte) 0xb3, (byte) 0x7d, (byte) 0xfa,
            (byte) 0xef, (byte) 0xc5, (byte) 0x91, (byte) 0x39, (byte) 0x72, (byte) 0xe4, (byte) 0xd3, (byte) 0xbd, (byte) 0x61, (byte) 0xc2, (byte) 0x9f, (byte) 0x25, (byte) 0x4a,
            (byte) 0x94, (byte) 0x33, (byte) 0x66, (byte) 0xcc, (byte) 0x83, (byte) 0x1d, (byte) 0x3a, (byte) 0x74, (byte) 0xe8, (byte) 0xcb, (byte) 0x8d, (byte) 0x01, (byte) 0x02,
            (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1b, (byte) 0x36, (byte) 0x6c, (byte) 0xd8, (byte) 0xab, (byte) 0x4d, (byte) 0x9a,
            (byte) 0x2f, (byte) 0x5e, (byte) 0xbc, (byte) 0x63, (byte) 0xc6, (byte) 0x97, (byte) 0x35, (byte) 0x6a, (byte) 0xd4, (byte) 0xb3, (byte) 0x7d, (byte) 0xfa, (byte) 0xef,
            (byte) 0xc5, (byte) 0x91, (byte) 0x39, (byte) 0x72, (byte) 0xe4, (byte) 0xd3, (byte) 0xbd, (byte) 0x61, (byte) 0xc2, (byte) 0x9f, (byte) 0x25, (byte) 0x4a, (byte) 0x94,
            (byte) 0x33, (byte) 0x66, (byte) 0xcc, (byte) 0x83, (byte) 0x1d, (byte) 0x3a, (byte) 0x74, (byte) 0xe8, (byte) 0xcb, (byte) 0x8d, (byte) 0x01, (byte) 0x02, (byte) 0x04,
            (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x1b, (byte) 0x36, (byte) 0x6c, (byte) 0xd8, (byte) 0xab, (byte) 0x4d, (byte) 0x9a, (byte) 0x2f,
            (byte) 0x5e, (byte) 0xbc, (byte) 0x63, (byte) 0xc6, (byte) 0x97, (byte) 0x35, (byte) 0x6a, (byte) 0xd4, (byte) 0xb3, (byte) 0x7d, (byte) 0xfa, (byte) 0xef, (byte) 0xc5,
            (byte) 0x91, (byte) 0x39, (byte) 0x72, (byte) 0xe4, (byte) 0xd3, (byte) 0xbd, (byte) 0x61, (byte) 0xc2, (byte) 0x9f, (byte) 0x25, (byte) 0x4a, (byte) 0x94, (byte) 0x33,
            (byte) 0x66, (byte) 0xcc, (byte) 0x83, (byte) 0x1d, (byte) 0x3a, (byte) 0x74, (byte) 0xe8, (byte) 0xcb
        };


        public AES(){
            this.sboxInv = generateSboxInv();
            //print_hex(sboxInv, 256, "sboxinv");
        }

        void print_hex(byte[] arr, int size, String title) {
            System.out.println(title + ":");
            for (int i = 1; i <= size; i++) {
                System.out.printf("%02x%c", arr[i - 1], (i % 16 != 0) ? ' ' : '\n');
            }
            System.out.println();
        }

        // --- Key functions ---

        public AESKey generateAESKey(int keySize) {
            AESKey key = new AESKey(keySize);
            Random rand = new Random();
            for (int i = 0; i < keySize; i++) {
                key.getKey()[i] = (byte) rand.nextInt(256);
            }
            return key;
        }

        // --- Encrypt functions ---

        /**
         * @brief Copy key & plain text, encrypt text with the key
         * @note 10 rounds for 128-bit keys.
         *       12 rounds for 192-bit keys.
         *       14 rounds for 256-bit keys.
         * 
         * @param plaintext Pointer to a block of plaintext data
         * @param key Pointer to the secret key (32 bytes)
         * @param ciphertext Return pointer to the encrypted block of data (16 bytes)
         */
        public byte[] aesEncrypt(String plaintext, AESKey key) {
            byte[] state = new byte[BLOCK_SIZE_128_BITS];
            byte[] expandedKey = new byte[240]; // 240 = 16 * (14 + 1)
    
            byte[] byteArray = Arrays.copyOf(plaintext.getBytes(), BLOCK_SIZE_128_BITS);
            Arrays.fill(byteArray, plaintext.length(), byteArray.length, (byte) 0);

            // Copy the plaintext to the state array
            System.arraycopy(byteArray, 0, state, 0, BLOCK_SIZE_128_BITS);

            // Expand the key into a set of round keys
            keyExpansion(key, expandedKey);

            // Encrypt
            mainEncrypt(state, expandedKey, 14);

            return state;
        }
        
        /**
         * @brief AES Algorithm encryption
         * 
         * @param state State
         * @param expandedKey Expanded key
         * @param nbrRounds 10, 12 or 14 (<- for 256 bits)
         */
        private void mainEncrypt(byte[] state, byte[] expandedKey, int nbrRounds){
            byte[] roundKey = new byte[BLOCK_SIZE_128_BITS];
        
            // Add the initial round key to the state
            createRoundKey(expandedKey, roundKey);
            addRoundKey(state, roundKey);

            // Perform the main rounds of encryption
            for (int round = 1; round < nbrRounds; round++){
                createRoundKey(expandedKey, roundKey, BLOCK_SIZE_128_BITS*round);     
                subBytes(state);
                shiftRows(state);
                mixColumns(state);
                addRoundKey(state, roundKey);
            }
        
            // Perform the final round of encryption
            createRoundKey(expandedKey, roundKey,  BLOCK_SIZE_128_BITS*nbrRounds);
            subBytes(state);
            shiftRows(state);
            addRoundKey(state, roundKey);
        }

        /**
         * @brief Create a round key object
         * 
         * @param expandedKey Expanded key
         * @param roundKey Return round key
         */
        private void createRoundKey(byte[] expandedKey, byte[] roundKey) {
            for (byte i = 0; i < 4; i++) {
                for (byte j = 0; j < 4; j++)
                    roundKey[i + (j * 4)] = expandedKey[(i * 4) + j];
            }
        }

        private void createRoundKey(byte[] expandedKey, byte[] roundKey, int keyOffset) {
            for (byte i = 0; i < 4; i++) {
                for (byte j = 0; j < 4; j++)
                    roundKey[i + (j * 4)] = expandedKey[keyOffset + (i * 4) + j];
            }
        }

        /**
         * @brief Each byte of the state is combined with a byte of the round key using bitwise xor
         * 
         * @param state Pointer to the current state (16 bytes)
         * @param round_key Pointer to the current round key (16 bytes)
         */
        private void addRoundKey(byte[] state, byte[] roundKey) {
            for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
                state[i] ^= roundKey[i];
            }
        }

        /**
         * @brief A non-linear substitution step where each byte is replaced with another according to a lookup table
         * 
         * @param state Pointer to the current state (16 bytes)
         * @param sbox Substitution box
         */
        private void subBytes(byte[] state) {
            for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
                state[i] = sbox[state[i] & 0xFF];
            }
        }

        /**
         * @brief A transposition step where the last three rows of the state are shifted cyclically a certain number of steps
         * 
         * @param state Pointer to the current state (16 bytes)
         */
        private void shiftRows(byte[] state) {
            for (int i = 0; i < 4; i++)
                shiftRow(state, i, i * 4);
        }
        
        /**
         * @brief Transpose on a row
         * 
         * @param state State
         * @param nbr Column
         * @param offset Starting index of the row in the state array
         */
        private void shiftRow(byte[] state, int nbr, int offset) {
            byte temp;
        
            for (int i = 0; i < nbr; i++) {
                temp = state[offset];
                for (int j = 0; j < 3; j++) {
                    state[offset + j] = state[offset + j + 1];
                }
                state[offset + 3] = temp;
            }
        }

        /**
         * @brief A linear mixing operation which operates on the columns of the state, combining the four bytes in each column.
         * 
         * @param state Pointer to the current state (16 bytes)
         */
        private void mixColumns(byte[] state) {
            byte[] column = new byte[4];
            for (int i = 0; i < 4; i++) {
                // Construct a column by iterating over the 4 rows
                for (int j = 0; j < 4; j++)
                    column[j] = state[(j * 4) + i];
                
                mixColumn(column);
        
                // Put the values back into the state
                for (int j = 0; j < 4; j++)
                    state[(j * 4) + i] = column[j];
            }
        }

        /**
         * @brief Mix one column : 0xdb, 0x13, 0x53, 0x45 -> 0x8e, 0x4d, 0xa1, 0xbc;
         * 
         * @param column Column of 4 bytes
         */
        private void mixColumn(byte[] column) {
            byte[] cpy = new byte[4];
            System.arraycopy(column, 0, cpy, 0, 4);
        
            column[0] = (byte)(gfMul(cpy[0], 2) ^ gfMul(cpy[3], 1) ^ gfMul(cpy[2], 1) ^ gfMul(cpy[1], 3));
            column[1] = (byte)(gfMul(cpy[1], 2) ^ gfMul(cpy[0], 1) ^ gfMul(cpy[3], 1) ^ gfMul(cpy[2], 3));
            column[2] = (byte)(gfMul(cpy[2], 2) ^ gfMul(cpy[1], 1) ^ gfMul(cpy[0], 1) ^ gfMul(cpy[3], 3));
            column[3] = (byte)(gfMul(cpy[3], 2) ^ gfMul(cpy[2], 1) ^ gfMul(cpy[1], 1) ^ gfMul(cpy[0], 3));
        }


        // --- Decrypt functions ---

        /**
         * @brief Copy key & cipher text, encrypt text with the key
         * 
         * @param ciphertext Pointer to the encrypted block of data (16 bytes)
         * @param key Pointer to the secret key (32 bytes)
         * @param plaintext Return pointer to a block of plaintext data
         */
        public String aesDecrypt(byte[] ciphertext, AESKey key) {
            byte[] state = new byte[BLOCK_SIZE_128_BITS];
            byte[] expandedKey = new byte[240]; // 240 = 16 * (14 + 1)
        
            // Copy the ciphertext into the state array
            System.arraycopy(ciphertext, 0, state, 0, BLOCK_SIZE_128_BITS);
        
            // Expand the key into a set of round keys
            keyExpansion(key, expandedKey);
        
            // Decrypt
            mainDecrypt(state, expandedKey, 14);
            return new String(state, StandardCharsets.UTF_8);
        }

        private void mainDecrypt(byte[] state, byte[] expandedKey, int nbrRounds){
            byte[] roundKey = new byte[BLOCK_SIZE_128_BITS];
        
            // Add the initial round key to the state
            createRoundKey(expandedKey, roundKey, BLOCK_SIZE_128_BITS * nbrRounds);
            addRoundKey(state, roundKey);
        
            // Perform the main rounds of encryption
            for (int round = nbrRounds - 1; round > 0; round--){
                createRoundKey(expandedKey, roundKey, BLOCK_SIZE_128_BITS*round);
                shiftRowsInv(state);
                subBytesInv(state);
                addRoundKey(state, roundKey);
                mixColumnsInv(state);
            }
        
            // Perform the final round of encryption
            createRoundKey(expandedKey, roundKey);
            subBytesInv(state);
            shiftRowsInv(state);
            addRoundKey(state, roundKey);
        }

        /**
         * @brief Invert sub bytes operation
         * 
         * @param state State
         */
        private void subBytesInv(byte[] state) {
            for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
                state[i] = this.sboxInv[state[i] & 0xFF];
            }
        }

        /**
         * @brief A transposition step where the last three rows of the state are shifted cyclically a certain number of steps
         * 
         * @param state Pointer to the current state (16 bytes)
         */
        private void shiftRowsInv(byte[] state) {
            for (int i = 0; i < 4; i++)
                shiftRowInv(state, i, i * 4);
        }
        
        /**
         * @brief Transpose on a row
         * 
         * @param state State
         * @param nbr Value of column of state
         */
        private void shiftRowInv(byte[] state, int nbr, int offset) {
            byte temp;
            for (int i = 0; i < nbr; i++) {
                temp = state[offset + 3];
                for (int j = 3; j > 0; j--)
                    state[offset + j] = state[offset + j - 1];
                state[offset] = temp;
            }
        }
        
        /**
         * @brief A linear mixing operation which operates on the columns of the state, combining the four bytes in each column.
         * 
         * @param state Pointer to the current state (16 bytes)
         */
        private void mixColumnsInv(byte[] state) {
            byte[] column = new byte[4];
            for (int i = 0; i < 4; i++) {
                // Construct a column by iterating over the 4 rows
                for (int j = 0; j < 4; j++)
                    column[j] = state[(j * 4) + i];
                
                mixColumnInv(column);
        
                // Put the values back into the state
                for (int j = 0; j < 4; j++)
                    state[(j * 4) + i] = column[j];
            }
        }
        
        /**
         * @brief Mix column from state
         * 
         * @param column Column of state
         */
        private void mixColumnInv(byte[] column) {
            byte[] cpy = new byte[4];
            System.arraycopy(column, 0, cpy, 0, 4);
        
            column[0] = (byte)(gfMul(cpy[0], 14) ^ gfMul(cpy[3], 9) ^ gfMul(cpy[2], 13) ^ gfMul(cpy[1], 11));
            column[1] = (byte)(gfMul(cpy[1], 14) ^ gfMul(cpy[0], 9) ^ gfMul(cpy[3], 13) ^ gfMul(cpy[2], 11));
            column[2] = (byte)(gfMul(cpy[2], 14) ^ gfMul(cpy[1], 9) ^ gfMul(cpy[0], 13) ^ gfMul(cpy[3], 11));
            column[3] = (byte)(gfMul(cpy[3], 14) ^ gfMul(cpy[2], 9) ^ gfMul(cpy[1], 13) ^ gfMul(cpy[0], 11));
        }
        

        // --- General functions ---

        /**
         * @brief Rotate the word eight bits to the left
         * @note Rotate(1d2c3a4f) = 2c3a4f1d
         * 
         * @param word Is an 8bits array of size 4 (32 bit)
         */
        private void rotate(byte[] word) {
            byte c = word[0];
            for (int i = 0; i < 3; i++)
                word[i] = word[i+1];
            word[3] = c;
        }

        /**
         * @brief Key substitution core
         * 
         * @param word Word of 4*8bits
         * @param iteration Number of iteration
         */
        private void core(byte[] word, int iteration) {
            // Rotate the 32-bit word 8 bits to the left
            rotate(word);
        
             // Apply S-Box substitution on all 4 parts of the 32-bit word
            for (int i = 0; i < 4; ++i)
                word[i] = sbox[word[i] & 0xFF];
            
                // XOR the output of the rcon operation with i to the first part (leftmost) only
            word[0] ^= rcon[iteration];
        }

        /**
         * @brief Key expansion function.
         * Implement AES-256 key expansion,
         * This involves applying a series of transformations to the original key
         * to generate a set of round keys that will be used for each round of encryption.
         * 
         * @param key Pointer to the secret key (32 bytes)
         * @param expanded_key Pointer to the expanded set of round keys (240 bytes)
         */
        private void keyExpansion(AESKey key, byte[] expandedKey) {
            int expandedKeySize = (BLOCK_SIZE_128_BITS * (setNumberRound(key.keySize) + 1));
            int currentSize = key.keySize; // for 256 bits key
            byte[] temp = new byte[4];
            int rconIteration = 1;
        
            // Copy the original key to the first set of round keys
            System.arraycopy(key.key, 0, expandedKey, 0, currentSize);
        
            while (currentSize < expandedKeySize) {
                // Assign the previous 4 bytes to the temporary value t
                for (int i = 0; i < 4; i++) {
                    temp[i] = expandedKey[(currentSize - 4) + i];
                }
        
                // Every 16, 24, 32 bytes, we apply the core schedule to temp and increment rconIteration afterwards
                if (currentSize % key.keySize == 0) {
                    core(temp, rconIteration++);
                }
        
                // For 256-bit keys, we add an extra sbox to the calculation
                if (key.keySize == KEY_256_BITS && ((currentSize % key.keySize) == 16)) {
                    for (int i = 0; i < 4; i++) {
                        //System.out.println("i:" + i + " temp:" + (temp[i] & 0xFF) + " sbox:" + "sbox[temp[i]]");
                        temp[i] = sbox[(temp[i] & 0xFF)];
                    }
                }
        
                // We XOR temp with the four-byte block 16, 24, 32 bytes before the new expanded key. This becomes the next four bytes in the expanded key.
                for (int i = 0; i < 4; i++) {
                    expandedKey[currentSize] = (byte) ((expandedKey[currentSize - key.keySize] & 0xFF) ^ (temp[i] & 0xFF));
                    currentSize++;
                }
            }
        }

        /**
         * @brief Set the number round object
         * 
         * @param key_size Size of the aes key
         * @return uint8_t Number of round of the key
         */
        private int setNumberRound(int key_size){
            switch (key_size){
            case KEY_128_BITS:
                return 10;
            case KEY_192_BITS:
                return 12;
            case KEY_256_BITS:
                return 14;
            default:
                return 0;
            }
        }

        /**
         * @brief Galois Field multiplication function
         * 
         * @param a A byte value
         * @param b A byte value
         * @return uint8_t The result of multiplying a and b in the Galois Field
         */
        private int gfMul(int a, int b) {
            int p = 0;
            int hbit = 0;
            for (int i = 0; i < 8; i++) {
                if ((b & 0x01) == 0x01) p ^= a;
                hbit = (a & 0x80);
                a <<= 1;
                if (hbit == 0x80) a ^= 0x1b;
                b >>= 1;
            }
            return p;
        }

        /**
         * @brief Generate sbox inverted from sbox
         * 
         */
        private byte[] generateSboxInv() {
            byte[] sboxInverted = new byte[256];
            for (int i = 0; i < 256; i++) {
                sboxInverted[sbox[i] & 0xFF] = (byte) i;
            }
            return sboxInverted;
        }
    }

}
