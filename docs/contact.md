# Contact API Spec

## Create Contact

### Request :
- Method : POST
- Endpoint : `/api/contacts`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
- Body :
```json
{
    "firstName": "string",
    "lastName": "string",
    "email": "string, unique",
    "phone": "string, unique"
}
```
### Response :
- Body
```json
{
    "code": "integer",
    "message": "string",
    "data": {
        "id": "uuid",
        "firstName": "string",
        "lastName": "string",
        "email": "string, unique",
        "phone": "string, unique"
    }
}
```
OR
```json
{
    "code": "integer",
    "message": "string",
    "data": null
}
```

## Update Contact

### Request :
- Method : PUT
- Endpoint : `/api/contacts/{idContact}`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
- Body :
```json
{
    "firstName": "string",
    "lastName": "string",
    "email": "string, unique",
    "phone": "string, unique"
}
```
### Response :
- Body
```json
{
    "code": "integer",
    "message": "string",
    "data": {
        "id": "uuid",
        "firstName": "string",
        "lastName": "string",
        "email": "string, unique",
        "phone": "string, unique"
    }
}
```
OR
```json
{
    "code": "integer",
    "message": "string",
    "data": null
}
```

## Get Contact

- Method : GET
- Endpoint : `/api/contacts/{idContact}`
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
        "id": "uuid",
        "firstName": "string",
        "lastName": "string",
        "email": "string, unique",
        "phone": "string, unique"
    }
}
```
OR
```json
{
    "code": "integer",
    "message": "string",
    "data": null
}
```

## Search Contact

- Method : GET
- Endpoint : `/api/contacts`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
- Query Param :
    - name : string, contact/firstName/lastName, optional
    - phone : string, optional
    - email : string, optional
    - size : integer, default 10
    - page : integer, default 0
### Response :
- Body
```json
{
    "code": "integer",
    "message": "string",
    "data": [
        {
            "id": "uuid",
            "firstName": "string",
            "lastName": "string",
            "email": "string, unique",
            "phone": "string, unique"
        }
    ],
    "paging": {
        "currentPage": "integer",
        "totalPage": "integer",
        "size": "integer"
    }
}
```

## Remove Contact

- Method : DELETE
- Endpoint : `/api/contacts/{idContact}`
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
