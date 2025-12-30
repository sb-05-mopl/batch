package com.mopl.mopl_batch.batch.batch.common;

import com.mopl.mopl_batch.batch.entity.Content;
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
	private long sourceId;

	public static Content of(ContentSaveDto dto) {
		return new Content(dto.getTitle(), dto.getDescription(), dto.getType(), dto.getThumbnailUrl(),
			dto.getSourceId());
	}
}
