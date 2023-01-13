package ru.practicum.shareit.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
