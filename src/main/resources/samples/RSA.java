package com.github.schmittjoaopedro;

import java.math.BigInteger;
import java.util.Random;

public class RSA {

    /**
     * Um tamanho típico para n é de 1024-bits, ou 309 dígitos decimais. Ou seja, n é menor do que 2^1024.
     * Além disso, o tamanho da mensagem M deve ser menor do que o valor de n. [1]
     */
    private int keySize = 1024;

    private BigInteger lowBound = new BigInteger("200");

    private BigInteger p = null;

    private BigInteger q = null;

    private BigInteger n = null;

    private BigInteger phi = null;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    public RSA() {
        super();
    }

    public RSA(int keySize) {
        super();
        this.keySize = keySize;
    }

    public void generateKeys() { // O(n^4)
        generatePrimeNumbers(); // O(n^4)
        n = p.multiply(q); // O(n^2)
        phi = getTotientEuler(); // O(n^2)
        createPublicKey(); // O(n^3)
        createPrivateKey(); // O(n^3)
    }

    private void generatePrimeNumbers() { // O(n^4)
        int half = keySize / 2; // O(n) considerando operação de shift e O(n^2) divisão normal da escola
        while(p == null) {
            BigInteger temp = getRandomNumber(half); // O(1), necessita de mais buscas para entender como analisar essa complexidade
            if(temp.compareTo(lowBound) > 0 && isPrime(temp)) { // O(n^4)
                p = temp;
            }
        }
        while(q == null) {
            BigInteger temp = getRandomNumber(half); // O(1)
            if(temp.compareTo(lowBound) > 0 && !temp.equals(p) && isPrime(temp)) { // O(n^4)
                q = temp;
            }
        }
    }

    /**
     * Essa função define o número de primos relativos encontrados menores que "n" [1].
     *
     * Quando a função recebe um número primo "n", como todos números de 1 até n-1 são primos relativos de "n", então
     * totient(n) para "n" primo é igual à "n-1" [1].
     *
     * Quando a função recebe um número não primo de "n", precisamos listar todos os números que são primos relativos de "n".
     * Exemplo, a para totient(35) os primos relativos são: [1,2,3,4,6,8,9,11,12,13,16,17,18,19,22,23,24,26,27,29,31,32,33,34]
     * logo totient(35) = 24 [1].
     *
     * Dessa forma a função totient de um número primo p é definida como:
     *
     *      totient(p) = p - 1
     *
     * Logo:
     *
     *      totient(n) = totient(p*q) = totient(p) * totient(q) = (p-1)(q-1).
     */
    private BigInteger getTotientEuler() { // O(n^2)
        BigInteger p1 = p.subtract(new BigInteger("1")); // O(n)
        BigInteger q1 = q.subtract(new BigInteger("1")); // O(n)
        return p1.multiply(q1); // O(n^2)
    }

    /**
     * Na prática, as escolhas comuns de e são 3, 5, 17, 257 e 65537 (2^16 + 1). Esses números particulares são escolhidos
     * porque eles são primos e fazem a operação de exponenciação modular ser rápida, tendo somente dois bits de valor 1. [3]
     *
     * Esses cinco números são os cinco primeiros números de Fermat, referidos como F0 até F4, onde Fx = 2^(2^x) + 1. Entretanto
     * somente os cinco primeiros números são primos, o número F5 já não é primo: F5 = 4294967297 = 641 × 6700417.
     */
    private void createPublicKey() {
        BigInteger[] tries = new BigInteger[5];
        tries[0] = new BigInteger("65537");
        tries[1] = new BigInteger("257");
        tries[2] = new BigInteger("17");
        tries[3] = new BigInteger("5");
        tries[4] = new BigInteger("3");
        publicKey = new PublicKey();
        publicKey.n = n;
        publicKey.keySize = keySize;
        for(BigInteger e : tries) {
            if(e.compareTo(phi) > 0) continue; // O(n)
            if(Euclid.getMDC(e, phi).equals(new BigInteger("1"))) { // O(n^3)
                publicKey.e = e;
                break;
            }
        }
        if(publicKey.e == null) throw new RuntimeException("Not found public key");
    }

    private void createPrivateKey() { // O(n^3)
        privateKey = new PrivateKey();
        privateKey.n = n;
        privateKey.keySize = keySize;
        privateKey.d = Euclid.getModInv(publicKey.e, phi); // O(n^3)
    }

    /**
     * Gera um número aleatório limitado a 2^bits com exatamente bits algarismos. [2]
     *
     */
    private BigInteger getRandomNumber(int bits) {
        Random random = new Random();
        BigInteger n = new BigInteger("2").pow(bits);
        BigInteger result = new BigInteger(bits, random);
        while(result.compareTo(n) >= 0 || result.compareTo(new BigInteger("1")) < 0) {
            result = new BigInteger(bits, random);
        }
        return result;
    }

