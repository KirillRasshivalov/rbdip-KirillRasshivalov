package com.rbdip.bookstore.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "customer_address")
    private String address;

    @Column(name = "customer_phone")
    private String phone;

    protected Customer() {
        // for JPA
    }

    public Customer(String firstName, String lastName, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName == null ? "" : lastName;
        this.address = address;
        this.phone = phone;
    }

    /**
     * Dual-write точка входа: из API по-прежнему приходит полное имя,
     * сохраняем его как first/last (новая версия схемы). Пока жила колонка
     * customer_full_name, мигра же backfill/триггеры держали старую версию
     * синхронной на уровне БД (V3–V4).
     */
    public static Customer fromFullName(String fullName, String address, String phone) {
        CustomerNameParts parts = CustomerNameParts.fromFullName(fullName);
        return new Customer(parts.firstName(), parts.lastName(), address, phone);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return new CustomerNameParts(firstName, lastName).toFullName();
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public boolean hasPhone() {
        return phone != null && !phone.isBlank();
    }
}
