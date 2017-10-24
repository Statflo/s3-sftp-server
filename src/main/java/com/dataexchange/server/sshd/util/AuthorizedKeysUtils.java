package com.dataexchange.server.sshd.util;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Utils for encode and decode PublicKey java objects to Linux
 * authorized_keys file format entries.
 */
public class AuthorizedKeysUtils {

    /**
     * Encode PublicKey (DSA or RSA encoded) to authorized_keys like string
     *
     * @param publicKey DSA or RSA encoded
     * @param user      username for output authorized_keys like string
     * @return authorized_keys like string
     * @throws IOException
     */
    public static String encodePublicKey(PublicKey publicKey, String user)
            throws IOException {
        String publicKeyEncoded;
        if (publicKey.getAlgorithm().equals("RSA")) {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(byteOs);
            dos.writeInt("ssh-rsa".getBytes().length);
            dos.write("ssh-rsa".getBytes());
            dos.writeInt(rsaPublicKey.getPublicExponent().toByteArray().length);
            dos.write(rsaPublicKey.getPublicExponent().toByteArray());
            dos.writeInt(rsaPublicKey.getModulus().toByteArray().length);
            dos.write(rsaPublicKey.getModulus().toByteArray());
            publicKeyEncoded = new String(
                    Base64.encodeBase64(byteOs.toByteArray()));
            return "ssh-rsa " + publicKeyEncoded + " " + user;
        } else if (publicKey.getAlgorithm().equals("DSA")) {
            DSAPublicKey dsaPublicKey = (DSAPublicKey) publicKey;
            DSAParams dsaParams = dsaPublicKey.getParams();

            ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(byteOs);
            dos.writeInt("ssh-dss".getBytes().length);
            dos.write("ssh-dss".getBytes());
            dos.writeInt(dsaParams.getP().toByteArray().length);
            dos.write(dsaParams.getP().toByteArray());
            dos.writeInt(dsaParams.getQ().toByteArray().length);
            dos.write(dsaParams.getQ().toByteArray());
            dos.writeInt(dsaParams.getG().toByteArray().length);
            dos.write(dsaParams.getG().toByteArray());
            dos.writeInt(dsaPublicKey.getY().toByteArray().length);
            dos.write(dsaPublicKey.getY().toByteArray());
            publicKeyEncoded = new String(
                    Base64.encodeBase64(byteOs.toByteArray()));
            return "ssh-dss " + publicKeyEncoded + " " + user;
        } else {
            throw new IllegalArgumentException(
                    "Unknown public key encoding: " + publicKey.getAlgorithm());
        }
    }

    public static PublicKey decodePublicKey(String keyLine) throws Exception {
        PublicKeyDecoder publicKeyDecoder = new PublicKeyDecoder();

        return publicKeyDecoder.decode(keyLine);
    }

