package com.example.sparta.mytest;

import com.example.sparta.dto.OrderCreateRequest;
import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.Product;
import com.example.sparta.service.OrderService;
import com.example.sparta.service.OrderServiceSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

//단위 테스트이므로
@ExtendWith(MockitoExtension.class)
class MyOrderServiceTest {
    List<Product> productList;
    List<OrderLineRequest> orderLines;
    Order order;
    @InjectMocks
    OrderService orderService;

    @BeforeEach
    void init() {
        productList = List.of(
                new Product(1L, "티셔츠", "무지 티 입니다.", 10000L, 100L, 0L)
        );
        order = new Order(0L);
    }
    @Test
    void 실패_주문수량이_0이하() {
        //given
        orderLines = List.of(
                new OrderLineRequest(1L, 0L)

        );
        OrderCreateRequest request = new OrderCreateRequest(0L, orderLines);

        //when
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> orderService.create(request));

        //then
        assertThat(e.getMessage()).isEqualTo("주문 상품의 개수가 0 이하입니다.");

    }
}