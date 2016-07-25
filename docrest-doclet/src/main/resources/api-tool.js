 function Helper() {
     this.formUrlEncoded = "application/x-www-form-urlencoded; charset=utf-8";
     this.applicationJson = "application/json; charset=utf-8";
     this.accessToken = "?access_token=";
     this.endpointApiUrl = "/api";
     this.stringAccessToken;
     this.encodeHtmlEntity = function (str) {
         var buf = [];
         for (var i = str.length - 1; i >= 0; i--) {
             buf.unshift(['&#', str[i].charCodeAt(), ';'].join(''));
         }
         return buf.join('');
     };
 }

 $(function () {
     var helper = new Helper();

     function toJson(values, rootElement) {
         try {
             //JSON.stringify(data, null, 4)
             return JSON.stringify(JSON.parse(values), null, 4);
         } catch (error) {
             throw new JsonException(rootElement, error);
         }
     }

     function JsonException(rootElement, error) {
         rootElement.find("pre").empty();
         rootElement.find("pre").append(error);
         rootElement.find(".response_body").show(function () {
             $(this).animate({
                 height: 'auto'
             });
         });

     }

     function InvalidArgumentException(message) {
         alert(message);
     }

     InvalidArgumentException.prototype = Error.prototype;
     JsonException.prototype = Error.prototype;

     function getData(contentType, rootElement) {
         var data;
         /* raw json */
         if (contentType === helper.applicationJson) {
             data = rootElement.find(".raw_request_body").val();
             return toJson(data, rootElement);
         }

         /* x-www-form-urlencode. the default contajaxOptionsent */
         else if (contentType === helper.formUrlEncoded) {
             return toParams(rootElement, ".x_wwww_form  input[name='key[]']", ".x_wwww_form  input[name='param[]']");
         }
         // form/data
     }

     function prepareData(requestMethod, contentType, rootElement) {
         if (requestMethod !== "GET") {
             return getData(contentType, rootElement);
         }
     }

     function ajaxRequest(url, requestMethod, data, contentType, rootElement) {
         $.ajaxSetup({
             headers: {
                 Accept: "application/javascript charset=utf-8",
                 Accept: "text/html charset=utf-8",
                 Accept: "application/json; charset=utf-8"
             },
             dataType: 'json',
             cache: false,
             crossDomain: true
         });
         $.ajax({
                 url: url,
                 type: requestMethod,
                 data: data,
                 contentType: contentType
             })
             // using the done promise callback
             .done(function (data) {
                 console.log(data);
                 try {
                     rootElement.find("pre").empty();
                     
                     rootElement.find("pre").append(helper.encodeHtmlEntity(JSON.stringify(data, null, 4)));
                 } catch (e) {
                     rootElement.find("pre").append(data);
                 }
                 rootElement.find(".response_body").show(function () {
                     $(this).animate({
                         height: 'auto'
                     });
                 });
             })
             // using the fail promise callback
             .fail(function (data, statusText, error) {
                 // show any errors
                 // best to remove for production
                 console.log(data);
                 rootElement.find("pre").empty();
                 try {
                     rootElement.find("pre").append(JSON.stringify(JSON.parse(data.responseText), null, 4));
                 } catch (e) {
                     if (data.status == 404) {
                         rootElement.find("pre").append(helper.encodeHtmlEntity(data.responseText))

                     }
                 }
                 rootElement.find(".response_body").show(function () {
                     $(this).animate({
                         height: 'auto'
                     });
                 });

             });
     }

     function getURL(rootElement) {
         if (rootElement.find(".url_request").val() === "" && rootElement.find(".url_request").is(":visible")) {
             throw new InvalidArgumentException("Please enter url");
         } else if (rootElement.find(".serverUrl").is(":visible")) {
             if (rootElement.find(".restPaths").val() === "") {
                 throw new InvalidArgumentException("Please enter api path");
             }
             return rootElement.find(".serverUrl").val() + rootElement.find(".restPaths").val();
         } else {
             if (rootElement.find(".restPaths").val() === "") {
                 throw new InvalidArgumentException("Please enter api path");
             }
             return rootElement.find(".url_request").val() + rootElement.find(".restPaths").val();
         }
     }

     function getAccessToken(rootElement) {
         var stringAccessToken = rootElement.find(".access_token").val();
         if (rootElement.find(".cbo_access_token").prop('checked') && stringAccessToken === "") {
             throw new InvalidArgumentException("Please enter accessToken !");
         } else if (rootElement.find(".cbo_access_token").prop('checked') && stringAccessToken) {
             return helper.accessToken + stringAccessToken;
         }
         return "";
     }

     function toParams(rootElement, keyStringElement, valueStringElement) {
         var requestParamsKeys = rootElement.find(keyStringElement).map(function () {
             return $(this).val();
         }).get();
         var requestParamsValues = rootElement.find(valueStringElement).map(function () {
             return $(this).val();
         }).get();

         var params = {};
         for (var i = 0; i < requestParamsKeys.length; i++) {
             if (requestParamsKeys[i] === "" || requestParamsValues[i] === "") {
                 continue;
             }
             params[requestParamsKeys[i]] = requestParamsValues[i];
         }
         return $.param(params);
     }

     function getParamsUrl(rootElement) {

         var params = toParams(rootElement, ".request_param input[name='key[]']", ".request_param input[name='param[]']")

         if (params === "=") {
             return "";
         } else if (rootElement.find(".cbo_access_token").prop('checked')) {
             return "&" + params;
         }
         return "?" + params;
     }

     function prepareURL(rootElement) {
         var url = getURL(rootElement);
         var accessToken = getAccessToken(rootElement);
         var params = getParamsUrl(rootElement);
         return url + accessToken + params;
     }

     function getContentType(rootElement, requestMethod) {
         if (requestMethod !== "GET") {
             return rootElement.find("input[type='radio']:checked").val();
         }
         return null;
     }
     //If you want to target a dynamically added or removed element you'll have to use
     $(document).on("click", ".submit", function () {

         var rootElement = $(this).closest(".api"),
             url, requestMethod, contentType, data;

         rootElement.find(".response_body").hide(function () {
             $(this).animate({
                 height: 'auto'
             });
         });
         try {
             url = prepareURL(rootElement);
             requestMethod = rootElement.find(".request_method").val();
             contentType = getContentType(rootElement, requestMethod);
             console.log("log reqest :", url, requestMethod, contentType);
             data = prepareData(requestMethod, contentType, rootElement);
             ajaxRequest(url, requestMethod, data, contentType, rootElement);
         } catch (e) {
             if (e instanceof InvalidArgumentException) {
                 /* do something */
             } else if (e instanceof JsonException) {
                 console.log("Json Exception");
             } else {
                 console.log(e);
             }
         }

         event.preventDefault();
     });

     function getDialogBaseUrl() {
         var rootElement = $(".modal-body");
         if (rootElement.find(".serverUrls").is(":visible")) {
             return $("#dialogServerUrls").val();
         } else if (rootElement.find(".requestUrl").is(":visible") && $("#dialogRequestUrl").val() === "") {
             throw new InvalidArgumentException("Please enter url");
         } else {
             return $("#dialogRequestUrl").val();
         }

     }
     $.ajaxSetup({
         cache: false,
         data: null,
     });

     function getBasicKey(username, password) {
         return "Basic " + window.btoa(username + ":" + password);
     }

     $("#verifyUserAccountCredentialsTypeButton").button().click(
         function () {
             try {
                 $("#verifyUserAccountCredentialsTypeResponse").hide();
                 var baseUrl = getDialogBaseUrl();
                 var userName = $("#loginNameVerifyLoginCredentialsType").val();
                 var loginPassword = $("#passwordVerifyLoginCredentialsType").val();
                 $.ajax({
                     type: "POST",
                     url: baseUrl + helper.endpointApiUrl + "/oauth2/token",
                     data: {
                         grant_type: "client_credentials",
                     },
                     beforeSend: function (request) {
                         request.setRequestHeader('Authorization', getBasicKey(userName, loginPassword));
                     },
                     dataType: "text",
                     error: function (jqXHR, textStatus, errorThrown) {
                         console.log(jqXHR, textStatus, jqXHR.status);
                         if (jqXHR.status == 404) {
                             $("#verifyUserAccountCredentialsTypeResponse").empty();
                             $("#verifyUserAccountCredentialsTypeResponse").append(helper.encodeHtmlEntity(jqXHR.responseText));
                             $("#verifyUserAccountCredentialsTypeResponse").show();
                         } else {
                             var jsonString = JSON.stringify(JSON.parse(jqXHR.responseText), null, 2);
                             $("#verifyUserAccountCredentialsTypeResponse").empty();
                             $("#verifyUserAccountCredentialsTypeResponse").append(jsonString);
                             $("#verifyUserAccountCredentialsTypeResponse").show();
                         }

                     },
                     success: function (data, textStatus, jqXHR) {
                         console.log(data);
                         var objectResponse = JSON.parse(data);
                         helper.stringAccessToken = objectResponse.access_token;
                         $(".access_token").val(helper.stringAccessToken);
                         var jsonString = JSON.stringify(JSON.parse(data), null, 2);
                         $("#verifyUserAccountCredentialsTypeResponse").empty();
                         $("#verifyUserAccountCredentialsTypeResponse").append(jsonString);
                         $("#verifyUserAccountCredentialsTypeResponse").show();
                     },
                     complete: function (jqXHR, textStatus) {

                     }
                 });
             } catch (e) {

             }

         });

     $("#verifyLoginButton").button().click(
         function () {
             $("#verifyLoginResponse").hide();
             var baseUrl = getDialogBaseUrl();
             console.log(baseUrl);
             console.log($("#loginNameVerifyLogin").val(), $("#passwordVerifyLogin").val());
             $.ajax({
                 type: "POST",
                 url: baseUrl + helper.endpointApiUrl + "/oauth2/token" + helper.accessToken + helper.stringAccessToken,
                 data: {
                     username: $("#loginNameVerifyLogin").val(),
                     password: $("#passwordVerifyLogin").val(),
                     grant_type: "password"
                 },
                 dataType: "text",
                 error: function (jqXHR, textStatus, errorThrown) {
                     if (jqXHR.status == 404) {
                         $("#verifyLoginResponse").empty();
                         $("#verifyLoginResponse").append(helper.encodeHtmlEntity(jqXHR.responseText));
                         $("#verifyLoginResponse").show();
                     } else {
                         var jsonString = JSON.stringify(JSON.parse(jqXHR.responseText), null, 2);
                         $("#verifyLoginResponse").empty();
                         $("#verifyLoginResponse").append(jsonString);
                         $("#verifyLoginResponse").show();
                     }
                 },
                 success: function (data, textStatus, jqXHR) {
                     var objJson = JSON.parse(data);
                     var jsonString = JSON.stringify(objJson, null, 2);
                     helper.stringAccessToken = objJson.access_token;
                     $(".access_token").val(helper.stringAccessToken);
                     
                     $("#verifyLoginResponse").empty();
                     $("#verifyLoginResponse").append(jsonString);
                     $("#verifyLoginResponse").show();
                 },
                 complete: function (jqXHR, textStatus) {

                 }
             });
         });

     /**** functions support UI ****/

     $(":checkbox").change(function () {
         if (this.checked) {
             $(this).closest(".row").find("input[type=text]").prop('disabled', false);
             if (typeof isEnableUserSecurity !== 'undefined' && isEnableUserSecurity === true) {
                $(this).closest(".row").find(":button").show();
             }
         } else {
             $(this).closest(".row").find("input[type=text]").prop('disabled', true);
             $(this).closest(".row").find(":button").hide();
         }
     });

     $(window).load(function () {
         $("select.request_method").each(function (index) {
             //console.log(index + ": " + $(this).val());
             if ($(this).val() == "GET") {
                 $(this).closest(".api").find(".request_body").hide(function () {
                     $(this).animate({
                         height: 'auto'
                     });
                 });
             } else {
                 $(this).closest(".api").find(".request_body").show(function () {
                     $(this).animate({
                         height: 'auto'
                     });
                 });
             }
         });
         if (typeof serverUrls === 'undefined' || serverUrls === null || serverUrls.length < 1) {
             // console.log ("serverUrls is not found");
             $(".serverUrls").each(function (index) {
                 $(this).hide();
             });
             $(".requestUrl").each(function (index) {
                 $(this).show();
             });
         } else {
             $(".serverUrls").each(function (index) {
                 $(this).show();
             });
             $(".requestUrl").each(function (index) {
                 $(this).hide();
             });
         }
         if (typeof isEnableUserSecurity !== 'undefined' && isEnableUserSecurity === true) {
             $(".authorization-dialog").show()
         }

         $("#loginNameVerifyLoginCredentialsType").val(consts.DEFAULT_USER_ACCOUNT);
         $("#passwordVerifyLoginCredentialsType").val(consts.DEFAULT_USER_ACCOUNT_PASSWORD);

     });
     $("select").bind("click", function () {
         if ($(this).hasClass("request_method")) {
             if ($(this).val() == "GET") {
                 $(this).closest(".api").find(".request_body").hide(function () {
                     $(this).animate({
                         height: 'auto'
                     });
                 });
             } else {
                 $(this).closest(".api").find(".request_body").show(function () {
                     $(this).animate({
                         height: 'auto'
                     });
                 });
             }
         }
     });


     $(":radio").bind("click", function () {
         if ($(this).val() === helper.formUrlEncoded) {
             $(this).closest(".api").find(".x_wwww_form").show(function () {
                 $(this).animate({
                     height: 'auto'
                 });
             });
             $(this).closest(".api").find(".raw").hide(function () {
                 $(this).animate({
                     height: 'auto'
                 });
             });
         } else if ($(this).val() === helper.applicationJson) {
             $(this).closest(".api").find(".x_wwww_form").hide(function () {
                 $(this).animate({
                     height: 'auto'
                 });
             });
             $(this).closest(".api").find(".raw").show(function () {
                 $(this).animate({
                     height: 'auto'
                 });
             });
         }
     });

     $("button").bind("click", function () {
         if ($(this).hasClass("param")) {
             $(this).closest(".api").find(".request_param").toggle(function () {
                 $(this).animate({
                     height: 'auto'
                 });
             }, function () {
                 $(this).animate({
                     height: 'auto'
                 });
             });
         }
     });

     //If you want to target a dynamically added or removed element you'll have to use
     $(document).on("click", ".removeButton", function () {

         if ($(this).closest(".row").parent().hasClass("x_wwww_form")) {
             removeObject($(this));
         } else if ($(this).closest(".row").parent().hasClass("request_param")) {
             removeObject($(this));
         }
     });

     $(document).on("click", ".addButton", function () {
         if ($(this).closest(".row").parent().hasClass("x_wwww_form")) {
             cloneObject($(this).closest(".x_wwww_form"));
         } else if ($(this).closest(".row").parent().hasClass("request_param")) {
             cloneObject($(this).closest(".request_param"));
         }
     });

     function cloneObject(parentElement) {
         var $template = $(".template"),
             $clone = $template.clone().removeClass('hide template').appendTo(parentElement);
     }

     function removeObject(currentElement) {
         currentElement.parents('.row').remove();
     }

     $(document).on("click", ".api-toggle", function () {
         $(this).next(".api").toggle(function () {
             $(this).animate({
                 height: 'auto'
             });
         }, function () {
             $(this).animate({
                 height: 'auto'
             });
         });
     });
     $(document).on ("click",".api-slide-toggle",function () {
    	 $(this).next().next().slideToggle();
     });

 });
