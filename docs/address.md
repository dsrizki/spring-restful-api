# Address API Spec

## Create Address

### Request :
- Method : POST
- Endpoint : `/api/contacts/{idContact}/addresses`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
- Body :
```json
{
    "street": "string",
    "city": "string",
    "province": "string",
    "country": "string",
    "postalCode": "string"
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
        "street": "string",
        "city": "string",
        "province": "string",
        "country": "string",
        "postalCode": "string"
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

## Update Address

### Request :
- Method : PUT
- Endpoint : `/api/contacts/{idContact}/addresses/{idAddress}`
- Header :
	- Content-Type: application/json
	- Accept: application/json
    - X-Api-Key : "your secret api key"
- Body :
```json
{
    "street": "string",
    "city": "string",
    "province": "string",
    "country": "string",
    "postalCode": "string"
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
        "street": "string",
        "city": "string",
        "province": "string",
        "country": "string",
        "postalCode": "string"
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

## Get Address

### Request :
- Method : GET
- Endpoint : `/api/contacts/{idContact}/addresses/{idAddress}`
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
        "street": "string",
        "city": "string",
        "province": "string",
        "country": "string",
        "postalCode": "string"
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

## Remove Address

### Request :
- Method : DELETE
- Endpoint : `/api/contacts/{idContact}/addresses/{idAddress}`
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

## List Address

### Request :
- Method : GET
- Endpoint : `/api/contacts/{idContact}/addresses`
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
    "data": [
        {
            "id": "uuid",
            "street": "string",
            "city": "string",
            "province": "string",
            "country": "string",
            "postalCode": "string"
        }
    ]
}
