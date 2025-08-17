package com.projects.billing.model;

import lombok.Data;

@Data
public class Customer {
    private String customerId;
    private String name;
    private String phone;
    private String email;
}