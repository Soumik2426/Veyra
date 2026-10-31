package com.payment.veyra.merchant.security;

import com.payment.veyra.merchant.entity.ApiKey;
import com.payment.veyra.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming request: {}", request.getRequestURI());
        try {
            final String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String[] credentials = decodeApiKey(authorizationHeader);
            if (credentials == null) {
                throw new BadRequestException("Malformed API key header");
            }
            String keyId = credentials[0];
            String rawSecret = credentials[1];

            ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                    .orElseThrow(() -> new BadRequestException("Invalid API key"));

            if (!apiKey.isEnabled() || !secretMatches(rawSecret, apiKey)) {
                throw new BadRequestException("Invalid API key");
            }

            var auth = new UsernamePasswordAuthenticationToken(keyId, null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setMerchantId(apiKey.getMerchant().getId());
            merchantContext.setKeyId(apiKey.getKeyId());
            filterChain.doFilter(request, response);
        }catch (Exception e){
            log.error("API Key Authentication failed: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private boolean secretMatches(String rawSecret, ApiKey apiKey){
        if(BCRYPT.matches(rawSecret, apiKey.getKeySecretHash())) {
            return true;
        }

        boolean isInGracePeriod = apiKey.getGracePeriodExpiryAt() != null
                && LocalDateTime.now().isBefore(apiKey.getGracePeriodExpiryAt());

        return isInGracePeriod
                && apiKey.getGracePeriodExpiryAt()!=null
                && BCRYPT.matches(rawSecret, apiKey.getPreviousKeySecretHash());
    }


    private String[] decodeApiKey(String header){
        String encoded = header.split("Basic ")[1];
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        int colonIndex = decoded.indexOf(":");
        if(colonIndex<1){
            return null;
        }
        return new String[]{decoded.substring(0, colonIndex), decoded.substring(colonIndex+1)};
    }
}
