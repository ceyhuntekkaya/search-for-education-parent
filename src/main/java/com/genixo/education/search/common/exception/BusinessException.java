package com.genixo.education.search.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.ArrayList;

/**
 * Exception thrown when business logic rules are violated
 * This exception results in HTTP 400 Bad Request response
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    private String errorCode;
    private List<String> details;
    private Object data;

    /**
     * Constructor with simple message
     */
    public BusinessException(String message) {
        super(message);
        this.details = new ArrayList<>();
    }

    /**
     * Constructor with message and cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.details = new ArrayList<>();
    }

    /**
     * Constructor with message and error code
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = new ArrayList<>();
    }

    /**
     * Constructor with message, error code and details
     */
    public BusinessException(String message, String errorCode, List<String> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details != null ? details : new ArrayList<>();
    }

    /**
     * Constructor with message and data
     */
    public BusinessException(String message, Object data) {
        super(message);
        this.data = data;
        this.details = new ArrayList<>();
    }

    /**
     * Add detail to the exception
     */
    public BusinessException addDetail(String detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
        return this;
    }

    /**
     * Set error code
     */
    public BusinessException withErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * Set data
     */
    public BusinessException withData(Object data) {
        this.data = data;
        return this;
    }

    // Getters
    public String getErrorCode() {
        return errorCode;
    }

    public List<String> getDetails() {
        return details;
    }

    public Object getData() {
        return data;
    }

    // Static factory methods for common business exceptions

    /**
     * Access denied exception
     */
    public static BusinessException accessDenied(String resource) {
        return new BusinessException("Access denied to " + resource, "ACCESS_DENIED");
    }

    /**
     * Permission required exception
     */
    public static BusinessException permissionRequired(String permission) {
        return new BusinessException("Permission required: " + permission, "PERMISSION_REQUIRED");
    }

    /**
     * Invalid operation exception
     */
    public static BusinessException invalidOperation(String operation) {
        return new BusinessException("Invalid operation: " + operation, "INVALID_OPERATION");
    }

    /**
     * Duplicate resource exception
     */
    public static BusinessException duplicateResource(String resourceName, String fieldName, Object value) {
        return new BusinessException(
                String.format("%s already exists with %s: '%s'", resourceName, fieldName, value),
                "DUPLICATE_RESOURCE"
        );
    }

    /**
     * Invalid state exception
     */
    public static BusinessException invalidState(String currentState, String operation) {
        return new BusinessException(
                String.format("Cannot perform '%s' operation in current state: %s", operation, currentState),
                "INVALID_STATE"
        );
    }

    /**
     * Validation failed exception
     */
    public static BusinessException validationFailed(List<String> validationErrors) {
        return new BusinessException("Validation failed", "VALIDATION_FAILED", validationErrors);
    }

    /**
     * Resource in use exception
     */
    public static BusinessException resourceInUse(String resourceName, String dependency) {
        return new BusinessException(
                String.format("Cannot delete %s because it has associated %s", resourceName, dependency),
                "RESOURCE_IN_USE"
        );
    }

    /**
     * Quota exceeded exception
     */
    public static BusinessException quotaExceeded(String resource, int current, int limit) {
        return new BusinessException(
                String.format("Quota exceeded for %s. Current: %d, Limit: %d", resource, current, limit),
                "QUOTA_EXCEEDED"
        );
    }

    /**
     * Subscription required exception
     */
    public static BusinessException subscriptionRequired(String feature) {
        return new BusinessException(
                "Active subscription required to access: " + feature,
                "SUBSCRIPTION_REQUIRED"
        );
    }

    /**
     * Configuration missing exception
     */
    public static BusinessException configurationMissing(String configKey) {
        return new BusinessException(
                "Required configuration missing: " + configKey,
                "CONFIGURATION_MISSING"
        );
    }

    /**
     * Service unavailable exception
     */
    public static BusinessException serviceUnavailable(String service) {
        return new BusinessException(
                "Service temporarily unavailable: " + service,
                "SERVICE_UNAVAILABLE"
        );
    }
}