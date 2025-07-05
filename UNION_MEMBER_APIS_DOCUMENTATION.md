# Union Member API Documentation

## Overview

This document describes the secure, native SQL-based REST APIs for managing Union Members and Union Heads in the Shaffaf Service.

## Security

All APIs are restricted to users with **ROLE_ADMIN** or **ROLE_SELLER** authorities only.

## Uniqueness Constraint

**Union Members are unique per project** based on the following criteria:

- **phoneNumber** (exact match)
- **projectId** (exact match)

Attempting to create or update a union member with the same phone number for the same project will result in a `400 Bad Request` error with error code `duplicatemember`.

## API Endpoints

### 1. Add Union Member

**Endpoint:** `POST /services/shaffafservice/api/union-members/v1/member`
**Description:** Creates a new union member (non-head) using JPA with sequence generation.
**Security:** Requires ROLE_ADMIN or ROLE_SELLER
**Pagination:** N/A (creation endpoint)

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+923311234569",
  "project": {
    "id": 123
  }
}
```

**Response:** 201 Created

```json
{
  "id": 456,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+923311234569",
  "isUnionHead": false,
  "project": {
    "id": 123,
    "name": "Project Name",
    "description": "Project Description"
  },
  "createdBy": "admin",
  "createdDate": "2025-07-05T10:00:00Z"
}
```

**Validation Rules:**

- All fields are validated according to entity constraints
- Project ID is required
- Email must be unique within the system
- **Phone Number Format:** Must be Pakistani mobile number in format +923xxxxxxxxx (e.g., +923311234569)
- Automatically sets `isUnionHead` to `false`
- ID is auto-generated using PostgreSQL sequence

### 2. Add Union Head

**Endpoint:** `POST /services/shaffafservice/api/union-members/v1/head`
**Description:** Creates a new union head using JPA with sequence generation. Ensures only one head per project.
**Security:** Requires ROLE_ADMIN or ROLE_SELLER
**Pagination:** N/A (creation endpoint)

**Request Body:**

```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "phoneNumber": "+923317898915",
  "project": {
    "id": 123
  }
}
```

**Response:** 201 Created

```json
{
  "id": 457,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "phoneNumber": "+923317898915",
  "isUnionHead": true,
  "project": {
    "id": 123,
    "name": "Project Name",
    "description": "Project Description"
  },
  "createdBy": "admin",
  "createdDate": "2025-07-05T10:00:00Z"
}
```

**Validation Rules:**

- All fields are validated according to entity constraints
- Project ID is required
- Email must be unique within the system
- **Phone Number Format:** Must be Pakistani mobile number in format +923xxxxxxxxx (e.g., +923317898915)
- Automatically sets `isUnionHead` to `true`
- **Business Rule:** Only one union head is allowed per project
- ID is auto-generated using PostgreSQL sequence

**Error Cases:**

- 400 Bad Request: If a union head already exists for the specified project

### 3. View Union Member/Head

**Endpoint:** `GET /services/shaffafservice/api/union-members/v1/native/{id}`
**Description:** Retrieves a union member or head by ID using native SQL.
**Security:** Requires ROLE_ADMIN or ROLE_SELLER
**Pagination:** N/A (single record endpoint)

**Path Parameters:**

- `id` (Long): The ID of the union member to retrieve

**Response:** 200 OK

```json
{
  "id": 456,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+923311234569",
  "isUnionHead": false,
  "project": {
    "id": 123,
    "name": "Project Name",
    "description": "Project Description"
  },
  "createdBy": "admin",
  "createdDate": "2025-07-05T10:00:00Z",
  "lastModifiedBy": "admin",
  "lastModifiedDate": "2025-07-05T10:30:00Z"
}
```

### 4. Update Union Member/Head

**Endpoint:** `PUT /services/shaffafservice/api/union-members/v1/native/{id}`
**Description:** Updates an existing union member or head using native SQL.
**Security:** Requires ROLE_ADMIN or ROLE_SELLER
**Pagination:** N/A (update endpoint)

**Path Parameters:**

- `id` (Long): The ID of the union member to update

**Request Body:**

```json
{
  "firstName": "John Updated",
  "lastName": "Doe Updated",
  "email": "john.doe.updated@example.com",
  "phoneNumber": "+923319876543",
  "isUnionHead": true,
  "project": {
    "id": 123
  }
}
```

**Response:** 200 OK

```json
{
  "id": 456,
  "firstName": "John Updated",
  "lastName": "Doe Updated",
  "email": "john.doe.updated@example.com",
  "phoneNumber": "+923319876543",
  "isUnionHead": true,
  "project": {
    "id": 123,
    "name": "Project Name",
    "description": "Project Description"
  },
  "createdBy": "admin",
  "createdDate": "2025-07-05T10:00:00Z",
  "lastModifiedBy": "admin",
  "lastModifiedDate": "2025-07-05T11:00:00Z"
}
```

**Validation Rules:**

- All fields are validated according to entity constraints
- Project ID is required
- **Phone Number Format:** Must be Pakistani mobile number in format +923xxxxxxxxx
- Union member must exist

### 5. Get All Union Members for a Project

**Endpoint:** `GET /services/shaffafservice/api/union-members/v1/project/{projectId}`
**Description:** Retrieves all union members (including heads) for a specific project using native SQL with pagination.
**Security:** Requires ROLE_ADMIN or ROLE_SELLER
**Pagination:** Supported

**Path Parameters:**

- `projectId` (Long): The ID of the project

**Query Parameters:**

- `page` (int): Page number (0-based, default: 0)
- `size` (int): Page size (default: 20, max: 100)
- `sort` (string): Sort criteria (e.g., "firstName,asc" or "createdDate,desc")

**Example Request:**

```
GET /services/shaffafservice/api/union-members/v1/project/123?page=0&size=10&sort=isUnionHead,desc&sort=createdDate,asc
```

**Response:** 200 OK

```json
{
  "content": [
    {
      "id": 457,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "jane.smith@example.com",
      "phoneNumber": "+923317898915",
      "isUnionHead": true,
      "project": {
        "id": 123,
        "name": "Project Name",
        "description": "Project Description"
      },
      "createdBy": "admin",
      "createdDate": "2025-07-05T10:00:00Z"
    },
    {
      "id": 456,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phoneNumber": "+923311234569",
      "isUnionHead": false,
      "project": {
        "id": 123,
        "name": "Project Name",
        "description": "Project Description"
      },
      "createdBy": "admin",
      "createdDate": "2025-07-05T10:15:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 2,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false
  }
}
```

**Default Sorting:** Union heads are displayed first (isUnionHead DESC), then by creation date ascending.

### 6. Get All Union Members (Global)

**Endpoint:** `GET /services/shaffafservice/api/union-members/v1/native`
**Description:** Retrieves all union members across all projects using native SQL with pagination and sorting.
**Security:** Requires ROLE_ADMIN or ROLE_SELLER
**Pagination:** Supported

**Query Parameters:**

- `page` (int): Page number (0-based, default: 0)
- `size` (int): Page size (default: 20, max: 100)
- `sort` (string): Sort criteria (supported fields: firstName, lastName, email, createdDate, isUnionHead)

**Supported Sort Fields:**

- `firstName`
- `lastName`
- `email`
- `createdDate`
- `isUnionHead`

**Example Request:**

```
GET /services/shaffafservice/api/union-members/v1/native?page=0&size=20&sort=firstName,asc
```

**Response:** Same structure as endpoint #5, but includes all union members from all projects.

## Technical Implementation

### Sequence Generation

- **ID Generation:** Uses PostgreSQL sequence `union_member_seq` with JPA `@GeneratedValue`
- **Sequence Configuration:** `@SequenceGenerator(name = "sequenceGenerator", sequenceName = "union_member_seq")`
- **Insert Operations:** Creation APIs use JPA save with automatic sequence value generation
- **Concurrency Safe:** PostgreSQL sequences handle concurrent access automatically

### Native SQL Queries

Read and update operations use native SQL queries for:

- **Performance:** Direct database access without JPA overhead for complex queries
- **Security:** Parameterized queries prevent SQL injection
- **Control:** Fine-grained control over query execution and result mapping
- **Pagination:** Custom pagination with COUNT queries for accurate totals

### Security Features

1. **Authorization:** `@PreAuthorize` annotations restrict access to ROLE_ADMIN and ROLE_SELLER
2. **Authentication:** Current user context is captured for audit fields
3. **Validation:** Input validation using Bean Validation annotations
4. **Phone Number Validation:** Pakistani mobile format +923xxxxxxxxx enforced using regex pattern
5. **SQL Injection Prevention:** All queries use parameterized inputs

### Pagination and Sorting

- All list endpoints support Spring Data pagination
- Native queries include COUNT queries for accurate total counts
- Dynamic sorting is implemented with CASE statements in SQL
- Default sorting prioritizes union heads and creation date

### Error Handling

- **400 Bad Request:** Invalid input data, validation errors, business rule violations
  - `duplicatemember`: Union member with this phone number already exists for this project
  - `unionheadexists`: A union head already exists for this project
  - `idexists`: Attempting to create entity with existing ID
  - `projectrequired`: Project is required for union member/head
- **401 Unauthorized:** Missing or invalid authentication
- **403 Forbidden:** Insufficient permissions (requires ROLE_ADMIN or ROLE_SELLER)
- **404 Not Found:** Resource not found
- **500 Internal Server Error:** Server-side errors

### Audit Trail

All operations automatically track:

- `createdBy`: User who created the record
- `createdDate`: Timestamp of creation
- `lastModifiedBy`: User who last modified the record
- `lastModifiedDate`: Timestamp of last modification

## Database Schema Changes

- Added `is_union_head` boolean column to `union_member` table
- Added database index on `(project_id, is_union_head)` for performance
- Added NOT NULL constraint with default value `false`
- Included in Liquibase changelog: `20250705000001_add_is_union_head_to_union_member.xml`

## Business Rules

1. **One Union Head Per Project:** Each project can have only one union head
2. **Project Requirement:** All union members must be associated with a project
3. **Union Member Uniqueness:** Each union member must be unique per project based on phone number
4. **Phone Number Format:** Pakistani mobile number format (+923xxxxxxxxx) required
5. **Soft Delete:** Records are soft-deleted (marked with `deleted_on` timestamp)

## Testing Recommendations

1. **Unit Tests:** Test service methods with various input scenarios
2. **Integration Tests:** Test REST endpoints with proper security context
3. **Security Tests:** Verify authorization requirements are enforced
4. **Performance Tests:** Validate pagination and native query performance
5. **Business Rule Tests:** Verify one-head-per-project constraint

## Future Enhancements

1. **Bulk Operations:** APIs for bulk import/export of union members
2. **Search and Filtering:** Advanced search capabilities with multiple criteria
3. **Reporting:** Specialized endpoints for reporting and analytics
4. **Notifications:** Integration with notification service for member updates
