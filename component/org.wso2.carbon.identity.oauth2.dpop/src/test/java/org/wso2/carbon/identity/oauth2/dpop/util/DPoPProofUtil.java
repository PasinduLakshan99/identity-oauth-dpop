/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth2.dpop.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import static com.nimbusds.jose.JWSAlgorithm.ES256;
import static com.nimbusds.jose.JWSAlgorithm.RS384;
import static org.wso2.carbon.identity.oauth2.dpop.util.DPoPTestConstants.DUMMY_HTTP_METHOD;
import static org.wso2.carbon.identity.oauth2.dpop.util.DPoPTestConstants.DUMMY_HTTP_URL;
import static org.wso2.carbon.identity.oauth2.dpop.util.DPoPTestConstants.DUMMY_JTI;

public class DPoPProofUtil {

    private static final String rsaPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt9x8A/JZb313HsuwnUNMat52cNQSo" +
            "I7HfHtv2IwM7QFtuq/HzMLwlYajYPIkaCiIhG67vGStNQAYPUG+z7fW6uXI3cLX+9ws2moPwj" +
            "SnPhCf/UFmwRUXSSXNBUthVWTFJeUIYQ/WldeZyOD4LGpc+OhxHkj4PQvz2nZUhYM0vu163a8" +
            "NbKvC3IQ+pbFOmW9mnGCSO2YqPN/zS1G1X76CdGxtJzVIpdjj4/HgoKCo+RAysMnnKDQz3+lm" +
            "d+kQBqXzvVx0ZNuPY/B7nBzT6kvKqNBRwduPwzEgkH3rBpIBv0Ve+pHdI6Tm/2c6bC1NRlu+b" +
            "/g8CeZDE0tZ4IyhTVsAIQIDAQAB";

    private static final String rsaPrivateKey =
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC33HwD8llvfXcey7CdQ0xq3" +
            "nZw1BKgjsd8e2/YjAztAW26r8fMwvCVhqNg8iRoKIiEbru8ZK01ABg9Qb7Pt9bq5cjdwtf73C" +
            "zaag/CNKc+EJ/9QWbBFRdJJc0FS2FVZMUl5QhhD9aV15nI4Pgsalz46HEeSPg9C/PadlSFgzS" +
            "+7Xrdrw1sq8LchD6lsU6Zb2acYJI7Zio83/NLUbVfvoJ0bG0nNUil2OPj8eCgoKj5EDKwyeco" +
            "NDPf6WZ36RAGpfO9XHRk249j8HucHNPqS8qo0FHB24/DMSCQfesGkgG/RV76kd0jpOb/ZzpsL" +
            "U1GW75v+DwJ5kMTS1ngjKFNWwAhAgMBAAECggEADrgFa5F2rHC4XQxEZsqQ7wtBIxYvKZBUkv" +
            "gUw5qunDidjrDsx00h0m6VXLj1xirchvGQcOwEW7ZWumyteFaIy4Q6uNoUzVJaet+7xDnP262" +
            "cCTu3nKRyGUZ/67kVoS7wg3Ca455PeO5qHsU3yOJ47+o3yAtiaAyxaF9Js+iFi/U3JCM54S9s" +
            "OiTP7j62O72CZhqZcQcmZcbxXzJl/4F9pIJvMaj5IAWt7KNZGlZ62aa1G+cXWghcCcgQf7k7I" +
            "eWAPbHl1eviXDxGo9mIG41NMCklZOpdmQkTStsEVzALI+jx9miqv1Beenb+hHoK7oiKTFCl9f" +
            "vB1yFunJl1zHEWeQKBgQDIODvXMz34i74kzVZzyUkBve10J2xjTqSTB0XZVUEJeWmsWWLUbvy" +
            "xmeLrVOMMPwLQi5JmloAcUdfXSUkU73fOGDJ8K1Z0Tm8NHIl2UBgWmZfIpg/rZDqb195cqWZz" +
            "/nf1Nko8WJXFGwBmeLPR4HVvIa7HeSglUjCGY0QKBefiLQKBgQDrFY/N9HezKMjtaj5JYjRLL" +
            "FNf3wE/CqmYa9+w6U96AVNswD/DlCPnCGRo7fpmobm2brEDKVJm78ZBfMIL3p76O6OjFlQT+o" +
            "P/2dAE6hU4MlYmr+w2Mqxut+BSNv6AHgtdKRUxAY6Ld5EocKdabwaL9t/TiY8pAcS9rT63DI9" +
            "yRQKBgQC+S/xMOG7RGXian/NoT0qtdigHOyUgafGvsLzpqMcMyzHt1nNBd0+DOcDcbSzzSbxS" +
            "HCYEjUysHfmorAXi+QuEfakWLVaZaqbP7myUX+HVMRx7X6JH11aBIrY8meE/o/+9t2DtZEDNO" +
            "zGxM02tz8mt23S0MGpAtpJaWGSlpiFT7QKBgDoEUj8z7C6tDBl7tO+Lavh6cgEhGj+itARH6y" +
            "bQDatAlIQsVhBAiTPFYHJ8+OVHWHvriYgMNKfu2PDkh0dCo92BxnrDUfC0TMthx/LOinoaAiT" +
            "+Gb+uddvFSXlA1UJtJ8TQFMjJZ5KH6a0fUE4DRIxaWxbrxgcKxrFBBk9KrEQ5AoGBAKcFnoup" +
            "LWTebgLlQ2ox80sXTdCdweSdHv8tIAZGZUs97BcPpTujyVldx7bRgLpcV93FpBafPIN5FjU1H" +
            "uihfak3h0SQi2WxyCJpZiH+XNK/9tabN2MKQji7wjbrQRN06jNuUXeo6X18vcBVVVj2TogFJL" +
            "fQqNUgIMZN35pyBtja";


