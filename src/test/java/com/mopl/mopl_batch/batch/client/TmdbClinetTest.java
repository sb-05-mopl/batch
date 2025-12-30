package com.mopl.mopl_batch.batch.client;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mopl.mopl_batch.batch.batch.common.ContentSaveDto;
import com.mopl.mopl_batch.batch.batch.tmdb.client.TmdbClient;
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

	/**
	 * 데이터 중복됨
	 *         {
	 *             "adult": false,
	 *             "backdrop_path": "/AeJgnEXaFcsGzU5Y4Nrq9WggAQ5.jpg",
	 *             "genre_ids": [
	 *                 10765,
	 *                 9648,
	 *                 35
	 *             ],
	 *             "id": 119051,
	 *             "origin_country": [
	 *                 "US"
	 *             ],
	 *             "original_language": "en",
	 *             "original_name": "Wednesday",
	 *             "overview": "똑똑함은 기본, 비꼬는 것에도 도가 튼 웬즈데이 아담스. 암울함을 풍기는 그녀가 네버모어 아카데미에서 얽히고설킨 미스터리를 파헤치기 시작한다. 새 친구도 사귀고, 앙숙도 만들며.",
	 *             "popularity": 59.7384,
	 *             "poster_path": "/oCpvKD0yuRV9OAZ8xklRy9HWc6l.jpg",
	 *             "first_air_date": "2022-11-23",
	 *             "name": "웬즈데이",
	 *             "vote_average": 8.368,
	 *             "vote_count": 10155
	 *         }
	 *     ],
	 *          {
	 *             "adult": false,
	 *             "backdrop_path": "/AeJgnEXaFcsGzU5Y4Nrq9WggAQ5.jpg",
	 *             "genre_ids": [
	 *                 10765,
	 *                 9648,
	 *                 35
	 *             ],
	 *             "id": 119051,
	 *             "origin_country": [
	 *                 "US"
	 *             ],
	 *             "original_language": "en",
	 *             "original_name": "Wednesday",
	 *             "overview": "똑똑함은 기본, 비꼬는 것에도 도가 튼 웬즈데이 아담스. 암울함을 풍기는 그녀가 네버모어 아카데미에서 얽히고설킨 미스터리를 파헤치기 시작한다. 새 친구도 사귀고, 앙숙도 만들며.",
	 *             "popularity": 65.027,
	 *             "poster_path": "/oCpvKD0yuRV9OAZ8xklRy9HWc6l.jpg",
	 *             "first_air_date": "2022-11-23",
	 *             "name": "웬즈데이",
	 *             "vote_average": 8.368,
	 *             "vote_count": 10156
	 *         },
	 */
	@Test
	public void duplicateTest() {
		for (int page = 1; page < 50; page++) {
			List<ContentSaveDto> contentSaveDtos = tmdbClient.fetchContent(Type.TV_SERIES, page);
			for (ContentSaveDto dto : contentSaveDtos) {
				System.out.println(dto.getSourceId());
				if (dto.getSourceId() == 119051) {
					System.out.println("duplicate!! page: " + page);
				}
			}

		}
	}
}
