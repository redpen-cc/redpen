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

var redpen = (function () {

    // basic API call
    var doAPICall = function (method, parameters, callback, type) {
        type = type ? type : "GET";
        $.ajax({
            type: type,
            url: "rest/" + method,
            data: parameters,
            success: function (data) {
                if (callback) {
                    callback(data);
                }
            },
            dataType: "json"
        }).fail(function (err) {
                console.log(err);
            });
    };

    // placeholder (and cheap client-side implementation) of a detect-language function
    this.detectLanguage = function (text, callback) {
        var japanese = (text.indexOf('。') != -1) || (text.indexOf('、') != -1) || (text.indexOf('は') != -1);
        callback(japanese ? 'ja' : 'en');
    };

    // validate the document {text: text, lang: [en|ja..]}
    this.validate = function (parameters, callback) {
        doAPICall('document/validate', parameters, callback, "POST");
    };

    // validate the document {text: text, lang: [en|ja..]}
    this.validateBySentence = function (parameters, callback) {
        doAPICall('document/validate_by_sentence', parameters, callback, "POST");
    };
    // get the current redpen configuration
    this.getRedPens = function (callback) {
        doAPICall('config/redpens', {}, callback);
    };

    return this;
})();
