package com.java.mapper;

import com.java.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper {

    List<Map<String, Object>> listConversations(@Param("userId") Long userId);

    List<Message> listMessages(@Param("userId") Long userId,
                               @Param("otherUserId") Long otherUserId);

    long countUnread(@Param("userId") Long userId);

    void insertMessage(Message message);

    void markAsRead(@Param("userId") Long userId,
                    @Param("otherUserId") Long otherUserId);

    List<Map<String, Object>> listAllUsers();
}
