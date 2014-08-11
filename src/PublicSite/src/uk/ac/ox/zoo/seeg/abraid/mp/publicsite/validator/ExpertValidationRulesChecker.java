package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for applying validation rules to experts (domain or dto).
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertValidationRulesChecker {
    // Regex from knockout.validation codebase https://github.com/Knockout-Contrib/Knockout-Validation/blob/4a0f89e6abf468e9ee9dc0d31d7303a40480a807/Src/rules.js#L183 ///CHECKSTYLE:SUPPRESS LineLengthCheck
    private static final Pattern EMAIL_REGEX = Pattern.compile("^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))$", Pattern.CASE_INSENSITIVE); ///CHECKSTYLE:SUPPRESS LineLengthCheck
    // Regex from http://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity ///CHECKSTYLE:SUPPRESS LineLengthCheck
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=^[^\\s]{6,128}$)((?=.*?\\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[^\\w\\d\\s])(?=.*?[a-z])|(?=.*?[^\\w\\d\\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\\d)(?=.*?[A-Z])(?=.*?[^\\w\\d\\s]))^.*$"); ///CHECKSTYLE:SUPPRESS LineLengthCheck

    private static final int MAX_NAME_LENGTH = 1000;
    private static final int MAX_JOB_TITLE_LENGTH = 100;
    private static final int MAX_INSTITUTION_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 320;

    private static final String NAME_FIELD_NAME = "Name";
    private static final String EMAIL_ADDRESS_FIELD_NAME = "Email address";
    private static final String INSTITUTION_FIELD_NAME = "Institution";
    private static final String JOB_TITLE_FIELD_NAME = "Job title";
    private static final String PASSWORD_FIELD_NAME = "Password";
    private static final String DISEASE_INTERESTS_FIELD_NAME = "Disease interests";
    private static final String VISIBILITY_REQUESTED_FIELD_NAME = "Visibility requested";

    private static final String FAILURE_STRING_LENGTH = "%s must less than %s letters in length.";
    private static final String FAILURE_VALUE_MISSING = "%s must be provided.";

    private static final String FAILURE_INSUFFICIENT_COMPLEXITY = "%s not sufficiently complex.";
    private static final String FAILURE_INVALID_VALUE = "%s not valid.";
    private static final String FAILURE_ALREADY_EXISTS = "%s already has an associated account.";

    private final ExpertService expertService;

    @Autowired
    public ExpertValidationRulesChecker(ExpertService expertService) {
        this.expertService = expertService;
    }

    /**
     * Validates an email address, with the following conditions.
     *  * Not null
     *  * Not empty
     *  * Be a valid email address
     *  * Not already be in use
     * @param value The email address to validate.
     * @param validationFailures A list of validation failures.
     */
    public void checkEmail(String value, List<String> validationFailures) {
        validateString(EMAIL_ADDRESS_FIELD_NAME, value, MAX_EMAIL_LENGTH, validationFailures);

        if (validationFailures.isEmpty()) {
            if (!EMAIL_REGEX.matcher(value).matches()) {
                validationFailures.add(String.format(FAILURE_INVALID_VALUE, EMAIL_ADDRESS_FIELD_NAME));
            }

            if (expertService.getExpertByEmail(value) != null) {
                validationFailures.add(String.format(FAILURE_ALREADY_EXISTS, EMAIL_ADDRESS_FIELD_NAME));
            }
        }
    }

    /**
     * Validates a password, with the following conditions.
     *  * Not null
     *  * Not empty
     *  * Be sufficiently complex
     * @param value The password to validate.
     * @param validationFailures A list of validation failures.
     */
    public void checkPassword(String value, List<String> validationFailures) {
        if (StringUtils.isEmpty(value)) {
            validationFailures.add(String.format(FAILURE_VALUE_MISSING, PASSWORD_FIELD_NAME));
        }

        if (validationFailures.isEmpty() && !PASSWORD_REGEX.matcher(value).matches()) {
            validationFailures.add(String.format(FAILURE_INSUFFICIENT_COMPLEXITY, PASSWORD_FIELD_NAME));
        }
    }

    /**
     * Validates an institution, with the following conditions.
     *  * Not null
     *  * Not empty
     * @param value The institution to validate.
     * @param validationFailures A list of validation failures.
     */
    public void checkInstitution(String value, List<String> validationFailures) {
        validateString(INSTITUTION_FIELD_NAME, value, MAX_INSTITUTION_LENGTH, validationFailures);
    }

    /**
     * Validates a job title, with the following conditions.
     *  * Not null
     *  * Not empty
     * @param value The job title to validate.
     * @param validationFailures A list of validation failures.
     */
    public void checkJobTitle(String value, List<String> validationFailures) {
        validateString(JOB_TITLE_FIELD_NAME, value, MAX_JOB_TITLE_LENGTH, validationFailures);
    }

    /**
     * Validates a name, with the following conditions.
     *  * Not null
     *  * Not empty
     * @param value The name to validate.
     * @param validationFailures A list of validation failures.
     */
    public void checkName(String value, List<String> validationFailures) {
        validateString(NAME_FIELD_NAME, value, MAX_NAME_LENGTH, validationFailures);
    }

    /**
     * Validates visibility requested, with the following conditions.
     *  * Not null
     * @param value The value to validate.
     * @param validationFailures A list of validation failures.
     */
    public void checkVisibilityRequested(Boolean value, List<String> validationFailures) {
        validateNotNull(VISIBILITY_REQUESTED_FIELD_NAME, value, validationFailures);
    }

    /**
     * Validates list of disease interests, with the following conditions.
     *  * Not null
     * @param value The value to validate.
     * @param validationFailures A list of validation failures.
     */
    public void checkDiseaseInterests(List<Integer> value, List<String> validationFailures) {
        validateNotNull(DISEASE_INTERESTS_FIELD_NAME, value, validationFailures);
    }

    private static void validateNotNull(String name, Object value, List<String> validationFailures) {
        if (value == null) {
            validationFailures.add(String.format(FAILURE_VALUE_MISSING, name));
        }
    }

    private static void validateString(String name, String value, int maxLength, List<String> validationFailures) {
        if (StringUtils.isEmpty(value)) {
            validationFailures.add(String.format(FAILURE_VALUE_MISSING, name));
        }

        if (value != null && value.length() > maxLength) {
            validationFailures.add(String.format(FAILURE_STRING_LENGTH, name, maxLength));
        }
    }
}
