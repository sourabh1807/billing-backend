package com.projects.billing.controller;

import com.projects.billing.service.BillService;
import com.projects.billing.model.Bill;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bills")
public class BillController {
    private final BillService service;

    public BillController(BillService service) {
        this.service = service;
    }

    @PostMapping
    public String createBill(@RequestBody Bill bill) {
        service.createBill(bill);
        return "Bill saved successfully with ID: " + bill.getBillId();
    }


    @GetMapping("/{billId}")
    public Bill getBill(@PathVariable String billId) {
        return service.getBill(billId);
    }
}
