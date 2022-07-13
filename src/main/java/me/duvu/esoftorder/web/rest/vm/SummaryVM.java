package me.duvu.esoftorder.web.rest.vm;

import java.time.Instant;

public class SummaryVM {
    Instant from;
    Instant to;
    Long numberOfOrder;
    Double revenue;

    public SummaryVM(Long numberOfOrder, Double revenue) {
        this.numberOfOrder = numberOfOrder;
        this.revenue = revenue;
    }

    public SummaryVM(Instant from, Instant to, Long numberOfOrder, Double revenue) {
        this.from = from;
        this.to = to;
        this.numberOfOrder = numberOfOrder;
        this.revenue = revenue;
    }

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public Long getNumberOfOrder() {
        return numberOfOrder;
    }

    public void setNumberOfOrder(Long numberOfOrder) {
        this.numberOfOrder = numberOfOrder;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SummaryVM summaryVM = (SummaryVM) o;

        if (getFrom() != null ? !getFrom().equals(summaryVM.getFrom()) : summaryVM.getFrom() != null) return false;
        if (getTo() != null ? !getTo().equals(summaryVM.getTo()) : summaryVM.getTo() != null) return false;
        if (getNumberOfOrder() != null ? !getNumberOfOrder().equals(summaryVM.getNumberOfOrder()) : summaryVM.getNumberOfOrder() != null) return false;
        return getRevenue() != null ? getRevenue().equals(summaryVM.getRevenue()) : summaryVM.getRevenue() == null;
    }

    @Override
    public int hashCode() {
        int result = getFrom() != null ? getFrom().hashCode() : 0;
        result = 31 * result + (getTo() != null ? getTo().hashCode() : 0);
        result = 31 * result + (getNumberOfOrder() != null ? getNumberOfOrder().hashCode() : 0);
        result = 31 * result + (getRevenue() != null ? getRevenue().hashCode() : 0);
        return result;
    }
}
