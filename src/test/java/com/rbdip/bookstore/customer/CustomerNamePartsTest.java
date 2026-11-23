package com.rbdip.bookstore.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CustomerNamePartsTest {

    @Test
    void splitsFirstAndLastName() {
        CustomerNameParts parts = CustomerNameParts.fromFullName("Ivan Petrov");

        assertThat(parts.firstName()).isEqualTo("Ivan");
        assertThat(parts.lastName()).isEqualTo("Petrov");
        assertThat(parts.toFullName()).isEqualTo("Ivan Petrov");
    }

    @Test
    void keepsSingleWordAsFirstName() {
        CustomerNameParts parts = CustomerNameParts.fromFullName("Madonna");

        assertThat(parts.firstName()).isEqualTo("Madonna");
        assertThat(parts.lastName()).isEmpty();
        assertThat(parts.toFullName()).isEqualTo("Madonna");
    }

    @Test
    void keepsEverythingAfterFirstSpaceAsLastName() {
        CustomerNameParts parts = CustomerNameParts.fromFullName("Anna Maria Silva");

        assertThat(parts.firstName()).isEqualTo("Anna");
        assertThat(parts.lastName()).isEqualTo("Maria Silva");
    }

    @Test
    void rejectsBlankFullName() {
        assertThatThrownBy(() -> CustomerNameParts.fromFullName("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
