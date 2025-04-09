package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Chats;
import com.example.ecomobile.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatsRepository extends JpaRepository<Chats, Integer> {
    Optional<Chats> findByCustomerAndSeller(User user, User seller);

    List<Chats> findByCustomer(User customer);

    Chats findByCustomerIdAndSellerId(Integer userId, Integer currentSellerId);

    List<Chats> findBySeller(User seller);
}
