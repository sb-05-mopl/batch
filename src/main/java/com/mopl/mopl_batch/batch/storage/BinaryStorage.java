package com.mopl.mopl_batch.batch.storage;

import java.io.InputStream;
import java.util.UUID;

import com.mopl.mopl_batch.batch.entity.Type;

public interface BinaryStorage {
	void put(UUID userId, byte[] data, String contentType);

	InputStream get(UUID userId);

	Boolean exists(UUID userId);

	String getUrl(UUID userId);

	String putThumbnail(Type type, long sourceId, byte[] data, String contentType);

	String getThumbnailUrl(Type type, long sourceId);
}