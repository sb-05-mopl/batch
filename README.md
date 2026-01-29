# MOPL Batch

TMDB, TheSportsDB 등 외부 API에서 콘텐츠 데이터를 수집하여 PostgreSQL에 저장하고, 썸네일 이미지를 AWS S3에 업로드하는 Spring Batch 애플리케이션입니다.

<img width="1270" height="706" alt="image" src="https://github.com/user-attachments/assets/f38bb99c-9889-440e-ad73-b95daa93595d" />


## 기술 스택

- **Java 21** / **Spring Boot 3.5.9**
- **Spring Batch** - 배치 처리
- **Spring Data JPA** + **PostgreSQL** - 데이터 저장
- **AWS S3** - 썸네일 이미지 저장
- **Prometheus + PushGateway** - 메트릭 모니터링
- **Gradle** - 빌드 도구

## 프로젝트 구조

```
src/main/java/com/mopl/mopl_batch/batch/
├── batch/
│   ├── common/          # 공통 컴포넌트 (Processor, Writer, DTO, Util)
│   ├── job/             # 배치 Job 설정
│   ├── tmdb/            # TMDB API 연동 (영화/TV 시리즈)
│   │   ├── client/      # TMDB API 클라이언트
│   │   ├── dto/         # 응답 DTO
│   │   ├── reader/      # ItemReader
│   │   └── step/        # Step 설정
│   └── sport/           # TheSportsDB API 연동 (스포츠 이벤트)
│       ├── client/      # Sports API 클라이언트
│       ├── dto/         # 응답 DTO
│       ├── reader/      # ItemReader
│       └── step/        # Step 설정
├── config/              # 설정 (RestClient, S3, Batch, Prometheus)
├── entity/              # JPA 엔티티
├── Repository/          # Spring Data 리포지토리
├── schedule/            # 배치 스케줄러
├── storage/             # 스토리지 추상화 (S3 구현체)
└── properties/          # 외부 API 프로퍼티
```

## 배치 Job

### 1. fetchTmdbContentsJob

TMDB API에서 영화와 TV 시리즈 데이터를 수집합니다.

- **tmdbMovieStep** - 영화 데이터 수집 (최대 500페이지)
- **tmdbTvStep** - TV 시리즈 데이터 수집 (최대 500페이지)
- Chunk Size: 1,000 / Retry: 3회 (지수 백오프)

### 2. fetchSportContentsJob

TheSportsDB API에서 축구 경기 데이터를 수집합니다.

- **sportApiStep** - 현재 날짜 기준 전후 50일간의 경기 데이터 수집
- Chunk Size: 100 / Retry: 3회

### 배치 처리 흐름

```
Reader (API 호출) → Processor (중복 필터링) → Writer (DB 저장 + S3 이미지 업로드 + 태그 생성)
```

- **ContentsProcessor**: `sourceId + type` 기준 중복 체크
- **ContentsWriter**: 썸네일 병렬 다운로드/업로드 (10 스레드), 태그 자동 생성

## 환경 변수

| 변수명 | 설명 | 필수 |
|--------|------|------|
| `TMDB_READ_ACCESS_TOKEN` | TMDB API Bearer 토큰 | O |
| `TMDB_KEY` | TMDB API 키 | O |
| `SPORT_API_KEY` | TheSportsDB API 키 | X (기본값: "123") |
| `PROFILE` | Spring 활성 프로파일 | O |
| `AWS_S3_ACCESS_KEY` | AWS 액세스 키 | O |
| `AWS_S3_SECRET_KEY` | AWS 시크릿 키 | O |
| `AWS_S3_REGION` | AWS 리전 | O |
| `AWS_S3_BUCKET` | S3 버킷명 | O |
| `PUSHGATEWAY_ENABLED` | PushGateway 활성화 여부 | X (기본값: true) |
| `PUSH_GATE_WAY_URL` | PushGateway URL | X (기본값: http://localhost:9091) |

## 실행 방법

```bash
# 환경 변수 설정 (.env 파일 또는 직접 설정)
export TMDB_READ_ACCESS_TOKEN=your_token
export AWS_S3_ACCESS_KEY=your_key
# ... 기타 환경 변수

# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

## 모니터링

- 애플리케이션 포트: `8080`
- 관리 포트: `8081`
- Prometheus 메트릭: `http://localhost:8081/actuator/prometheus`
- 수집 메트릭:
  - `batch.content.saved.total` - 저장된 콘텐츠 수
  - `batch.content.duplicate.total` - 중복 콘텐츠 수
  - `batch.content.new.total` - 신규 콘텐츠 수
