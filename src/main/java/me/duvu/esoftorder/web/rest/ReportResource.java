package me.duvu.esoftorder.web.rest;

import me.duvu.esoftorder.service.OrderService;
import me.duvu.esoftorder.util.ResponseUtil;
import me.duvu.esoftorder.web.rest.vm.SummaryByUserVM;
import me.duvu.esoftorder.web.rest.vm.SummaryVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReportResource {
    private final Logger log = LoggerFactory.getLogger(ReportResource.class);
    private final OrderService orderService;

    public ReportResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/report/summaryByUser/{userId}")
    public ResponseEntity<SummaryByUserVM> getReportSummaryByUser(@PathVariable Long userId) {
        SummaryByUserVM vm = orderService.summaryByUser(userId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(vm));
    }

    @GetMapping("/report/summary")
    public ResponseEntity<SummaryVM> getReportSummary(
            @RequestParam(name = "from", required = false) Long fromDt,
            @RequestParam(name = "to", required = false) Long toDt) {

        long toDtx = toDt != null ? toDt : (new Date()).getTime();
        long fromDtx = fromDt != null ? fromDt : toDtx - 24 * 60 * 60 * 1000; // 24 hours

        Instant toDT = Instant.ofEpochMilli(toDtx);
        Instant fromDT = Instant.ofEpochMilli(fromDtx);
        SummaryVM vm = orderService.summary(fromDT, toDT);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(vm));
    }
}
