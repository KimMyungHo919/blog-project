# 블로그 API

<br>

<details>
  <summary><span style="color: red;">🗂 유저 관련 API</span></summary>

  <br>

  <details>
    <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 유저회원가입</summary>

## 📌 Request 필드 필수 여부

| 필드             | 타입     | 설명     | 필수 여부 |
  |----------------|--------|--------|-------|
| `email`        | String | 유저 이메일 | ✅     |
| `password`     | String | 비밀번호   | ✅     |
| `nickname`     | String | 닉네임    | ✅     |
| `imageId`      | Long   | 이미지아이디 | ❌     |
| `profileImage` | String | 이미지Url | ❌     |

**Request**

  ```
  - URL : /api/public/users/signup
  - Request Body : JSON
  
  {
      "email" : "test@gmail.com",
      "password" : "123asdfASD!@",
      "nickname" : "TESTNICKNAME",
      "imageId" : 1,
      "profileImage" : "https://your-bucket.amazonaws.com/
          fbcb63d6-2%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%
          AB%E1%84%89%E1%85%A3%E1%86%BA%202025-02-11%20%E1%84%8B%E1%85%A9%E1%84%
          92%E1%85%AE%202.19.09.png"
  }
  ```

**Response**

  ```
  201 Created
  
  {
    "status": 201,
    "message": "CREATED",
    "data": {
        "id": 1,
        "email": "test@gmail.com",
        "nickname": "TESTNICKNAME"
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 유저 로그인</summary>

## 📌 Request 필드 필수 여부

| 필드         | 타입     | 설명     | 필수 여부 |
  |------------|--------|--------|-------|
| `email`    | String | 유저 이메일 | ✅     |
| `password` | String | 비밀번호   | ✅     |

**Request**

  ```
  - URL : /api/public/users/login
  - Request Body : JSON
  
  {
      "email" : "test@gmail.com",
      "password" : "123asdfASD!@"
  }
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "id": 1,
        "email": "test@gmail.com",
        "nickname": "MMOOKK",
        "role": "일반유저"
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 유저 로그아웃</summary>

