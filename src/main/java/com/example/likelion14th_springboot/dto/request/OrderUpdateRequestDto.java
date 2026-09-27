package com.example.likelion14th_springboot.dto.request;

import com.example.likelion14th_springboot.domain.ShippingAddress;
import lombok.Getter;

@Getter
public class OrderUpdateRequestDto {

    private String receiver;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String zipCode;

    public ShippingAddress toShippingAddress() {
        return ShippingAddress.builder()
                .receiver(receiver)
                .phoneNumber(phoneNumber)
                .roadAddress(roadAddress)
                .detailAddress(detailAddress)
                .zipCode(zipCode)
                .build();
    }
}