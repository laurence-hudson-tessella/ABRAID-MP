/* A suite of tests for the atlas WmsLayerParameterFactory AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/atlas/WmsLayerParameterFactory",
    "underscore"
], function (WmsLayerParameterFactory, _) {
    "use strict";

    describe("The atlas 'WMS layer parameter factory'", function () {
        var vm = new WmsLayerParameterFactory();

        describe("has a 'createLayerParametersForDisplay' method, which", function () {
            it("returns the standard abraid WMS configuration", function () {
                var params = vm.createLayerParametersForDisplay({ type: "uncertainty", run: { id: "foo" } });
                expect(params.format).toEqual("image/png");
                expect(params.reuseTiles).toEqual(true);
                expect(params.tiled).toEqual(true);
            });

            it("returns the correct extent specific configuration", function () {
                var params = vm.createLayerParametersForDisplay({ type: "extent", run: { id: "foo" } });
                expect(params.layers).toEqual("abraid:atlas_extent_layer");
                expect(params.cql_filter).toEqual("model_run_name='foo'"); // jshint ignore:line
            });

            it("returns the correct mean specific configuration", function () {
                var params = vm.createLayerParametersForDisplay({ type: "mean", run: { id: "foo" } });
                expect(params.layers).toEqual("abraid:foo_mean");
                expect(params.cql_filter).toBeUndefined(); // jshint ignore:line
            });

            it("returns the correct uncertainty specific configuration", function () {
                var params = vm.createLayerParametersForDisplay({ type: "uncertainty", run: { id: "foo" } });
                expect(params.layers).toEqual("abraid:foo_uncertainty");
                expect(params.cql_filter).toBeUndefined(); // jshint ignore:line
            });
        });

        describe("has a 'createLayerParametersForDownload' method, which", function () {
            it("returns the same as the display function but adds standard download parameters", function () {
                var paramsDownload, paramsDisplay;
                var extraDownloadParameters = {
                    service: "WMS",
                    version: "1.1.0",
                    request: "GetMap",
                    bbox: "-180.1,-60.0,180.0,85.0",// BBox min set to -180.1 due to bug in Geoserver (should be -180.0)
                    width: 1656,
                    height: 667,
                    srs: "EPSG:4326"
                };

                paramsDisplay = vm.createLayerParametersForDisplay({ type: "uncertainty", run: { id: "foo" } });
                paramsDownload = vm.createLayerParametersForDownload({ type: "uncertainty", run: { id: "foo" } });
                expect(paramsDownload).toEqual(_(paramsDisplay).extend(extraDownloadParameters));

                paramsDisplay = vm.createLayerParametersForDisplay({ type: "mean", run: { id: "foo" } });
                paramsDownload = vm.createLayerParametersForDownload({ type: "mean", run: { id: "foo" } });
                expect(paramsDownload).toEqual(_(paramsDisplay).extend(extraDownloadParameters));

                paramsDisplay = vm.createLayerParametersForDisplay({ type: "extent", run: { id: "foo" } });
                paramsDownload = vm.createLayerParametersForDownload({ type: "extent", run: { id: "foo" } });
                expect(paramsDownload).toEqual(_(paramsDisplay).extend(extraDownloadParameters));
            });
        });
    });
});
