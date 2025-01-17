package com.example.oauth.authorization.example.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenHelper {

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	public static final String SECRET = "SecretKeyToGenJWTs";

	public String getUsernameFromToken(String token) {
		return getClaimsFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimsFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(SECRET)
			.parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getUsername());
	}

	private String doGenerateToken(Map<String, Object> claims, String username) {
		return Jwts.builder()
			.setClaims(claims).setSubject(username)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
			.signWith(SignatureAlgorithm.HS256, SECRET).compact();
	}


	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		boolean isUserNameEquals = username.equals(userDetails.getUsername());
		boolean isTokenValid =!isTokenExpired(token);
		return isUserNameEquals && isTokenValid;
	}



}
