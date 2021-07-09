package com.mingu.restfulwebapp.user.persistence;

import com.mingu.restfulwebapp.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
