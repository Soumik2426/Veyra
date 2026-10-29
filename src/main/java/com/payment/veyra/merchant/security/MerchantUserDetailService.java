package com.payment.veyra.merchant.security;

import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.merchant.repository.AppUserRepository;
import com.payment.veyra.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantUserDetailService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(username)
                .orElseThrow(()-> new ResourceNotFoundException("User", username));
    }
}