**Request**

  ```
  - URL : /api/users/logout
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "로그아웃 성공"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 유저 정보조회</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명    | 필수 여부 |
  |----------|------|-------|-------|
| `userId` | Long | 유저아이디 | ✅     |

**Request**

  ```
  - URL : /api/public/users/{userId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "id": 1,
        "email": "test1123@naver.com",
        "nickname": "testuser"
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 한 유저의 포스팅 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                                      |
  |-------------|--------|-----------|-------|-------------|-----------------------------------------|
| `userId`    | Long   | 유저아이디     | ✅     |             |                                         |
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`                               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 20`                  |
| `sortBy`    | String | 정렬기준      | ❌     | `createdAt` | `title` `createdAt` `updatedAt` `views` |
| `direction` | String | 오름차순,내림차순 | ❌     | `desc`      | `asc` or `desc`                         |

**Request**

  ```
  - URL : /api/public/users/{userId}/posts?page=0&size=10&sortBy=title&direction=asc
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "postId": 1,
                "title": "1번글",
                "content": "1번글 내용입니다",
                "userNickname": "TEST1",
                "createdAt": "2025-02-09T14:51:19.328537",
                "updatedAt": "2025-02-09T14:51:19.328537"
            },
            {
                "postId": 2,
                "title": "2번글",
                "content": "2번글 내용입니다",
                "userNickname": "TEST2",
                "createdAt": "2025-02-10T14:51:22.742681",
                "updatedAt": "2025-02-10T14:51:22.742681"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 한 유저의 댓글 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                      |
  |-------------|--------|-----------|-------|-------------|-------------------------|
| `userId`    | Long   | 유저아이디     | ✅     |             |                         |
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 20`  |
| `sortBy`    | String | 정렬기준      | ❌     | `createdAt` | `createdAt` `updatedAt` |
| `direction` | String | 오름차순,내림차순 | ❌     | `desc`      | `asc` or `desc`         |

**Request**

  ```
  - URL : /api/users/{userId}/comments?page=0&size=10&sortBy=createdAt&direction=asc
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "commentId": 1,
                "comment": "좋은글이네요.",
                "userNickname": "TEST1",
                "createdAt": "2025-02-09T14:51:27.935912",
                "updatedAt": "2025-02-09T14:51:27.935912"
            },
            {
                "commentId": 2,
                "comment": "좋은글이네요.22",
                "userNickname": "TEST1",
                "createdAt": "2025-02-10T14:51:28.997775",
                "updatedAt": "2025-02-10T14:51:28.997775"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 한 유저의 좋아요 누른 포스팅 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                      |
  |-------------|--------|-----------|-------|-------------|-------------------------|
| `userId`    | Long   | 유저아이디     | ✅     |             |                         |
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 20`  |
| `sortBy`    | String | 정렬기준      | ❌     | `createdAt` | `createdAt` `updatedAt` |
| `direction` | String | 오름차순,내림차순 | ❌     | `desc`      | `asc` or `desc`         |

**Request**

  ```
  - URL : /api/users/{userId}/likes?page=0&size=10&sortBy=createdAt&direction=asc
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "postId": 1,
                "postTitle": "1번글",
                "postContent": "글 11111111111",
                "postCreatedAt": "2025-02-09T14:51:19.328537",
                "postUpdatedAt": "2025-02-09T14:51:19.328537"
            },
            {
                "postId": 2,
                "postTitle": "2번글",
                "postContent": "글 222222222",
                "postCreatedAt": "2025-02-10T14:51:19.328537",
                "postUpdatedAt": "2025-02-10T14:51:19.328537"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 내 친구 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                      |
  |-------------|--------|-----------|-------|-------------|-------------------------|
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 20`  |
| `sortBy`    | String | 정렬기준      | ❌     | `createdAt` | `createdAt` `updatedAt` |
| `direction` | String | 오름차순,내림차순 | ❌     | `desc`      | `asc` or `desc`         |

**Request**

  ```
  - URL : /api/users/friends?page=0&size=10&sortBy=createdAt&direction=asc
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "userId": 1,
                "userNickname": "TEST1",
                "userEmail": "test1@naver.com"
            },
            {
                "userId": 2,
                "userNickname": "TEST2",
                "userEmail": "test2@naver.com"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">PATCH</span> 유저 비밀번호변경</summary>

## 📌 Request 필드 필수 여부

| 필드            | 타입     | 설명      | 필수 여부 |
  |---------------|--------|---------|-------|
| `oldPassword` | String | 현재 비밀번호 | ✅     |
| `newPassword` | String | 바꿀 비밀번호 | ✅     |

**Request**

  ```
  - URL : /api/users/me/password
  - Request Body : JSON
  
  {
      "oldPassword" : "sdflkjs12@1!A",
      "newPassword" : "sdflkjs12@1!A!!"
  }
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "비밀번호 변경완료"
  }
  ```

  </details>

  <details>
    <summary style="margin-left: 20px;"><span style="color: red;">PATCH</span> 유저 프로필변경</summary>

## 📌 Request 필드 필수 여부

| 필드             | 타입     | 설명     | 필수 여부 |
  |----------------|--------|--------|-------|
| `password`     | String | 비밀번호   | ✅     |
| `nickname`     | String | 바꿀 닉네임 | ✅     |
| `imageId`      | Long   | 이미지아이디 | ❌     |
| `profileImage` | String | 이미지Url | ❌     |

**Request**

  ```
  - URL : /api/users/me/profile
  - Request Body : JSON
  
  {
      "password" : "sdflkjs12@1!A",
      "nickname" : "바뀐닉네임",
      "imageId" : 4,
      "profileImage" : "https://your-bucket.amazonaws.com/
            fbcb63d6-2%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86
            %AB%E1%84%89%E1%85%A3%E1%86%BA%202025-02-11%20%E1%84%8B%E1%85%A9%E1%84
            %92%E1%85%AE%202.19.09.png"
  }
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "프로필 변경완료"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">DELETE</span> 유저 회원탈퇴</summary>

## 📌 Request 필드 필수 여부

| 필드         | 타입     | 설명   | 필수 여부 |
  |------------|--------|------|-------|
| `password` | String | 비밀번호 | ✅     |

**Request**

  ```
  - URL : /api/users/me
  - Request Body : JSON
  
  {
      "password" : "sdflkjs12@1!A"
  }
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "회원 탈퇴완료"
  }
  ```

  </details>
</details>

***

<details>
  <summary><span style="color: red;">🗂 포스팅 관련 API</span></summary>

  <br>

  <details>
    <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 포스팅 작성</summary>

## 📌 Request 필드 필수 여부

