# Spring Backend Shop

> Simple EC-style backend built with Spring Boot, PostgreSQL, Docker, and GitHub Actions.

## 1. Overview

This project is a **practice EC backend** that manages products and product groups.
The main goals are:

* To design a clean Spring Boot layered architecture.
* To connect the application to **PostgreSQL via Docker**.
* To set up **CI with GitHub Actions** and basic **containerization with Docker**.

(個人学習用/ポートフォリオ用に開発中のプロジェクトです。)

---

## 2. Features

Implemented (as of now):

* Product & Product Group domain

  * Product list with pagination `/api/products`
  * Product group information
* Database

  * PostgreSQL with Flyway migrations
  * Test profile with in-memory H2 (PostgreSQL mode)
* Infrastructure

  * Dockerfile for Spring Boot application
  * `docker-compose.yml` for PostgreSQL container
* CI

  * GitHub Actions workflow (`.github/workflows/ci.yml`)
  * Runs `./gradlew build` on `main` branch push and pull request

Planned / In progress:

* Order API & stock handling
* Authentication / authorization (JWT)
* More API endpoints (create/update/delete product, etc.)
* API documentation (OpenAPI/Swagger UI)
* Basic frontend or API client (Postman collection)

---

## 3. Tech Stack

| Layer      | Tech                          |
| ---------- | ----------------------------- |
| Language   | Java 21                       |
| Framework  | Spring Boot 3.x               |
| Build tool | Gradle                        |
| Database   | PostgreSQL 16 (Docker)        |
| Migration  | Flyway                        |
| Test DB    | H2 (PostgreSQL compatibility) |
| Container  | Docker, Docker Compose        |
| CI         | GitHub Actions                |

---

## 4. Project Structure (simplified)

```text
spring-backend-shop
├─ src
│  ├─ main
│  │  ├─ java/com/example/backend
│  │  │  ├─ domain      
│  │  │  ├─ repository  
│  │  │  ├─ service     
│  │  │  └─ controller  
│  │  └─ resources
│  │     ├─ application.yml        
│  │     └─ db/migration           
│  └─ test
│     └─ java/...                  
├─ Dockerfile
├─ docker-compose.yml
└─ .github/workflows/ci.yml
```

---

## 5. How to run (local)

### 5.1 Prerequisites

* JDK 21
* Docker & Docker Compose
* Gradle wrapper (included)

### 5.2 Start PostgreSQL with Docker

```bash
# at repository root
docker compose up -d
# or (older Docker versions)
# docker-compose up -d
```

This will start PostgreSQL with the configuration defined in `docker-compose.yml`.
Check that the container is running:

```bash
docker ps
```

### 5.3 Run Spring Boot application (local JDK)

```bash
./gradlew clean build
./gradlew bootRun
```

The application will start on:

* `http://localhost:8080`

Example API:

```bash
curl "http://localhost:8080/api/products"
```

You should see a JSON response with paginated product list.

---

## 6. Run with Docker image

You can also build and run the backend as a Docker container.

### 6.1 Build image

```bash
./gradlew clean build
docker build -t jongdaepark/spring-backend-shop:latest .
```

### 6.2 Run image

```bash
docker run --name spring-backend-shop \
  -p 8080:8080 \
  --env SPRING_PROFILES_ACTIVE=default \
  jongdaepark/spring-backend-shop:latest
```

> Note: For SMTP or other secrets, use environment variables or an external `application-local.yml` that is **not committed** to Git.

---

## 7. CI (GitHub Actions)

* Workflow file: `.github/workflows/ci.yml`
* Triggers:

  * `push` to `main`
  * `pull_request` targeting `main`
* Jobs:

  * Check out repository
  * Set up JDK 17
  * Gradle build (including tests)

If the build fails, the pull request will show a failed status, and you can check the logs in the **Actions** tab.

---

## 8. Roadmap

* [ ] Implement Order API and stock decrease logic
* [ ] Add authentication (JWT based)
* [ ] Add integration tests (Testcontainers with PostgreSQL)
* [ ] Generate OpenAPI docs and Swagger UI
* [ ] Add Postman collection or simple frontend
* [ ] Add monitoring / logging improvements
* [ ] Add CD pipeline (build Docker image and push to Docker Hub automatically)

---

## 9. License

This project is currently for personal study and portfolio use.
License will be decided later.


---日本語---

### プロジェクト概要
Spring Boot を使用した社内向け EC / 在庫管理バックエンド API です。  
商品・商品グループ・注文などの基本機能に加え、認証・認可、CI/CD、Docker 実行環境までを含めた「ポートフォリオ用フルスタック基盤」を目的としています。

### 技術スタック

- 言語: Java 21
- フレームワーク: Spring Boot, Spring Web, Spring Data JPA, Spring Security
- ビルドツール: Gradle
- データベース: PostgreSQL (Docker Compose)
- インフラ:
  - Docker, Docker Compose
  - GitHub Actions（CI: Gradle ビルド & テスト）
  - Docker Hub（アプリケーションイメージの配布）
- その他: Lombok, Validation, Pageable API など

### 主な機能

- ユーザー / ログイン
  - Spring Security によるフォームログイン
  - ロールに応じた API アクセス制御（例：管理者のみ登録・更新可能）
- 商品管理
  - `/api/products` 一覧取得（ページング対応）
  - `/api/products/{id}` 詳細取得
  - 商品の登録・更新・削除 API（管理者向け）
- 商品グループ管理
  - `/api/product-groups` 一覧・登録・更新・削除
- 注文（Order）API
  - `/api/orders` での注文登録（※現在実装中／拡張予定）
  - 在庫数の減算ロジック（購入時に stock を更新）

### アーキテクチャ

- パッケージ構成（レイヤードアーキテクチャ）
  - `controller` – REST API エンドポイント
  - `service` – ビジネスロジック
  - `repository` – DB アクセス(JPA)
  - `domain` / `entity` – エンティティ定義
  - `dto` – リクエスト/レスポンス用 DTO
- エラーハンドリング
  - 共通例外ハンドラによるエラーレスポンス統一（予定／一部実装）

### ローカル実行方法（開発用）

#### 1. 必要なツール

- JDK 21
- Docker / Docker Compose
- Gradle（ラッパー使用）

#### 2. DB 起動（Docker Compose）

```bash
cd backend
docker compose up -d