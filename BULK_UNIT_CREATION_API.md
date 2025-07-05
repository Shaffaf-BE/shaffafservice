# Bulk Unit Creation API Documentation

## Overview

The Bulk Unit Creation API allows authorized users to create multiple units, blocks, and unit types in a single request. This API is designed to streamline the process of setting up large residential projects with multiple units across different blocks and unit types.

## Authentication & Authorization

### Required Roles

- `ROLE_ADMIN` - Full access to all projects
- `ROLE_SELLER` - Access limited to own projects only

### Authentication Method

- Bearer Token authentication required
- For sellers: Username must match seller's phone number in the system

## API Endpoint

### Create Units in Bulk

**Endpoint:** `POST /api/bulk-unit-creation/v1/units`

**Description:** Creates blocks, unit types, and units in bulk for a specific project.

**Content-Type:** `application/json`

**Authorization:** Requires `ROLE_ADMIN` or `ROLE_SELLER`

---

## Request Format

### Request Body Schema

```json
{
  "projectId": "number (required)",
  "items": [
    {
      "block": "string (required, min: 1, max: 50)",
      "unitType": "string (required, min: 1, max: 50)",
      "unitStart": "number (required, min: 1)",
      "unitEnd": "number (required, min: 1)"
    }
  ]
}
```

### Field Descriptions

| Field               | Type   | Required | Validation                  | Description                           |
| ------------------- | ------ | -------- | --------------------------- | ------------------------------------- |
| `projectId`         | number | Yes      | Must exist in database      | ID of the project to create units for |
| `items`             | array  | Yes      | Min: 1 item, Max: 100 items | List of bulk creation items           |
| `items[].block`     | string | Yes      | Length: 1-50 chars          | Name of the block to create/use       |
| `items[].unitType`  | string | Yes      | Length: 1-50 chars          | Name of the unit type to create/use   |
| `items[].unitStart` | number | Yes      | Min: 1                      | Starting unit number (inclusive)      |
| `items[].unitEnd`   | number | Yes      | Min: 1                      | Ending unit number (inclusive)        |

### Validation Rules

1. **Project Access Control:**

   - Sellers can only create units for projects they own
   - Admins can create units for any project

2. **Unit Range Validation:**

   - `unitStart` must be ≤ `unitEnd`
   - Maximum 500 units per request across all items
   - Unit numbers are treated as integers

3. **Duplicate Prevention:**

   - Existing units are skipped with warnings
   - New blocks and unit types are created as needed

4. **Business Limits:**
   - Maximum 100 items per request
   - Maximum 500 total units per request

---

## Request Examples

### Example 1: Create Units in Multiple Blocks

```json
{
  "projectId": 123,
  "items": [
    {
      "block": "Block A",
      "unitType": "2BHK",
      "unitStart": 101,
      "unitEnd": 110
    },
    {
      "block": "Block A",
      "unitType": "3BHK",
      "unitStart": 201,
      "unitEnd": 205
    },
    {
      "block": "Block B",
      "unitType": "2BHK",
      "unitStart": 101,
      "unitEnd": 108
    }
  ]
}
```

### Example 2: Create Single Unit

```json
{
  "projectId": 456,
  "items": [
    {
      "block": "Tower 1",
      "unitType": "Penthouse",
      "unitStart": 2501,
      "unitEnd": 2501
    }
  ]
}
```

---

## Response Format

### Success Response (HTTP 200)

```json
{
  "message": "Successfully created 23 units, 2 blocks, and 2 unit types for project ProjectName",
  "totalUnitsCreated": 23,
  "totalBlocksCreated": 2,
  "totalUnitTypesCreated": 2,
  "createdBlocks": ["Block A", "Block B"],
  "createdUnitTypes": ["2BHK", "3BHK"],
  "warnings": ["Unit 102 in Block A with unit type 2BHK already exists", "Unit 103 in Block A with unit type 2BHK already exists"]
}
```

### Response Field Descriptions

| Field                   | Type   | Description                                     |
| ----------------------- | ------ | ----------------------------------------------- |
| `message`               | string | Human-readable success message                  |
| `totalUnitsCreated`     | number | Number of new units created                     |
| `totalBlocksCreated`    | number | Number of new blocks created                    |
| `totalUnitTypesCreated` | number | Number of new unit types created                |
| `createdBlocks`         | array  | Names of blocks that were created               |
| `createdUnitTypes`      | array  | Names of unit types that were created           |
| `warnings`              | array  | List of warning messages for skipped duplicates |

---

## Error Responses

### 400 Bad Request - Validation Errors

```json
{
  "type": "https://zalando.github.io/problem/constraint-violation",
  "title": "Constraint Violation",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/bulk-unit-creation/v1/units",
  "violations": [
    {
      "field": "items[0].unitStart",
      "message": "Unit start number must be at least 1"
    }
  ]
}
```

