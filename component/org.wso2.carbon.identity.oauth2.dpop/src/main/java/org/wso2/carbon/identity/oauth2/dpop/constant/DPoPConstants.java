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

package org.wso2.carbon.identity.oauth2.dpop.constant;

/**
 * This class defines constants for Oauth2 DPoP validation.
 */
public class DPoPConstants {

    public static final String VALIDITY_PERIOD = "header_validity_period";
    public static final int DEFAULT_HEADER_VALIDITY = 60000;
    public static final String DPOP_ISSUED_AT = "iat";
    public static final String DPOP_HTTP_URI = "htu";
    public static final String DPOP_HTTP_METHOD = "htm";
    public static final String DPOP_ACCESS_TOKEN_HASH = "ath";
    public static final String DPOP_JWT_TYPE = "dpop+jwt";
    public static final String DPOP_TOKEN_TYPE = "DPoP";
    public static final String INVALID_DPOP_PROOF = "invalid_dpop_proof";
    public static final String INVALID_DPOP_ERROR = "Invalid DPoP Proof";
    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_CLIENT_ERROR = "Invalid Client";
    public static final String ECDSA_ENCRYPTION = "EC";
    public static final String RSA_ENCRYPTION = "RSA";
    public static final String HTTP_METHOD ="httpMethod";
    public static final String HTTP_URL ="httpUrl";
    public static final String JTI = "jti";
    public static final String OAUTH_DPOP_HEADER = "DPoP";
    public static final String CNF = "cnf";
    public static final String TOKEN_TYPE = "token_type";
    public static final String JWK_THUMBPRINT = "jkt";
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String OAUTH_REVOKE_ENDPOINT = "/oauth2/revoke";
    public static final String SKIP_DPOP_VALIDATION_IN_REVOKE = "skip_dpop_validation_in_revoke";
    public static final boolean DEFAULT_SKIP_DPOP_VALIDATION_IN_REVOKE_VALUE = true;

    /**
     * This class defines SQLQueries.
     */
    public static class SQLQueries {

        public static final String RETRIEVE_TOKEN_BINDING_BY_REFRESH_TOKEN =
                "SELECT BINDING.TOKEN_BINDING_TYPE,BINDING.TOKEN_BINDING_VALUE,BINDING.TOKEN_BINDING_REF " +
                        "FROM IDN_OAUTH2_ACCESS_TOKEN TOKEN LEFT JOIN IDN_OAUTH2_TOKEN_BINDING BINDING ON " +
                        "TOKEN.TOKEN_ID=BINDING.TOKEN_ID WHERE TOKEN.REFRESH_TOKEN = ? " +
                        "AND BINDING.TOKEN_BINDING_TYPE = ?";
    }
}
