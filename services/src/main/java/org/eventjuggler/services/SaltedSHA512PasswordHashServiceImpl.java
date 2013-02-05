package org.eventjuggler.services;

import org.eventjuggler.util.IOUtils;
import org.jboss.util.Base64;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Singleton
public class SaltedSHA512PasswordHashServiceImpl implements PasswordHashService {

    private byte [] salt;

    @PostConstruct
    public void init() {
        try {
            if (salt != null)
                return;

            salt = loadSalt();
            if (salt == null) {
                salt = initSalt();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize salt: ", e);
        }
    }

    private byte[] initSalt() throws IOException, NoSuchAlgorithmException {
        File file = getSaltFile();
        SecureRandom random = new SecureRandom();
        byte [] seed = new byte[4096];
        random.nextBytes(seed);
        String baseEncoded = Base64.encodeBytes(seed);
        IOUtils.copy(new ByteArrayInputStream(baseEncoded.getBytes("utf-8")), new FileOutputStream(file));
        return seed;
    }

    private File getSaltFile() {
        String userDir = System.getProperty("user.home");
        if (userDir == null) {
            throw new IllegalStateException("Runtime has no user.home property set!");
        }

        return new File(userDir, ".eventjuggler.salt");
    }

    private byte[] loadSalt() throws IOException {
        File file = getSaltFile();
        if (file.isFile()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(new FileInputStream(file), baos);
            return baos.toByteArray();
        }
        return null;
    }

    @Lock(LockType.READ)
    public String hash(String cleartext) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 not supported on this runtime: ", e);
        }
        md.update(salt);
        try {
            return Base64.encodeBytes(md.digest(cleartext.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Runtime does not support utf-8!");
        }
    }
}