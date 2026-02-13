package com.example.sparta.service;

import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.OrderLine;
import com.example.sparta.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceSupport {

    public static List<OrderLine> buildOrderLines(
            List<Product> products,
            List<OrderLineRequest> orderLineRequests,
            Order order
    ) {
        if (products.size() != orderLineRequests.size()) {
            throw new RuntimeException("존재하지 않는 상품은 주문할 수 없습니다 !");
        }

        List<OrderLine> orderLines = new ArrayList<>();
        for (Product product : products) {
            for (OrderLineRequest olr : orderLineRequests) {
                if (Objects.equals(product.getId(), olr.getProductId())) {
                    product.purchased(olr.getAmount());
                    orderLines.add(new OrderLine(order, product, olr.getAmount()));
                }
            }
        }
        return orderLines;
    }
}
