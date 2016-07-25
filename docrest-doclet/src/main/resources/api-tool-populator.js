var consts = {
    TITLE_USER_ACCOUNT: 'Client credentials type. ',
    TITLE_LOGIN_AUTHORIZATION: 'Resource owner password. ',

    USER_ACCOUNT: "user-account",
    LOGIN_NAME_VERIFY_LOGIN_CREDENTIALS_TYPE: "loginNameVerifyLoginCredentialsType",
    PASSWORD_VERIFY_LOGIN_CREDENTIALS_TYPE: "passwordVerifyLoginCredentialsType",
    VERIFY_USER_ACCOUNT_CREDENTIALS_TYPE_BUTTON: "verifyUserAccountCredentialsTypeButton",
    VERIFY_USER_ACCOUNT_CREDENTIALS_TYPE_RESPONSE: "verifyUserAccountCredentialsTypeResponse",
    VERIFY_USER_ACCOUNT_CREDENTIALS_TYPE_RESPONSE_CODE: "verifyUserAccountCredentialsTypeResponseCode",
    DEFAULT_USER_ACCOUNT : "webapp",
    DEFAULT_USER_ACCOUNT_PASSWORD : "1ho8ISSE0qiUWtdhXz3",

    LOGIN_AUTHORIZATION: "login-authorization",
    LOGIN_NAME_VERIFY_LOGIN: "loginNameVerifyLogin",
    PASSWORD_VERIFY_LOGIN: "passwordVerifyLogin",
    VERIFY_LOGIN_BUTTON: "verifyLoginButton",
    VERIFY_LOGIN_RESPONSE: "verifyLoginResponse",
    VERIFY_LOGIN_RESPONSE_CODE: "verifyLoginResponseCode"
};

function populateElement(elementName, attrs, childElements, textNode) {

    var element = document.createElement(elementName);

    // add attributes
    if (attrs != null) {
        for (var key in attrs) {
            if (attrs.hasOwnProperty(key)) {
                var attr = document.createAttribute(key);
                attr.value = attrs[key];
                element.setAttributeNode(attr);
            }
        }
    }
    // appendChild node
    if (childElements != null) {
        for (var i = 0; i < childElements.length; i++) {
            element.appendChild(childElements[i]);
        }
    }
    // appendChild text
    if (textNode != null) {
        var text = document.createTextNode(textNode);
        element.appendChild(text);
    }

    return element;
}

function getArrayChildElements(...args) {
    var childElements = [];
    for (i = 0; i < args.length; i++) {
        childElements.push(args[i]);
    }
    return childElements;
}

function populateInputField(attrs) {
    return populateElement("input", attrs);
}

function populateOptions(requestMethod) {

    var selected = {
        "selected": "selected"
    };
    switch (requestMethod) {
        case "GET":
            return selectOptions(selected, null, null, null);
        case "POST":
            return selectOptions(null, selected, null, null);
        case "PUT":
            return selectOptions(null, null, selected, null);
        case "DELETE":
            return selectOptions(null, null, null, selected);
        default:
            return selectOptions(selected, null, null, null);
    }

}

function selectOptions(optGet, optPost, optPut, optDelete) {
    var options = [];
    options.push(populateElement("option", optGet, null, "GET"));
    options.push(populateElement("option", optPost, null, "POST"));
    options.push(populateElement("option", optPut, null, "PUT"));
    options.push(populateElement("option", optDelete, null, "DELETE"));
    return options;
}

