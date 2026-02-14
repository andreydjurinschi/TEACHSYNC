package com.teachsync.courseservice.repositories;

import com.teachsync.courseservice.domain.Topic;
import com.teachsync.courseservice.dto_s.topics.TopicCountedDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TopicRepository extends JpaRepository<Topic, Long> {

}
