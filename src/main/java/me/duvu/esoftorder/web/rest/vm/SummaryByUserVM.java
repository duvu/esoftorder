package me.duvu.esoftorder.web.rest.vm;

import java.io.Serializable;

public class SummaryByUserVM implements Serializable {
    Long userId;
    String username;
    Long numberOfOrder;
    Double revenue;

    public SummaryByUserVM(Long userId, String username, Long numberOfOrder, Double revenue) {
        this.userId = userId;
        this.username = username;
        this.numberOfOrder = numberOfOrder;
        this.revenue = revenue;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SummaryByUserVM that = (SummaryByUserVM) o;

        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null) return false;
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getNumberOfOrder() != null ? !getNumberOfOrder().equals(that.getNumberOfOrder()) : that.getNumberOfOrder() != null) return false;
        return getRevenue() != null ? getRevenue().equals(that.getRevenue()) : that.getRevenue() == null;
    }

    @Override
    public int hashCode() {
        int result = getUserId() != null ? getUserId().hashCode() : 0;
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getNumberOfOrder() != null ? getNumberOfOrder().hashCode() : 0);
        result = 31 * result + (getRevenue() != null ? getRevenue().hashCode() : 0);
        return result;
    }
}
