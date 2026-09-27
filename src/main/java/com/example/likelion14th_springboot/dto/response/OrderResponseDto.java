package com.example.likelion14th_springboot.dto.response;

import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponseDto {

    private Long orderId;
    private Long buyerId;

    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer totalPrice;

    private DeliverStatus deliverStatus;

    private String receiver;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String zipCode;

    public static OrderResponseDto fromEntity(Orders order) {

        ProductOrders productOrder = order.getProductOrders().get(0);
        ShippingAddress address = order.getShippingAddress();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .buyerId(order.getBuyer().getId())

                .productId(productOrder.getProduct().getId())
                .productName(productOrder.getProduct().getName())
                .quantity(productOrder.getQuantity())
                .totalPrice(
                        productOrder.getProduct().getPrice()
                                * productOrder.getQuantity()
                )

                .deliverStatus(order.getDeliverStatus())

                .receiver(address.getReceiver())
                .phoneNumber(address.getPhoneNumber())
                .roadAddress(address.getRoadAddress())
                .detailAddress(address.getDetailAddress())
                .zipCode(address.getZipCode())

                .build();
    }
}