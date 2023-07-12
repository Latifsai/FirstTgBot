package io.proj3ct.LatifFirstSpringBot.repository;

import io.proj3ct.LatifFirstSpringBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
