## 블로그 API

<details>
  <summary><span style="color: red;">유저 관련 API</span></summary>

  <br>

  <details>
    <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 유저회원가입</summary>

## 📌 Request 필드 필수 여부

| 필드         | 타입     | 설명     | 필수 여부 |
  |------------|--------|--------|-------|
| `email`    | String | 유저 이메일 | ✅     |
| `password` | String | 비밀번호   | ✅     |
| `nickname` | String | 닉네임    | ✅     |

**Request**

  ```
  - URL : /api/public/users/signup
  - Request Body : JSON
  
  {
      "email" : "test@gmail.com",
      "password" : "123asdfASD!@",
      "nickname" : "TESTNICKNAME"
  }
  ```

**Response**

  ```
  201 Created
  
  {
      "id": 1,
      "email": "test@gmail.com",
      "nickname": "TESTNICKNAME"
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
      "id": 1,
      "email": "test@gmail.com",
      "nickname": "MMOOKK",
      "role": "USER"
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
  200 OK
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
  200 OK
  
  {
      "id": 1,
      "email": "test@gmail.com",
      "nickname": "MMOOKK"
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
  200 OK
  
  {
      "content": [
          {
              "postId": 1,
              "title": "제목1",
              "content": "글1",
              "userNickname": "TESTNICKNAME",
              "createdAt": "2025-02-08T14:57:01.109587",
              "updatedAt": "2025-02-08T14:57:01.109587"
          },
          {
              "postId": 2,
              "title": "나나나나나나나나",
              "content": "글 블라블라블라블라 허허헣허",
              "userNickname": "MMOOKK",
              "createdAt": "2025-02-08T14:57:03.046669",
              "updatedAt": "2025-02-08T14:57:03.046669"
          }
      ],
      "page": {
          "size": 10,
          "number": 0,
          "totalElements": 2,
          "totalPages": 1
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
  200 OK
  
  {
      "content": [
          {
              "commentId": 1,
              "comment": "좋은글이네요.",
              "userNickname": "MMOOKK",
              "createdAt": "2025-02-08T14:58:47.279805",
              "updatedAt": "2025-02-08T14:58:47.279805"
          },
          {
              "commentId": 2,
              "comment": "좋은글이네요.",
              "userNickname": "MMOOKK",
              "createdAt": "2025-02-08T14:58:48.148984",
              "updatedAt": "2025-02-08T14:58:48.148984"
          }
      ],
      "page": {
          "size": 10,
          "number": 0,
          "totalElements": 2,
          "totalPages": 1
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
  200 OK
  
  {
      "content": [
          {
              "postId": 2,
              "postTitle": "나나나나나나나나",
              "postContent": "글 블라블라블라블라 허허헣허",
              "postCreatedAt": "2025-02-08T14:57:03.046669",
              "postUpdatedAt": "2025-02-08T14:57:03.046669"
          },
          {
              "postId": 1,
              "postTitle": "나나나나나나나나",
              "postContent": "글 블라블라블라블라 허허헣허",
              "postCreatedAt": "2025-02-08T14:57:01.109587",
              "postUpdatedAt": "2025-02-08T14:57:01.109587"
          }
      ],
      "page": {
          "size": 10,
          "number": 0,
          "totalElements": 2,
          "totalPages": 1
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
  200 OK
  
  {
      "content": [
          {
              "userId": 1,
              "userNickname": "owqeiqweowqe",
              "userEmail": "qwewqeqweqwe@naver.com"
          },
          {
              "userId": 5,
              "userNickname": "WERUO",
              "userEmail": "sdfsdfsdf@naver.com"
          }
      ],
      "page": {
          "size": 10,
          "number": 0,
          "totalElements": 2,
          "totalPages": 1
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
  200 OK
  비밀번호 변경 완료. 다시 로그인해주세요
  ```

  </details>

  <details>
    <summary style="margin-left: 20px;"><span style="color: red;">PATCH</span> 유저 닉네임변경</summary>

## 📌 Request 필드 필수 여부

| 필드         | 타입     | 설명     | 필수 여부 |
  |------------|--------|--------|-------|
| `password` | String | 비밀번호   | ✅     |
| `nickname` | String | 바꿀 닉네임 | ✅     |

**Request**

  ```
  - URL : /api/users/me/nickname
  - Request Body : JSON
  
  {
      "password" : "sdflkjs12@1!A",
      "nickname" : "JANANANANA"
  }
  ```

**Response**

  ```
  200 OK
  닉네임 변경완료
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
  200 OK
  회원탈퇴가 완료되었습니다.
  ```

  </details>
</details>

***

<details>
  <summary><span style="color: red;">포스팅 관련 API</span></summary>

  <br>

  <details>
    <summary style="margin-left: 20px;"><span style="color: red;">POST</span> 포스팅 작성</summary>

## 📌 Request 필드 필수 여부

| 필드        | 타입     | 설명 | 필수 여부 | 기본값  | 조건                     |
  |-----------|--------|----|-------|------|------------------------|
| `title`   | String | 제목 | ✅     | `0`  | `Min = 5` , `Max = 50` |
| `content` | String | 내용 | ✅     | `10` | `Min = 10`             |