| 필드               | 타입     | 설명   | 필수 여부 | 기본값  | 조건                     |
  |------------------|--------|------|-------|------|------------------------|
| `title`          | String | 제목   | ✅     | `0`  | `Min = 5` , `Max = 50` |
| `content`        | String | 내용   | ✅     | `10` | `Min = 10`             |
| `postVisibility` | String | 공개여부 | ❌     | `공개` | `공개` `비공개`             |

**Request**

  ```
  - URL : /api/posts
  - Request Body : JSON
  
  {
      "title" : "1번제목",
      "content" : "1번글 내용입니다",
      "postVisibility" : "공개"
  }
  ```

**Response**

  ```
  {
    "status": 201,
    "message": "CREATED",
    "data": {
        "postId": 1,
        "title": "1번제목",
        "content": "1번글 내용입니다",
        "views": 0,
        "likes": 0,
        "userNickname": "TESTUSER",
        "postVisibility": "공개",
        "createdAt": "2025-02-09T14:59:09.173419",
        "updatedAt": "2025-02-09T14:59:09.173419"
    }
  }
  ```

  </details>

<details>
    <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 이미지 삽입</summary>

## 📌 Request 필드 필수 여부

| 필드      | 타입   | 설명  | 필수 여부 | 기본값 | 조건                       |
  |---------|------|-----|-------|-----|--------------------------|
| `image` | file | 이미지 | ✅     |     | `jpg` `jpeg` `png` `gif` |

**Request**

  ```
  - URL : /api/s3/upload
  - Content-Type : multipart/form-data
  ```

**Response**

  ```
  {
      "status": 201,
      "message": "CREATED",
      "data": {
          "imageId": 8,
          "publicUrl": "https://your-bucket.s3.ap-northeast-2.amazonaws.com/55ec544c-3%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202025-02-11%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%202.12.45.png"
      }
  }
  ```

  </details>

<details>
    <summary style="margin-left: 20px;"><span style="color: red;">DELETE</span> 이미지 삭제</summary>

## 📌 Request 필드 필수 여부

| 필드     | 타입     | 설명      | 필수 여부 |
  |--------|--------|---------|-------|
| `addr` | String | 이미지 URL | ✅     |  

