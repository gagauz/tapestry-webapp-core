T5.extendInitializers({
	AjaxEvent : function(specs) {
		var zoneId = specs.zoneId,
			url = specs.url,
			eventName = specs.bind, 
			element = $(specs.elementId);

        $T(element).zoneUpdater = true;

        var zoneElement = zoneId == '^' ? $(element).up('.t-zone')
            : $(zoneId);

        if (!zoneElement) {
            Tapestry.error("Could not find zone element '#{zoneId}' to update on #{eventName} of element '#{elementId}'.",
                {
                    zoneId: zoneId,
                    eventName: eventName,
                    elementId: element.id
                });
            return;
        }

        /*
		 * Update the element with the id of zone div. This may be changed
		 * dynamically on the client side.
		 */

        $T(element).zoneId = zoneElement.id;

        if (element.tagName == "FORM") {

            // Create the FEM if necessary.
            element.addClassName(Tapestry.PREVENT_SUBMISSION);

            /*
			 * After the form is validated and prepared, this code will
			 * process the form submission via an Ajax call. The original
			 * submit event will have been cancelled.
			 */

            element.observe(
                Tapestry.FORM_PROCESS_SUBMIT_EVENT, function() {
                    var zoneManager = Tapestry.findZoneManager(element);

                    if (!zoneManager)
                        return;

                    var successHandler = function (transport) {
                        zoneManager.processReply(transport.responseJSON);
                    };

                    element.sendAjaxRequest(url, {
                        parameters: {
                            "t:zoneid": zoneId
                        },
                        onSuccess: successHandler
                    });
                });

            return;
        }

        /* Otherwise, assume it's just an ordinary link or input field. */

        element.observeAction(eventName, function (event) {
            element.fire(Tapestry.TRIGGER_ZONE_UPDATE_EVENT);
        });

        element.observe(Tapestry.TRIGGER_ZONE_UPDATE_EVENT, function () {

            var zoneObject = Tapestry.findZoneManager(element);

            if (!zoneObject)
                return;

            /*
			 * A hack related to allowing a Select to perform an Ajax update
			 * of the page.
			 */

            var parameters = {};
            
            parameters[specs.valueParam] = element[specs.valueParam];

            zoneObject.updateFromURL(url, parameters);
        });
	}
});