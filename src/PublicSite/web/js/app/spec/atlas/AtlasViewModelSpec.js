/* A suite of tests for the AtlasViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "app/atlas/AtlasViewModel"
], function (ko, AtlasViewModel) {
    "use strict";

    describe("The 'AtlasViewModel", function () {
        it("holds the three sub-view-models", function () {
            // Act
            var vm = new AtlasViewModel("covariateInfluences", "downloadLinks", "submodelStatistics");
            // Assert
            expect(vm.covariateInfluencesViewModel).toBe("covariateInfluences");
            expect(vm.downloadLinksViewModel).toBe("downloadLinks");
            expect(vm.submodelStatisticsViewModel).toBe("submodelStatistics");
        });

        describe("holds the current layer, which", function () {
            // Arrange
            var vm = new AtlasViewModel({}, {}, {});

            it("is observable", function () {
                expect(vm.activeLayer).toBeObservable();
            });

            it("reacts to the 'active-atlas-layer' event", function () {
                // Arrange
                var payload = {};
                // Act
                expect(vm.activeLayer()).toBeUndefined();
                ko.postbox.publish("active-atlas-layer", payload);
                // Assert
                expect(vm.activeLayer()).toBe(payload);
            });
        });
    });
});