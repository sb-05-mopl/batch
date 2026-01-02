package com.mopl.mopl_batch.batch.client;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mopl.mopl_batch.batch.batch.common.dto.ContentFetchDto;
import com.mopl.mopl_batch.batch.batch.sport.client.SportsApiClient;

@SpringBootTest
public class SportsApiClientTest {

	@Autowired
	private SportsApiClient sportsApiRestClient;

	@Test
	public void fetchContentTest() {
		LocalDate date = LocalDate.now();
		List<ContentFetchDto> contentFetchDtos = sportsApiRestClient.fetchContent(date);

		for (ContentFetchDto dto : contentFetchDtos) {
			System.out.println("title: " + dto.getTitle());
			System.out.println("desc: " + dto.getDescription());
			System.out.println("type: " + dto.getType());
			System.out.println("thumbnail: " + dto.getThumbnailUrl());
			System.out.println("======================");
		}
	}

}
