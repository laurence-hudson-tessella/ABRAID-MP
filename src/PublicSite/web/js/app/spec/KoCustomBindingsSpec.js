define([
    "ko",
    "domReady!",
    "app/spec/lib/squire"
], function (ko, doc, Squire) {
    "use strict";

    describe("KoCustomBindings defines", function () {
        describe("the 'option' binding which", function () {
            it("writes a value to the given element", function () {
                // Arrange
                var element = {};
                var value = "value";
                // Act
                ko.bindingHandlers.option.update(element, value);
                // Assert
                expect(ko.selectExtensions.readValue(element)).toBe(value);
            });

            it("writes a wrapped value to the given element", function () {
                // Arrange
                var element = {};
                var value = "value";
                var wrappedValue = function () { return value; };
                // Act
                ko.bindingHandlers.option.update(element, wrappedValue);
                // Assert
                expect(ko.selectExtensions.readValue(element)).toBe(value);
            });
        });

        describe("the 'fadeVisible' binding which", function () {
            var showSpy, delaySpy, fadeSpy, jqSpy, injectorWithJQuerySpy;
            var expectedElement = "expectedElement";
            beforeEach(function () {
                showSpy = jasmine.createSpy();
                fadeSpy = jasmine.createSpy();
                delaySpy = jasmine.createSpy().and.returnValue({ fadeOut: fadeSpy });
                var noop = function () {};

                jqSpy = jasmine.createSpy().and.callFake(function (arg) {
                    return (arg === expectedElement) ?
                    { show: showSpy, delay: delaySpy } :
                    { show: noop, delay: noop };
                });

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                injectorWithJQuerySpy = new Squire();

                injectorWithJQuerySpy.mock("jquery", jqSpy);
            });

            describe("shows the element,", function () {
                it("when taking an unwrapped 'true' value", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: true });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(showSpy).toHaveBeenCalled();
                        done();
                    });
                });

                it("when taking a wrapped 'true' value", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, function () { return {visible: true}; });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(showSpy).toHaveBeenCalled();
                        done();
                    });
                });
            });

            describe("fades out the element on a 'false' value", function () {
                it("after a 500 ms delay", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: false });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(delaySpy).toHaveBeenCalledWith(500);
                        done();
                    });
                });

                it("with the specified duration, when provided", function (done) {
                    // Arrange
                    var duration = 54321;

                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: false, duration: duration });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(fadeSpy).toHaveBeenCalledWith(duration);
                        done();
                    });
                });

                it("with the correct default duration otherwise", function (done) {
                    var defaultDuration = 1000;
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: false });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(fadeSpy).toHaveBeenCalledWith(defaultDuration);
                        done();
                    });
                });
            });
        });

        describe("the 'date' binding, which", function () {
            var textSpy, jqSpy, injectorWithJQuerySpy, momentSpy;
            var expectedElement = "expectedElement";
            var expectedText = "expectedText";
            beforeEach(function () {
                textSpy = jasmine.createSpy();
                momentSpy =  jasmine.createSpy().and.returnValue({ lang: function () {
                    return { format: function () { return expectedText; }};
                } });
                jqSpy = jasmine.createSpy().and.returnValue({ text: textSpy });

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                injectorWithJQuerySpy = new Squire();

                injectorWithJQuerySpy.mock("jquery", jqSpy);
                injectorWithJQuerySpy.mock("moment", momentSpy);
            });

            it("adds text to the element", function (done) {
                // Arrange
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    var expectedDate = "expectedDate";
                    ko.bindingHandlers.date.update(expectedElement, expectedDate);
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(momentSpy).toHaveBeenCalledWith(expectedDate);
                    expect(textSpy).toHaveBeenCalledWith(expectedText);
                    done();
                });
            });
        });

        describe("the 'highlight' binding, which", function () {
            // Arrange
            var expectedElement, removeSpy, addSpy, jqSpy, injectorWithJQuerySpy;
            beforeEach(function () {
                removeSpy = jasmine.createSpy();
                addSpy = jasmine.createSpy();
                jqSpy = jasmine.createSpy().and.returnValue({removeClass: removeSpy, addClass: addSpy});

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                injectorWithJQuerySpy = new Squire();

                injectorWithJQuerySpy.mock("jquery", jqSpy);
            });

            it("adds the css class to the selected admin unit", function (done) {
                // Arrange
                var expectedAdminUnit = { id: "0" };
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    ko.bindingHandlers.highlight.update(expectedElement, { target: expectedAdminUnit, compareOn: "id" },
                        {}, {}, { "$data": expectedAdminUnit });
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(removeSpy).toHaveBeenCalledWith("highlight");
                    expect(addSpy).toHaveBeenCalledWith("highlight");
                    done();
                });
            });

            it("does not add the css class to any other admin unit", function (done) {
                // Arrange
                var expectedAdminUnit = { id: 0 };
                var anotherAdminUnit = { id: 1 };
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    ko.bindingHandlers.highlight.update(expectedElement, { target: expectedAdminUnit, compareOn: "id" },
                        {}, {}, { "$data": anotherAdminUnit });
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(removeSpy).toHaveBeenCalledWith("highlight");
                    expect(addSpy).not.toHaveBeenCalled();
                    done();
                });
            });

            it("does not add the css class when target is null", function (done) {
                // Arrange
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    ko.bindingHandlers.highlight.update(expectedElement, { target: null, compareOn: "id" },
                        {}, {}, { "$data": {} });
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(removeSpy).toHaveBeenCalledWith("highlight");
                    expect(addSpy).not.toHaveBeenCalled();
                    done();
                });
            });
        });

        describe("multiple composite bindings, including", function () {
            var findBuilder = function (valueForValid, valueForSubmitting) {
                return function (arg) {
                    if ("isSubmitting" === arg) {
                        return valueForSubmitting;
                    }

                    if ("isValid" === arg) {
                        return valueForValid;
                    }

                    return false;
                };
            };

            describe("the 'bootstrapDisable' binding, which", function () {
                ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                var element = "1234";
                var accessor = function () { return true; };
                ko.bindingHandlers.bootstrapDisable.init(element, accessor);
                var subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies an 'enable' binding with a negated version of the value accessor", function () {
                    expect(subBindings.enable).toBeDefined();
                    expect(typeof subBindings.enable).toBe("function");
                    expect(subBindings.enable()).toBe(!accessor());
                });

                it("applies a 'css' binding with a value accessor enabling the disabled class by the parent accessor",
                    function () {
                        expect(subBindings.css).toBeDefined();
                        expect(typeof subBindings.css).toBe("function");
                        expect(subBindings.css().disabled).toBeDefined();
                        expect(subBindings.css().disabled).toBe(accessor());
                    }
                );
            });

            describe("the 'formSubmit' binding, which", function () {
                var context, subBindings, submitFunction;
                var element = "1234";
                var accessor = function () { return submitFunction; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    submitFunction = jasmine.createSpy("submitFunction");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formSubmit.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });


                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });


                describe("applies a 'submit' binding with a wrapped version of the value accessor, which", function () {
                    it("is fired for valid and non-submitting forms", function () {
                        expect(subBindings.submit).toBeDefined();
                        expect(typeof subBindings.submit).toBe("function");
                        expect(typeof subBindings.submit()).toBe("function");

                        context.find = findBuilder(true, false);
                        subBindings.submit()();
                        expect(submitFunction).toHaveBeenCalledWith(element);
                    });

                    it("applies a 'submit' binding which only fires for valid forms", function () {
                        context.find = findBuilder(false, false);
                        subBindings.submit()();
                        expect(submitFunction).not.toHaveBeenCalled();
                    });

                    it("applies a 'submit' binding which only fires for non-submitting forms", function () {
                        context.find = findBuilder(true, true);
                        subBindings.submit()();
                        expect(submitFunction).not.toHaveBeenCalled();
                    });
                });
            });

            describe("the 'formButton' binding, which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { submitting: "submitting", standard: "standard" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formButton.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies a 'bootstrapDisable' binding with a validity/submitting based accessor", function () {
                    expect(subBindings.bootstrapDisable).toBeDefined();
                    expect(typeof subBindings.bootstrapDisable).toBe("function");

                    context.find = findBuilder(true, false); // valid & not submitting
                    expect(subBindings.bootstrapDisable()).toBe(false);     // enabled

                    context.find = findBuilder(true, true);  // valid & submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);      // disabled

                    context.find = findBuilder(false, false); // invalid & not submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);       // disabled

                    context.find = findBuilder(false, true);  // invalid & submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);       // disabled
                });

                it("applies a 'text' binding with a submitting based accessor, using the values from parent accessor",
                    function () {
                        expect(subBindings.text).toBeDefined();
                        expect(typeof subBindings.text).toBe("function");

                        context.find = findBuilder(true, false); // not submitting
                        expect(subBindings.text()).toBe(accessor().standard);

                        context.find = findBuilder(true, true);  // submitting
                        expect(subBindings.text()).toBe(accessor().submitting);
                    }
                );
            });

            describe("the 'formChecked' binding, which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { checked: "1234", value: "4321" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formChecked.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies a 'checked' binding with the checked sub-accessor", function () {
                    expect(subBindings.checked).toBeDefined();
                    expect(typeof subBindings.checked).toBe("function");
                    expect(subBindings.checked()).toBe(accessor().checked);
                });

                it("applies a 'checkedValue' binding with the value sub-accessor", function () {
                    expect(subBindings.checkedValue).toBeDefined();
                    expect(typeof subBindings.checkedValue).toBe("function");
                    expect(subBindings.checkedValue()).toBe(accessor().value);
                });

                it("applies a 'bootstrapDisable' binding with a submitting based accessor", function () {
                    expect(subBindings.bootstrapDisable).toBeDefined();
                    expect(typeof subBindings.bootstrapDisable).toBe("function");

                    context.find = findBuilder(true, false); // not submitting
                    expect(subBindings.bootstrapDisable()).toBe(false);

                    context.find = findBuilder(true, true);  // submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);
                });
            });

            describe("the 'formValue' binding, which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { checked: "1234", value: "4321" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formValue.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("disables autocomplete fon the element", function (done) {
                    // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    var attrSpy = jasmine.createSpy("attr");
                    var jqSpy = jasmine.createSpy("$").and.callFake(function () {
                        return { attr: attrSpy };
                    });
                    injector.mock("jquery", jqSpy);

                    injector.require(["ko"], function (ko) {
                        ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                        ko.bindingHandlers.formValue.init(element, accessor, {}, {}, context);

                        expect(jqSpy.calls.count()).toBe(1);
                        expect(jqSpy).toHaveBeenCalledWith(element);
                        expect(attrSpy.calls.count()).toBe(1);
                        expect(attrSpy).toHaveBeenCalledWith("autocomplete", "off");

                        done();
                    });
                });

                it("applies a 'value' binding with the parent accessor", function () {
                    expect(subBindings.value).toBeDefined();
                    expect(typeof subBindings.value).toBe("function");
                    expect(subBindings.value).toBe(accessor);
                });

                it("applies a 'valueUpdate' binding with the value accessor that returns 'input'", function () {
                    expect(subBindings.valueUpdate).toBeDefined();
                    expect(typeof subBindings.valueUpdate).toBe("function");
                    expect(subBindings.valueUpdate()).toBe("input");
                });

                it("applies a 'bootstrapDisable' binding with a submitting based accessor", function () {
                    expect(subBindings.bootstrapDisable).toBeDefined();
                    expect(typeof subBindings.bootstrapDisable).toBe("function");

                    context.find = findBuilder(true, false); // not submitting
                    expect(subBindings.bootstrapDisable()).toBe(false);

                    context.find = findBuilder(true, true);  // submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);
                });
            });
        });
    });
});