    private static final String ecPublicKey =
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE+6F4irv76jwSiLHebVzksLfjtXYplS9RwmvJF" +
            "dRp+rcZtUIbQLcscH1SjsIigl4Ha80CG14Y0OofBVwwS7IAjQ==";

    private static final String ecPrivateKey =
            "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCAfBD6WwWEgj5FQ01/2zqR4NNu/i" +
            "MEB/6fwhaMMh3GQFw==";

    private static final String DPOP_JWT_TYPE = "dpop+jwt";

    public static final String EC_DPOP_JWK_THUMBPRINT = "C07a9MZgz5wYywPc39Tw81gE8QzhkpC14sjx-2pAwbI";

    public static final String RSA_DPOP_JWK_THUMBPRINT = "_Z3DHS03lCZVeRs-J9fO7JHuTE0BmVYuBF6Rdc5qjII";

    /*
        * Generate a DPoP proof with the default values.
        * @return DPoP proof.
        * @throws NoSuchAlgorithmException
        * @throws JOSEException
        * @throws InvalidKeySpecException
     */
    public static String genarateDPoPProof()
            throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {

        return genarateDPoPProof("RSA", DUMMY_JTI, DUMMY_HTTP_METHOD, DUMMY_HTTP_URL,
                new Date(System.currentTimeMillis()),  DPoPTestConstants.ACCESS_TOKEN_HASH, DPOP_JWT_TYPE);
    }

    /*
        * Generate a DPoP proof by passing keyPairType, jti, httpMethod, httpUrl.
        * @param keyPairType
        * @param jti
        * @param httpMethod
        * @param httpUrl
        * @return DPoP proof.
        * @throws NoSuchAlgorithmException
        * @throws JOSEException
        * @throws InvalidKeySpecException
     */
    public static String genarateDPoPProof(String keyPairType, String jti, String httpMethod, String httpUrl)
            throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {

        return genarateDPoPProof(keyPairType, jti, httpMethod, httpUrl, new Date(System.currentTimeMillis()),
                DPoPTestConstants.ACCESS_TOKEN_HASH, DPOP_JWT_TYPE);
    }

