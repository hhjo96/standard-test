package com.example.sparta.mytest;

import com.example.sparta.dto.OrderCreateRequest;
import com.example.sparta.dto.OrderLineRequest;
import com.example.sparta.entity.Order;
import com.example.sparta.entity.Product;
import com.example.sparta.repository.OrderLineRepository;
import com.example.sparta.repository.OrderRepository;
import com.example.sparta.repository.ProductRepository;
import com.example.sparta.service.OrderService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MyOrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;

    @BeforeEach
    void setup() {
        Product product1 = new Product(null, "스파오반팔티", "스파오무지티", 10000L, 100L, 0L);
        Product product2 = new Product(null, "유니클로반팔티", "유니클로무지티", 15000L, 50L, 0L);

        productRepository.save(product1);
        productRepository.save(product2);

    }

    @Test
    void 주문생성시_order_orderLine_product_모두연동됨() {
        //given
        List<OrderLineRequest> orderLines = List.of(
                new OrderLineRequest(1L, 10L),
                new OrderLineRequest(2L, 20L)
        );
        //1번 테스트코드를 위해서 OrderCreateRequest에 새로운 생성자를 만든다
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(150000L, orderLines);

        //when
        Order order = orderService.create(orderCreateRequest);

        //then
        AssertionsForClassTypes.assertThat(order.getId()).isNotNull();
        AssertionsForInterfaceTypes.assertThat(orderRepository.findAll()).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(orderLineRepository.findAll()).hasSize(2);

        Product product1 = productRepository.findById(1L).get();
        Product product2 = productRepository.findById(2L).get();

        AssertionsForClassTypes.assertThat(product1.getAmount()).isEqualTo(90L);
        AssertionsForClassTypes.assertThat(product1.getSaleCount()).isEqualTo(10L);
        AssertionsForClassTypes.assertThat(product2.getAmount()).isEqualTo(30L);
        AssertionsForClassTypes.assertThat(product2.getSaleCount()).isEqualTo(20L);
    }

    @Test
    void 존재하지_않는_상품은_주문_불가() {
        // given
        List<OrderLineRequest> orderLines = List.of(
                new OrderLineRequest(999L, 1L) // 없는 상품
        );
        OrderCreateRequest request = new OrderCreateRequest(1000L, orderLines);

        // when & then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("존재하지 않는 상품");
    }

    @Test
    void 주문수량이_0이면_주문_불가() {
        // given
        List<OrderLineRequest> orderLines = List.of(
                new OrderLineRequest(1L, 0L) // 수량이 0이다
        );
        OrderCreateRequest request = new OrderCreateRequest(0L, orderLines);

        // when & then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("주문 상품의 개수가 0 이하입니다.");
    }
}
