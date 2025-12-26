//package ir.maktabsharif.onlineexaminationplatform.util;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//    private static final String secretKey = "0nline-ex@min@ti0n-pl@tf0rm-jwt-$ignature-$ecret-key";
//
//    public String generateToken(String username){
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) //One Hour
//                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String extractUsername(String token){
//        return Jwts.parser().setSigningKey(secretKey.getBytes())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    public boolean validateToken(String token, UserDetails userDetails){
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        Date expirationDate = Jwts.parser()
//                .setSigningKey(secretKey.getBytes())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getExpiration();
//        return expirationDate.before(new Date());
//    }
//
//
//}
//
