package me.duvu.esoftorder.service;

import me.duvu.esoftorder.domain.Order;
import me.duvu.esoftorder.web.rest.vm.SummaryByUserVM;
import me.duvu.esoftorder.web.rest.vm.SummaryVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Date;
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

    SummaryByUserVM summaryByUser(Long userId);
    SummaryVM summary(Instant fromDt, Instant toDt);
}
