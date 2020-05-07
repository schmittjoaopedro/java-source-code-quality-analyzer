package com.github.schmittjoaopedro;

import java.math.BigInteger;

/**
 * ax + by = gcd(a, b)
 * d = gcd(a, b)
 */
public class Euclid {

    /**
     * Algorithm: (Euclidean algorithm) Computing the greatest common divisor of two integers. [4]
     *
     * Para a = 348 e b = 156
     *
     *  q   |  2  |  4  |  3  |
     *  348 | 156 |  36 |  12 |
     *  r   |  36 |  12 |  0  |
     */
    public static BigInteger getMDC(BigInteger a, BigInteger b) { // O(n^3) [6]
        if(a.compareTo(b) < 0) {
            BigInteger aux = b;
            b = a;
            a = aux;
        }
        while (b.compareTo(new BigInteger("0")) > 0) {
            BigInteger r = a.mod(b);
            a = b;
            b = r;
        }
        return a;
    }

    /**
     * Modular inverse [5].
     *
     * Para lidar sempre com inversos múltiplicativos inteiros, existe uma versão do algoritmo
     * estendido de euclides que caĺcula o valor de unverso da seguinte forma:
     *      p_{i} = p_{i-2} - p_{i-1}*q_{i-2}
     * Como p_{0} iniciando em 0 e p_{1} inciando em 1.
     */
    public static BigInteger getModInv(BigInteger x, BigInteger N) {
        if(x.compareTo(N) > 0) {
            BigInteger aux = N;
            N = x;
            x = aux;
        }
        BigInteger a = N;
        BigInteger b = x;
        BigInteger p2 = new BigInteger("0");
        BigInteger p1 = new BigInteger("1");
        BigInteger q;
        BigInteger p;
        while (b.compareTo(new BigInteger("0")) > 0) { // O(n^3) [6]
            BigInteger r = a.mod(b);
            if(r.compareTo(new BigInteger("0")) > 0) {
                q = a.divide(b);
                p = p2.subtract(p1.multiply(q)).mod(N);
                p2 = p1;
                p1 = p;
            }
            a = b;
            b = r;
        }
        return p1;
    }

}
