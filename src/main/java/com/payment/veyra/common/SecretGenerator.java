package com.payment.veyra.common;

import io.jsonwebtoken.Jwts;
import java.util.Base64;

public class SecretGenerator {
    public static void main(String[] args) {
        byte[] key = Jwts.SIG.HS256.key().build().getEncoded();
        System.out.println(
                Base64.getEncoder().encodeToString(key)
        );
    }
}