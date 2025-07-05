# UnionMember IsUnionHead Field Implementation

## Overview

Added a new boolean field `isUnionHead` to the UnionMember entity to identify which union members serve as union heads.

## Changes Made

### 1. Entity Layer (`UnionMember.java`)

- Added `isUnionHead` field as `Boolean` type with default value `false`
- Added JPA column annotation: `@Column(name = "is_union_head", nullable = false)`
- Added getter, setter, and fluent setter methods
- Updated `toString()` method to include the new field

### 2. DTO Layer (`UnionMemberDTO.java`)

- Added `isUnionHead` field with default value `false`
- Added getter and setter methods
- Updated `toString()` method to include the new field

### 3. Database Migration

- Created Liquibase changeset: `20250705000001_add_is_union_head_to_union_member.xml`
- Adds `is_union_head` boolean column with default value `false` and NOT NULL constraint
- Includes database comment explaining the field purpose
- Creates index `idx_union_member_is_union_head` for query performance
- Updated `master.xml` to include the new changeset

### 4. Repository Layer (`UnionMemberRepository.java`)

Added convenience query methods:

- `findByIsUnionHeadTrue()` - Find all union heads
- `findByIsUnionHeadFalse()` - Find all non-union head members
- `findByProjectIdAndIsUnionHead(Long projectId, Boolean isUnionHead)` - Find by project and head status

### 5. Test Support

- Updated `UnionMemberTestSamples.java` to include `isUnionHead` in test data
- Updated `UnionMemberAsserts.java` to verify the field in assertions
- Updated fake data CSV to include the new column with sample values

### 6. Mapper Support

- No changes needed to `UnionMemberMapper.java` - MapStruct automatically handles the new field

## Database Schema

```sql
ALTER TABLE union_member
ADD COLUMN is_union_head BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN union_member.is_union_head IS 'Indicates whether this union member is the head of the union';

CREATE INDEX idx_union_member_is_union_head ON union_member(is_union_head);
```

## Usage Examples

### Creating a Union Head

```java
UnionMember unionHead = new UnionMember()
  .firstName("John")
  .lastName("Doe")
  .email("john.doe@example.com")
  .phoneNumber("1234567890")
  .isUnionHead(true);

```

### Querying Union Heads

```java
// Find all union heads
List<UnionMember> unionHeads = unionMemberRepository.findByIsUnionHeadTrue();

// Find union head for a specific project
List<UnionMember> projectHeads = unionMemberRepository.findByProjectIdAndIsUnionHead(projectId, true);

```

## Validation

- All changes compile without errors
- Liquibase migration is properly formatted and included in master.xml
- Test support is updated and functional
- Repository query methods follow Spring Data JPA conventions

## Migration Notes

- The field defaults to `false` for all existing records
- An index is created for efficient querying
- The field is NOT NULL to ensure data consistency
- Backward compatibility is maintained with existing code
