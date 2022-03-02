package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.OrderStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    EntityManager em;

    private Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellstatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    public Order createOrder() {
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setOrder(order);
            orderItem.setOrderPrice(1000);
            orderItem.setRegTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());
            orderItem.setCount(10);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {

        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setOrder(order);
            orderItem.setOrderPrice(1000);
            orderItem.setRegTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());
            orderItem.setCount(10);
            order.getOrderItems().add(orderItem);
        }

        orderRepository.saveAndFlush(order);
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest() {
        Order order = this.createOrder();
        order.getOrderItems().remove(0); // 부모 엔티티와 연관관계가 끊어지므로 고아 객체를 삭제하는 쿼리문이 실행됨
        em.flush();
    }

    @Test
    @DisplayName("지연로딩 테스트")
    public void lazyLoadingTest() {
        Order order = this.createOrder();
        OrderItem orderItem = order.getOrderItems().get(0);

        em.flush();
        em.clear();

        OrderItem orderItem1 = orderItemRepository.findById(orderItem.getId())
                .orElseThrow(EntityNotFoundException::new); // 즉시로딩 설정돼있다면, orderItem 엔티티 하나 조회할 때 order_item, item, orders, member 테이블을 조인해서 한꺼번에 가져오게 됨
        System.out.println("Order class : " + orderItem1.getOrder().getClass()); // 지연 로딩일 경우, 실제 사용 전까지 실제 엔티티 대신 프록시 객체가 들어가게 된다.
        System.out.println("===============================");
        orderItem1.getOrder().getOrderDate(); // 프록시 객체는 실제 사용 전까지 데이터 로딩을 하지 않고, 사용 시점에 조회 쿼리문이 실행된다.
        System.out.println("===============================");
    }
}