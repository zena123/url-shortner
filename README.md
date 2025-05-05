# URL Shortener Service

A production-ready URL shortening service built with Spring Boot (Kotlin) that converts long URLs into short, shareable links.

## Features

- **URL Shortening**: Convert long URLs to short url
- **Redirection**: 301 redirects from short URLs to original destinations
- **Metadata Lookup**: get original URL without redirection
- **Duplicate Handling**: Returns existing short URL if URL was previously shortened
- **Race Condition Protection**: Handles concurrent requests gracefully
- **Distributed ID Generation**: Uses Sonyflake for unique ID generation
- **Base62 Encoding**: Generates compact, human-friendly short codes
- **Caching**: Improves performance for frequently accessed URLs

## Tech Stack

- **Backend**: Spring Boot (Kotlin)
- **Database**: JPA/Hibernate (mysql used)
- **Cache**: Spring Cache abstraction
- **ID Generation**: Sonyflake
- **Encoding**: Base62 for short code generation
- **UrlValidation**: Apache Commons Validator
- **API Docs**: OpenAPI(Swagger)

## API Endpoints


# URL Shortener Service

## API Endpoints

### Shorten URL
```http
POST /api/v1/urls
Content-Type: application/json

{
  "longUrl": "https://example.com/very/long/url"
}
Response:

json
{
  "shortKey": "abc123",
  "shortUrl": "https://domain/abc123"
}


```
### Redirect to Original URL
```
http
GET /{shortKey}
Returns 301 redirect to original URL
```


### Get URL Metadata
```http
GET /{shortKey}/meta
Response:

json
{
"originalUrl": "https://example.com/very/long/url"
}
```


### Getting Started
- install docker 
- navigate to project root then run:
```docker compose up --build```
### Access Swagger UI:
- navigate to:
  ```http://localhost:8080/swagger-ui.html```

#### To run the project locally:
- connect to MYSQL database and update DB connection in application properties
- this project uses java 17, make sure it's installed
- run : 
```
./gradlew bootRun
```

#### Run tests: 
```
./gradlew test
```

### Configure base domain:
- to configure the short url domain, go to application.properties and change:

```app.domain= <Your Domain>```


### Phase2 and improvements: 

 - add **Redis** to cache frequently requested Urls
 - Rate Limiting