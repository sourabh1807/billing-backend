package com.projects.billing.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.billing.model.Bill;
import com.projects.billing.model.Customer;
import com.projects.billing.model.Product;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BillRepository {

    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper;

    public BillRepository(DynamoDbClient dynamoDbClient, ObjectMapper objectMapper) {
        this.dynamoDbClient = dynamoDbClient;
        this.objectMapper = objectMapper;
    }

    public void save(Bill bill) {
        try {

            if (bill.getBillId() == null || bill.getBillId().isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                bill.setBillId("BILL-" + timestamp);
            }
            
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("billId", AttributeValue.builder().s(bill.getBillId()).build());
            item.put("date", AttributeValue.builder().s(bill.getDate()).build());
            item.put("totalAmount", AttributeValue.builder().n(String.valueOf(bill.getTotalAmount())).build());

            // Serialize customer & products to JSON
            String customerJson = objectMapper.writeValueAsString(bill.getCustomer());
            String productsJson = objectMapper.writeValueAsString(bill.getProducts());

            item.put("customer", AttributeValue.builder().s(customerJson).build());
            item.put("products", AttributeValue.builder().s(productsJson).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName("Bills")
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);

            System.out.println("Bill saved successfully with ID: " + bill.getBillId());
        } catch ( JsonProcessingException e) {
            throw new RuntimeException("Error serializing customer/products", e);
        }
    }

    public Bill getBill(String billId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("billId", AttributeValue.builder().s(billId).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName("Bills")
                .key(key)
                .build();

        Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();

        if (returnedItem == null || returnedItem.isEmpty()) {
            throw new RuntimeException("Bill not found: " + billId);
        }

        Bill bill = new Bill();
        bill.setBillId(returnedItem.get("billId").s());
        bill.setDate(returnedItem.get("date").s());
        bill.setTotalAmount(Double.parseDouble(returnedItem.get("totalAmount").n()));

        try {
            bill.setCustomer(objectMapper.readValue(returnedItem.get("customer").s(), Customer.class));
            bill.setProducts(Arrays.asList(objectMapper.readValue(returnedItem.get("products").s(), Product[].class)));
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing data", e);
        }

        return bill;
    }
}
