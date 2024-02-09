package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
    @Query(value = "SELECT * FROM users WHERE code_to_verify = :codeToVerify ",nativeQuery = true)
    Optional<User> findByCodeToVerify(String codeToVerify);
    @Query(value = "SELECT * FROM users WHERE name ILIKE :name% AND is_verify = true LIMIT 10 OFFSET :page",nativeQuery = true)
    List<User> findAllByNameLimitPage(@Param("name") String name,@Param("page") Integer page);
    @Query(value = "SELECT * FROM users WHERE name ILIKE :name% AND is_verify = true",nativeQuery = true)
    List<User> findAllByName(@Param("name") String name);
    @Query(value = "SELECT * FROM users WHERE email ILIKE :email% AND is_verify = true LIMIT 10 OFFSET :page",nativeQuery = true)
    List<User> findAllByEmailLimitPage(@Param("email") String email,@Param("page") Integer page);
    @Query(value = "SELECT * FROM users WHERE email ILIKE :email% AND is_verify = true",nativeQuery = true)
    List<User> findAllByEmail(@Param("email") String email);
    @Query(value = "SELECT * FROM users WHERE is_verify = true LIMIT 10 OFFSET :page",nativeQuery = true)
    List<User> findAllLimitPage(@Param("page") Integer page);
    @Query(value = "SELECT * FROM users WHERE is_verify = true",nativeQuery = true)
    List<User> findAllByVerify();
    @Query(value = "SELECT * FROM users WHERE role_id = 1",nativeQuery = true)
    List<User> findAllByAdmin();
}
