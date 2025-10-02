package com.yog.test.springrediscachevanilla.service;


import com.yog.test.springrediscachevanilla.hash.Customrs;
import com.yog.test.springrediscachevanilla.repository.CustomrsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomrsService {

    @Autowired
    private CustomrsDAO customrsDAO;

    public Customrs addCustomrs(Customrs customrs) {
        return customrsDAO.addCustomrs(customrs);
    }

    public Customrs getCustomrs(Integer id) {
        return customrsDAO.getCustomrs(id);
    }

    public List<Customrs> getAllCustomrs() {
        return customrsDAO.getAllCustomrs();
    }

    public String deleteCustomrs(Integer id) {
        return customrsDAO.deleteCustomrs(id);
    }

    public Customrs updateCustomrs(int id, Customrs customrs) {
        return customrsDAO.updateCustomrs(id, customrs);
    }
}
