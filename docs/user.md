# User API Spec

## Register User

### Request :
- Method : POST
- Endpoint : `/api/users`
- Header :
	- Content-Type: application/json
	- Accept: application/json
- Body :
```json
{
	"username": "string, unique",
	"password": "string",
	"name": "string"
}
```

### Response :
- Body :
```json
{
	"code": "number",
	"message": "string"
}
```

## Login User

### Request :
- Method : POST
- Endpoint : `/api/auth/login`
- Header :
	- Content-Type: application/json
	- Accept: application/json
- Body :
```json
{
	"username": "string, unique",
	"password": "string"
}
```

### Response :
- Body
```json
{
    "code": "integer",
    "message": "string",
	"data": {
        "token": "string, unique",
        "expiredAt": "bigint" // millisecond
	}
}
```
OR
```json
{
    "code": "integer",
    "message": "string"
	"data": null,
}
```

## Get User

### Request :
- Method : GET
- Endpoint : `/api/users/current`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
### Response :
- Body
```json
{
    "code": "integer",
    "message": "string",
	"data": {
        "username": "string",
        "name": "string"
	}
}
```

## Update User

### Request :
- Method : PATCH
- Endpoint : `/api/users/current`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
- Body :
```json
{
	"name": "string, unique",
	"password": "string"
}
```
### Response :
- Body
```json
{
    "code": "integer",
    "message": "string",
	"data": {
        "username": "string",
        "name": "string"
	}
}
```

## Logout User

### Request :
- Method : DELETE
- Endpoint : `/api/auth/logout`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
### Response :
- Body
```json
{
    "code": "integer",
    "message": "string"
}
```
