package com.mopl.mopl_batch.batch.entity;

import lombok.Getter;

@Getter
public enum Type {
	MOVIE("영화"),
	TV_SERIES("TV 시리즈"),
	SPORTS("스포츠");

	private final String typeTag;

	Type(String typeTag) {
		this.typeTag = typeTag;
	}
}