**Request**

  ```
  - URL : /api/posts
  - Request Body : JSON
  
  {
      "title" : "나나나나나나나나",
      "content" : "글 블라블라블라블라 허허헣허"
  }
  ```

**Response**

  ```
  201 Created
  
  {
      "postId": 3,
      "title": "나나나나나나나나",
      "content": "글 블라블라블라블라 허허헣허",
      "views": 0,
      "likes": 0,
      "userNickname": "MMOOKK",
      "createdAt": "2025-02-08T14:57:03.963832",
      "updatedAt": "2025-02-08T14:57:03.963832"
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
      "postId": 1,
      "title": "포스팅제목얍얍야뱌얍",
      "content": "글 블라블라블라블라 허허헣허",
      "views": 2,
      "likes": 0,
      "userNickname": "MMOOKK",
      "createdAt": "2025-02-08T01:27:35.753301",
      "updatedAt": "2025-02-08T01:29:05.0444"
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
  200 OK
  {
      "content": [
          {
              "postId": 1,
              "title": "나나나나나나나나11",
              "content": "글 블라블라블라블라 허허헣허111",
              "views": 10,
              "likes": 2,
              "userNickname": "MMOOKK",
              "createdAt": "2025-02-08T17:19:59.442715",
              "updatedAt": "2025-02-08T17:19:59.442715"
          },
          {
              "postId": 2,
              "title": "나나나나나나나나22",
              "content": "글 블라블라블라블라 허허헣허222",
              "views": 15,
              "likes": 3,
              "userNickname": "MMOOKK",
              "createdAt": "2025-02-08T17:20:00.179761",
              "updatedAt": "2025-02-08T17:20:00.179761"
          }
      ],
      "page": {
          "size": 10,
          "number": 0,
          "totalElements": 2,
          "totalPages": 1
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
  200 OK
  
  {
      "content": [
          {
              "commentId": 1,
              "comment": "좋은글이네요.",
              "userNickname": "MMOOKK",
              "createdAt": "2025-02-08T17:26:38.272944",
              "updatedAt": "2025-02-08T17:26:38.272944"
          },
          {
              "commentId": 2,
              "comment": "좋은글이네요.",
              "userNickname": "MMOOKK",
              "createdAt": "2025-02-08T17:26:38.997265",
              "updatedAt": "2025-02-08T17:26:38.997265"
          }
      ],
      "page": {
          "size": 10,
          "number": 0,
          "totalElements": 2,
          "totalPages": 1
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
  200 OK
  
  {
      "content": [
          {
              "userNickname": "MMOOKK"
          },
          {
              "userNickname": "DSISIDSDI"
          }
      ],
      "page": {
          "size": 10,
          "number": 0,
          "totalElements": 2,
          "totalPages": 1
      }
  }
  ```

  </details>

  <details>
   <summary style="margin-left: 20px;"><span style="color: red;">PATCH</span> 포스팅 수정</summary>

## 📌 Request 필드 필수 여부

| 필드       | 타입   | 설명     | 필수 여부 |
  |----------|------|--------|-------|
| `postId` | Long | 포스팅아이디 | ✅     |

**Request**

  ```
  - URL : /api/posts/{postId}
  - Request Body : JSON
  
  {
      "title" : "바뀐제목입니다으아아아아아",
      "content" : "바뀐내용입니당야아아아아아"
  }
  ```

**Response**

  ```
  200 OK
  글 업데이트 완료
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
  200 OK
  삭제가 완료되었습니다.
  ```

  </details>
</details>

***

<details>
  <summary><span style="color: red;">댓글 관련 API</span></summary>

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
  201 Created
  
  {
      "commentId": 2,
      "comment": "좋은글이네요.",
      "userNickname": "MMOOKK",
      "createdAt": "2025-02-08T17:26:38.997265",
      "updatedAt": "2025-02-08T17:26:38.997265"
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
      "comment" : "댓글을 바꿔보자!!!!"
  }
  ```

**Response**

  ```
  200 OK
  댓글 수정 완료.
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
  200 OK
  댓글 삭제 완료.
  ```

  </details>

</details>

***

<details>
  <summary><span style="color: red;">좋아요 관련 API</span></summary>

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
  201 Created
  좋아요!
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
  200 Ok
  좋아요 취소.
  ```

  </details>

</details>

***

<details>
  <summary><span style="color: red;">친구 관련 API</span></summary>

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
  200 Ok
  친구요청을 보냈습니다.
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
  200 Ok
  친구요청을 수락했습니다.
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
  200 OK
  친구삭제완료.
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
  200 OK
  
  {
      "content": [
          {
              "userId": 2,
              "userEmail": "NNSADII@naver.com",
              "userNickname": "ANAMAM",
              "friendStatus": "PENDING"
          },
          {
              "userId": 4,
              "userEmail": "testetests@naver.com",
              "userNickname": "EIWUEUE",
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
  200 OK
  
  {
      "content": [
          {
              "userId": 1,
              "userEmail": "qqewewqeqeqwe@naver.com",
              "userNickname": "MMOOKK",
              "friendStatus": "PENDING"
          },
          {
              "userId": 7,
              "userEmail": "sdffewfwef@naver.com",
              "userNickname": "EIWKWKWK",
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
  ```

  </details>
</details>

***


