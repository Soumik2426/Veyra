package com.payment.veyra.mapper;

import com.payment.veyra.payments.dto.response.PaymentResponse;
import com.payment.veyra.payments.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    @Mapping(target = "orderId", source="order.id")
    PaymentResponse toResponse(Payment payment);
}
