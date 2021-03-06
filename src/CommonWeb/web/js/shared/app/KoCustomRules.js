/* An AMD defining and registering a set of custom knockout validation rules.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "underscore",
    "moment",
    "knockout",
    "knockout.validation",
    "shared/app/KoCustomUtils"
], function (_, moment, ko) {
    "use strict";

    ko.validation.rules.minDate = {
        validator: function (valueAccessor, options) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            var threshold = ko.utils.recursiveUnwrap(options.date);
            var format = options.format;
            return moment(value, format) >= moment(threshold, format);
        },
        message: function (options) {
            var date = moment(ko.utils.recursiveUnwrap(options.date)).format(options.format);
            return "Please select a date on or after " + date;
        }
    };

    ko.validation.rules.maxDate = {
        validator: function (valueAccessor, options) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            var threshold = ko.utils.recursiveUnwrap(options.date);
            var format = options.format;
            return moment(value, format) <= moment(threshold, format);
        },
        message: function (options) {
            var date = moment(ko.utils.recursiveUnwrap(options.date)).format(options.format);
            return "Please select a date on or before " + date;
        }
    };

    // Value must be strictly greater than (not equal to) the threshold, but the values are not required. The comparison
    // will only be performed if both values are defined, so 'required: true' must be explicitly used in the extend on
    // the observable field if desired.
    ko.validation.rules.customMin = {
        validator: function (valueAccessor, thresholdAccessor) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            var threshold = ko.utils.recursiveUnwrap(thresholdAccessor);
            return (value === "" || threshold === "") || (parseInt(value, 10) > parseInt(threshold, 10));
        },
        message: "Please enter a number greater than {0}."
    };

    // Value must be strictly less than the threshold, but the values are not required.
    ko.validation.rules.customMax = {
        validator: function (valueAccessor, thresholdAccessor) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            var threshold = ko.utils.recursiveUnwrap(thresholdAccessor);
            return (value === "" || threshold === "") || (parseInt(value, 10) < parseInt(threshold, 10));
        },
        message: "Please enter a number less than {0}."
    };

    // Adapted from:
    // https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#are-same
    ko.validation.rules.areSame = {
        validator: function (val, otherField) {
            return ko.utils.recursiveUnwrap(val) === ko.utils.recursiveUnwrap(otherField);
        },
        message: "Password fields must match"
    };

    // Must be different when converted to lower case.
    ko.validation.rules.emailChanged = {
        validator: function (val, otherField) {
            return ko.utils.recursiveUnwrap(val).toLowerCase() !== ko.utils.recursiveUnwrap(otherField).toLowerCase();
        },
        message: "New email address must be different from your current email address."
    };

    // Adapted from:
    // https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
    ko.validation.rules.passwordComplexity = {
        validator: function (val) {
            var pattern = /(?=^[^\s]{6,128}$)((?=.*?\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[^\w\d\s])(?=.*?[a-z])|(?=.*?[^\w\d\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[A-Z])(?=.*?[^\w\d\s]))^.*/; /* jshint ignore:line */ // Line length
            return pattern.test("" + val + "");
        },
        message: "Password must be between 6 and 128 characters long and contain three of the following 4 items: upper case letter, lower case letter, a symbol, a number" /* jshint ignore:line */ // Line length
    };

    // Adapted from:
    // https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
    ko.validation.rules.usernameComplexity = {
        validator: function (val) {
            return (new RegExp("^[a-z0-9_-]{3,15}$")).test("" + val + "");
        },
        message: "Username must be between 3 and 15 characters long and consist of only letters, numbers, '_' or '-'"
    };

    ko.validation.rules.startWith = {
        validator: function (value, other) {
            var theValue = ko.utils.recursiveUnwrap(value);
            var theOther = ko.utils.recursiveUnwrap(other);
            return theValue.indexOf(theOther) === 0;
        },
        message: "Must start with '{0}'"
    };

    ko.validation.rules.endWith = {
        validator: function (value, other) {
            var theValue = ko.utils.recursiveUnwrap(value);
            var theOther = ko.utils.recursiveUnwrap(other);
            return theValue.lastIndexOf(theOther) === (theValue.length - theOther.length);
        },
        message: "Must end with '{0}'"
    };

    ko.validation.rules.inList = {
        validator: function (value, other) {
            var theValue = ko.utils.recursiveUnwrap(value);
            var theOther = ko.utils.recursiveUnwrap(other);
            return _(theOther).contains(theValue);
        },
        message: "Must be one of: '{0}'"
    };

    ko.validation.rules.notContain = {
        validator: function (value, other) {
            var theValue = ko.utils.recursiveUnwrap(value);
            var theOther = ko.utils.recursiveUnwrap(other);
            if (_.isArray(theOther)) {
                return _(theOther).all(function (o) { return theValue.indexOf(o) === -1; });
            }
            return theValue.indexOf(theOther) === -1;
        },
        message: function (params) {
            var message = "Must not contain: ";
            if (_.isArray(params)) {
                var patterns = params.slice(0); // clone
                var last = patterns.pop();
                message = message + "'" + patterns.join("', '") + "' or '" + last + "'";
            } else {
                message = message + "'" + params + "'";
            }
            return message;
        }
    };

    ko.validation.rules.digit.message = "Please enter a whole number";

    ko.validation.rules.isUniqueProperty = {
        validator: function (val, options) {
            if (val) {
                var array = ko.utils.recursiveUnwrap(options.array);
                var id = ko.utils.recursiveUnwrap(options.id);
                var property = options.property;
                var caseInsensitive = options.caseInsensitive;

                // Operations on the comparison array:
                return ! _(array)
                    .chain()
                    // Remove myself
                    .filter(function (o) { return id === undefined || o.id !== id; })
                    // Select the property for comparison
                    .pluck(property)
                    // Unwrap if necessary
                    .map(ko.utils.recursiveUnwrap)
                    // Make lowercase if the operation is to be case-insensitive
                    .map(function (p) { return (p === undefined || !caseInsensitive) ? p : p.toLowerCase(); })
                    // Search for the input value
                    .contains(caseInsensitive ? val.toLowerCase(): val)
                    // Return true if found, otherwise false
                    .value();
            } else {
                return true;
            }
        },
        message: "Value must be unique"
    };
});
