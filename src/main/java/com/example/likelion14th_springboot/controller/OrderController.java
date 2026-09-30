package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.request.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.request.OrderUpdateRequestDto;
import com.example.likelion14th_springboot.dto.response.OrderResponseDto;
import com.example.likelion14th_springboot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderCreateRequestDto dto) {

        return ResponseEntity.ok(
                orderService.createOrder(dto)
        );
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBuyer(
            @PathVariable Long buyerId) {

        return ResponseEntity.ok(
                orderService.getOrdersByBuyer(buyerId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderUpdateRequestDto dto) {

        return ResponseEntity.ok(
                orderService.updateOrder(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity.ok(
                "주문이 성공적으로 삭제되었습니다."
        );
    }
}