**Request**

  ```
  - URL : /api/s3/delete
  - Request Body : JSON
  
  {
    "addr" : "https://your-bucket.amazonaws.com/
             08b334f8-0%E1%84%89%E1%85%B3%E1%84%8F%E1%85%12.49.22.png"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 나의 비공개포스팅 조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                                      |
  |-------------|--------|-----------|-------|-------------|-----------------------------------------|
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`                               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 20`                  |

**Request**

  ```
  - URL : /api/posts?page=0&size=10
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "postId": 7,
                "title": "블로그 테스트 제목7",
                "content": "블로그 테스트내용입니다7",
                "views": 0,
                "likes": 0,
                "userNickname": "MMOOKK",
                "postVisibility": "비공개",
                "createdAt": "2025-02-10T11:37:20.741547",
                "updatedAt": "2025-02-10T14:15:51.51889"
            },
            {
                "postId": 3,
                "title": "블로그 테스트 제목3",
                "content": "블로그 테스트내용입니다3",
                "views": 1,
                "likes": 0,
                "userNickname": "MMOOKK",
                "postVisibility": "비공개",
                "createdAt": "2025-02-10T11:06:17.253805",
                "updatedAt": "2025-02-10T14:15:47.872434"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 포스팅 특정 글 조회</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명      | 필수 여부 |
  |----------|------|---------|-------|
| `postId` | Long | 포스팅 아이디 | ✅     |

**Request**

  ```
  - URL : /api/public/posts/{postId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "postId": 1,
        "title": "1번제목",
        "content": "1번글 내용입니다",
        "views": 1,
        "likes": 1,
        "userNickname": "TESTUSER",
        "postVisibility": "공개",
        "createdAt": "2025-02-09T14:51:19.328537",
        "updatedAt": "2025-02-09T14:51:19.328537"
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 포스팅 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                                      |
  |-------------|--------|-----------|-------|-------------|-----------------------------------------|
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`                               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 20`                  |
| `sortBy`    | String | 정렬기준      | ❌     | `createdAt` | `createdAt` `updatedAt` `title` `views` |
| `direction` | String | 오름차순,내림차순 | ❌     | `desc`      | `asc` or `desc`                         |

**Request**

  ```
  - URL : /api/public/posts?page=0&size=10&sortBy=views&direction=asc
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "postId": 2,
                "title": "2번글제목입니다",
                "content": "2번글내용입니다",
                "views": 0,
                "likes": 0,
                "userNickname": "MMOOKK",
                "postVisibility": "공개",
                "createdAt": "2025-02-09T14:51:22.742681",
                "updatedAt": "2025-02-09T14:51:22.742681"
            },
            {
                "postId": 3,
                "title": "3번글제목입니다",
                "content": "3번글내용입니다",
                "views": 0,
                "likes": 0,
                "userNickname": "JANEM",
                "postVisibility": "공개",
                "createdAt": "2025-02-09T14:55:14.235145",
                "updatedAt": "2025-02-09T14:55:14.235145"
            },
            {
                "postId": 1,
                "title": "1번글제목입니다",
                "content": "1번글내용입니다",
                "views": 1,
                "likes": 1,
                "userNickname": "MMOOKK",
                "postVisibility": "공개",
                "createdAt": "2025-02-09T14:51:19.328537",
                "updatedAt": "2025-02-09T14:55:16.745267"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 3,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 포스팅 댓글 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명     | 필수 여부 | 기본값  | 조건                     |
  |----------|------|--------|-------|------|------------------------|
| `postId` | Long | 포스팅아이디 | ✅     |      |                        |
| `page`   | int  | 페이지    | ❌     | `0`  | `Min = 0`              |
| `size`   | int  | 페이지크기  | ❌     | `10` | `Min = 1` , `Max = 20` |

**Request**

  ```
  - URL : /api/public/posts/{postId}/comments?page=0&size=10
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "commentId": 1,
                "comment": "좋은글이네요11",
                "userNickname": "TESTUSER1",
                "createdAt": "2025-02-09T14:51:27.935912",
                "updatedAt": "2025-02-09T14:51:27.935912"
            },
            {
                "commentId": 2,
                "comment": "좋은글이네요22",
                "userNickname": "TESTUSER2",
                "createdAt": "2025-02-10T14:51:28.997775",
                "updatedAt": "2025-02-10T14:51:28.997775"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 포스팅 좋아요 누른 유저조회</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명     | 필수 여부 | 기본값  | 조건                     |
|----------|------|--------|-------|------|------------------------|
| `postId` | Long | 포스팅아이디 | ✅     |      |                        |
| `page`   | int  | 페이지    | ❌     | `0`  | `Min = 0`              |
| `size`   | int  | 페이지크기  | ❌     | `10` | `Min = 1` , `Max = 20` | 

**Request**

  ```
  - URL : /api/public/posts/{postId}/likes?page=0&size=10
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "userNickname": "TESTUSER1"
            },
            {
                "userNickname": "TESTUSER2"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">PATCH</span> 포스팅 수정</summary>

## 📌 Request 필드 필수 여부

| 필드               | 타입     | 설명     | 필수 여부 |
  |------------------|--------|--------|-------|
| `postId`         | Long   | 포스팅아이디 | ✅     |
| `title`          | String | 제목     | ✅     |
| `content`        | String | 내용     | ✅     |
| `postVisibility` | String | 공개여부   | ❌     |

**Request**

  ```
  - URL : /api/posts/{postId}
  - Request Body : JSON
  
  {
      "title" : "제목을 바꾸려고합니다",
      "content" : "내용을 바꾸려고합니다",
      "postVisibility" : "공개"
  }
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "포스팅 업데이트완료"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">DELETE</span> 포스팅 삭제</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명     | 필수 여부 |
  |----------|------|--------|-------|
| `postId` | Long | 포스팅아이디 | ✅     |

**Request**

  ```
  - URL : /api/posts/{postId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "포스팅 삭제완료"
  }
  ```

  </details>
</details>

***

<details>
  <summary><span style="color: red;">🗂 댓글 관련 API</span></summary>

  <br>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 댓글 생성</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명     | 필수 여부 |
  |----------|------|--------|-------|
| `postId` | Long | 포스팅아이디 | ✅     |

**Request**

  ```
  - URL : /api/comments/post/{postId}
  - Request Body : JSON
  
  {
      "comment" : "좋은글이네요."
  }
  ```

**Response**

  ```
  {
    "status": 201,
    "message": "CREATED",
    "data": {
        "commentId": 1,
        "comment": "좋은글이네요.",
        "userNickname": "TESTUSER",
        "createdAt": "2025-02-09T14:59:15.355567",
        "updatedAt": "2025-02-09T14:59:15.355567"
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">PATCH</span> 댓글 수정</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입   | 설명    | 필수 여부 |
  |-------------|------|-------|-------|
| `commentId` | Long | 댓글아이디 | ✅     |

**Request**

  ```
  - URL : /api/comments/{commentId}
  - Request Body : JSON
  
  {
      "comment" : "댓글을 수정해보세요"
  }
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "댓글 수정완료"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">DELETE</span> 댓글 삭제</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입   | 설명    | 필수 여부 |
  |-------------|------|-------|-------|
| `commentId` | Long | 댓글아이디 | ✅     |

**Request**

  ```
  - URL : /api/comments/{commentId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "댓글 삭제완료"
  }
  ```

  </details>

</details>

***

<details>
  <summary><span style="color: red;">🗂 좋아요 관련 API</span></summary>

  <br>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 좋아요 누르기</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명     | 필수 여부 |
  |----------|------|--------|-------|
| `postId` | Long | 포스팅아이디 | ✅     |

**Request**

  ```
  - URL : /api/likes/post/{postId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "좋아요 완료"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">DELETE</span> 좋아요 취소</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입   | 설명    | 필수 여부 |
  |-------------|------|-------|-------|
| `commentId` | Long | 댓글아이디 | ✅     |

**Request**

  ```
  - URL : /api/likes/post/{postId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "좋아요 취소완료"
  }
  ```

  </details>

</details>

***

<details>
  <summary><span style="color: red;">🗂 친구 관련 API</span></summary>

  <br>

  <details>
    <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 친구요청 보내기</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명         | 필수 여부 |
  |----------|------|------------|-------|
| `userId` | Long | 요청보낼 유저아이디 | ✅     |

**Request**

  ```
  - URL : /api/friends/{userId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "친구요청을 보냈습니다"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">PATCH</span> 친구요청 수락하기</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명            | 필수 여부 |
  |----------|------|---------------|-------|
| `userId` | Long | 친구요청 보낸 유저아이디 | ✅     |

**Request**

  ```
  - URL : /api/friends/request/{userId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "친구요청을 수락했습니다"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">DELETE</span> 친구관계 거절,삭제</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명        | 필수 여부 |
  |----------|------|-----------|-------|
| `userId` | Long | 삭제할 친구아이디 | ✅     |

**Request**

  ```
  - URL : /api/friends/request/{userId}
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": "친구삭제 완료"
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 내가보낸 친구요청 조회</summary>

## 📌 Request 필드 필수 여부

| 필드     | 타입  | 설명    | 필수 여부 | 기본값  | 조건                     |
  |--------|-----|-------|-------|------|------------------------|
| `page` | int | 페이지   | ❌     | `0`  | `Min = 0`              |
| `size` | int | 페이지크기 | ❌     | `10` | `Min = 1` , `Max = 20` |

**Request**

  ```
  - URL : /api/friends/pending/sent?page=0&size=10
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "userId": 2,
                "userEmail": "test2@naver.com",
                "userNickname": "TESTUSER2",
                "friendStatus": "PENDING"
            },
            {
                "userId": 3,
                "userEmail": "test3@naver.com",
                "userNickname": "TESTUSER3",
                "friendStatus": "PENDING"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">GET</span> 내가받은 친구요청 조회</summary>

## 📌 Request 필드 필수 여부

| 필드     | 타입  | 설명    | 필수 여부 | 기본값  | 조건                     |
  |--------|-----|-------|-------|------|------------------------|
| `page` | int | 페이지   | ❌     | `0`  | `Min = 0`              |
| `size` | int | 페이지크기 | ❌     | `10` | `Min = 1` , `Max = 20` |

**Request**

  ```
  - URL : /api/friends/pending/received?page=0&size=10
  ```

**Response**

  ```
  {
    "status": 200,
    "message": "OK",
    "data": {
        "content": [
            {
                "userId": 1,
                "userEmail": "test1@naver.com",
                "userNickname": "TESTUSER1",
                "friendStatus": "PENDING"
            },
            {
                "userId": 2,
                "userEmail": "test2@naver.com",
                "userNickname": "TESTUSER2",
                "friendStatus": "PENDING"
            }
        ],
        "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
        }
    }
  }
  ```

  </details>
</details>

***