function populateRestPathRow(requestMethod, restPath) {

    var label = populateElement("label", null, null, "REST API");
    var column = populateElement("div", {
        class: "col-md-2"
    }, getArrayChildElements(label));

    var inputField = populateInputField({
        type: "text",
        class: "form-control col-md-8 restPaths",
        placeholder: "Enter API Path",
        value: restPath
    });

    var buttonControl = populateElement("button", {
        type: "button",
        class: "btn btn-default param"
    }, null, "Param");

    var span = populateElement("span", {
        class: "input-group-btn"
    }, getArrayChildElements(buttonControl));
    var inputGroup = populateElement("div", {
        class: "input-group"
    }, getArrayChildElements(inputField, span));
    var firstColumn = populateElement("div", {
        class: "col-md-8"
    }, getArrayChildElements(inputGroup));

    var options = populateOptions(requestMethod);


    var select = populateElement("select", {
        class: "form-control request_method"
    }, options);

    var spanGroupButton = populateElement("span", {
        class: "input-group-btn"
    }, getArrayChildElements(select));
    var secondColumn = populateElement("div", {
        class: "col-md-2"
    }, getArrayChildElements(spanGroupButton));
    return populateElement("div", {
        class: "row"
    }, getArrayChildElements(column, firstColumn, secondColumn));
}
function getParamRowList (paramKeypairs) {
    var rows = [];
    var i = 0 ;
    var stringButton = "btn-success addButton" ;  
    var glyphiconIcon = "glyphicon glyphicon-plus";
    var key = "";
    var value = "";
    do {
        if (paramKeypairs.length > 0) {
            console.log(paramKeypairs);
            key = paramKeypairs[i].key; value = paramKeypairs[i].value;
        }
        var inputField = populateInputField({
            type: "text",
            class: "form-control col-md-4 keys",
            name: "key[]",
            placeholder: "key",
            value : key
        });
        var firstColumn = populateElement("div", {
            class: "col-md-4 col-md-offset-2"
        }, getArrayChildElements(inputField));

        inputField = populateInputField({
            type: "text",
            class: "form-control col-md-4 values",
            name: "param[]",
            placeholder: "value",
            value : value
        });
        var secondColumn = populateElement("div", {
            class: "col-md-4"
        }, getArrayChildElements(inputField));


        var span = populateElement("span", {
            class: glyphiconIcon
        });
        var buttonControl = populateElement("button", {
            type: "button",
            class: "btn "+ stringButton,
            style: "padding: 9px 12px;"
        }, getArrayChildElements(span));
        var thirdColumn = populateElement("div", {
            class: "col-md-1"
        }, getArrayChildElements(buttonControl));
        var row = populateElement("div", {
            class: "row"
        }, getArrayChildElements(firstColumn, secondColumn, thirdColumn));
        
        rows.push (row);
        stringButton = "btn-danger removeButton";
        glyphiconIcon = "glyphicon glyphicon-minus";
        i++;
    }while (i < paramKeypairs.length);
    
    return rows;
}
function populateRequestParamRow(paramKeypairs) {
    rows = getParamRowList (paramKeypairs);
    var attrs = paramKeypairs.length > 0 ? {class: "request_param"}: {class: "request_param", style :"display:none;"};
    return populateElement("div",attrs, rows);
}

function populateAccessTokenRow() {
    var checkbox = populateInputField({
        type: "checkbox",
        class: "cbo_access_token"
    });
    var label = populateElement("label", null, getArrayChildElements(checkbox), "AccessToken");
    var classCheckbox = populateElement("div", {
        class: "checkbox"
    }, getArrayChildElements(label));
    var firstColumn = populateElement("div", {
        class: "col-md-2"
    }, getArrayChildElements(classCheckbox));

    var inputField = populateInputField({
        type: "text",
        class: "form-control col-md-8 access_token",
        placeholder: "Enter AccessToken",
        disabled: "true"
    })
    var secondColumn = populateElement("div", {
        class: "col-md-8"
    }, getArrayChildElements(inputField));
    
    var buttonControl = populateElement("button", {
        "type": "button",
        "class": "btn btn btn-link",
        "style": "padding-left: 0px; display:none;",
        "data-toggle":"modal",
        "data-target":"#auth-dialog-modal"
    }, null, "Generate AccessToken");
    var thirdColumn = populateElement("div", {
        class: "col-md-1 "
    }, getArrayChildElements(buttonControl));
    
    return populateElement("div", {
        class: "row"
    }, getArrayChildElements(firstColumn, secondColumn,thirdColumn));
}

