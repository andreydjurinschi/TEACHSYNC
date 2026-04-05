package com.teachsync.repository;

import com.teachsync.domain.Role;
import com.teachsync.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from users u where u.email = :email", nativeQuery = true)
    User findUserByEmail(@Param("email") String email);

    List<User> findAllByRole(Role role);

}
