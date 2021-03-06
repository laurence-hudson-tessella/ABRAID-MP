/* AMD defining the statistics table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'selected-run' - published by LayerSelectorViewModel
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        self.statistics = ko.observable(undefined);

        var ajax;
        ko.postbox.subscribe("active-atlas-layer", function (layer) {
            self.statistics(undefined);
            if (layer) {
                if (ajax) {
                    ajax.abort();
                }
                ajax = $.getJSON(baseUrl + "atlas/details/modelrun/" + layer.run.id + "/statistics")
                    .done(function (data) {
                        self.statistics(data);
                    }).always(function () {
                        ajax = undefined;
                    });
            }
        });
    };
});
