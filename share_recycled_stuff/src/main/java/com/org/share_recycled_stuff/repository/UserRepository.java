package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   Optional<User> findByPhone(String  phoneNumber);

}
