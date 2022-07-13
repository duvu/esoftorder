package me.duvu.esoftorder.repository;

import me.duvu.esoftorder.domain.Order;
import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.web.rest.vm.SummaryByUserVM;
import me.duvu.esoftorder.web.rest.vm.SummaryVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);

    Long countOrdersByUser(User user);

    @Query("SELECT new me.duvu.esoftorder.web.rest.vm.SummaryByUserVM(o.user.id, o.user.username, count(o), sum(o.quantity * o.price)) from Order o where o.user.id=:userId group by o.user")
    SummaryByUserVM summaryByUser(@Param("userId") Long userId);

    @Query("SELECT new me.duvu.esoftorder.web.rest.vm.SummaryVM(COUNT(o), SUM(o.quantity * o.price)) FROM Order o WHERE o.createdAt BETWEEN :fromDT AND :toDT")
    SummaryVM summary(@Param("fromDT") Instant fromDT, @Param("toDT") Instant toDT);
}
