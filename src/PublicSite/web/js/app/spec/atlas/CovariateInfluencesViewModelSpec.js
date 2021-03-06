/* A suite of tests for the CovariateInfluencesViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/CovariateInfluencesViewModel"
], function (ko, CovariateInfluencesViewModel) {
    "use strict";

    describe("The Covariate Influences View Model", function () {
        var baseUrl = "/";
        var vm;
        beforeEach(function () {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            vm = new CovariateInfluencesViewModel(baseUrl);
        });

        afterEach(function () {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
        });

        describe("holds the effect curve covariate influences link which", function () {
            it("is observable", function () {
                expect(vm.effectCurvesLink).toBeObservable();
            });

            it("starts empty with '#'", function () {
                expect(vm.effectCurvesLink()).toEqual("#");
            });

            it("is updated when the 'selected-run' event is fired", function () {
                // Arrange
                var modelRunId = "abc";
                var expected = baseUrl + "atlas/details/modelrun/" + modelRunId + "/effectcurves.csv";
                // Act
                ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                // Assert
                expect(vm.effectCurvesLink()).toEqual(expected);
            });
        });

        describe("holds the list of covariate influences which", function () {
            it("is observable", function () {
                expect(vm.covariateInfluences).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.covariateInfluences()).toBeUndefined();
            });

            describe("is updated", function () {
                var modelRunId = "modelRunId";

                it("with a GET request when the 'active-atlas-layer' event is fired", function () {
                    // Arrange
                    var expectedUrl = "/atlas/details/modelrun/" + modelRunId + "/covariates";
                    // Act
                    ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                    // Assert
                    expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                    expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
                });

                it("when successful", function () {
                    // Arrange
                    var expectation = [1, 2, 3];
                    // Act
                    ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                    jasmine.Ajax.requests.mostRecent().response({
                        "status": 200,
                        "contentType": "application/json",
                        "responseText": JSON.stringify(expectation)
                    });
                    // Assert
                    expect(vm.covariateInfluences()).toEqual(expectation);
                });

                it("when unsuccessful, to empty", function () {
                    // Act
                    ko.postbox.publish("active-atlas-layer", { run: { id: modelRunId } });
                    jasmine.Ajax.requests.mostRecent().response({ status: 400 });
                    // Assert
                    expect(vm.covariateInfluences()).toBeUndefined();
                });
            });
        });

        describe("holds the list of covariate influences to plot which", function () {
            it("is observable", function () {
                expect(vm.covariateInfluencesToPlot).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.covariateInfluencesToPlot()).toEqual([]);
            });

            it("is a subset of all covariate influences", function () {
                vm.covariateInfluences([
                    { meanInfluence: 101, effectCurve: [] }
                ]);
                expect(vm.covariateInfluencesToPlot()[0].covariate).toEqual({ meanInfluence: 101, effectCurve: [] });
                expect(vm.covariateInfluencesToPlot().length).toEqual(1);

                vm.covariateInfluences([
                    { meanInfluence: 100, effectCurve: [] },
                    { meanInfluence: 10, effectCurve: [] },
                    { meanInfluence: 50, effectCurve: [] }
                ]);
                expect(vm.covariateInfluencesToPlot()[0].covariate).toEqual({ meanInfluence: 100, effectCurve: [] });
                expect(vm.covariateInfluencesToPlot()[1].covariate).toEqual({ meanInfluence: 50, effectCurve: [] });
                expect(vm.covariateInfluencesToPlot().length).toEqual(2);
            });

            it("is adds a max value to each entry, which is the max upper quantile in the filtered covariates",
                function () {
                    vm.covariateInfluences([
                        { meanInfluence: 101, effectCurve:
                            [ { upperQuantile: 1}, { upperQuantile: 5}, { upperQuantile: 2 } ] }
                    ]);
                    expect(vm.covariateInfluencesToPlot()[0].max).toEqual(5);

                    vm.covariateInfluences([
                        { meanInfluence: 100, effectCurve:
                            [ { upperQuantile: 3 }, { upperQuantile: 2 }, { upperQuantile: 1 } ] },
                        { meanInfluence: 10, effectCurve:
                            [ { upperQuantile: 10 }, { upperQuantile: -10 }, { upperQuantile: -2 } ] },
                        { meanInfluence: 50, effectCurve:
                            [ { upperQuantile: -2 }, { upperQuantile: -5 }, { upperQuantile: 3 } ] }
                    ]);
                    expect(vm.covariateInfluencesToPlot()[0].max).toEqual(3);
                    expect(vm.covariateInfluencesToPlot()[1].max).toEqual(3);
                }
            );

            it("is adds a min value to each entry, which is the min lower quantile in the filtered covariates",
                function () {
                    vm.covariateInfluences([
                        { meanInfluence: 101, effectCurve:
                            [ { lowerQuantile: 1}, { lowerQuantile: 5}, { lowerQuantile: 2 } ] }
                    ]);
                    expect(vm.covariateInfluencesToPlot()[0].min).toEqual(1);

                    vm.covariateInfluences([
                        { meanInfluence: 100, effectCurve:
                            [ { lowerQuantile: 3 }, { lowerQuantile: 2 }, { lowerQuantile: 1 } ] },
                        { meanInfluence: 10, effectCurve:
                            [ { lowerQuantile: 10 }, { lowerQuantile: -10 }, { lowerQuantile: -2 } ] },
                        { meanInfluence: 50, effectCurve:
                            [ { lowerQuantile: -2 }, { lowerQuantile: -5 }, { lowerQuantile: 3 } ] }
                    ]);
                    expect(vm.covariateInfluencesToPlot()[0].min).toEqual(-5);
                    expect(vm.covariateInfluencesToPlot()[1].min).toEqual(-5);
                }
            );
        });

        describe("holds the active curve field which", function () {
            it("is observable", function () {
                expect(vm.activeCurve).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.activeCurve()).toBeUndefined();
            });
        });
    });
});
