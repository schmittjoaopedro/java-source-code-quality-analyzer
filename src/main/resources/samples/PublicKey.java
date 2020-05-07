package com.github.schmittjoaopedro;

import java.io.Serializable;
import java.math.BigInteger;

public class PublicKey implements Serializable {

    public int keySize;

    public BigInteger n;

    public BigInteger e;

    public String encrypt(String message) {
        StringBuilder msgCrypt = new StringBuilder();
        for(int c = 0; c < message.length(); c++) {
            BigInteger msgText = new BigInteger(String.valueOf(((int) message.charAt(c))));
            BigInteger msgCode = msgText.modPow(e, n);
            msgCrypt.append(msgCode.toString());
            if(c != message.length() - 1) {
                msgCrypt.append("-");
            }
        }
        return msgCrypt.toString();
    }

    public String encrypt(String message, Integer blockSize) { // O(n^3)
        StringBuilder msgCrypt = new StringBuilder();
        StringBuilder msgTemp = new StringBuilder();
        for(int c = 1; c <= message.length(); c++) { // O(m)
            String temp = String.valueOf(((int) message.charAt(c-1)));
            if(temp.length() == 1) temp = "00" + temp;
            if(temp.length() == 2) temp = "0" + temp;
            msgTemp.append(temp);
            if(c == message.length() || c % blockSize == 0) {
                BigInteger msgNumber = new BigInteger(msgTemp.toString());
                if(msgNumber.compareTo(n) > 0) throw new RuntimeException("Restriction error, M has to be less than n.");
                BigInteger msgCode = msgNumber.modPow(e, n); // Exponenciação modular = O(n^3)
                msgCrypt.append(msgCode.toString());
                msgTemp = new StringBuilder();
                if(c != message.length()) {
                    msgCrypt.append("-");
                }
            }
        }
        return msgCrypt.toString();
    }

}
