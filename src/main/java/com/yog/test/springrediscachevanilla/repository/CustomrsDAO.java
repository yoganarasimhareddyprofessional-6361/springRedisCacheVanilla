package com.yog.test.springrediscachevanilla.repository;

import com.yog.test.springrediscachevanilla.hash.Customrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomrsDAO {

    public static final String HASH_KEY = "Customrs";
    // this value should be from @Hash annotation on Customrs class case-sensitive.


    @Autowired
    private RedisTemplate template;

    public Customrs addCustomrs(Customrs customrs) {
        //KEY_HASH is the name of the hash in redis
        //KEY is the id of the customrs object which is used as key in hash and unique
        //VALUE is the customrs object which is stored in hash
        template.opsForHash().put(HASH_KEY, customrs.getId(), customrs);
        return customrs;
    }

    public Customrs getCustomrs(Integer id) {
        //KEY_HASH is the name of the hash in redis
        //KEY is the id of the customrs object which is used as key in hash and unique
        return (Customrs) template.opsForHash().get(HASH_KEY, id);
    }

    public List<Customrs> getAllCustomrs() {
        //KEY_HASH is the name of the hash in redis
        return template.opsForHash().values(HASH_KEY);
    }

    public String deleteCustomrs(Integer id) {
        template.opsForHash().delete(HASH_KEY, id);
        return "Customer " + id + " has been removed from system Successfully!";
    }

    public Customrs updateCustomrs(int id, Customrs customrs) {
        Customrs existingCustomer = (Customrs) template.opsForHash().get(HASH_KEY, id);
        if (existingCustomer != null) {
            existingCustomer.setFirstName(customrs.getFirstName());
            existingCustomer.setLastName(customrs.getLastName());
            existingCustomer.setEmail(customrs.getEmail());
            existingCustomer.setPhone(customrs.getPhone());
            existingCustomer.setDob(customrs.getDob());
            template.opsForHash().put(HASH_KEY, id, existingCustomer);
            return existingCustomer;
        } else {
            throw new RuntimeException("Customer not found !");
        }
    }

}
