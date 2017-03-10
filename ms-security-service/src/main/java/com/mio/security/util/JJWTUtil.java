package com.mio.security.util;

import java.util.HashMap;
import java.util.Map;

import com.mio.security.dto.JJWTBean;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



public class JJWTUtil {

	public static String generateToken(String secretKey,String identity,String issuer,Object bean) {
		JwtBuilder jwtBuilder = Jwts.builder();
		Map claims = new HashMap();
		claims.put("reback", bean);
		jwtBuilder.setSubject(identity).setIssuer(issuer).setClaims(claims);
		jwtBuilder.setSubject(identity).setIssuer(issuer).setClaims(claims).signWith(SignatureAlgorithm.HS256, secretKey).compact();

		String token = jwtBuilder.signWith(SignatureAlgorithm.HS256, secretKey).compact();
		return token;

	}
	
	public static JJWTBean decodeJJWTBeanToken(String secretKey,String token){
		Map<String,Object>  beanMap = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("reback", Map.class);
		JJWTBean bean = new JJWTBean();
		bean.setUuid((String)beanMap.get("uuid"));
		return bean;
	}
}
