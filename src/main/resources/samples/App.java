package com.github.schmittjoaopedro;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Paths;

/**
 * Bibliography:
 * <p>
 * [1] W. Stallings, D. Vieira, A. Barbosa and M. Ferreira, Criptografia e segurança de redes. São Paulo: Pearson Prentice Hall, 2008.
 * [2] H. Java?, "How to generate a random BigInteger value in Java?", Stackoverflow.com, 2018. [Online]. Available: https://stackoverflow.com/questions/2290057/how-to-generate-a-random-biginteger-value-in-java. [Accessed: 20- Apr- 2018].
 * [3] w. David Ireland, "RSA Algorithm", Di-mgt.com.au, 2018. [Online]. Available: https://www.di-mgt.com.au/rsa_alg.html. [Accessed: 21- Apr- 2018].
 * [4] w. David Ireland, "The Euclidean Algorithm and the Extended Euclidean Algorithm", Di-mgt.com.au, 2018. [Online]. Available: https://www.di-mgt.com.au/euclidean.html. [Accessed: 21- Apr- 2018].
 * [5] "Extended Euclidean Algorithm", Www-math.ucdenver.edu, 2018. [Online]. Available: http://www-math.ucdenver.edu/~wcherowi/courses/m5410/exeucalg.html. [Accessed: 22- Apr- 2018].
 * [6] S. Dasgupta, C. Papadimitriou and U. Vazirani, Algorithms.
 * [7] W. RSA?, "Why do we need Euler's totient function $\varphi(N)$ in RSA?", Crypto.stackexchange.com, 2018. [Online]. Available: https://crypto.stackexchange.com/questions/33676/why-do-we-need-eulers-totient-function-varphin-in-rsa. [Accessed: 22- Apr- 2018].
 * [8] "A Quick Tutorial on Pollard's Rho Algorithm", Cs.colorado.edu, 2018. [Online]. Available: https://www.cs.colorado.edu/~srirams/courses/csci2824-spr14/pollardsRho.html. [Accessed: 26- Apr- 2018].
 * [9] T. Cormen and C. Leiserson, Introduction to algorithms, 3rd edition.
 */
