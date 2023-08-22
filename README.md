# Library Model API
This API allows you to do create, read, update, delete (CRUD) operations on Books and Authors. Supports search books by name, author and ISBN number. 

## Set up 
- [*clone*](https://github.com/HUNT-ER/library-model.git) the project
- change [application.properties](src/main/resources/application.properties) file based on your database configurations
- run the project using [LibraryApplication.java](src/main/java/com/boldyrev/library/LibraryApplication.java)

## Authors operations

**GET** `/api/authors?page&size`
  returns list of authors

**POST** `/api/authors `
  create new authors by request body:

```agsl
  request body:
    {
      "name": "Author",
      "birth_date": "1956-05-12",
      "country": "Country"
    }
```

**PUT** `/api/authors/{id} `
updates author by id

**DELETE** `/api/authors/{id} `
deletes author by Id


## Books operations

**GET** `/api/books/search?name&author&ISBN&page&size`
  returns list of authors by name, author, ISBN

**POST** `/api/books `
  create new book by request body:

```agsl
  request body:
    {
      "title": "Title",
      "isbn": "9785041079277",
      "num_pages": 800,
      "publication_date": "1873-01-01",
      "authors": [
          {
              "id": 7
          }
      ]
    }
```

**PUT** `/api/books/{id} `
updates book by id

**DELETE** `/api/books/{id} `
deletes book by Id

# Entity diagram
![Модель данных](https://github.com/HUNT-ER/library-model/assets/38404914/27d33c26-fe64-4101-a35b-27dc07e67ab1)

# Build with
- Spring Boot
- Spring Data JPA
- Spring Web MVC
- Hibernate
- Lombok
- Docker
- Spring Boot Test
- Mockito
- Testcontainers
- Flyway

# What I learned
- Improved skills in Spring Boot, unit testing, Docker, Entity relationships, API Architecture
