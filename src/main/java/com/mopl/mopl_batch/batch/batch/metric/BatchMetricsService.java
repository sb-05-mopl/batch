package com.mopl.mopl_batch.batch.batch.metric;

import com.mopl.mopl_batch.batch.entity.Type;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchMetricsService {

    private final MeterRegistry meterRegistry;

    public void incrementSavedCount(Type type, int count) {
        Counter.builder("batch.content.saved.total")
                .tag("job", getJob(type))
                .tag("step", getStep(type))
                .tag("type", getType(type))
                .description("Total number of contents saved to database")
                .register(meterRegistry)
                .increment(count);
    }

    public void incrementDuplicateCount(Type type, int count) {
        Counter.builder("batch.content.duplicate.total")
                .tag("job", getJob(type))
                .tag("step", getStep(type))
                .tag("type", getType(type))
                .description("Total number of duplicate contents skipped")
                .register(meterRegistry)
                .increment(count);
    }

    public void incrementNewCount(Type type, int count) {
        Counter.builder("batch.content.new.total")
                .tag("job", getJob(type))
                .tag("step", getStep(type))
                .tag("type", getType(type))
                .description("Total number of new contents discovered")
                .register(meterRegistry)
                .increment(count);
    }

    private String getJob(Type type){
        switch (type) {
            case Type.MOVIE, Type.TV_SERIES:
                return "fetchTmdbContentsJob";
            case Type.SPORTS:
                return "fetchSportContentsJob";
            default:
                return "unknown-batch";
        }
    }

    private String getType(Type type) {
        switch (type) {
            case Type.MOVIE:
                return "movie";
            case Type.SPORTS:
                return "sport";
            case Type.TV_SERIES:
                return "tv";
            default:
                return "unknown";
        }
    }

    private String getStep(Type type) {
        switch (type) {
            case Type.MOVIE:
                return "tmdbMovieStep";
            case Type.SPORTS:
                return "sportApiStep";
            case Type.TV_SERIES:
                return "tmdbTvStep";
            default:
                return "unknown";
        }
    }

}
