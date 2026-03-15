# 📱 Social Media API

A production-ready RESTful API for a social networking platform built with **Spring Boot**, **PostgreSQL**, **JWT authentication**, and **WebSocket** real-time notifications.

---

## ✨ Features

- 🔐 **JWT Authentication** — Stateless auth with secure token generation and validation
- 👤 **User Profiles** — Registration, login, profile management
- 🤝 **Follow System** — Follow/unfollow users, follower/following counts
- 📝 **Posts** — Create, delete, paginated feed from followed users
- ❤️ **Likes** — Like/unlike posts with real-time like counts
- 💬 **Comments** — Threaded comments per post with pagination
- 🔔 **Real-time Notifications** — WebSocket push notifications for likes, comments, and follows
- 🐳 **Docker Ready** — Full Docker + Docker Compose setup for instant local run
- ✅ **Input Validation** — Request validation with meaningful error messages
- 🛡️ **Security** — BCrypt password hashing, stateless sessions, protected endpoints

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (jjwt 0.12) |
| Real-time | WebSocket (STOMP over SockJS) |
| Containerization | Docker + Docker Compose |
| Build Tool | Maven |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose (for containerized setup)

### Option 1 — Run with Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/Manoj-surya/social-media-api.git
cd social-media-api

# Start PostgreSQL + API
docker-compose up --build
```

API will be available at `http://localhost:8080`

### Option 2 — Run Locally

```bash
# 1. Start a PostgreSQL instance (or update application.properties)
# 2. Set environment variables
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=your-secret-key-at-least-32-characters-long

# 3. Build and run
mvn clean install -DskipTests
mvn spring-boot:run
```

---

## 📡 API Reference

Base URL: `http://localhost:8080/api`

All protected endpoints require the header:
```
Authorization: Bearer <token>
```

---

### 🔐 Auth

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/register` | ❌ | Register a new user |
| POST | `/auth/login` | ❌ | Login and receive JWT |

**Register**
```json
POST /api/auth/register
{
  "username": "manoj",
  "email": "manoj@example.com",
  "password": "secret123",
  "displayName": "Manoj Surya"
}
```

**Response**
```json
{
  "success": true,
  "message": "User registered successfully.",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "manoj",
    "email": "manoj@example.com"
  }
}
```

---

### 👤 Users

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/users/{username}` | ❌ | Get user profile |
| PUT | `/users/me` | ✅ | Update own profile |
| POST | `/users/{username}/follow` | ✅ | Follow a user |
| DELETE | `/users/{username}/follow` | ✅ | Unfollow a user |

**Get Profile Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "manoj",
    "displayName": "Manoj Surya",
    "bio": "Java Full Stack Developer",
    "followersCount": 42,
    "followingCount": 18,
    "isFollowing": false,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

---

### 📝 Posts

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/posts` | ✅ | Create a post |
| GET | `/posts/feed` | ✅ | Get feed (posts from followed users) |
| GET | `/posts/user/{username}` | ✅ | Get posts by user |
| GET | `/posts/{postId}` | ✅ | Get a single post |
| DELETE | `/posts/{postId}` | ✅ | Delete own post |
| POST | `/posts/{postId}/like` | ✅ | Like a post |
| DELETE | `/posts/{postId}/like` | ✅ | Unlike a post |

**Create Post**
```json
POST /api/posts
{
  "content": "Hello world! First post 🎉",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Feed Response** (paginated)
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 5,
        "content": "Hello world!",
        "author": { "id": 2, "username": "alice", "displayName": "Alice" },
        "likesCount": 12,
        "commentsCount": 3,
        "likedByCurrentUser": false,
        "createdAt": "2024-03-15T09:00:00"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "size": 20,
    "number": 0
  }
}
```

**Pagination** — Append `?page=0&size=20` to paginated endpoints.

---

### 💬 Comments

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/posts/{postId}/comments` | ✅ | Add a comment |
| GET | `/posts/{postId}/comments` | ❌ | Get comments for a post |
| DELETE | `/posts/{postId}/comments/{commentId}` | ✅ | Delete own comment |

---

### 🔔 Notifications

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/notifications` | ✅ | Get all notifications |
| GET | `/notifications/unread-count` | ✅ | Get unread count |
| PUT | `/notifications/read-all` | ✅ | Mark all as read |

**Notification Types:** `LIKE` · `COMMENT` · `FOLLOW`

---

### 🔌 WebSocket (Real-time Notifications)

Connect to the STOMP endpoint and subscribe to your personal notification queue:

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);

client.connect({ Authorization: 'Bearer <token>' }, () => {
  client.subscribe('/user/queue/notifications', (message) => {
    const notification = JSON.parse(message.body);
    console.log('New notification:', notification);
  });
});
```

---

## 🗃️ Database Schema

```
users               posts               comments
─────────────       ─────────────       ─────────────
id (PK)             id (PK)             id (PK)
username            content             content
email               image_url           post_id (FK)
password            user_id (FK)        user_id (FK)
display_name        created_at          created_at
bio                 updated_at
profile_picture_url

user_followers      post_likes          notifications
─────────────       ─────────────       ─────────────
follower_id (FK)    post_id (FK)        id (PK)
following_id (FK)   user_id (FK)        recipient_id (FK)
                                        actor_id (FK)
                                        type (LIKE/COMMENT/FOLLOW)
                                        reference_id
                                        read
                                        created_at
```

---

## ⚙️ Configuration

| Property | Env Variable | Default |
|---|---|---|
| Database URL | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/socialmedia_db` |
| DB Username | `DB_USERNAME` | `postgres` |
| DB Password | `DB_PASSWORD` | `postgres` |
| JWT Secret | `JWT_SECRET` | *(set in prod)* |
| JWT Expiry | — | 24 hours |

---

## 📁 Project Structure

```
src/main/java/com/manoj/socialmedia/
├── auth/               # Registration, login, JWT issuance
├── user/               # Profiles, follow/unfollow
├── post/               # CRUD, likes, feed
├── comment/            # Comments per post
├── notification/       # Persistence + WebSocket push
├── config/             # Security, JWT, WebSocket config
├── exception/          # Global error handling
└── common/             # Shared ApiResponse wrapper
```

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 📄 License

MIT License — feel free to use this project as a reference or starting point.
