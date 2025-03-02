package com.project.blog.global.base;

public final class QueueBindings {

    private QueueBindings() {}

    // Exchange 이름
    public static final String USER_SIGNUP_EXCHANGE = "userSignupExchange";

    // Routing Key 이름
    public static final String USER_SIGNUP_KEY = "userSignupKey";

    // Queue 이름
    public static final String USER_SIGNUP_QUEUE = "userSignupQueue";

    // Dead Letter 관련 추가
    public static final String USER_DLQ = "userDeadLetterQueue"; // 큐이름
    public static final String USER_DLQ_KEY = "userDeadLetterKey"; // 라우팅키
    public static final String USER_DLX = "userDeadLetterExchange"; // Exchange 이름

}
