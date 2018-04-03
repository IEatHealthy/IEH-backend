package info.ieathealthy.cryptoutils;

import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;

public class JWTKeyFactory {
    private static Key jwtKey = MacProvider.generateKey();

    public static Key getInstance() {
        return jwtKey;
    }

    private JWTKeyFactory() {
    }
}
