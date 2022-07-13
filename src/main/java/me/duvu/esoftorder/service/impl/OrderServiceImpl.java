package me.duvu.esoftorder.service.impl;

import me.duvu.esoftorder.domain.Order;
import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.repository.OrderRepository;
import me.duvu.esoftorder.repository.UserRepository;
import me.duvu.esoftorder.service.OrderService;
import me.duvu.esoftorder.web.rest.vm.SummaryByUserVM;
import me.duvu.esoftorder.web.rest.vm.SummaryVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order save(Order order) {
        log.debug("Request to save Order : {}", order);
        return orderRepository.save(order);
    }

    @Override
    public Order update(Order order) {
        log.debug("Request to save Order : {}", order);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> partialUpdate(Order order) {
        log.debug("Request to partially update Order : {}", order);

        return orderRepository
            .findById(order.getId())
            .map(existingOrder -> {
                if (order.getReference() != null) {
                    existingOrder.setReference(order.getReference());
                }
                if (order.getCategory() != null) {
                    existingOrder.setCategory(order.getCategory());
                }
                if (order.getQuantity() != null) {
                    existingOrder.setQuantity(order.getQuantity());
                }
                if (order.getPrice() != null) {
                    existingOrder.setPrice(order.getPrice());
                }
                if (order.getServiceName() != null) {
                    existingOrder.setServiceName(order.getServiceName());
                }
                if (order.getDescription() != null) {
                    existingOrder.setDescription(order.getDescription());
                }
                if (order.getNotes() != null) {
                    existingOrder.setNotes(order.getNotes());
                }
                if (order.getCreatedAt() != null) {
                    existingOrder.setCreatedAt(order.getCreatedAt());
                }
                if (order.getUpdatedAt() != null) {
                    existingOrder.setUpdatedAt(order.getUpdatedAt());
                }

                return existingOrder;
            })
            .map(orderRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllByUserId(Long userId, Pageable pageable) {
        log.debug("Request to get all Orders");
        User user = userRepository.findById(userId).get();
        return orderRepository.findAllByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id);
    }

    @Override
    public Optional<Order> findByUserIdAndId(Long userId, Long id) {
        log.debug("Request to get Order : {}/User: {}", id, userId);
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null && order.getUser() != null && order.getUser().getId().equals(userId)) {
            return Optional.of(order);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    @Override
    public SummaryByUserVM summaryByUser(Long userId) {
        return orderRepository.summaryByUser(userId);
    }

    @Override
    public SummaryVM summary(Instant fromDt, Instant toDt) {
        SummaryVM vm = orderRepository.summary(fromDt, toDt);
        vm.setFrom(fromDt);
        vm.setTo(toDt);
        return vm;
    }
}
