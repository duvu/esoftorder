package me.duvu.esoftorder.web.rest;

import me.duvu.esoftorder.domain.Order;
import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.repository.OrderRepository;
import me.duvu.esoftorder.security.SecurityUtils;
import me.duvu.esoftorder.service.OrderService;
import me.duvu.esoftorder.service.UserService;
import me.duvu.esoftorder.util.HeaderUtil;
import me.duvu.esoftorder.util.PaginationUtil;
import me.duvu.esoftorder.util.ResponseUtil;
import me.duvu.esoftorder.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link me.duvu.esoftorder.domain.Order}.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "esoftOrderOrder";

    @Value("${app.clientApp.name}")
    private String applicationName;

    private final OrderService orderService;
    private final UserService userService;

    private final OrderRepository orderRepository;

    public OrderResource(OrderService orderService, UserService userService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order : {}", order);
        if (order.getId() != null) {
            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Long currentUserId = SecurityUtils.getCurrentUserIdLogin().orElse(null);
        User user = userService.findOne(currentUserId).get();
        order.setUser(user);
        Order result = orderService.save(order);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Order order)
        throws URISyntaxException {
        log.debug("REST request to update Order : {}, {}", id, order);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Long currentUserId = SecurityUtils.getCurrentUserIdLogin().orElse(null);
        Order existedOrder = orderService.findOne(id).get();

        if (!existedOrder.getUser().getId().equals(currentUserId)) {
            throw new BadRequestAlertException("You are not allowed to modify this order", ENTITY_NAME, "idnotfound");
        }
        User user = userService.findOne(currentUserId).get();
        order.setUser(user);
        Order result = orderService.update(order);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Order> partialUpdateOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Order order
    ) throws URISyntaxException {
        log.debug("REST request to partial update Order partially : {}, {}", id, order);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }


        Long currentUserId = SecurityUtils.getCurrentUserIdLogin().orElse(null);
        Order existedOrder = orderService.findOne(id).get();

        if (!existedOrder.getUser().getId().equals(currentUserId)) {
            throw new BadRequestAlertException("You are not allowed to modify this order", ENTITY_NAME, "idnotfound");
        }

        Optional<Order> result = orderService.partialUpdate(order);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString())
        );
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Orders");

        Long currentUserId = SecurityUtils.getCurrentUserIdLogin().orElse(null);
        Page<Order> page = orderService.findAllByUserId(currentUserId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        Long currentUserId = SecurityUtils.getCurrentUserIdLogin().orElse(null);
        Optional<Order> order = orderService.findByUserIdAndId(currentUserId, id);
        return ResponseUtil.wrapOrNotFound(order);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        Long currentUserId = SecurityUtils.getCurrentUserIdLogin().orElse(null);
        Order order = orderService.findOne(id).orElse(null);
        if(order != null && order.getUser() != null && order.getUser().getId().equals(currentUserId)) {
            orderService.delete(id);
            return ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build();
        } else {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
    }
}
