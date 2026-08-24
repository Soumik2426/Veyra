package com.payment.veyra.merchant.repository;

import com.payment.veyra.merchant.dto.response.ApiKeyResponse;
import com.payment.veyra.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_Id(UUID merchantId);
}
