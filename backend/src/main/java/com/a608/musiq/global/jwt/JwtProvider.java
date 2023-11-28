package com.a608.musiq.global.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
	// private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60L * 5L; // 엑세스 토큰 유효 시간 (5분)
	private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60L * 60L * 24L; // 임시 엑세스 토큰 유효 시간 (24시간)
	private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60L * 60L * 24L; // 리후레쉬 토큰 유효 시간 (24시간)

	@Value("${jwt.secret-key}")
	private String SECRET_KEY;

	public <T> String createAccessToken(T data) {
		return createToken("access-token", data, ACCESS_TOKEN_EXPIRE_TIME);
	}

	public <T> String createRefreshToken(T data) {
		return createToken("refresh-token", data, REFRESH_TOKEN_EXPIRE_TIME);
	}

	private <T> String createToken(String subject, T data, long expire) {

		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS256");

		// 클레임 생성
		Claims claims = Jwts.claims()
			.setSubject(subject)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + expire));
		claims.put("data", data);

		Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

		// 토큰 Builder
		String jwt = Jwts.builder()
			.setHeader(headers)
			.setClaims(claims)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		return jwt;
	}
}