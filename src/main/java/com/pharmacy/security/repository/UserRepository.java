package com.pharmacy.security.repository;

import com.pharmacy.security.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends MongoRepository<User,String> {
	boolean existsByUserName(String userName);
    public User findByUserName(String username);
}
