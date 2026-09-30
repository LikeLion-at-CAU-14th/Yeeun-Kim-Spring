package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.Product;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.dto.request.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrderResponseDto;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import com.example.likelion14th_springboot.repository.MemberRepository;
import com.example.likelion14th_springboot.repository.OrdersRepository;
import com.example.likelion14th_springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto dto) {

        // 1. 구매자 조회
        Member buyer = memberRepository.findById(dto.getBuyerId())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 구매자입니다."));

        // 2. 상품 조회
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 3. 주문 수량 검증
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        }

        // 4. 재고 확인
        if (product.getStock() < dto.getQuantity()) {
            throw new IllegalArgumentException("상품 재고가 부족합니다.");
        }

        // 5. 전체 주문 금액 계산
        int totalPrice = product.getPrice() * dto.getQuantity();

        // 6. 구매자 잔액 확인
        if (buyer.getDeposit() == null || buyer.getDeposit() < totalPrice) {
            throw new IllegalArgumentException("계좌 잔액이 부족합니다.");
        }

        // 7. 주문 생성
        Orders order = Orders.builder()
                .buyer(buyer)
                .deliverStatus(DeliverStatus.PREPARATION)
                .shippingAddress(dto.toShippingAddress())
                .productOrders(new ArrayList<>())
                .deleted(false)
                .build();

        // 8. 상품과 주문을 연결하는 ProductOrders 생성
        ProductOrders productOrder = ProductOrders.builder()
                .product(product)
                .orders(order)
                .quantity(dto.getQuantity())
                .build();

        // 9. 주문에 상품 정보 추가
        order.getProductOrders().add(productOrder);

        // 10. 상품 재고 차감
        product.reduceStock(dto.getQuantity());

        // 11. 구매자 잔액 차감
        buyer.useDeposit(totalPrice);

        // 12. 주문 저장
        Orders savedOrder = ordersRepository.save(order);

        // 13. 응답 DTO로 변환
        return OrderResponseDto.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByBuyer(Long buyerId) {

        return ordersRepository
                .findByBuyerIdAndDeletedFalse(buyerId)
                .stream()
                .map(OrderResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (Boolean.TRUE.equals(order.getDeleted())) {
            throw new IllegalArgumentException("삭제된 주문입니다.");
        }

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public OrderResponseDto updateOrder(
            Long orderId,
            OrderUpdateRequestDto dto) {

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (Boolean.TRUE.equals(order.getDeleted())) {
            throw new IllegalArgumentException("삭제된 주문입니다.");
        }

        if (order.getDeliverStatus() != DeliverStatus.PREPARATION) {
            throw new IllegalArgumentException(
                    "배송 준비 중인 주문만 배송정보를 수정할 수 있습니다."
            );
        }

        order.updateShippingAddress(
                dto.toShippingAddress()
        );

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (Boolean.TRUE.equals(order.getDeleted())) {
            throw new IllegalArgumentException("이미 삭제된 주문입니다.");
        }

        if (order.getDeliverStatus() != DeliverStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "배송 완료된 주문만 삭제할 수 있습니다."
            );
        }

        order.softDelete();
    }
}