function populateRequestBodyRow(number) {
    var radio = populateInputField({
        type: "radio",
        name: "radio_" + number,
        value: "application/x-www-form-urlencoded; charset=utf-8"
    });
    var firstLabel = populateElement("label", {
        class: "radio-inline"
    }, getArrayChildElements(radio), "x-www-form-urlencode");

    radio = populateInputField({
        type: "radio",
        name: "radio_" + number,
        value: "application/json; charset=utf-8",
        checked: "true"
    });
    var secondLabel = populateElement("label", {
        class: "radio-inline"
    }, getArrayChildElements(radio), "raw");
    var firstColumn = populateElement("div", {
        class: "col-md-4 col-md-offset-2"
    }, getArrayChildElements(firstLabel, secondLabel));
    var firstRow = populateElement("div", {
        class: "row"
    }, getArrayChildElements(firstColumn));



    var inputField = populateInputField({
        type: "text",
        class: "form-control col-md-4 keys",
        name: "key[]",
        placeholder: "key"
    });
    firstColumn = populateElement("div", {
        class: "col-md-4 col-md-offset-2"
    }, getArrayChildElements(inputField));


    inputField = populateInputField({
        type: "text",
        class: "form-control col-md-4 values",
        name: "param[]",
        placeholder: "value"
    });
    var secondColumn = populateElement("div", {
        class: "col-md-4"
    }, getArrayChildElements(inputField));


    var span = populateElement("span", {
        class: "glyphicon glyphicon-plus"
    });
    var buttonControl = populateElement("button", {
        type: "button",
        class: "btn btn-success addButton",
        style: "padding: 9px 12px;"
    }, getArrayChildElements(span));
    var thirdColumn = populateElement("div", {
        class: "col-md-1"
    }, getArrayChildElements(buttonControl));
    var secondRowSub = populateElement("div", {
        class: "row"
    }, getArrayChildElements(firstColumn, secondColumn, thirdColumn));
    var secondRow = populateElement("div", {
        class: "x_wwww_form"
    }, getArrayChildElements(secondRowSub));


    var label = populateElement("label", {
        for: "raw_request_body"
    }, null, "Body:");
    var textare = populateElement("textarea", {
        class: "form-control raw_request_body",
        rows: "10",
        style: "resize: none;"
    });
    firstColumn = populateElement("div", {
        class: "col-md-8 col-md-offset-2"
    }, getArrayChildElements(label, textare));
    var thirdRow = populateElement("div", {
        class: "row raw"
    }, getArrayChildElements(firstColumn));

    return populateElement("div", {
        class: "request_body"
    }, getArrayChildElements(firstRow, secondRow, thirdRow));
}

function populateResponseBody() {
    var pre = populateElement("pre", {
        style: "background-color: #ffffff;"
    });
    var label = populateElement("label", null, null, "Response Body:");
    var column = populateElement("div", {
        class: "col-md-8 col-md-offset-2"
    }, getArrayChildElements(label, pre));
    return populateElement("div", {
        class: "row response_body"
    }, getArrayChildElements(column));
}

function populateTemplate() {
    var inputField = populateInputField({
        type: "text",
        class: "form-control col-md-4 keys",
        name: "key[]",
        placeholder: "key"
    });
    var firstColumn = populateElement("div", {
        class: "col-md-4 col-md-offset-2"
    }, getArrayChildElements(inputField));

    inputField = populateInputField({
        type: "text",
        class: "form-control col-md-4 values",
        name: "param[]",
        placeholder: "value"
    });
    var secondColumn = populateElement("div", {
        class: "col-md-4"
    }, getArrayChildElements(inputField));


    var span = populateElement("span", {
        class: "glyphicon glyphicon-minus"
    });
    var buttonControl = populateElement("button", {
        type: "button",
        class: "btn btn-danger removeButton",
        style: "padding: 9px 12px;"
    }, getArrayChildElements(span));
    var thirdColumn = populateElement("div", {
        class: "col-md-1"
    }, getArrayChildElements(buttonControl));
    return populateElement("div", {
        class: "row hide template"
    }, getArrayChildElements(firstColumn, secondColumn, thirdColumn));
}

function populateServerUrlsRow(urls) {
    var options = [];
    if (typeof urls !== 'undefined' & urls !== null) {
        urls.forEach(function (value, index, array) {
            // here value is the array element being iterated
            options.push(populateElement("option", null, null, value));
        });
    }
    var select = populateElement("select", {
        class: "form-control serverUrl"
    }, options);

    var label = populateElement("label", null, null, "Server URL");
    var firstColumn = populateElement("div", {
        class: "col-md-2"
    }, getArrayChildElements(label));

    var secondColumn = populateElement("div", {
        class: "col-md-8"
    }, getArrayChildElements(select));

    buttonControl = populateElement("button", {
        type: "button",
        class: "btn btn-primary submit",
        style: "width :100%"
    }, null, "Send");
    var thirdColumn = populateElement("div", {
        class: "col-md-2"
    }, getArrayChildElements(buttonControl));

    return populateElement("div", {
        class: "row serverUrls"
    }, getArrayChildElements(firstColumn, secondColumn, thirdColumn));
}

function populateRequestUrlRow() {
    var label = populateElement("label", null, null, "Server URL");
    var firstColumn = populateElement("div", {
        class: "col-md-2"
    }, getArrayChildElements(label));

    var inputField = populateInputField({
        type: "text",
        class: "form-control col-md-8 url_request",
        placeholder: "Enter request URL"
    });
    var secondColumn = populateElement("div", {
        class: "col-md-8"
    }, getArrayChildElements(inputField));

    buttonControl = populateElement("button", {
        type: "button",
        class: "btn btn-primary submit",
        style: "width :100%"
    }, null, "Send");
    var thirdColumn = populateElement("div", {
        class: "col-md-2"
    }, getArrayChildElements(buttonControl));

    return populateElement("div", {
        class: "row requestUrl"
    }, getArrayChildElements(firstColumn, secondColumn, thirdColumn));
}

