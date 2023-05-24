package com.aristy.gogocar.AES;

import static com.aristy.gogocar.AES.AESCommon.BLOCK_SIZE_128_BITS;
import static com.aristy.gogocar.AES.AESCommon.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class AES {

    public AES(){
        generateSboxInv();
    }

    // --- Key function ---

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
     * Copy key & plain text, encrypt text with the key
     * @apiNote  10 rounds for 128-bit keys.
     *          12 rounds for 192-bit keys.
     *          14 rounds for 256-bit keys.
     *
     * @param plaintext Pointer to a block of plaintext data
     * @param key Pointer to the secret key (32 bytes)
     * @return ciphertext Return pointer to the encrypted block of data (16 bytes)
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
     * AES Algorithm encryption
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
     * Create a round key object
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

    /**
     * Create a round key object
     *
     * @param expandedKey Expanded key
     * @param roundKey Return round key
     * @param keyOffset offset
     */
    private void createRoundKey(byte[] expandedKey, byte[] roundKey, int keyOffset) {
        for (byte i = 0; i < 4; i++) {
            for (byte j = 0; j < 4; j++)
                roundKey[i + (j * 4)] = expandedKey[keyOffset + (i * 4) + j];
        }
    }

    /**
     * Each byte of the state is combined with a byte of the round key using bitwise xor
     *
     * @param state Pointer to the current state (16 bytes)
     * @param roundKey Pointer to the current round key (16 bytes)
     */
    private void addRoundKey(byte[] state, byte[] roundKey) {
        for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
            state[i] ^= roundKey[i];
        }
    }

    /**
     * A non-linear substitution step where each byte is replaced with another according to a lookup table
     *
     * @param state Pointer to the current state (16 bytes)
     */
    private void subBytes(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
            state[i] = getSboxValue(state[i] & 0xFF);
        }
    }

    /**
     * A transposition step where the last three rows of the state are shifted cyclically a certain number of steps
     *
     * @param state Pointer to the current state (16 bytes)
     */
    private void shiftRows(byte[] state) {
        for (int i = 0; i < 4; i++)
            shiftRow(state, i, i * 4);
    }

    /**
     * Transpose on a row
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
     * A linear mixing operation which operates on the columns of the state, combining the four bytes in each column.
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
     * Mix one column : 0xdb, 0x13, 0x53, 0x45 -> 0x8e, 0x4d, 0xa1, 0xbc;
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
     * Copy key & cipher text, encrypt text with the key
     *
     * @param ciphertext encrypted block of data (16 bytes)
     * @param key ecret key (32 bytes)
     * @return String block of plaintext data
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
        return new String(removeNullBytes(state), StandardCharsets.UTF_8);
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
     * Invert sub bytes operation
     *
     * @param state State
     */
    private void subBytesInv(byte[] state) {
        for (int i = 0; i < BLOCK_SIZE_128_BITS; i++) {
            state[i] = getSboxInvValue(state[i] & 0xFF);
        }
    }

    /**
     * A transposition step where the last three rows of the state are shifted cyclically a certain number of steps
     *
     * @param state Pointer to the current state (16 bytes)
     */
    private void shiftRowsInv(byte[] state) {
        for (int i = 0; i < 4; i++)
            shiftRowInv(state, i, i * 4);
    }

    /**
     * Transpose on a row
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
     * A linear mixing operation which operates on the columns of the state, combining the four bytes in each column.
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
     * Mix column from state
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
     * Key expansion function.
     * Implement AES-256 key expansion,
     * This involves applying a series of transformations to the original key
     * to generate a set of round keys that will be used for each round of encryption.
     *
     * @param key Pointer to the secret key (32 bytes)
     * @param expandedKey Pointer to the expanded set of round keys (240 bytes)
     */
    private void keyExpansion(AESKey key, byte[] expandedKey) {
        int keySize = key.getKey().length;
        int expandedKeySize = (BLOCK_SIZE_128_BITS * (setNumberRound(keySize) + 1));
        int currentSize = keySize; // for 256 bits key
        byte[] temp = new byte[4];
        int rconIteration = 1;

        // Copy the original key to the first set of round keys
        System.arraycopy(key.getKey(), 0, expandedKey, 0, currentSize);

        while (currentSize < expandedKeySize) {
            // Assign the previous 4 bytes to the temporary value t
            System.arraycopy(expandedKey, (currentSize - 4), temp, 0, 4);

            // Every 16, 24, 32 bytes, we apply the core schedule to temp and increment rconIteration afterwards
            if (currentSize % keySize == 0) {
                core(temp, rconIteration++);
            }

            // For 256-bit keys, we add an extra sbox to the calculation
            if (keySize == KEY_256_BITS && ((currentSize % keySize) == 16)) {
                for (int i = 0; i < 4; i++) {
                    //System.out.println("i:" + i + " temp:" + (temp[i] & 0xFF) + " sbox:" + "sbox[temp[i]]");
                    temp[i] = getSboxValue((temp[i] & 0xFF));
                }
            }

            // We XOR temp with the four-byte block 16, 24, 32 bytes before the new expanded key. This becomes the next four bytes in the expanded key.
            for (int i = 0; i < 4; i++) {
                expandedKey[currentSize] = (byte) ((expandedKey[currentSize - keySize] & 0xFF) ^ (temp[i] & 0xFF));
                currentSize++;
            }
        }
    }

    /**
     * Key substitution core
     *
     * @param word Word of 4*8bits
     * @param iteration Number of iteration
     */
    private void core(byte[] word, int iteration) {
        // Rotate the 32-bit word 8 bits to the left
        rotate(word);

        // Apply S-Box substitution on all 4 parts of the 32-bit word
        for (int i = 0; i < 4; ++i)
            word[i] = getSboxValue(word[i] & 0xFF);

        // XOR the output of the rcon operation with i to the first part (leftmost) only
        word[0] ^= getRconValue(iteration);
    }

    /**
     * Rotate the word eight bits to the left
     * @implNote Rotate(1d2c3a4f) = 2c3a4f1d
     *
     * @param word Is an 8bits array of size 4 (32 bit)
     */
    private void rotate(byte[] word) {
        byte c = word[0];
        for (int i = 0; i < 3; i++)
            word[i] = word[i+1];
        word[3] = c;
    }

}
