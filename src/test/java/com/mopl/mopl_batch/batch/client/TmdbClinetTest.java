package com.mopl.mopl_batch.batch.client;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mopl.mopl_batch.batch.batch.tmdb.client.TmdbClient;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.ContentSaveDto;
import com.mopl.mopl_batch.batch.entity.Type;

@SpringBootTest
public class TmdbClinetTest {

	@Autowired
	private TmdbClient tmdbClient;

	@Test
	public void fetchMovieContentsTest() {
		List<ContentSaveDto> contentSaveDtos = tmdbClient.fetchContent(Type.MOVIE, 1);
		for (ContentSaveDto dto : contentSaveDtos) {
			System.out.println("title: " + dto.getTitle());
			System.out.println("desc: " + dto.getDescription());
			System.out.println("type: " + dto.getType());
			System.out.println("thumbnail: " + dto.getThumbnailUrl());
			System.out.println("======================");
		}
	}

	@Test
	public void fetchTvContentsTest() {
		List<ContentSaveDto> contentSaveDtos = tmdbClient.fetchContent(Type.TV_SERIES, 1);
		for (ContentSaveDto dto : contentSaveDtos) {
			System.out.println("title: " + dto.getTitle());
			System.out.println("desc: " + dto.getDescription());
			System.out.println("type: " + dto.getType());
			System.out.println("thumbnail: " + dto.getThumbnailUrl());
			System.out.println("======================");
		}
	}
}