function populateDialogServerUrlsRow(urls) {
    var h5 = populateElement("h5", null, null, "Server URL");
    var options = [];
    if (typeof urls !== 'undefined' & urls !== null) {
        urls.forEach(function (value, index, array) {
            // here value is the array element being iterated
            options.push(populateElement("option", null, null, value));
        });
    }
    var select = populateElement("select", {
        class: "form-control",
        "id": "dialogServerUrls"
    }, options);
    var col = populateElement("div", {
        "class": "col-md-12"
    }, getArrayChildElements(h5, select));
    return populateElement("div", {
        "class": "row serverUrls"
    }, getArrayChildElements(col));
}

function populateDialogRequestUrlRow() {
    var h5 = populateElement("h5", null, null, "Server URL");
    var inputField = populateInputField({
        "type": "text",
        "class": "form-control",
        "id": "dialogRequestUrl",
        "placeholder": "Enter request URL"
    });
    var col = populateElement("div", {
        class: "col-md-12"
    }, getArrayChildElements(h5, inputField));

    return populateElement("div", {
        class: "row requestUrl"
    }, getArrayChildElements(col));
}

function populateDialogBody(title, mainClass, loginId, passwordId, buttonId, responseId,account,password) {

    var p = populateElement("p", {
        "style": "color:#fff; font-weight: bold; font-size:14px;"
    }, null, title + "POST : /api/oauth2/token");
    var post = populateElement("div", {
        "style": "background-color: #cc0000; padding: 10px 5px"
    }, getArrayChildElements(p));
    col = populateElement("div", {
        "class": "col-md-12"
    }, getArrayChildElements(post));
    var row1 = populateElement("div", {
        "class": "row"
    }, getArrayChildElements(col));

    var inputField = populateInputField({
        "type": "text",
        "class": "form-control",
        "placeholder": "Login name",
        "id": loginId,

    });
    col = populateElement("div", {
        "class": "col-md-12"
    }, getArrayChildElements(inputField));
    var row2 = populateElement("div", {
        "class": "row"
    }, getArrayChildElements(col));

    var inputField = populateInputField({
        type: "password",
        class: "form-control",
        placeholder: "Password",
        "id": passwordId,
    });
    col = populateElement("div", {
        "class": "col-md-12"
    }, getArrayChildElements(inputField));
    var row3 = populateElement("div", {
        "class": "row"
    }, getArrayChildElements(col));

    var pre = populateElement("pre", {
        "id": responseId
    }, null, "Response Body");
    col = populateElement("div", {
        "class": "col-md-12"
    }, getArrayChildElements(pre));
    var row4 = populateElement("div", {
        "class": "row response",
        "style": "display: block;"
    }, getArrayChildElements(col));
    
    var submit = populateElement("button", {
        "class": "btn btn-primary btn-block",
        "id": buttonId
    }, null, "Submit");
    col = populateElement("div", {
        "class": "col-md-offset-8 col-md-4"
    }, getArrayChildElements(submit));
    var row5 = populateElement("div", {
        "class": "row"
    }, getArrayChildElements(col));
    return populateElement("div", {
        "class": mainClass
    }, getArrayChildElements(row1, row2, row3, row5, row4));

}
var dialog = function (urls) {
    var span = populateElement("span", {
        "aria-hidden": "true"
    }, null, "x");
    var buttonClose = populateElement("button", {
            "type": "button",
            "class": "close",
            "data-dismiss": "modal",
            "aria-label": "close"
        },
        getArrayChildElements(span));

    var title = populateElement("h4", {
        "class": "modal-title",
        "id": "gridModalLabel"
    }, null, "Authorization");
    var modalHeader = populateElement("div", {
        "class": "modal-header"
    }, getArrayChildElements(buttonClose, title));

    var hr = populateElement("hr");
    var hr1 = populateElement("hr");
    var requestUrl = populateDialogRequestUrlRow();
    var requestServerUrl = populateDialogServerUrlsRow(urls);
    var userAccount = populateDialogBody(consts.TITLE_USER_ACCOUNT, 
                                         consts.USER_ACCOUNT, 
                                         consts.LOGIN_NAME_VERIFY_LOGIN_CREDENTIALS_TYPE, consts.PASSWORD_VERIFY_LOGIN_CREDENTIALS_TYPE, consts.VERIFY_USER_ACCOUNT_CREDENTIALS_TYPE_BUTTON, consts.VERIFY_USER_ACCOUNT_CREDENTIALS_TYPE_RESPONSE);
    var loginAuthorization = populateDialogBody(consts.TITLE_LOGIN_AUTHORIZATION, 
                                                consts.LOGIN_AUTHORIZATION, 
                                                consts.LOGIN_NAME_VERIFY_LOGIN, 
                                                consts.PASSWORD_VERIFY_LOGIN, 
                                                consts.VERIFY_LOGIN_BUTTON, 
                                                consts.VERIFY_LOGIN_RESPONSE);
    var containerBody = populateElement("div", {"class": "container-fluid"}, 
                                        getArrayChildElements(
                                                requestUrl,
                                                requestServerUrl,
                                                hr,userAccount, hr1,
                                                loginAuthorization
                                        ));
    var modalBody = populateElement("div", {
        "class": "modal-body"
    }, getArrayChildElements(containerBody));


    /*
    var buttonDismiss = populateElement("button", {"type" : "button", "class" : "btn btn-default", "data-dismiss" : "modal"},null,"Close");
    var modalFooter = populateElement ("div",{"class" : "modal-footer"},getArrayChildElements(buttonDismiss));
    */
    var modalContent = populateElement("div", {
        "class": "modal-content"
    }, getArrayChildElements(modalHeader, modalBody));


    var modalDialog = populateElement("div", {
        "class": "modal-dialog",
        "role": "document"
    }, getArrayChildElements(modalContent));

    var modal = populateElement("div", {
        "class": "modal fade",
        "id": "auth-dialog-modal",
        "tabindx": "-1",
        "role": "dialog",
        "aria-labelledby": "gridModalLabel",
        "aria-hidden": "true"
    }, getArrayChildElements(modalDialog));

    var popUpDialog = populateElement("button", {
        "type": "button",
        "class": "btn btn-md btn-dialog-popup",
        "data-toggle": "modal",
        "data-target": "#auth-dialog-modal",
        "style": "margin-bottom: 20px"
    }, null, "Authorization");
    return populateElement("div", null, getArrayChildElements(modal, popUpDialog));
}