public class App {

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(Option.builder("g").longOpt("generateKeys").hasArg().numberOfArgs(1).argName("key_size").desc("Generate keys").required(false).build());
        options.addOption(Option.builder("i").longOpt("input").hasArg().numberOfArgs(1).argName("input_file").desc("Input file for processing").build());
        options.addOption(Option.builder("e").longOpt("encrypt").hasArg().numberOfArgs(1).argName("pub_key_file").desc("Encrypt file").build());
        options.addOption(Option.builder("d").longOpt("decrypt").hasArg().numberOfArgs(1).argName("pri_key_file").desc("Decrypt file").build());
        options.addOption(Option.builder("b").longOpt("brute").hasArg().numberOfArgs(1).argName("public_key_file").desc("Break code with brute-force").required(false).build());
        options.addOption(Option.builder("p").longOpt("pollard").hasArg().numberOfArgs(1).argName("public_key_file").desc("Break code with pollard-force").required(false).build());
        options.addOption(Option.builder("o").longOpt("output").required(false).hasArg().numberOfArgs(1).argName("output_file").desc("Output file").build());
        options.addOption(Option.builder("s").longOpt("samples").required(false).hasArg().numberOfArgs(1).argName("samples").desc("Samples separated by comma").build());
        options.addOption(Option.builder("t").longOpt("testTrials").required(false).hasArg().numberOfArgs(1).argName("trials").desc("Trials number").build());
        options.addOption(Option.builder("a").longOpt("algorithm").required(false).hasArg().numberOfArgs(1).argName("algorithm").desc("Algorithm brute or pollard").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("g")) {
            int keySize = Integer.valueOf(cmd.getOptionValue("g"));
            generateKeyFiles(keySize);
        } else if (cmd.hasOption("e") && cmd.hasOption("i")) {
            String inputFile = cmd.getOptionValue("i");
            String publicKey = cmd.getOptionValue("e");
            String outputDir = "encrypted_message.rsa";
            if(cmd.hasOption("o"))
                outputDir = cmd.getOptionValue("o");
            encrypt(inputFile, publicKey, outputDir);
        } else if (cmd.hasOption("d") && cmd.hasOption("i")) {
            String inputFile = cmd.getOptionValue("i");
            String decryptKey = cmd.getOptionValue("d");
            String outputDir = "original.txt";
            if(cmd.hasOption("o"))
                outputDir = cmd.getOptionValue("o");
            decrypt(inputFile, decryptKey, outputDir);
        } else if (cmd.hasOption("b") && cmd.hasOption("i")) {
            String inputFile = cmd.getOptionValue("i");
            String publicKey = cmd.getOptionValue("b");
            String outputDir = "brute_force.txt";
            if(cmd.hasOption("o"))
                outputDir = cmd.getOptionValue("o");
            bruteForce(inputFile, publicKey, outputDir);
        } else if (cmd.hasOption("p") && cmd.hasOption("i")) {
            String inputFile = cmd.getOptionValue("i");
            String publicKey = cmd.getOptionValue("p");
            String outputDir = "brute_force.txt";
            if(cmd.hasOption("o"))
                outputDir = cmd.getOptionValue("o");
            pollardForce(inputFile, publicKey, outputDir);
        } else if (cmd.hasOption("s") && cmd.hasOption("t") && cmd.hasOption("i") && cmd.hasOption("a")) {
            int trials = Integer.valueOf(cmd.getOptionValue("t"));
            String samples[] = cmd.getOptionValue("s").split(",");
            int keySizes[] = new int[samples.length];
            for(int i = 0; i < samples.length; i++) {
                keySizes[i] = Integer.valueOf(samples[i]);
            }
            String inputFile = cmd.getOptionValue("i");
            String outputDir = "results.csv";
            if(cmd.hasOption("o"))
                outputDir = cmd.getOptionValue("o");
            TestExecutor.executeBruteForce(inputFile, outputDir, keySizes, trials, cmd.getOptionValue("a"));
        } else {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("rsa", options);
        }

    }

    private static void generateKeyFiles(int keySize) throws Exception {
        RSA rsa = new RSA(keySize);
        rsa.generateKeys();
        FileUtils.writeByteArrayToFile(Paths.get("public_key.rsa").toFile(), serialize(rsa.getPublicKey()));
        FileUtils.writeByteArrayToFile(Paths.get("private_key.rsa").toFile(), serialize(rsa.getPrivateKey()));
    }

    private static void encrypt(String inputFile, String publicKey, String outputFile) throws Exception {
        PublicKey pubK = (PublicKey) deserialize(FileUtils.readFileToByteArray(Paths.get(publicKey).toFile()));
        String message = FileUtils.readFileToString(Paths.get(inputFile).toFile(), "UTF-8");
        EncryptedMessage encryptedMessage = new EncryptedMessage();
        encryptedMessage.crypto = pubK.encrypt(message);
        FileUtils.writeByteArrayToFile(Paths.get(outputFile).toFile(), serialize(encryptedMessage));
    }

    private static void decrypt(String inputFile, String decryptKey, String outputFile) throws Exception {
        PrivateKey priK = (PrivateKey) deserialize(FileUtils.readFileToByteArray(Paths.get(decryptKey).toFile()));
        EncryptedMessage encryptedMessage = (EncryptedMessage) deserialize(FileUtils.readFileToByteArray(Paths.get(inputFile).toFile()));
        FileUtils.write(Paths.get(outputFile).toFile(), priK.decrypt(encryptedMessage.crypto), "UTF-8");
    }

    private static void bruteForce(String inputFile, String publicKey, String outputFile) throws Exception {
        PublicKey pubK = (PublicKey) deserialize(FileUtils.readFileToByteArray(Paths.get(publicKey).toFile()));
        EncryptedMessage encryptedMessage = (EncryptedMessage) deserialize(FileUtils.readFileToByteArray(Paths.get(inputFile).toFile()));
        String solution = BruteForce.solve(pubK, encryptedMessage.crypto);
        FileUtils.write(Paths.get(outputFile).toFile(), solution, "UTF-8");
    }

    private static void pollardForce(String inputFile, String publicKey, String outputFile) throws Exception {
        PublicKey pubK = (PublicKey) deserialize(FileUtils.readFileToByteArray(Paths.get(publicKey).toFile()));
        EncryptedMessage encryptedMessage = (EncryptedMessage) deserialize(FileUtils.readFileToByteArray(Paths.get(inputFile).toFile()));
        String solution = PollardForce.solve(pubK, encryptedMessage.crypto);
        FileUtils.write(Paths.get(outputFile).toFile(), solution, "UTF-8");
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

}
