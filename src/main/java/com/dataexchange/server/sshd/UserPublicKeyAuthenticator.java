package com.dataexchange.server.sshd;

import com.dataexchange.server.domain.UserService;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class UserPublicKeyAuthenticator implements PublickeyAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPublicKeyAuthenticator.class);

    private final Map<String, Collection<PublicKey>> userKeyMap;
    private final UserService userService;

    public UserPublicKeyAuthenticator(Map<String, Collection<PublicKey>> userKeyMap, UserService userService) {
        this.userKeyMap = (userKeyMap == null) ? Collections.emptyMap() : userKeyMap;
        this.userService = userService;
    }

    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) {
        if (GenericUtils.isEmpty(userKeyMap)) {
            LOGGER.debug("authenticate({} )[{}] no users or keys", username, session);

            return false;
        }

        Collection<? extends PublicKey> keys = userKeyMap.get(username);
        if (GenericUtils.isEmpty(keys)) {
            LOGGER.debug("authenticate({} )[{}] no keys for user", username, session);

            return false;
        }

        PublicKey matchKey = KeyUtils.findMatchingKey(key, keys);
        boolean matchFound = matchKey != null;
        LOGGER.debug("authenticate({})[{}] match found={}", username, session, matchFound);
        if (matchFound) {
            userService.checkOrAutoCreateUserForPrivateKeyAuthentication(username);
        }

        return matchFound;
    }
}
