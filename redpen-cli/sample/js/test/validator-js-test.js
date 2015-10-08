/*
tests validator.js implementation.

steps to run this test:
 1. install mocha
 $ npm install -g mocha

 2. run RedPen in server mode
 $ cd $REDPEN_HOME/bin
 $ ./redpen-server

 3. rename validator.js.example to enable the validator implementation
 $ cd $REDPEN_HOME/sample
 $ mv validator.js.example validator.js

 4. run mocha
 $ cd $REDPEN_HOME/sample
 $ mocha

 */
var assert = require('assert');
var redpen = require('./redpen');

describe('redpen-test', function () {
    it('test validator.js', function (done) {
        var request = {
            "document": "This sentence contains toolongword. This sentence doesn't contain too long word.",
            "format": "json2",
            "documentParser": "PLAIN",
            "config": {
                "lang": "en",
                "validators": {
                    "JavaScript": {}

                }
            }
        };
        var assertion = function (errorSentences) {
            // only one sentence contains error
            assert.equal(errorSentences.length, 1);
            firstErrorSentence = errorSentences[0];
            assert.equal(firstErrorSentence.sentence, 'This sentence contains toolongword.');
            // there is one too word exceeds 10 chalacteres long
            assert.equal(1, firstErrorSentence.errors.length);
            assert.equal('[validator.js] word [toolongword.] is too long. length: 12', firstErrorSentence.errors[0].message);
            done();
        };

        redpen.callRedPen(request, assertion);
    });
});

