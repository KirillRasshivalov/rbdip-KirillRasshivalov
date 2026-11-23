package com.rbdip.bookstore.customer;

public record CustomerNameParts(String firstName, String lastName) {

    public static CustomerNameParts fromFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("fullName is required");
        }
        String trimmed = fullName.trim();
        int space = trimmed.indexOf(' ');
        if (space < 0) {
            return new CustomerNameParts(trimmed, "");
        }
        return new CustomerNameParts(trimmed.substring(0, space), trimmed.substring(space + 1).trim());
    }

    public String toFullName() {
        if (lastName == null || lastName.isBlank()) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
