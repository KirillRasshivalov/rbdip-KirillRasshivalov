package com.rbdip.bookstore.order;

import com.rbdip.bookstore.customer.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Заказ ссылается на нормализованную сущность Customer (ЛР2).
 * Поле полного имени клиента остаётся доступным через customer —
 * цель expand-contract миграции в ЛР3 (разбить на firstName/lastName).
 */
@Entity
@Table(name = "orders")
public class Order {

    private static final String STATUS_NEW = "new";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_CANCELLED = "cancelled";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private final Instant createdAt = Instant.now();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
        // for JPA
    }

    public Order(Customer customer, String status) {
        this.customer = customer;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCustomerFullName() {
        return customer.getFullName();
    }

    public String getCustomerAddress() {
        return customer.getAddress();
    }

    public String getCustomerPhone() {
        return customer.getPhone();
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public boolean isNew() {
        return STATUS_NEW.equals(status);
    }

    public boolean hasCustomerPhone() {
        return customer.hasPhone();
    }

    public void markCompleted() {
        this.status = STATUS_COMPLETED;
    }

    public void markCancelled() {
        this.status = STATUS_CANCELLED;
    }
}
