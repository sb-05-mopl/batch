package com.mopl.mopl_batch.batch.batch.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentWithTags {
	private ContentFetchDto content;
	private List<String> tags;
}
