package my.project.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

//    @GetMapping("/{orderId}")
//    public ResponseEntity<>


    /**
     * POST /api/orders
     * Создание нового заказа.
     *
     * GET /api/orders/{orderId}
     * Получение информации о конкретном заказе.
     *
     * GET /api/orders/user/{userId}
     * Получение всех заказов пользователя.
     *
     * PATCH /api/orders/{orderId}/status
     * Обновление статуса заказа.
     *
     * DELETE /api/orders/{orderId}
     * Отмена заказа.
     */
}
