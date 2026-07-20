package com.requestmanagement.base.repository;

import com.requestmanagement.base.model.Request;
import com.requestmanagement.base.model.RequestActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestActivityRepository extends JpaRepository<RequestActivity, Long> {
    List<RequestActivity> findByRequestOrderByCreatedAtAsc(Request request);
}
