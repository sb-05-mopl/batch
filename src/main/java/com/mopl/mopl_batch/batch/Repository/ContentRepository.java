package com.mopl.mopl_batch.batch.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mopl.mopl_batch.batch.entity.Content;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {

	boolean existsBySourceId(long id);
}
