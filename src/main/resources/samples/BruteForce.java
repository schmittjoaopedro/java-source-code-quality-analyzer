package com.github.schmittjoaopedro;

import ch.obermuhlner.math.big.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class BruteForce {

    public static String solve(PublicKey publicKey, String crypt) { // O(2^n)
        BigInteger p = null;
        BigInteger q = null;
        BigInteger n = publicKey.n;
        BigInteger max = BigDecimalMath.sqrt(new BigDecimal(n), new MathContext(0)).toBigInteger();
        BigInteger i = new BigInteger("3");
        for(; i.compareTo(max) <= 0; i = i.add(new BigInteger("2"))) { // O(2^n)
            if(n.mod(i).equals(new BigInteger("0"))) { // O(n^2)
                p = n.divide(i);
                q = i;
                break;
            }
        }
        BigInteger phi = getTotientEuler(p, q); // O(n^2)
        PrivateKey privateKey = new PrivateKey();
        privateKey.n = publicKey.n;
        privateKey.d = Euclid.getModInv(publicKey.e, phi); // O(n^3)
        return privateKey.decrypt(crypt); // O(n^3)
    }

    private static BigInteger getTotientEuler(BigInteger p, BigInteger q) {
        return p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
    }

}
