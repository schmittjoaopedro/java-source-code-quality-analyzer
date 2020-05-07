package com.github.schmittjoaopedro;

import java.math.BigInteger;

public class PollardForce {

    public static String solve(PublicKey publicKey, String crypt) {
        BigInteger p = factorize(publicKey.n);
        BigInteger q = publicKey.n.divide(p);
        BigInteger phi = getTotientEuler(p, q);
        PrivateKey privateKey = new PrivateKey();
        privateKey.n = publicKey.n;
        privateKey.d = Euclid.getModInv(publicKey.e, phi);
        return privateKey.decrypt(crypt);
    }

    private static BigInteger getTotientEuler(BigInteger p, BigInteger q) {
        return p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
    }

    /**
     * Pollards rho factorization [8] e [9].
     *
     * Esse algoritmo é uma heurística, onde que uma de suas vantagems é o uso constante de recursos para
     * buscar da solução do problema.
     * O algoritmo de Pollards-rho nunca imprime um divisor não trivial (ou seja, d != 1 e d != n).
     */
    public static BigInteger factorize(BigInteger n) { // O(sqrt(p)), p sendo o fator primo de n [9]
        /*
        // From wikipedia
        BigInteger xFixed = new BigInteger("2");
        BigInteger cycleSize = new BigInteger("2");
        BigInteger x = new BigInteger("2");
        BigInteger factor = new BigInteger("1");
        // while (factor == 1)
        while(factor.compareTo(new BigInteger("1")) == 0) {
            // for (count = 1; count <= cycleSize && factor <= 1; count++)
            for(BigInteger count = new BigInteger("1"); count.compareTo(cycleSize) <= 0 && factor.compareTo(new BigInteger("1")) <= 0; count = count.add(new BigInteger("1"))) {
                // x = (x*x + 1) % number;
                x = (x.multiply(x).add(new BigInteger("1"))).mod(n);
                // factor = gcd(x - x_fixed, number);
                factor = gcd(x.subtract(xFixed), n);
            }
            cycleSize = cycleSize.multiply(new BigInteger("2"));
            xFixed = x;
        }
        return factor;
        */

        // From Cormen
        BigInteger i = BigInteger.ONE;
        BigInteger xi = new RSA().getRandomNumber(n.subtract(i));
        BigInteger y = xi; // Salva x1
        BigInteger k = new BigInteger("2");
        BigInteger d = null;
        boolean cont = true;
        while(cont) { // Procura por fatores de n
            i = i.add(BigInteger.ONE);
            // Usa a recorrencia xi = (x_{i-1}^2 - 1) mod n para produzir o próximo xi na sequência x1, x2, x3, ...
            // supomos que essa função se comporta "aleatoriamente", porque ela retira um elemento "aleatoriamente" distribuido
            // no conjunto Zn antes de ciclar.
            xi = xi.pow(2).subtract(BigInteger.ONE).mod(n);
            d = gcd(y.subtract(xi), n);
            if(d.compareTo(BigInteger.ONE) != 0 && d.compareTo(n) != 0) {
                cont = false;
            }
            if (i.compareTo(k) == 0) {
                // Salva em y valores de xi que são potências de 2, x1, x2, x4, x8, x16, ...
                y = xi;
                // k é dobrado sempre que y é atualizado, assim k segue a sequência 1, 2, 4, 8, 16, ...
                k = k.multiply(new BigInteger("2"));
            }
        }
        return d;
    }

    public static BigInteger gcd(BigInteger a, BigInteger b) {
        BigInteger remainder;
        while(b.compareTo(new BigInteger("0")) != 0) {
            remainder = a.mod(b);
            a = b;
            b = remainder;
        }
        return a;
    }

}
