package com.yog.test.springrediscachevanilla.controller;

import com.yog.test.springrediscachevanilla.hash.Customrs;
import com.yog.test.springrediscachevanilla.service.CustomrsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomrsController {

    @Autowired
    private CustomrsService service;

    @PostMapping
    public Customrs saveCustomer(@RequestBody Customrs customer) {
        return service.addCustomrs(customer);
    }

    @GetMapping
    public List<Customrs> getAllCustomers() {
        return service.getAllCustomrs();
    }

    @GetMapping("/{id}")
    public Customrs getCustomer(@PathVariable int id) {
        return service.getCustomrs(id);
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable int id) {
        return service.deleteCustomrs(id);
    }

    @PutMapping("/{id}")
    public Customrs updateCustomer(@PathVariable int id, @RequestBody Customrs customer) {
        return service.updateCustomrs(id, customer);
    }
}