package com.jpa.learning.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpa.learning.entity.User;

public interface UserRepository extends CrudRepository<User, Long>{

}
