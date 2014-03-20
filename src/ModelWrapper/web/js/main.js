/**
 * JS file for the model wrapper.
 * Copyright (c) 2014 University of Oxford
 */
(function (document, baseURL, ko, $) {
    (function configureKnockoutValidation() {
        // Adapted from https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#are-same
        ko.validation.rules.areSame = {
            getValue: function (o) {
                return (typeof o === 'function' ? o() : o);
            },
            validator: function (val, otherField) {
                return val === this.getValue(otherField);
            },
            message: "Password fields must match"
        };

        // Adapted from https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
        ko.validation.rules.passwordComplexity = {
            validator: function (val) {
                return /(?=^[^\s]{6,128}$)((?=.*?\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[^\w\d\s])(?=.*?[a-z])|(?=.*?[^\w\d\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[A-Z])(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
            },
            message: 'Password must be between 6 and 128 characters long and contain three of the following 4 items: upper case letter, lower case letter, a symbol, a number'
        };

        // Adapted from https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
        ko.validation.rules.usernameComplexity = {
            validator: function (val) {
                return /^[a-z0-9_-]{3,15}$/.test('' + val + '');
            },
            message: 'Username must be between 3 and 15 characters long and consist of only letters, numbers, "_" or "-"'
        };

        ko.validation.configure({
            insertMessages: true,
            messageTemplate: 'validation-template',
            messagesOnModified: true,
            registerExtenders: true
        });
    }());

    var authViewModel = ko.validatedObservable((function () {
        var username = ko.observable().extend({ required: true,  usernameComplexity: true });
        var password = ko.observable().extend({ required: true, passwordComplexity: true });
        var passwordConfirmation = ko.observable().extend({ required: true, passwordComplexity: true, areSame: password });
        var saving = ko.observable(false);
        var notices = ko.observableArray();
        var submit = function () {
            notices.removeAll();
            if (this.isValid()) {
                this.saving(true);
                this.notices.removeAll();
                $.post(baseURL + "auth", { username: this.username(), password: this.password(), passwordConfirmation: this.passwordConfirmation() })
                    .done(function () { notices.push({ 'message': "Saved successfully.", 'priority': 'success'}); })
                    .fail(function () { notices.push({ 'message': "Authentication details could not be saved.", 'priority': 'warning'}); })
                    .always(function () { saving(false); });
            } else {
                this.notices.push({ message: "All field must be valid before saving.", priority: 'warning'});
            }
        };

        return {
            username: username,
            password: password,
            passwordConfirmation: passwordConfirmation,
            saving: saving,
            notices: notices,
            submit: submit
        };
    }()));

    ko.applyBindings(authViewModel, document.getElementById("auth-body"));
}(document, baseURL, ko, $));