package com.mopl.mopl_batch.batch.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mopl.mopl_batch.batch.entity.Content;
import com.mopl.mopl_batch.batch.entity.Type;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
	boolean existsBySourceIdAndType(long sourceId, Type type);
}
