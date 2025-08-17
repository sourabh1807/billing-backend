package com.projects.billing.model;

import lombok.Data;
import java.util.List;

@Data
public class Bill {
    private String billId;
    private String date;
    private Customer customer;
    private List<Product> products;
    private double totalAmount;
}




