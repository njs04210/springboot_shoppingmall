package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.OrderStatus;
import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;

import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("주문 테스트")
    public void order() {
        // 아이템 생성
        Item item = saveItem();

        // 유저 생성
        Member member = saveMember();

        // 주문 생성
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(10);
        Long orderId = orderService.order(orderDto, member.getEmail());

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        int totalPrice = orderDto.getCount() * item.getPrice();
        assertEquals(totalPrice, order.getTotalPrice());
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder() {
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(item.getId());
        orderDto.setCount(10);
        Long orderId = orderService.order(orderDto, member.getEmail());

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        orderService.cancelOrder(orderId);

        assertEquals(100, item.getStockNumber());
        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());
    }


    private Member saveMember() {
        Member member = new Member();
        member.setEmail("test@gmail.com");
        return memberRepository.save(member);
    }

    private Item saveItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setItemDetail("테스트 상품입니다.");
        item.setPrice(10000);
        item.setStockNumber(100);
        item.setItemSellStatus(ItemSellStatus.SELL);

        return itemRepository.save(item);
    }


}