package com.example.sparta.service;

import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.OrderLine;
import com.example.sparta.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceSupportTest {

    List<Product> productList;
    List<OrderLineRequest> orderLines;
    Order order;

    @BeforeEach
    void init() {
        productList = List.of(
                new Product(1L, "티셔츠", "무지 티 입니다.", 10000L, 100L, 0L),
                new Product(2L, "바지", "긴 바지 입니다.", 15000L, 50L, 0L),
                new Product(3L, "신발", "컨버스 신발입니다.", 18000L, 200L, 0L)
        );
        order = new Order(10000L);
    }

    @Test
    void 성공_주문_생성() {
        // given
        orderLines = List.of(
                new OrderLineRequest(1L, 10L),
                new OrderLineRequest(2L, 15L),
                new OrderLineRequest(3L, 20L)
        );

        // when
        List<OrderLine> result = OrderServiceSupport.buildOrderLines(productList, orderLines, order);

        // then
        assertThat(result.size()).isEqualTo(3);

        // product 1 - 티셔츠
        assertThat(result.get(0).getProduct().getAmount()).isEqualTo(90L);
        assertThat(result.get(0).getProduct().getSaleCount()).isEqualTo(10L);
        // product 2 - 바지
        assertThat(result.get(1).getProduct().getAmount()).isEqualTo(35L);
        assertThat(result.get(1).getProduct().getSaleCount()).isEqualTo(15L);
        // product 3 - 신발
        assertThat(result.get(2).getProduct().getAmount()).isEqualTo(180L);
        assertThat(result.get(2).getProduct().getSaleCount()).isEqualTo(20L);
    }

    @Test
    void 실패_존재하지_않는_상품_주문() {
        // given
        orderLines = List.of(
                new OrderLineRequest(1L, 10L),
                new OrderLineRequest(2L, 15L),
                new OrderLineRequest(3L, 20L),
                new OrderLineRequest(4L, 25L)   // 존재하지 않는 상품
        );

        //
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> OrderServiceSupport.buildOrderLines(productList, orderLines, order));

        // then
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 상품은 주문할 수 없습니다 !");
    }

    @Test
    void 실패_상품_재고를_초과하는_주문량() {
        // given
        orderLines = List.of(
                new OrderLineRequest(1L, 1000L),  // 티셔츠 상품 재고 초과
                new OrderLineRequest(2L, 15L),
                new OrderLineRequest(3L, 20L)
        );

        //
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> OrderServiceSupport.buildOrderLines(productList, orderLines, order));

        // then
        assertThat(e.getMessage()).isEqualTo("티셔츠의 상품 재고가 부족합니다.");
    }

    @Test
    void 실패_주문수량이_0이하() {
        //given
        orderLines = List.of(
                new OrderLineRequest(1L, 0L),
                new OrderLineRequest(2L, 15L),
                new OrderLineRequest(3L, 20L)

        );

        //when
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> OrderServiceSupport.buildOrderLines(productList, orderLines, order));

        //then
        assertThat(e.getMessage()).isEqualTo("주문 상품의 개수가 0 이하입니다.");

    }
}