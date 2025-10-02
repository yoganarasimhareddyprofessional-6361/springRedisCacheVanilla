# 2. Spring Redis

Think of **Redis** as a **very fast, memory-based data store** – kind of like a notebook that's always open on your desk. You can write stuff in it, read it quickly, and even use it to send messages.

- 🔥 **In-memory**: It's super fast because it stores everything in RAM.
- 🗃️ **Key-value**: Works like a giant dictionary (e.g., `"user:123" -> "John"`).
- ⚡ **Used for**: caching, real-time analytics, leaderboards, chat systems, etc.

Let's say you're building a web app.

### Without Redis:

Every time a user logs in, the app hits the **database** to get their profile — slower and more expensive.

### With Redis:

- On first login, fetch from DB and **store in Redis**.
- On next login, check Redis first.
- Redis responds in milliseconds — super fast!

## 🎯 Summary

| Use Case | Description | Real-World Example |
| --- | --- | --- |
| Database | Stores data as key-value pairs | Session, cart, preferences |
| Cache | Temporary, fast-access data | Profile info, product lists |
| Pub/Sub | Real-time message broadcasting | Chat app, live notifications |

link to setup redis : https://redis.io/docs/getting-started/installation/

Basic theory about redis : https://www.educative.io/blog/what-is-redis

## Redis as DB without JPA Queries Vanilla :

- Step 1: create a spring boot project add dependencies >> web,lombok,spring data redis,lombok,devtools
- add Jedis in pom.xml
- Step 2: Create  Hash classes (Entity classes) ( in memory MAP Collection)

```java
package com.yog.test.springrediscachevanilla.hash;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Customrs") // is nothing but @Entity + @Table annotation for Redis Hash
public class Customrs implements Serializable {

    public static final long serialVersionUID = 1L;
    // This is the serial version UID for the class. which is needed for Redis Hash to validate between client and server.

    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dob;
    private String phone;

}

```

- Step 3: creatre a config class to config redis here we return default config as we are running in local but if we are using production level then we define config in code for host and IP which are commented in-line in code

```java
package com.yog.test.springrediscachevanilla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories // enable redis repositories mandatory for redis cache
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        //host and port config when using JedisConnectionFactory on prod environment
        //RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<Object, Object> template() {
        // template bean is mandatory for redis cache
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}

```

- Step 4: DAO layer repo layer

```java
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

```

- Step 5: create Service Layer from DAO Class

```java
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

```

- Step 6: Controller Class —> start server —> install redis Helper plugin intellj  visualize the data

```java
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
```

- Step 7: Application.properties

```java
spring.application.name=springRedisCacheVanilla

server.port=9991
```

- Step 8: Before start start ur redis :

```java
link to setup redis : https://redis.io/docs/getting-started/installation/

Basic theory about redis : https://www.educative.io/blog/what-is-redis

brew install redis
redis-server
or 
brew services start redis

brew services info redis
brew services stop redis
```

Now we can use Redis in 3 possible ways as below :

1. Redis as DB (JPA calls)
2. Redis as Cache
3. Redis as PUB-SUB