package com.projects.billing.service;

import com.projects.billing.repository.BillRepository;
import com.projects.billing.model.Bill;
import org.springframework.stereotype.Service;

@Service
public class BillService {
    private final BillRepository repository;

    public BillService(BillRepository repository) {
        this.repository = repository;
    }

    public Bill createBill(Bill bill) {
        return repository.save(bill);
    }

    public Bill getBill(String billId) {
        return repository.getBill(billId);
    }
}
