# SELLER-BASED UNION MEMBER FILTERING IMPLEMENTATION

## Overview

Implemented role-based access control for Union Member APIs to ensure sellers can only access union members from projects they own, while admins retain full access.

## Problem

Previously, both admins and sellers had unrestricted access to all union members across all projects. This violated data security principles as sellers should only see union members from their own projects.

## Solution

### 1. Enhanced Service Layer

#### New Service Methods

Added seller-aware methods to `UnionMemberService` interface:

```java
/**
 * Get all union members for a specific project with seller filtering.
 */
Page<UnionMemberDTO> findUnionMembersByProjectNativeFiltered(Long projectId, Pageable pageable, String currentUserLogin);

/**
 * Get all union members with seller filtering.
 */
Page<UnionMemberDTO> findAllUnionMembersNativeFiltered(Pageable pageable, String currentUserLogin);

```

#### Implementation Logic

In `UnionMemberServiceImpl`:

1. **Role Detection**: Uses `SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SELLER)` to detect seller users
2. **Seller Lookup**: Finds seller by phone number using `sellerRepository.findByPhoneNumber(currentUserLogin)`
3. **Project Ownership Validation**: For project-specific queries, validates seller owns the requested project
4. **Data Filtering**: Uses seller-specific repository queries to return only relevant union members

### 2. Enhanced Repository Layer

#### New Repository Methods

Added to `UnionMemberRepository`:

```java
/**
 * Check if a project is owned by a specific seller.
 */
boolean isProjectOwnedBySeller(@Param("projectId") Long projectId, @Param("sellerId") Long sellerId);

/**
 * Find all union members by seller's projects using native SQL.
 */
Page<Object[]> findAllUnionMembersNativeBySellerProjects(
  @Param("sellerId") Long sellerId,
  @Param("sortBy") String sortBy,
  @Param("sortDirection") String sortDirection,
  Pageable pageable
);

```

#### SQL Implementation

- **Project Ownership Check**: Verifies `project.seller_id = :sellerId`
- **Union Member Filtering**: Uses `INNER JOIN` with project table to filter by seller ownership
- **Soft Delete Awareness**: Excludes deleted projects and union members

### 3. Updated Controller Layer

#### Enhanced Endpoints

**getUnionMembersByProject** (`/api/union-members/v1/project/{projectId}`):

- Now validates seller has access to the specific project
- Returns 400 Bad Request with "accessdenied" error key if unauthorized
- Admins retain unrestricted access

**getAllUnionMembersNative** (`/api/union-members/v1/native`):

- Sellers see only union members from their owned projects
- Admins see all union members across all projects
- Maintains pagination and sorting functionality

### 4. Security Implementation

#### Authorization Flow

1. **Authentication**: Extract current user login from security context
2. **Role Check**: Determine if user is SELLER or ADMIN
3. **Seller Validation**: For sellers, lookup seller record by phone number
4. **Access Control**: Apply appropriate filtering based on role
5. **Error Handling**: Return user-friendly error messages for access violations

#### Error Scenarios

- **Seller Not Found**: Returns "Seller not found" error
- **Unauthorized Project Access**: Returns "Access denied: You don't have permission to view union members for this project"
- **Invalid Authentication**: Returns "Current user login not found"

## Files Modified

### Service Layer

- `UnionMemberService.java` - Added seller-filtered method signatures
- `UnionMemberServiceImpl.java` - Implemented seller-aware filtering logic

### Repository Layer

- `UnionMemberRepository.java` - Added project ownership check and seller-filtered queries

### Controller Layer

- `UnionMemberResource.java` - Updated endpoints to use filtered methods

### Tests

- `UnionMemberServiceImplMappingTest.java` - Updated constructor call for new dependency

## Database Schema Dependencies

### Required Relationships

- `project.seller_id` → `seller.id` (Foreign Key)
- `union_member.project_id` → `project.id` (Foreign Key)

### Key Columns Used

- `seller.phone_number` - Used for seller lookup via login
- `project.seller_id` - Used for ownership validation
- `project.deleted_date` - Soft delete filtering
- `union_member.deleted_on` - Soft delete filtering

## Security Benefits

1. **Data Isolation**: Sellers can only access their own project data
2. **Principle of Least Privilege**: Users see only what they need to see
3. **Audit Trail**: All access attempts are logged with user context
4. **Role-Based Access**: Different behavior for different user roles
5. **Error Transparency**: Clear error messages for unauthorized access

## API Behavior

### For Admin Users

- **getUnionMembersByProject**: Returns union members for any project
- **getAllUnionMembersNative**: Returns all union members across all projects

### For Seller Users

- **getUnionMembersByProject**: Returns union members only if seller owns the project
- **getAllUnionMembersNative**: Returns union members only from seller's owned projects

### Response Codes

- **200 OK**: Successful data retrieval
- **400 Bad Request**: Invalid parameters or access denied
- **404 Not Found**: Project or seller not found

## Testing

- All existing unit tests updated and passing
- Constructor dependency injection updated for new SellerRepository parameter
- Error handling paths covered in service layer

This implementation ensures data security while maintaining API functionality and providing clear error messages for unauthorized access attempts.
