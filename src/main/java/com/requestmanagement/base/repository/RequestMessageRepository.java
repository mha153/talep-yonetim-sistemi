package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.AppUser;
import com.requestmanagement.base.model.MessageChannel;
import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestMessageRepository extends JpaRepository<RequestMessage, Long> {
    List<RequestMessage> findByRequestAndChannelOrderByCreatedAtAsc(Request request, MessageChannel channel);

    boolean existsByRequestAndChannelAndReadFalseAndAuthorNot(Request request, MessageChannel channel,
                                                                AppUser author);

    boolean existsByRequestAndReadFalseAndAuthorNot(Request request, AppUser author);

    List<RequestMessage> findByRequest(Request request);
}
