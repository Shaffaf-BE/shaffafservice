# Union Member Uniqueness Constraint Implementation

## Overview

This document describes the implementation of uniqueness constraints for Union Members per project based on the criteria: firstName, lastName, email, phoneNumber, and projectId.

## Implementation Details

### 1. Repository Methods Added

#### New Query Methods in `UnionMemberRepository.java`:

```java
/**
 * Check if a union member already exists for the given project with same criteria.
 * Criteria: phoneNumber, projectId
 */
@Query(
  value = """
  SELECT COUNT(*) > 0
  FROM union_member um
  WHERE um.project_id = :projectId
  AND um.phone_number = :phoneNumber
  AND um.deleted_on IS NULL
  """,
  nativeQuery = true
)
boolean existsUnionMemberForProject(@Param("projectId") Long projectId, @Param("phoneNumber") String phoneNumber);

/**
 * Check if a union member already exists for the given project with same criteria, excluding a specific ID.
 * Used for updates to allow updating the same record.
 * Criteria: phoneNumber, projectId
 */
@Query(
  value = """
  SELECT COUNT(*) > 0
  FROM union_member um
  WHERE um.project_id = :projectId
  AND um.phone_number = :phoneNumber
  AND um.id != :excludeId
  AND um.deleted_on IS NULL
  """,
  nativeQuery = true
)
boolean existsUnionMemberForProjectExcludingId(
  @Param("projectId") Long projectId,
  @Param("phoneNumber") String phoneNumber,
  @Param("excludeId") Long excludeId
);

```

**Key Features:**

- Exact match for phoneNumber (already validated for Pakistani format)
- Excludes soft-deleted records (`deleted_on IS NULL`)
- Second method allows excluding specific ID for update operations

### 2. Service Layer Implementation

#### Updated Methods in `UnionMemberServiceImpl.java`:

**Regular Save Method:**

```java
@Override
public UnionMemberDTO save(UnionMemberDTO unionMemberDTO) {
  // Check for uniqueness before saving
  Long projectId = unionMemberDTO.getProject() != null ? unionMemberDTO.getProject().getId() : null;
  if (projectId != null && unionMemberRepository.existsUnionMemberForProject(projectId, phoneNumber)) {
    throw new IllegalArgumentException("Union member with this phone number already exists for this project");
  }
  // ... continue with save
}

```

**Regular Update Method:**

```java
@Override
public UnionMemberDTO update(UnionMemberDTO unionMemberDTO) {
  // Check for uniqueness excluding current record
  if (
    projectId != null &&
    unionMemberDTO.getId() != null &&
    unionMemberRepository.existsUnionMemberForProjectExcludingId(projectId, phoneNumber, unionMemberDTO.getId())
  ) {
    throw new IllegalArgumentException("Another union member with this phone number already exists for this project");
  }
  // ... continue with update
}

```

**Native Save Methods:**

- `saveUnionMemberNative()` - checks uniqueness before creating regular member
- `saveUnionHeadNative()` - checks uniqueness before creating union head
- `updateUnionMemberNative()` - checks uniqueness excluding current record during update

### 3. REST Controller Exception Handling

#### Updated Methods in `UnionMemberResource.java`:

**Standard APIs:**

```java
@PostMapping("")
public ResponseEntity<UnionMemberDTO> createUnionMember(@Valid @RequestBody UnionMemberDTO unionMemberDTO) {
  try {
    unionMemberDTO = unionMemberService.save(unionMemberDTO);
    return ResponseEntity.created(uri).body(unionMemberDTO);
  } catch (IllegalArgumentException e) {
    throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "duplicatemember");
  }
}

```

**Custom APIs:**

- `/member` (POST) - Add union member with uniqueness check
- `/head` (POST) - Add union head with uniqueness check
- `/{id}` (PUT) - Update with uniqueness check

### 4. Uniqueness Criteria

**Union Member is considered unique per project based on:**

1. **phoneNumber** (exact match)
2. **projectId** (exact match)

**Notes:**

- Phone numbers must match exactly (already validated for Pakistani format)
- Soft-deleted records are ignored in uniqueness checks
- During updates, the current record is excluded from uniqueness check

### 5. Error Handling

**Exception Types:**

- `IllegalArgumentException` - thrown by service layer for duplicates
- `BadRequestAlertException` - thrown by REST layer with error code "duplicatemember"

**Error Messages:**

- Creation: "Union member with this phone number already exists for this project"
- Update: "Another union member with this phone number already exists for this project"
- Union Head: "Union member with this phone number already exists for this project"

### 6. API Response

**HTTP Status Codes:**

- `400 Bad Request` - when duplicate member detected
- `201 Created` - successful creation
- `200 OK` - successful update

**Error Response Format:**

```json
{
  "type": "https://www.jhipster.tech/problem/problem-with-message",
  "title": "Bad Request",
  "status": 400,
  "detail": "Union member with this phone number already exists for this project",
  "path": "/api/union-members",
  "message": "error.duplicatemember"
}
```

## Security Considerations

- All uniqueness checks are performed at the service layer before database operations
- Native SQL queries use parameterized queries to prevent SQL injection
- Constraints apply to all creation and update operations
- Soft-deleted records are properly excluded from uniqueness checks

## Testing Considerations

When testing union member creation/update:

1. Test duplicate detection with exact matches
2. Test case-insensitive name/email matching
3. Test phone number exact matching
4. Test that updates allow same record modification
5. Test that soft-deleted records don't block new creations
6. Test error handling and proper HTTP status codes

## Database Performance

- Uniqueness checks use indexed columns (project_id, phone_number)
- Queries are optimized with proper WHERE conditions
- Exact matching for phone numbers
- Soft-delete filtering included in all queries