    /**
     * Gera um número aleatório limitado ao tamanho máximo de n.
     */
    public BigInteger getRandomNumber(BigInteger n) {
        Random random = new Random();
        BigInteger result = new BigInteger(n.bitLength(), random);
        while(result.compareTo(n) >= 0 || result.compareTo(new BigInteger("1")) < 0) {
            result = new BigInteger(n.bitLength(), random);
        }
        return result;
    }


    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * O teorema de fermat afirma que: Se "p" é primo e "a" é um inteiro positivo não divisível por "p", "a" < "p" então [1]:
     *
     *      a^(p-1) = 1 mod p               (8.2)
     *
     * Essa formula funciona porque todos elementos que vão de (1, 2, ..., p-1) não são divísiveis por p, logo
     * se fizermos X = {a mod p, 2a mod p, 3a mod p, ... (p-1) mod p} se "a" e "p" forem primos entre sí todos
     * esses valores irão resultar em 1, por que nem "a" e nenhum elemento de (1, 2, ... p-1) são divisível por "p".
     * Porém o teorema de fermat é simplicado da seguinte forma [1]:
     *
     *      a * 2a * ... * (p-1)a = [(1 * 2 * ... * (p-1)] mod p
     *      a^(p-1) (p-1)! = (p-1)! mod p
     *
     *  Como nenhum elemento de (1,2,...,p-1) é divível por "p" nem mesmo o produto, pode-se simplicar para [1]:
     *
     *      a^(p-1) = 1 mod p
     *
     */
    public boolean isPrime(BigInteger n) { // O(n^4)
        // Todos número mod 2 que da resto zero é par, logo não é primo. Não estamos validando o dois
        // pois assume-se que sempre serão testados valores grandes.
        if(isMod2(n)) { // Divisão = O(n^2)
            return false;
        } else {
            // Algoritmo de Miller-Rabin é usado para testar se um dado número ímpar é primo. Inicialmente qualquer
            // inteiro positivo ímpar > 3 pode ser escrito como [1]:
            //      n-1 = 2^k * q
            // Como números não primo são formados por fatores primos, e 2 é o único primo par podemos considerar que
            // a conta acima funcionará extraindo toda a parte que é potência de 2 e restando no final somente o fator ímpar.
            //
            // Se "n" for um número binário o resultado será alcançado deslocando-se o número para esquerda até encontrar
            // o primeiro bit 1 após k deslocamentos [1].
            BigInteger n1 = n.subtract(new BigInteger("1")); // O(n)
            int k = 0;
            BigInteger q = n1;
            while (isMod2(q)) { // Divisão vezes n bits que reduzem a cada divisão por 2 = O(n^3)
                if(q.equals(new BigInteger("0"))) return false; // Comparação = O(n)
                q = q.divide(new BigInteger("2")); // Divisão = O(n^2)
                k++; // O(n)
            }
            // A teoria dos números diz que os primos próximos de "n" são espaçados em média a cada t = ln(n)/2 inteiros.
            // assim a probabilidade de um número "n" composto não ser detectado como composto é de 1/4^t [1].
            double times = ln(n) / 2; // O(1)
            for(long j = 0; j < times; j++) { // O(n), é aproximadamente o número de bits
                if(!isPrimeMillerRabin(n, k, q)) return false; // O(n^3)
            }
            return true;
        }
    }

    private boolean isMod2(BigInteger n) {
        return n.mod(new BigInteger("2")).compareTo(new BigInteger("0")) == 0; // O(n^2) algorithm from Knuth
    }

    /**
     * Esse teste se apoia em duas propriedades para testar se um número é primo [1].
     * 1) A primeira propriedade é que a^2 mod p = 1 se e somente se:
     *      - a mod p = 1
     *      - a mod p = -1 mod p = p - 1, análogo a girar o relógio ao contrário.
     * 2) A segunda propriedade é baseada em n-1 = 2^k * q
     *      - No caso de 1 positivo: a^q = 1 mod p
     *      - No caso de 1 negativo: algum a^q, a^(2q), a^3q, a^((2^k)q) será equivalente a -1. Logo a^((2^j)*q) mod n = p-1.
     *
     *  Assim se n é primo, para a lista de resíduso definida [a^q, a^(q*2), ..., a^(q*2^(k-1)), a^(q*2^k)] módulo de n,
     *  ou o primeiro elemento da lista será primo ou algum elemento nessa lista será igual a (n-1). Essas duas
     *  possiblidades explicam a probabilidade menor que 1/4 em determinar um número composto como primo.
     */
    private boolean isPrimeMillerRabin(BigInteger n, int k, BigInteger q) {
        BigInteger a = getRandomNumber(n); // O(1)
        // a^q mod n == 1
        if(a.modPow(q, n).compareTo(new BigInteger("1")) == 0) return true; // Exponenciação modular = O(n^3) [6]
        for (int j = 0; j < k; j++) {
            // a^((2^j)*q) mod n
            BigInteger mod = a.modPow(new BigInteger("2").pow(j).multiply(q), n); // exponenciação modular = O(n^3) [6]
            if(mod.compareTo(n.subtract(new BigInteger("1"))) == 0) return true; // Subtract e compare = O(n) [6]
        }
        return false;
    }

    public static double ln(BigInteger val) {
        int blex = val.bitLength() - 1022; // any value in 60..1023 is ok
        if (blex > 0)
            val = val.shiftRight(blex);
        double res = Math.log(val.doubleValue());
        return blex > 0 ? res + blex * Math.log(2.0) : res;
    }

}
