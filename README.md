# 👨‍💻 단기예보 Open API


##💡 **모듈 구성**

root
├─ modules
│	├─ module - api
│	│	└─ controller
│	│	 │   └─ GetWeatherController
│	│	 │   └─ WeatherAPIController
│	│	 │   └─ CreateData
│	│      ├─ resource
│	│	 │       │   ├─ templates
│	│	 │       │   └─ static
│	│	 │       └─ storage
│	├─ module - core
│	│	├─ entity
│	│	 │   └─ Region
│	│	├─ dto
│	│	 │   ├─ Weather
│	│	 │   └─ WeatherResponseDTO
│	├─ module - domain
│	│	├─ repository
│	│	 │   └─ WeatherRepository
│	│	└─ service
│	│	     └─ WeatherService
└─ settings.gradle.kts

![Untitled](%F0%9F%91%A8%E2%80%8D%F0%9F%92%BB%20%E1%84%83%E1%85%A1%E1%86%AB%E1%84%80%E1%85%B5%E1%84%8B%E1%85%A8%E1%84%87%E1%85%A9%20Open%20API%20292065972c0343f6a089ba3c8991cb96/Untitled.png)

</aside>

<aside>
💡 **종속성**

project(":module-api") {
    dependencies {
        implementation(project(":module-core"))
        implementation(project(":module-domain"))
    }
}

project(":module-domain") {
    dependencies {
        implementation(project(":module-core"))
    }
}
기

</aside>

<aside>
💡 DB : MySQL

![Untitled](%F0%9F%91%A8%E2%80%8D%F0%9F%92%BB%20%E1%84%83%E1%85%A1%E1%86%AB%E1%84%80%E1%85%B5%E1%84%8B%E1%85%A8%E1%84%87%E1%85%A9%20Open%20API%20292065972c0343f6a089ba3c8991cb96/Untitled%201.png)

</aside>

**✔기능 요약**

1. `/weather` 주소로 접속 시, 자동으로 csv 파일에서 API URI 생성에 필요한 데이터를 한 줄씩 읽어 데이터베이스에 삽입합니다.
2. 사용자가 동네를 선택하고 버튼을 클릭하면, Ajax를 통해 JSON 데이터 타입으로 GET 요청을 합니다.
3. `getWeatherAPI`는 RESTTemplate를 이용해 단기예보 API를 받아오며, 이전에 생성된 데이터베이스 정보, 서비스키, 콜백 URL을 이용해 URI를 생성합니다. 이는 `requestParam`으로 받은 regionId에 해당하는 데이터를 가져옵니다.
4. RestTemplate을 이용해 URI를 String 타입으로 변환하고, JsonObject를 이용해 API 결과를 파싱합니다.
5. 프론트 단에서는 Ajax로 요청하여 해당 template에 컨텐츠가 표시되도록 합니다.

**✔세부 구현 과정**

1. `module-api/storage` 경로에 저장된 csv 파일은 `CreateData` 컨트롤러에서 BufferReader를 이용해 읽어 데이터를 DB에 삽입합니다. 이때 HTTP 메서드인 POST를 사용합니다.
2. `requestParam`에는 Long 타입인 `regionId`로 URI를 구성합니다.
3. DB에 삽입된 데이터를 이용해 `WeatherAPIController`에서 GET 방식으로 API를 요청하고, XML 형태로 들어오는 데이터를 JSON으로 파싱 후 필요한 데이터(`temp`, `rainAmount`, `humid`)를 `Region` entity에 `JPArepository`를 상속받은 repository를 이용해 저장합니다.
4. 사용자가 `weather` URL로 접속하면, Ajax로 POST 요청을 보내 데이터를 삽입합니다.
5. 사용자가 동네를 선택하고 '날씨 보기' 버튼을 클릭하면, Ajax로 GET 요청을 보냅니다.
6. `weatherAPIController`는 restTemplate를 이용해 만든 Uri를 통해 요청을 보내고, 받아온 요청을 JsonObject로 JSON 형태로 파싱합니다.
7. 파싱된 데이터는 JPA의 `save` 메서드를 이용해 기존 DB에 저장되며, 이미 데이터가 있을 경우 최신 시간에 맞는 데이터로 업데이트 됩니다.

**✔프론트 단 처리**

1. 프론트 단에서는 Ajax로 GET 요청을 보내 `getRegionWeather` 클래스에서 데이터를 받아옵니다.
2. `ResponseEntity.ok(ObjectMapper().writeValueAsString(dto))`를 통해 `WeatherResponseDTO`에 저장된 데이터를 프론트 단으로 전송합니다.

## **👍결과**

![Untitled](%F0%9F%91%A8%E2%80%8D%F0%9F%92%BB%20%E1%84%83%E1%85%A1%E1%86%AB%E1%84%80%E1%85%B5%E1%84%8B%E1%85%A8%E1%84%87%E1%85%A9%20Open%20API%20292065972c0343f6a089ba3c8991cb96/Untitled%202.png)

![Untitled](%F0%9F%91%A8%E2%80%8D%F0%9F%92%BB%20%E1%84%83%E1%85%A1%E1%86%AB%E1%84%80%E1%85%B5%E1%84%8B%E1%85%A8%E1%84%87%E1%85%A9%20Open%20API%20292065972c0343f6a089ba3c8991cb96/Untitled%203.png)
 
 
