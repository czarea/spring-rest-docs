package com.czarea.spring.restdocs.dao;

import com.czarea.spring.restdocs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zhouzx
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
