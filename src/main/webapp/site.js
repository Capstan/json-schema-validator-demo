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

// The list of our servlets
var Servlets = {
    VALIDATE: "/validate",
    LOAD_SAMPLES: "/loadSamples"
};

// The list of member names in a sample response
var SampleResponse = {
    SCHEMA: "schema",
    DATA: "data",
    USE_V4: "useV4",
    USE_ID: "useId"
};

// jQuery selectors for global elements
var DomElements = {
    FORM: "#validate",
    STARTHIDDEN: ".error, .success"
};

// jQuery selectors for input form elements
var FormElements = {
    INVALID_SCHEMA: "#invalidSchema",
    INVALID_DATA: "#invalidData",
    INPUTS: "textarea, input",
    SCHEMA: "#schema",
    DATA: "#data",
    USE_V4: "#useV4",
    USE_ID: "#useId"
};

// jQuery selectors for result pane elements
var ResultPane = {
    RESULTS: "textarea#results",
    VALIDATION_SUCCESS: "#validationSuccess",
    VALIDATION_FAILURE: "#validationFailure"
};

var TextAreas = {
    fillJson: function(selector, value)
    {
        $(selector).val(JSON.stringify(value, undefined, 4));
    },
    clear: function(selector)
    {
        $(selector).val("");
    }
};

function loadSamples()
{
    $(DomElements.STARTHIDDEN).hide();
    TextAreas.clear(ResultPane.RESULTS);

    var request = $.ajax({
        url: Servlets.LOAD_SAMPLES,
        type: "get",
        dataType: "json"
    });

    request.done(function(response, status, xhr)
    {
        var schema = response[SampleResponse.SCHEMA];
        var data = response[SampleResponse.DATA];
        var useV4 = response[SampleResponse.USE_V4];
        var useId = response[SampleResponse.USE_ID];

        TextAreas.fillJson(FormElements.SCHEMA, schema);
        TextAreas.fillJson(FormElements.DATA, data);
        $(FormElements.USE_V4).prop("checked", useV4);
        $(FormElements.USE_ID).prop("checked", useId);
    });

    request.fail(function (xhr, status, error)
    {
        // FIXME: that is very, very basic
        alert("Server error: " + status + " (" + error + ")");
    });
}

var main = function()
{
    // References to what we need
    var $form = $(DomElements.FORM);

    $form.submit(function (event)
    {
        // Clear/hide all necessary elements
        $(DomElements.STARTHIDDEN).hide();
        // Empty the results field
        TextAreas.clear(ResultPane.RESULTS);

        // Grab fields in the form
        // TODO: Complete list when necessary
        var $inputs = $form.find(FormElements.INPUTS);

        // Serialize all of the form -- _very_ convenient, that!
        // Note that unchecked checkboxes will not be taken into account; as to
        // checked ones, they default to "on" but this can be changed by speci-
        // fying the "value" attribute of an input. Here we set it to "true",
        // this allows Java to effectively parse it (Boolean.parseBoolean()
        // returns false when its argument is null).
        var payload = $form.serialize();

        // Lock inputs
        $inputs.prop("disabled", true);

        // The request
        var request = $.ajax({
            url: Servlets.VALIDATE,
            type: "post",
            data: payload,
            dataType: "json"
        });

        // On success
        // Since we specified that the data type we wanted was "json", the
        // response is directly passed along as a JavaScript object.
        request.done(function (response, status, xhr)
        {
            var invalidSchema = response["invalidSchema"];
            var invalidData = response["invalidData"];

            if (invalidSchema)
                $(FormElements.INVALID_SCHEMA).show();
            if (invalidData)
                $(FormElements.INVALID_DATA).show();

            // Stop right now if we have invalid inputs. Other fields will not
            // be defined.
            if (invalidSchema || invalidData)
                return;

            var validationMessage = response["valid"]
                ? ResultPane.VALIDATION_SUCCESS
                : ResultPane.VALIDATION_FAILURE;

            // Show the appropriate validation message and inject pretty-printed
            // JSON into the results text area
            $(validationMessage).show();
            TextAreas.fillJson(ResultPane.RESULTS, response["results"]);
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
