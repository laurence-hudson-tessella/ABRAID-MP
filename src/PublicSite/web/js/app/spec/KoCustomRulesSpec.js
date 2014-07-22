define([
    "ko",
    "domReady!",
    "app/spec/lib/squire"
], function (ko, doc, Squire) {
    "use strict";

    describe("KoCustomRules defines", function () {
        describe("the 'areSame' rule which", function () {
            it("checks the two values are the same", function () {
                expect(ko.validation.rules.areSame.validator(1, 1)).toBe(true);
                expect(ko.validation.rules.areSame.validator(1, 2)).toBe(false);
            });

            it("can validated wrapped values", function () {
                var wrap = function (value) {
                    return function () {
                        return value;
                    };
                };

                expect(ko.validation.rules.areSame.validator(wrap(1), wrap(wrap(1)))).toBe(true);
                expect(ko.validation.rules.areSame.validator(wrap(1), wrap(wrap(2)))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.areSame.message).toBe("Password fields must match");
            });
        });

        describe("the 'passwordComplexity' rule which", function () {
            it("rejects passwords below 6 chars", function () {
                expect(ko.validation.rules.passwordComplexity.validator("aBc12")).toBe(false);
            });

            it("rejects passwords over 128 chars", function () {
                var longString = (new Array(33)).join("aA*1");
                expect(longString.length).toBe(128);
                expect(ko.validation.rules.passwordComplexity.validator(longString + "a")).toBe(false);
            });

            it("rejects passwords without sufficient distinct char classes", function () {
                expect(ko.validation.rules.passwordComplexity.validator("abc123q")).toBe(false);
                expect(ko.validation.rules.passwordComplexity.validator("abc*&^q")).toBe(false);
                expect(ko.validation.rules.passwordComplexity.validator("ABC*&^Q")).toBe(false);
            });

            it("accepts complex passwords", function () {
                expect(ko.validation.rules.passwordComplexity.validator("qwe123Q")).toBe(true);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.passwordComplexity.message)
                    .toContain("Password must be between 6 and 128 characters long and contain three of the following");
            });
        });
    });
});

