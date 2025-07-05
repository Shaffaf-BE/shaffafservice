# Pakistani Phone Number Validation Implementation

## Overview

Implemented consistent Pakistani mobile phone number validation for Union Member entities, following the same pattern used in the Seller entity.

## Phone Number Format

- **Required Format:** `+923xxxxxxxxx`
- **Example:** `+923317898915`
- **Pattern:** `^\\+923[0-9]{9}$`
- **Total Length:** 13 characters

## Implementation Details

### 1. Validation Pattern

Uses the existing `PhoneNumberUtil` class which provides:

```java
public static final String PAKISTANI_MOBILE_REGEX = "^\\+923[0-9]{9}$";

public static final String INVALID_PHONE_ERROR_MESSAGE =
  "Phone number must be in format +923311234569 (Pakistani mobile number with +92 country code)";

```

### 2. Updated Files

#### UnionMemberDTO.java

- **Added Import:** `import com.shaffaf.shaffafservice.util.PhoneNumberUtil;`
- **Added Validation:**

```java
@NotNull
@Pattern(regexp = PhoneNumberUtil.PAKISTANI_MOBILE_REGEX, message = PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE)
private String phoneNumber;

```

#### API Documentation (UNION_MEMBER_APIS_DOCUMENTATION.md)

- Updated all phone number examples to use Pakistani format
- Added validation rules documentation
- Updated technical implementation section

#### Fake Data (union_member.csv)

- Replaced all fake phone numbers with valid Pakistani mobile numbers
- Used realistic Pakistani names and email addresses
- Maintained proper data structure with `is_union_head` column

#### Test Samples (UnionMemberTestSamples.java)

- Updated sample phone numbers to valid Pakistani format
- Modified random generator to produce valid Pakistani numbers
- Sample 1: `+923311234567`
- Sample 2: `+923327654321`
- Random generator: `+9233xxxxxxxx` with 8 random digits

## Validation Rules

### Format Requirements

1. **Prefix:** Must start with `+923`
2. **Country Code:** `+92` (Pakistan)
3. **Network Prefix:** `3` (Pakistani mobile networks)
4. **Number Length:** Exactly 9 digits after `+923`
5. **Total Length:** 13 characters including `+923`

### Error Handling

- **Client-side Validation:** Bean Validation `@Pattern` annotation
- **Error Message:** Clear description of expected format
- **API Response:** 400 Bad Request with validation error details

## Consistency with Seller Entity

The UnionMember phone number validation now matches exactly with the Seller entity:

- Same regex pattern from `PhoneNumberUtil.PAKISTANI_MOBILE_REGEX`
- Same error message from `PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE`
- Same validation approach using `@Pattern` annotation

## API Examples

### Valid Phone Numbers

- `+923311234567`
- `+923327654321`
- `+923339876543`
- `+923317898915`

### Invalid Phone Numbers

- `+1234567890` (Wrong country code)
- `923317898915` (Missing + sign)
- `+92317898915` (Missing mobile prefix 3)
- `+9233178989` (Too short)
- `+92331789891555` (Too long)

## Testing Impact

- All existing tests updated to use valid Pakistani phone numbers
- Random test data generator produces valid phone numbers
- Fake data for development uses realistic Pakistani mobile numbers

## Database Impact

- No schema changes required
- Existing data validation at application level
- Liquibase fake data updated with proper format

## Benefits

1. **Consistency:** Uniform phone number format across all entities
2. **Data Quality:** Ensures all phone numbers are valid Pakistani mobile numbers
3. **User Experience:** Clear error messages guide users to correct format
4. **Integration:** Easy integration with Pakistani telecom systems
5. **Validation:** Prevents invalid phone number data entry

## Future Considerations

- Can extend to support additional Pakistani number formats (landline, etc.)
- Can add phone number formatting utilities for display
- Can integrate with SMS/WhatsApp services using validated numbers
