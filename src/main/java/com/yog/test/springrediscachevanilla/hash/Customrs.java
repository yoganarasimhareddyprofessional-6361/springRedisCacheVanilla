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
