package com.teachsync.repositories;

import com.teachsync.domain.Topic;
import com.teachsync.domain.TopicTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("select t from Topic t where t.tag = :topicTag")
    List<Topic> getTopicByTag(@Param("topicTag") TopicTag topicTag);
}
