# API 모듈 가이드

## domain 패키지

`domain` 패키지는 유즈케이스 로직을 담당하는 패키지입니다.

`domain` 패키지 내부에는 도메인별로 패키지를 생성하여 유즈케이스 클래스를 구현합니다.

유즈케이스는 컨트롤러의 메서드와 1:1 매칭되는 클래스로 구성되어 있습니다.

각 도메인 패키지는 서로 독립적으로 동작하며, 다른 도메인 패키지에 의존하지 않도록 구현하는 것을 지향합니다.

유즈케이스 속 비즈니스 로직은 독립적으로 테스트 가능하도록 구현하는 것을 지향합니다.


## persistence 패키지

`persistence` 패키지는 데이터베이스와의 연동을 담당하는 패키지입니다.

`persistence` 패키지에 도메인별로 패키지를 생성하여 Repository 클래스를 구현합니다.

## web/controller 패키지

`web/controller` 패키지는 HTTP 요청을 처리하는 컨트롤러 클래스를 담당하는 패키지입니다.

`web/controller` 패키지에 도메인별로 패키지를 생성하여 컨트롤러 클래스를 구현합니다.

security 필터를 통해 인증이 불필요한 API는 `WebSecurityConfig#xxxWebSecurityFilterIgnoreCustomizer`에 추가합니다.

인증이 필요한 API는 `@AuthenticationPrincipal TokenUserDetails userDetails`를 파라미터로 받아 처리합니다.

응답의 경우 ` ApiResponseGenerator`를 사용하여 응답을 생성합니다.

```java
 // 반환값이 있는 경우
public ApiResponse<ApiResponse.SuccessBody<XXXResponse>> xxx() {
    XXXResponse response = xxxService.xxx();
    return ApiResponseGenerator.success(response, HttpStatus.OK, Message.SUCCESS);
}

// 반환값이 없는 경우
public ApiResponse<ApiResponse.SuccessBody> xxx() {
    xxxService.xxx();
    return ApiResponseGenerator.success(HttpStatus.OK, Message.SUCCESS);
}
```
