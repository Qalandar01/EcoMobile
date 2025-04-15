package com.example.ecomobile.repo;

import com.example.ecomobile.entity.Chats;
import com.example.ecomobile.entity.Messages;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Messages, Long> {

    List<Messages> findByChatOrderBySentAtAsc(Chats chat);
    List<Messages> findByChatIdAndIdGreaterThanOrderBySentAtAsc(Integer id, Integer lastMessageId);
}