function isEnableServerUrls() {
    return (typeof serverUrls === 'undefined' || serverUrls === null || serverUrls.length < 1) ? false : true;
}
function enableUserSecurity () {
    return (typeof isEnableUserSecurity !== 'undefined' && isEnableUserSecurity === true ) ? true : false ;
}
function populateAPI(index, requestMethod, restPath, paramKeypairs, urls) {
    var serverUrlRow = populateServerUrlsRow(urls);
    var serverRequestUrlRow = populateRequestUrlRow();
    var httpUrlRow = populateRestPathRow(requestMethod, restPath);
    var requestParamRow = populateRequestParamRow(paramKeypairs);
    var accessTokenRow = populateAccessTokenRow();
    var requestBodyRow = populateRequestBodyRow(index);
    var responseBodyRow = populateResponseBody();

    var api = populateElement("div", {
        class: "api well"
    }, getArrayChildElements(serverUrlRow, serverRequestUrlRow, httpUrlRow, requestParamRow, accessTokenRow, requestBodyRow, responseBodyRow));
    return api;
}

$(function () {
    // populate api tool
    $(".api-toggle").each(function (index) {
        /* auto populate request method "GET, POST, PUT, DELETE" */
        var requestMethod = $(this).data("request-method");
        if (requestMethod.trim().indexOf(',') > -1) {
            requestMethod = requestMethod.trim().slice(0, requestMethod.indexOf(','));
        }
        /* End : auto populate request method "GET, POST, PUT, DELETE" */
        
        /* auto populate query param */
        var tableId = $(this).data("table-index");
        var paramList =$("#"+tableId).find("tr").children("td[id]").map(function(){
            return $(this).text();       
        }).get();
        var paramKeypairs = [];
        for (var i = 0 ; i < paramList.length ; i+=2) {
            var paramKeypair = {};
            paramKeypair.key = paramList[i].trim();
            paramKeypair.value = paramList[i+1].trim();
            paramKeypairs.push (paramKeypair);
        }
        /* End : auto populate query param */
        
        var restPath = "/api" + $(this).data("path");
        
        var api = isEnableServerUrls() ? populateAPI(index, requestMethod, restPath,paramKeypairs, serverUrls) : populateAPI(index, requestMethod, restPath, paramKeypairs);
        $(this).after(api);
    });
    
    if (enableUserSecurity()){
        isEnableServerUrls() ? $(".authorization-dialog").append(dialog(serverUrls)) : $(".authorization-dialog").append(dialog());
    } 
    //populate tempate of element key/value pair 
    $("body").append(populateTemplate());
})
