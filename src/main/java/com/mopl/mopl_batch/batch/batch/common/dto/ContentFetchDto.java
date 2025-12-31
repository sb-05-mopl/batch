package com.mopl.mopl_batch.batch.batch.common.dto;

import java.util.List;

import com.mopl.mopl_batch.batch.entity.Content;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentFetchDto {
	private String title;
	private String description;
	private Type type;
	private String thumbnailUrl;
	private long sourceId;
	private List<String> tags;

	public static Content of(ContentFetchDto dto) {
		return new Content(dto.getTitle(), dto.getDescription(), dto.getType(), dto.getThumbnailUrl(),
			dto.getSourceId());
	}
}
