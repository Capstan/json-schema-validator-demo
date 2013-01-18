/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

// My first ever JavaScript code! Yay!

// Some constants
var Servlets = { VALIDATE: "validate" };
var DomElements = {
    FORM: "#validate",
    RESULTS: "textarea#results",
    INVALID_SCHEMA: "#invalidSchema",
    INVALID_DATA: "#invalidData",
    VALIDATION_SUCCESS: "#validationSuccess",
    VALIDATION_FAILURE: "#validationFailure",
    STARTHIDDEN_SELECTOR: ".errmsg, .success"
};

var Response;

var main = function()
{
    // References to what we need
    var $form = $(DomElements.FORM);
    var $results = $(DomElements.RESULTS);

    $form.submit(function (event)
    {
        // Clear/hide all necessary elements
        $(DomElements.STARTHIDDEN_SELECTOR).hide();
        $results.val("");

        // Grab fields in the form
        // TODO: Complete list when necessary
        var $inputs = $form.find("textarea, input");

        // Serialize all of the form -- _very_ convenient, that!
        var payload = $form.serialize();

        // Lock inputs
        $inputs.prop("disabled", true);

        // The request
        var request = $.ajax({
            // FIXME: hardcoded :/
            url: Servlets.VALIDATE,
            type: "post",
            data: payload,
            dataType: "text"
        });

        // On success
        request.done(function (response, status, xhr)
        {
            Response = jQuery.parseJSON(response);
            var invalidSchema = Response["invalidSchema"];
            var invalidData = Response["invalidData"];

            if (invalidSchema)
                $(DomElements.INVALID_SCHEMA).show();
            if (invalidData)
                $(DomElements.INVALID_DATA).show();

            if (invalidSchema || invalidData)
                return;

            var validationMessage = Response["valid"]
                ? DomElements.VALIDATION_SUCCESS
                : DomElements.VALIDATION_FAILURE;

            $(validationMessage).show();
            $results.val(JSON.stringify(Response["results"], undefined, 4));
        });

        // On failure
        request.fail(function (xhr, status, error)
        {
            // FIXME: that is very, very basic
            alert("Server error: " + status + " (" + error + ")");
        });

        // Always executed
        request.always(function ()
        {
            // Unlock inputs
            $inputs.prop("disabled", false);
        });

        // Prevent default post method
        event.preventDefault();

        // TODO: test if actually necessary
        return false;
    });
};
