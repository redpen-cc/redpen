/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * redpen.js - javascript RedPen API
 *
 */

/**
 * Call redpen API function with parameters
 * @param method
 * @param parameters
 * @param callback
 */

var redpen = (function ($) {
    var baseUrl = "";

    // basic API call
    var doAPICall = function (method, parameters, callback, type) {
        type = type ? type : "GET";
        $.ajax({
            type: type,
            url: baseUrl + "rest/" + method,
            data: parameters,
            success: function (data) {
                if (callback) {
                    callback(data);
                }
            }
        }).fail(function (err) {
            console.error(err);
        });
    };

    this.setBaseUrl = function(url) {
        baseUrl = url;
    };

    this.detectLanguage = function (text, callback) {
        if (text) {
            doAPICall('document/language', {document: text}, function(data) {
                callback(data.key);
            }, 'POST');
        }
    };

    // validate the document {document: text, lang: [en|ja..]}
    this.validate = function (parameters, callback) {
        doAPICall('document/validate', parameters, callback, "POST");
    };

    // get the current redpen configuration
    this.getRedPens = function (callback) {
        doAPICall('config/redpens', {}, callback);
    };


    // validate the document {document: text, lang: [en|ja..]}
    this.validateJSON = function (parameters, callback) {
        $.ajax({
            type: "POST",
            url: baseUrl + "rest/document/validate/json",
            data: JSON.stringify(parameters),
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                if (callback) {
                    callback(data);
                }
            }
        }).fail(function (err) {
                console.log(err);
            });
    };

    return this;
})(jQuery);
