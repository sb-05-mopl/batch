package com.mopl.mopl_batch.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String name;

	public Tag(String name) {
		this.name = name;
	}
}