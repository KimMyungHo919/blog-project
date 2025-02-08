### 블로그 프로젝트

<details>
  <summary><span style="color: red;">POST</span> 유저회원가입</summary>

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
 <summary><span style="color: red;">POST</span> 유저 로그인</summary>

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
 <summary><span style="color: red;">POST</span> 유저 로그아웃</summary>

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
 <summary><span style="color: red;">GET</span> 유저 정보조회</summary>

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
 <summary><span style="color: red;">GET</span> 한 유저의 포스팅 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                                      |
|-------------|--------|-----------|-------|-------------|-----------------------------------------|
| `userId`    | Long   | 유저아이디     | ✅     |             |                                         |
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`                               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 10`                  |
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
 <summary><span style="color: red;">GET</span> 한 유저의 댓글 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                      |
|-------------|--------|-----------|-------|-------------|-------------------------|
| `userId`    | Long   | 유저아이디     | ✅     |             |                         |
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 10`  |
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
 <summary><span style="color: red;">GET</span> 한 유저의 좋아요 누른 포스팅 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                      |
|-------------|--------|-----------|-------|-------------|-------------------------|
| `userId`    | Long   | 유저아이디     | ✅     |             |                         |
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 10`  |
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
 <summary><span style="color: red;">GET</span> 내 친구 전체조회</summary>

## 📌 Request 필드 필수 여부

| 필드          | 타입     | 설명        | 필수 여부 | 기본값         | 조건                      |
|-------------|--------|-----------|-------|-------------|-------------------------|
| `page`      | int    | 페이지       | ❌     | `0`         | `Min = 0`               |
| `size`      | int    | 페이지크기     | ❌     | `10`        | `Min = 1` , `Max = 10`  |
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
 <summary><span style="color: red;">PATCH</span> 유저 비밀번호변경</summary>

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
  <summary><span style="color: red;">PATCH</span> 유저 닉네임변경</summary>

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
 <summary><span style="color: red;">DELETE</span> 유저 회원탈퇴</summary>

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