### 400 Bad Request - Business Rule Violations

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Items list cannot be empty",
  "instance": "/api/bulk-unit-creation/v1/units"
}
```

**Common Business Rule Errors:**

- `"Items list cannot be empty"`
- `"Unit start number cannot be greater than unit end number for item: <item details>"`
- `"Total units across all items cannot exceed 500. Current total: <number>"`

### 401 Unauthorized

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Full authentication is required to access this resource",
  "instance": "/api/bulk-unit-creation/v1/units"
}
```

### 403 Forbidden - Insufficient Permissions

```json
{
  "type": "about:blank",
  "title": "Forbidden",
  "status": 403,
  "detail": "Access is denied",
  "instance": "/api/bulk-unit-creation/v1/units"
}
```

### 403 Forbidden - Seller Access Violation

```json
{
  "type": "about:blank",
  "title": "Forbidden",
  "status": 403,
  "detail": "Access denied: You can only create units for your own projects",
  "instance": "/api/bulk-unit-creation/v1/units"
}
```

### 404 Not Found - Project Not Found

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Project not found with ID: 999",
  "instance": "/api/bulk-unit-creation/v1/units"
}
```

### 404 Not Found - Seller Not Found

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Seller not found with phone number: 1234567890",
  "instance": "/api/bulk-unit-creation/v1/units"
}
```

### 500 Internal Server Error

```json
{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing your request",
  "instance": "/api/bulk-unit-creation/v1/units"
}
```

---

## Usage Examples with cURL

### Example 1: Admin Creating Units

```bash
curl -X POST \
  'https://api.shaffaf.com/api/bulk-unit-creation/v1/units' \
  -H 'Authorization: Bearer <admin-token>' \
  -H 'Content-Type: application/json' \
  -d '{
    "projectId": 123,
    "items": [
      {
        "block": "Tower A",
        "unitType": "2BHK",
        "unitStart": 101,
        "unitEnd": 110
      }
    ]
  }'
```

### Example 2: Seller Creating Units for Own Project

```bash
curl -X POST \
  'https://api.shaffaf.com/api/bulk-unit-creation/v1/units' \
  -H 'Authorization: Bearer <seller-token>' \
  -H 'Content-Type: application/json' \
  -d '{
    "projectId": 456,
    "items": [
      {
        "block": "Block 1",
        "unitType": "Studio",
        "unitStart": 1,
        "unitEnd": 20
      },
      {
        "block": "Block 1",
        "unitType": "1BHK",
        "unitStart": 21,
        "unitEnd": 40
      }
    ]
  }'
```

---

## Business Logic Details

### Entity Creation Strategy

1. **Block Creation:**

   - Check if block exists in the project
   - Create new block if it doesn't exist
   - Use existing block if found

2. **Unit Type Creation:**

   - Check if unit type exists in the project
   - Create new unit type if it doesn't exist
   - Use existing unit type if found

3. **Unit Creation:**
   - Generate units for the specified range (unitStart to unitEnd)
   - Skip units that already exist (with warning)
   - Create new units with proper block and unit type associations

### Transaction Management

- All operations within a single request are performed in one database transaction
- If any error occurs, the entire transaction is rolled back
- Partial failures are not allowed - it's all or nothing

### Sequence Generation

- All new entities (Block, UnitType, Unit) use JPA-managed ID sequences
- No manual ID assignment required
- Database-generated timestamps for audit fields

---

## Performance Considerations

### Recommended Batch Sizes

- **Small Projects:** 1-50 units per request
- **Medium Projects:** 51-200 units per request
- **Large Projects:** 201-500 units per request (maximum)

### Rate Limiting

- Consider implementing rate limiting for this endpoint
- Recommended: 10 requests per minute per user for large batches

### Database Impact

- Each request may create multiple database records
- Monitor database performance for large bulk operations
- Consider running large imports during off-peak hours

---

## Integration Notes

### Prerequisites

1. **Database Schema:** Ensure Block, UnitType, Unit, and Project tables exist with proper relationships
2. **Authentication:** Bearer token authentication must be configured
3. **Authorization:** Role-based security must be enabled
4. **Seller Management:** Seller records must exist with phone number mapping

### Error Handling Best Practices

1. **Client-Side Validation:** Validate input before sending requests
2. **Retry Logic:** Implement exponential backoff for 5xx errors
3. **User Feedback:** Display clear error messages to end users
4. **Logging:** Log all requests and responses for debugging

### Testing Recommendations

1. **Unit Tests:** Test with various input combinations
2. **Integration Tests:** Test with actual database
3. **Performance Tests:** Test with maximum allowed payload sizes
4. **Security Tests:** Verify role-based access control
5. **Error Tests:** Test all error scenarios

---

## Version History

| Version | Date       | Changes                    |
| ------- | ---------- | -------------------------- |
| v1.0    | 2025-07-05 | Initial API implementation |

---

## Support

For technical support or questions about this API, please contact the development team or refer to the project documentation.
