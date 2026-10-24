package com.payment.veyra.mapper;

import com.payment.veyra.payments.dto.response.OrderResponse;
import com.payment.veyra.payments.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderResponse toResponse(OrderRecord order);
}
