package com.mopl.mopl_batch.batch.storage;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryStorage {
	void put(UUID userId, byte[] data, String contentType);

	InputStream get(UUID userId);

	Boolean exists(UUID userId);

	String getUrl(UUID userId);
}