package com.mopl.mopl_batch.batch.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mopl.mopl_batch.batch.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

	Optional<Tag> findByName(String tagName);

	List<Tag> findAllByNameIn(Set<String> tagNames);
}
