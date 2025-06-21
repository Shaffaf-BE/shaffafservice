# Phone Number Validation Utility

## Overview

The `PhoneNumberUtil` class provides centralized phone number validation and formatting for Pakistani mobile numbers throughout the Shaffaf application. This utility enforces the standard format `+923xxxxxxxxx` across all components.

## Usage Examples

### 1. Basic Validation

```java
import com.shaffaf.shaffafservice.util.PhoneNumberUtil;

// Validate a phone number
String phoneNumber = "+923311234567";
if (PhoneNumberUtil.isValidPakistaniMobile(phoneNumber)) {
    // Phone number is valid
    System.out.println("Valid phone number");
}

// For authentication context
if (PhoneNumberUtil.isValidForAuthentication(phoneNumber)) {
    // Can be used for login
}
```

### 2. Validation with Exception Handling

```java
try {
    PhoneNumberUtil.validatePakistaniMobile(phoneNumber, "User Phone");
    // Validation passed
} catch (IllegalArgumentException e) {
    // Handle validation error
    System.err.println(e.getMessage());
}
```

### 3. In REST Controllers

```java
@RestController
public class UserController {

  @PostMapping("/users")
  public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
    // Validate phone number
    if (!PhoneNumberUtil.isValidPakistaniMobile(userDTO.getPhoneNumber())) {
      throw new BadRequestAlertException(PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE, "user", "invalidphone");
    }
    // Continue with user creation
  }
}

```

### 4. In DTOs with Bean Validation

```java
public class UserDTO {

  @NotNull
  @Pattern(regexp = PhoneNumberUtil.PAKISTANI_MOBILE_REGEX, message = PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE)
  private String phoneNumber;
  // ... other fields
}

```

### 5. In Service Classes

```java
@Service
public class UserService {

  public User createUser(UserDTO userDTO) {
    // Format and validate phone number
    String formattedPhone = PhoneNumberUtil.formatPakistaniMobile(userDTO.getPhoneNumber());

    User user = new User();
    user.setPhoneNumber(formattedPhone);
    // ... continue with user creation
  }
}

```

### 6. In Custom Validators

```java
@Component
public class PhoneNumberValidator implements ConstraintValidator<ValidPhone, String> {

  @Override
  public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
    return PhoneNumberUtil.isValidPakistaniMobile(phoneNumber);
  }
}

```

## Constants and Utilities

### Available Constants

```java
// Regex pattern for validation
PhoneNumberUtil.PAKISTANI_MOBILE_REGEX

// Example valid phone number
PhoneNumberUtil.EXAMPLE_PHONE_NUMBER  // "+923311234567"

// Standard error message
PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE
```

### Utility Methods

```java
// Get regex pattern (useful for annotations)
String regex = PhoneNumberUtil.getValidationRegex();

// Get format description
String description = PhoneNumberUtil.getFormatDescription();

```

## Migration Guide

### Replacing Existing Validation

**Before:**

```java
// Old inline validation
if (!phoneNumber.matches("^\\+923[0-9]{9}$")) {
    throw new BadRequestAlertException("Invalid phone format", entity, "invalidphone");
}
```

**After:**

```java
// Using utility
if (!PhoneNumberUtil.isValidPakistaniMobile(phoneNumber)) {
    throw new BadRequestAlertException(
        PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE,
        entity,
        "invalidphone"
    );
}
```

### Updating DTOs

**Before:**

```java
@Pattern(regexp = "^\\+923[0-9]{9}$", message = "Phone number must be in format +923311234569")
private String phoneNumber;

```

**After:**

```java
@Pattern(regexp = PhoneNumberUtil.PAKISTANI_MOBILE_REGEX, message = PhoneNumberUtil.INVALID_PHONE_ERROR_MESSAGE)
private String phoneNumber;

```

## Benefits

1. **Centralized Logic**: Single source of truth for phone number validation
2. **Consistency**: Same validation rules across all components
3. **Maintainability**: Easy to update validation rules in one place
4. **Reusability**: Can be used in controllers, services, DTOs, and validators
5. **Performance**: Compiled regex pattern for better performance
6. **Documentation**: Clear constants and method names
7. **Testing**: Comprehensive unit tests ensure reliability

## Best Practices

1. **Always use the utility** instead of inline regex patterns
2. **Use appropriate method names** (e.g., `isValidForAuthentication` for login contexts)
3. **Leverage constants** for error messages and examples
4. **Handle exceptions** properly when using validation methods
5. **Add the import** `import com.shaffaf.shaffafservice.util.PhoneNumberUtil;` to use the utility

## Format Specification

- **Required Format**: `+923xxxxxxxxx`
- **Country Code**: `+92` (Pakistan)
- **Mobile Prefix**: `3` (Pakistani mobile networks)
- **Length**: 13 characters total
- **Examples**: `+923001234567`, `+923111234567`, `+923451234567`

## Common Integration Points

1. **Authentication**: User login with phone number
2. **User Registration**: Validating user phone numbers
3. **Contact Information**: Validating contact details in various entities
4. **API Endpoints**: Request validation in REST controllers
5. **Database Constraints**: Ensuring data integrity
6. **Frontend Validation**: Consistent validation rules between backend and frontend
