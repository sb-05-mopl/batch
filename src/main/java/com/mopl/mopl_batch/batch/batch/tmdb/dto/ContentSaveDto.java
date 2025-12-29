package com.mopl.mopl_batch.batch.batch.tmdb.dto;

import com.mopl.mopl_batch.batch.entity.Type;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentSaveDto {
	private String title;
	private String description;
	private Type type;
	private String thumbnailUrl;
}
