/* AMD to represent the data in the account details page.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore", "jquery"], function (ko, _, $) {
    "use strict";

    return function (baseUrl, initialExpert, redirectPage, diseaseInterestListViewModel) {
        var self = this;

        // Function to convert the alerts into knockout.bootstrap notices.
        var buildNotices = function (alerts, priority) {
            return _(alerts).map(function (alert) {
                return { "message": alert, "priority": priority };
            });
        };

        // Field state
        self.name = ko.observable(initialExpert.name || "")
            .extend({ required: true, maxLength: 1000 });

        self.jobTitle = ko.observable(initialExpert.jobTitle || "")
            .extend({ required: true, maxLength: 100 });

        self.institution = ko.observable(initialExpert.institution || "")
            .extend({ required: true,  maxLength: 100 });

        self.visibilityRequested = ko.observable(initialExpert.visibilityRequested || false);

        self.diseaseInterestListViewModel = diseaseInterestListViewModel;

        // Meta state
        self.notices = ko.observableArray([]);
        self.isSubmitting = ko.observable(false);

        var buildSubmissionData = function () {
            return {
                name: self.name(),
                jobTitle: self.jobTitle(),
                institution: self.institution(),
                visibilityRequested: self.visibilityRequested(),
                diseaseInterests: self.diseaseInterestListViewModel.buildSubmissionData()
            };
        };

        // Actions
        self.submit = function () {
            self.notices.removeAll();
            if (!self.isValid()) {
                self.notices.push({ message: "Fields must be valid before saving.", priority: "warning"});
            } else {
                self.isSubmitting(true);
                $.ajax({
                    method: "POST",
                    url: baseUrl + "register/details",
                    data: JSON.stringify(buildSubmissionData()),
                    contentType : "application/json"
                })
                    .done(function () {
                        self.notices.push({
                            "message": "Account creation step 2/2 successfully completed.",
                            "priority": "success"
                        });
                        redirectPage(baseUrl);
                    })
                    .fail(function (xhr) {
                        if (xhr.status === 409) { // email address conflict
                            redirectPage(baseUrl + "register/account");
                        }
                        var alerts = buildNotices(JSON.parse(xhr.responseText), "warning");
                        _(alerts).each(function (alert) { self.notices.push(alert); });
                    })
                    .always(function () { self.isSubmitting(false); });
            }
        };
    };
});