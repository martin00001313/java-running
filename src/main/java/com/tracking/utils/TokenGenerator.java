package com.tracking.utils;

import com.tracking.data.TokenProperties;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.time.ZoneOffset;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.util.Optional;

/**
 * A structure responsible for token generation/validation.
 *
 * @author martin
 */
public class TokenGenerator {

    private final static long USAGE_DURATION = 100000000;
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private final static SignatureAlgorithm ALGO = SignatureAlgorithm.HS512;
    private final static String ALGO_KEY = "app-secret-000013";

    /**
     * Generate token from based on the provided properties.
     *
     * @param tokenProps
     * @return The generated string token.
     */
    public static String generateToken(TokenProperties tokenProps) {

        return Jwts.builder()
                .setIssuer("LoggingTracker")
                .setSubject(convertToJSON(tokenProps))
                .setAudience("web")
                .setIssuedAt(Date.from(java.time.ZonedDateTime.now(ZoneOffset.UTC).toInstant()))
                .setExpiration(generateExpirationDate())
                .signWith(ALGO, ALGO_KEY)
                .compact();
    }

    /**
     * Validate the token(i.e. is the subject is correct and it's not outdated)
     *
     * @param token The token need to be checked.
     * @return The validation result.
     */
    public static boolean validateAuthToken(final String token) {

        if (token == null || token.isEmpty()) {
            return false;
        }

        final Optional<TokenProperties> props = fetchAllCredentialsFromToken(token);
        if (!props.isPresent()) {
            return false;
        }

        final Optional<Claims> claim = getClaim(token);
        if (!claim.isPresent()) {
            return false;
        }

        final Date currentTime = Date.from(java.time.ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        return claim.get().getExpiration().getTime() >= currentTime.getTime();
    }

    /**
     * Get properties of the token.
     *
     * @param token
     * @return The received properties
     */
    public static Optional<TokenProperties> fetchAllCredentialsFromToken(String token) {

        final Optional<Claims> claims = getClaim(token);
        if (!claims.isPresent()) {
            return Optional.empty();
        }

        final TokenProperties authProps = convertToProp(claims.get().getSubject());

        return Optional.ofNullable(authProps);
    }

    /**
     * Get corresponding claim of the token.
     *
     * @param token
     * @return The received data.
     */
    private static Optional<Claims> getClaim(String token) {
        final Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(ALGO_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | SignatureException exp) {
            return Optional.empty();
        }
        return Optional.of(claims);
    }

    /**
     * Object to JSON string converter.
     *
     * @param object
     * @return The generated JSON string
     */
    private static String convertToJSON(TokenProperties object) {
        return new GsonBuilder().setPrettyPrinting().setDateFormat(DATE_FORMAT).enableComplexMapKeySerialization().serializeNulls().create().toJson(object);
    }

    /**
     * JSON string to Object converter.
     *
     * @param json
     * @return The generated object
     */
    private static TokenProperties convertToProp(String json) {
        return new GsonBuilder().setDateFormat(DATE_FORMAT).enableComplexMapKeySerialization().create().fromJson(json, TokenProperties.class);
    }

    /**
     * Generated expiration date by considering current date and accepted
     * duration.
     *
     * @return
     */
    private static Date generateExpirationDate() {
        return new Date(Date.from(java.time.ZonedDateTime.now(ZoneOffset.UTC).toInstant()).getTime() + USAGE_DURATION);
    }
}
