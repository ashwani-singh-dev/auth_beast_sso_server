package com.zentois.authbeast.security.rsa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.zentois.framework.security.enums.AlgoEnum;
import com.zentois.framework.security.rsa.RsaGenerator;
import com.zentois.authbeast.enums.ErrorDescription;
import com.zentois.authbeast.enums.PathEnum;

import lombok.RequiredArgsConstructor;

/**
 * Generates and manages RSA key pairs for use in the application.
 * 
 * The `RsaGenerator` class is responsible for generating RSA key pairs, storing them to a file, and loading them from the file. It uses the `KeyPairGenerator` class from the Java Cryptography Architecture (JCA) to generate the key pairs.
 * 
 * The class has the following methods:
 * 
 * - `generateKeyPair()`: Generates a new RSA key pair, stores it to a file, and returns the key pair.
 * - `storeKeyPair(KeyPair keyPair)`: Stores the given RSA key pair to a file.
 * - `loadKeyPairFromFile()`: Loads the RSA key pair from the file, or generates a new one if the file does not exist.
 * 
 * The location of the key pair file is specified by the `jwt.keypair.file` property, which is injected into the `keypairResource` field using Spring's `@Value` annotation.
 * 
 * @author Ashwani Singh
 * @version 1.0
 * @since 2024-Sep-16
 */
@Component
@RequiredArgsConstructor
public class RsaService
{
    private static final String STORE_FOLDER = PathEnum.LOCAL_FOLDER_PATH.getPath();

    private static final String PRIVATE_KEY_FILE = STORE_FOLDER + "/private_key.pem";

    private static final String PUBLIC_KEY_FILE = STORE_FOLDER + "/public_key.pem";

    private final RsaGenerator rsaGenerator;

    /**
     * Stores the given RSA key pair to a file.
     *
     * This method writes the provided `KeyPair` object to a file located at `KEYPAIR_FILE` using an `ObjectOutputStream`. If an error occurs during the file write operation, an `IOException` will be thrown.
     *
     * @param keyPair The RSA key pair to be stored.
     * @throws IOException If an error occurs while writing the key pair to the file.
     */
    private void storeKeyPair(KeyPair keyPair) throws IOException
    {
        final File storeFolder = new File(STORE_FOLDER);
        if (!storeFolder.exists())
        {
            storeFolder.mkdirs();
        }

        final PrivateKey privateKey = keyPair.getPrivate();

        final PublicKey publicKey = keyPair.getPublic();

        try (FileOutputStream fos = new FileOutputStream(PRIVATE_KEY_FILE))
        {
            fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(privateKey.getEncoded()));
            fos.write("\n-----END PRIVATE KEY-----\n".getBytes());
        }

        try (FileOutputStream fos = new FileOutputStream(PUBLIC_KEY_FILE))
        {
            fos.write("-----BEGIN PUBLIC KEY-----\n".getBytes());
            fos.write(Base64.getEncoder().encode(publicKey.getEncoded()));
            fos.write("\n-----END PUBLIC KEY-----\n".getBytes());
        }
    }

    /**
     * Loads the RSA key pair from a file. If the file does not exist, a new key pair is generated and saved to the file.
     *
     * @return The loaded or newly generated RSA key pair.
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     * @throws RuntimeException If an error occurs while loading or generating the key pair.
     */
    public KeyPair loadKeyPairFromFile() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        try
        {
            final File privateKeyFile = new File(PRIVATE_KEY_FILE);
            final File publicKeyFile = new File(PUBLIC_KEY_FILE);

            if (privateKeyFile.exists() && publicKeyFile.exists())
            {
                // Read private key
                final String privateKeyContent = new String(java.nio.file.Files.readAllBytes(privateKeyFile.toPath()))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
                final byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
                
                // Read public key
                final String publicKeyContent = new String(java.nio.file.Files.readAllBytes(publicKeyFile.toPath()))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
                final byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);

                final KeyFactory keyFactory = KeyFactory.getInstance(AlgoEnum.RSA.getAlgo());
                final PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
                final PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

                return new KeyPair(publicKey, privateKey);
            }
            else
            {
                final KeyPair keyPair = rsaGenerator.generateKeyPair(2048);
                storeKeyPair(keyPair);
                return keyPair;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(ErrorDescription.RSA_FILE_FAILED_TO_LOAD.getMessage(), e);
        }
    }
}