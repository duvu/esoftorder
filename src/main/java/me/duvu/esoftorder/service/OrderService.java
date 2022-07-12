package me.duvu.esoftorder.service;

import me.duvu.esoftorder.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link Order}.
 */
public interface OrderService {
    Order save(Order order);
    Order update(Order order);
    Optional<Order> partialUpdate(Order order);
    Page<Order> findAll(Pageable pageable);
    Page<Order> findAllByUserId(Long currentUserId, Pageable pageable);

    Optional<Order> findOne(Long id);
    Optional<Order> findByUserIdAndId(Long userId, Long id);
    void delete(Long id);

}