    public static void main(String[] args) throws Exception {
        PublicKeyDecoder publicKeyDecoder = new PublicKeyDecoder();
        PublicKey key = AuthorizedKeysUtils.decodePublicKey("ssh-dss AAAAB3NzaC1kc3MAAAEBAJyvhm7fo3fm5EpbyaZDSqy/8A5dw/MKq/vCdy2oG96DCbjW4yKD1D908Pp+MY5eIAVr8+e8h8ANgwGu+g3Oo9CoP33loMiTqLx9gZb3+0AbpR//6lWo5x3j8WB7fvGXLBlah80jiKh4jJKcj94C///F2Gr2Xq6uK4x7x0+t/av9MpwM5aeKLMsYVXNWUeyBYKYJ7rra9/8odppPAfikITE/RypPs2dz8gobZaz6RydxwcCGUehTN1Wb8pxDQ7n3YK1vdIHqJlQkWzdj2stM+zqQqE8MeOKQNJgzcrT6gG9ZR+1f+A2AhwPajkB3h+ihNWT98pKxNozxVGRZpICTye8AAAAVAM3s1rvUK5KuZC+6RTXziSKJW+S/AAABAD5lewSxC8oNlmB1c1BCmvub8C7DN9WwM0mgpkhBZu2d8m1RgOc/BNOmcBhI4OTGB0GDvAHH/hs7n7o9mH/Ouhl2ybvm33GYnZch/I+iGpVjqh3Tgg+6KnZBqZI2kNVw0VfLNvqrXY2AQwY9jfV1GaIYaC9F3P/g0TTsd37vBpUuPaH6BeSfJfSoQ8crCHS4NCwh/4BiBcid7mxQPbscYPomxW7Kyrhj9OWhnHCZ6WCS6MgP4QNGCyRHHLY0VaR3UAdO5yQEd7VIAMTs/Zvsl+Xp9oTLzsiyUa8rkyc4eIqYDFndEhbnBi28A1POs5kz16sjZXFKBph/azVdGNPMj/MAAAEAMWoJsXBqMrft68FYo/xaGVP0XA2T7gwan/0K2rn/2kCcQSe8nXpQhaLJ9H57GHRVC37kjLTMJfN9Q4Xy8xKx05GK/jiiJQAXiWV7io+ZH/1IIYInVnk2TCE0mF1UW7gQgDQqQWBXMPCdrObSK6i54oYg/o0i/6aYkL3hpJQQsiO4wt5yEUHR5Yz80QG2SkNMO0dZoVV8liaQMr9LBF16eASCZvE/Q2uY2NP+QfD2s79y1yUav0NySIOA87ok90l+0yoeDwPQ08LVL6covpA0HgtWrtsGN/2trDdbEKd6CTMu2YTjKGjcXjYBcSJdPqkGhV+n/mem4G66Edg0OUqZTA==");
        System.out.println(key);
        //System.out.println(AuthorizedKeysUtils.getKey("AAAAB3NzaC1kc3MAAAEBAJyvhm7fo3fm5EpbyaZDSqy/8A5dw/MKq/vCdy2oG96DCbjW4yKD1D908Pp+MY5eIAVr8+e8h8ANgwGu+g3Oo9CoP33loMiTqLx9gZb3+0AbpR//6lWo5x3j8WB7fvGXLBlah80jiKh4jJKcj94C///F2Gr2Xq6uK4x7x0+t/av9MpwM5aeKLMsYVXNWUeyBYKYJ7rra9/8odppPAfikITE/RypPs2dz8gobZaz6RydxwcCGUehTN1Wb8pxDQ7n3YK1vdIHqJlQkWzdj2stM+zqQqE8MeOKQNJgzcrT6gG9ZR+1f+A2AhwPajkB3h+ihNWT98pKxNozxVGRZpICTye8AAAAVAM3s1rvUK5KuZC+6RTXziSKJW+S/AAABAD5lewSxC8oNlmB1c1BCmvub8C7DN9WwM0mgpkhBZu2d8m1RgOc/BNOmcBhI4OTGB0GDvAHH/hs7n7o9mH/Ouhl2ybvm33GYnZch/I+iGpVjqh3Tgg+6KnZBqZI2kNVw0VfLNvqrXY2AQwY9jfV1GaIYaC9F3P/g0TTsd37vBpUuPaH6BeSfJfSoQ8crCHS4NCwh/4BiBcid7mxQPbscYPomxW7Kyrhj9OWhnHCZ6WCS6MgP4QNGCyRHHLY0VaR3UAdO5yQEd7VIAMTs/Zvsl+Xp9oTLzsiyUa8rkyc4eIqYDFndEhbnBi28A1POs5kz16sjZXFKBph/azVdGNPMj/MAAAEAMWoJsXBqMrft68FYo/xaGVP0XA2T7gwan/0K2rn/2kCcQSe8nXpQhaLJ9H57GHRVC37kjLTMJfN9Q4Xy8xKx05GK/jiiJQAXiWV7io+ZH/1IIYInVnk2TCE0mF1UW7gQgDQqQWBXMPCdrObSK6i54oYg/o0i/6aYkL3hpJQQsiO4wt5yEUHR5Yz80QG2SkNMO0dZoVV8liaQMr9LBF16eASCZvE/Q2uY2NP+QfD2s79y1yUav0NySIOA87ok90l+0yoeDwPQ08LVL6covpA0HgtWrtsGN/2trDdbEKd6CTMu2YTjKGjcXjYBcSJdPqkGhV+n/mem4G66Edg0OUqZTA=="));
        System.out.println(AuthorizedKeysUtils.decodePublicKey("ssh-dss AAAAB3NzaC1kc3MAAAEBAJyvhm7fo3fm5EpbyaZDSqy/8A5dw/MKq/vCdy2oG96DCbjW4yKD1D908Pp+MY5eIAVr8+e8h8ANgwGu+g3Oo9CoP33loMiTqLx9gZb3+0AbpR//6lWo5x3j8WB7fvGXLBlah80jiKh4jJKcj94C///F2Gr2Xq6uK4x7x0+t/av9MpwM5aeKLMsYVXNWUeyBYKYJ7rra9/8odppPAfikITE/RypPs2dz8gobZaz6RydxwcCGUehTN1Wb8pxDQ7n3YK1vdIHqJlQkWzdj2stM+zqQqE8MeOKQNJgzcrT6gG9ZR+1f+A2AhwPajkB3h+ihNWT98pKxNozxVGRZpICTye8AAAAVAM3s1rvUK5KuZC+6RTXziSKJW+S/AAABAD5lewSxC8oNlmB1c1BCmvub8C7DN9WwM0mgpkhBZu2d8m1RgOc/BNOmcBhI4OTGB0GDvAHH/hs7n7o9mH/Ouhl2ybvm33GYnZch/I+iGpVjqh3Tgg+6KnZBqZI2kNVw0VfLNvqrXY2AQwY9jfV1GaIYaC9F3P/g0TTsd37vBpUuPaH6BeSfJfSoQ8crCHS4NCwh/4BiBcid7mxQPbscYPomxW7Kyrhj9OWhnHCZ6WCS6MgP4QNGCyRHHLY0VaR3UAdO5yQEd7VIAMTs/Zvsl+Xp9oTLzsiyUa8rkyc4eIqYDFndEhbnBi28A1POs5kz16sjZXFKBph/azVdGNPMj/MAAAEAMWoJsXBqMrft68FYo/xaGVP0XA2T7gwan/0K2rn/2kCcQSe8nXpQhaLJ9H57GHRVC37kjLTMJfN9Q4Xy8xKx05GK/jiiJQAXiWV7io+ZH/1IIYInVnk2TCE0mF1UW7gQgDQqQWBXMPCdrObSK6i54oYg/o0i/6aYkL3hpJQQsiO4wt5yEUHR5Yz80QG2SkNMO0dZoVV8liaQMr9LBF16eASCZvE/Q2uY2NP+QfD2s79y1yUav0NySIOA87ok90l+0yoeDwPQ08LVL6covpA0HgtWrtsGN/2trDdbEKd6CTMu2YTjKGjcXjYBcSJdPqkGhV+n/mem4G66Edg0OUqZTA==").equals(key));
    }