    /*
        * Generate a DPoP proof by passing keyPairType, jti, httpMethod, httpUrl, iat.
        * @param keyPairType
        * @param jti
        * @param httpMethod
        * @param httpUrl
        * @param iat
        * @return DPoP proof.
        * @throws NoSuchAlgorithmException
        * @throws JOSEException
        * @throws InvalidKeySpecException
     */
    public static String genarateDPoPProof(String keyPairType, String jti, String httpMethod, String httpUrl, Date iat)
            throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {

        return genarateDPoPProof(keyPairType, jti, httpMethod, httpUrl, iat, DPoPTestConstants.ACCESS_TOKEN_HASH,
                DPOP_JWT_TYPE);
    }

    /*
        * Generate a DPoP proof by passing keyPairType, jti, httpMethod, httpUrl, iat, accessTokenHash, jwtType.
        * @param keyPairType
        * @param jti
        * @param httpMethod
        * @param httpUrl
        * @param iat
        * @param accessTokenHash
        * @param jwtType
        * @return DPoP proof.
        * @throws NoSuchAlgorithmException
        * @throws JOSEException
        * @throws InvalidKeySpecException
     */
    public static String genarateDPoPProof(String keyPairType, String jti, String httpMethod, String httpUrl, Date iat,
                                           String accessTokenHash, String jwtType)
            throws NoSuchAlgorithmException, JOSEException, InvalidKeySpecException {

        /* Read all bytes from the private key file */
        String privateKeyString = keyPairType.equals("RSA") ? rsaPrivateKey : ecPrivateKey;
        byte[] bytes = Base64.getDecoder().decode(privateKeyString);

        /* Generate private key. */
        PKCS8EncodedKeySpec privateKs = new PKCS8EncodedKeySpec(bytes);
        KeyFactory privateKf = KeyFactory.getInstance(keyPairType);
        PrivateKey privateKey = privateKf.generatePrivate(privateKs);

        /* Read all the public key bytes */
        String publicKeyString = keyPairType.equals("RSA") ? rsaPublicKey : ecPublicKey;
        byte[] pubBytes = Base64.getDecoder().decode(publicKeyString);

        /* Generate public key. */
        X509EncodedKeySpec ks = new X509EncodedKeySpec(pubBytes);
        KeyFactory kf = KeyFactory.getInstance(keyPairType);
        PublicKey publicCert = kf.generatePublic(ks);

        JWK jwk;
        if ("EC".equals(keyPairType)) {
            jwk = new ECKey.Builder(Curve.P_256, (ECPublicKey) publicCert).build();
        } else {
            jwk = new RSAKey.Builder((RSAPublicKey) publicCert).build();
        }

        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder();
        jwtClaimsSetBuilder.issueTime(iat);
        jwtClaimsSetBuilder.jwtID(jti);
        jwtClaimsSetBuilder.claim("htm", httpMethod);
        jwtClaimsSetBuilder.claim("htu", httpUrl);
        jwtClaimsSetBuilder.claim("ath", accessTokenHash);

        JWSHeader.Builder headerBuilder;
        if ("EC".equals(keyPairType)) {
            headerBuilder = new JWSHeader.Builder(ES256);
        } else {
            headerBuilder = new JWSHeader.Builder(RS384);
        }
        headerBuilder.type(new JOSEObjectType(jwtType));
        headerBuilder.jwk(jwk);
        SignedJWT signedJWT = new SignedJWT(headerBuilder.build(), jwtClaimsSetBuilder.build());

        if ("EC".equals(keyPairType)) {
            ECDSASigner ecdsaSigner = new ECDSASigner(privateKey, Curve.P_256);
            signedJWT.sign(ecdsaSigner);
        } else {
            RSASSASigner rsassaSigner = new RSASSASigner(privateKey);
            signedJWT.sign(rsassaSigner);
        }
        return signedJWT.serialize();
    }
}
