package com.minimenu.app.service;

import com.minimenu.app.dto.*;
import com.minimenu.app.entity.OrderEntity;
import com.minimenu.app.entity.OrderItem;
import com.minimenu.app.entity.Product;
import com.minimenu.app.repository.OrderItemRepository;
import com.minimenu.app.repository.OrderRepository;
import com.minimenu.app.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse create(OrderCreateRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order items required");
        }

        OrderEntity order = new OrderEntity();
        order.setAddress(req.getAddress());
        order.setCardHolderName(req.getCardHolderName());
        order.setMaskedCardNumber(maskCard(req.getCardNumber()));
        order.setStatus("CONFIRMED");

        // ETA: 15 dk hazırlık + rastgele 5-10 dk
        int eta = 15 + ThreadLocalRandom.current().nextInt(5, 11);
        order.setEtaMinutes(eta);

        double total = 0.0;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest ir : req.getItems()) {
            Product product = productRepository.findById(ir.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found: " + ir.getProductId()));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(ir.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setLineTotal(product.getPrice() * ir.getQuantity());
            item.setOrder(order);

            total += item.getLineTotal();
            items.add(item);
        }

        order.setTotalAmount(total);
        order.setItems(items);

        OrderEntity saved = orderRepository.save(order);
        // orderItem’lar cascade ile kaydolur; ayrıca saveAll gerekmez ama isterseniz bırakılabilir
        // orderItemRepository.saveAll(items);

        return toResponse(saved);
    }

    public OrderResponse getById(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return toResponse(order);
    }

    public List<OrderResponse> list() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    // --- Helpers ---
    private String maskCard(String cardNumber) {
        String digits = cardNumber.replaceAll("\\D", "");
        if (digits.length() < 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "**** **** **** " + last4;
    }

    private OrderResponse toResponse(OrderEntity order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setAddress(order.getAddress());
        res.setCardHolderName(order.getCardHolderName());
        res.setMaskedCardNumber(order.getMaskedCardNumber());
        res.setTotalAmount(order.getTotalAmount());
        res.setEtaMinutes(order.getEtaMinutes());
        res.setStatus(order.getStatus());
        res.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponse> itemDtos = new ArrayList<>();
        for (OrderItem it : order.getItems()) {
            OrderItemResponse d = new OrderItemResponse();
            d.setProductId(it.getProduct().getId());
            d.setProductName(it.getProduct().getName());
            d.setUnitPrice(it.getUnitPrice());
            d.setQuantity(it.getQuantity());
            d.setLineTotal(it.getLineTotal());
            itemDtos.add(d);
        }
        res.setItems(itemDtos);
        return res;
    }
}