    private static class PublicKeyDecoder {

        private byte[] bytes;
        private int pos;

        private PublicKeyDecoder() {
        }

        /**
         * @param key String as authorized_keys like format
         * @return PublicKey object with RSA or DSA key encoded.
         * @throws Exception
         */
        PublicKey decode(String key) throws Exception {
            bytes = null;
            pos = 0;

            // look for the Base64 encoded part of the line to decode
            // both ssh-rsa and ssh-dss begin with "AAAA" due to the length bytes
            for (String part : key.split(" ")) {
                if (part.startsWith("AAAA")) {
                    byte[] bytePart = part.getBytes();
                    bytes = Base64.decodeBase64(bytePart);
                    break;
                }
            }
            if (bytes == null) {
                throw new IllegalArgumentException("no Base64 part to decode");
            }

            String type = decodeType();
            switch (type) {
                case "ssh-rsa": {
                    BigInteger e = decodeBigInt();
                    BigInteger m = decodeBigInt();
                    RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
                    return KeyFactory.getInstance("RSA").generatePublic(spec);
                }
                case "ssh-dss": {
                    BigInteger p = decodeBigInt();
                    BigInteger q = decodeBigInt();
                    BigInteger g = decodeBigInt();
                    BigInteger y = decodeBigInt();
                    DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);
                    return KeyFactory.getInstance("DSA").generatePublic(spec);
                }
                default:
                    throw new IllegalArgumentException("unknown type " + type);
            }
        }

        private String decodeType() {
            int len = decodeInt();
            String type = new String(bytes, pos, len);
            pos += len;
            return type;
        }

        private int decodeInt() {
            return ((bytes[pos++] & 0xFF) << 24) | ((bytes[pos++] & 0xFF) << 16)
                    | ((bytes[pos++] & 0xFF) << 8) | (bytes[pos++] & 0xFF);
        }

        private BigInteger decodeBigInt() {
            int len = decodeInt();
            byte[] bigIntBytes = new byte[len];
            System.arraycopy(bytes, pos, bigIntBytes, 0, len);
            pos += len;
            return new BigInteger(bigIntBytes);
        }
    }
}