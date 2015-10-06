/*
steps to run this test:
1. install mocha
 $ npm install -g mocha

2. run RedPen in server mode
 $ cd $REDPEN_HOME/bin
 $ ./redpen-server

3. enable example validator implementation (or put your Validator implementation in $REDPEN/test)
 $ cd $REDPEN_HOME/sample
 $ mv validator.js.example validator.js

4. run mocha
 $ cd $REDPEN_HOME/sample
 $ mocha

 */
assert = require('assert');
httpReq = require('http');

describe('redpen-test', function () {
    it('test validator.js', function (done) {
        var options = {
            hostname: 'localhost', port: 8080,
            path: '/rest/document/validate/json',
            method: 'POST',
            headers: {'Content-Type': 'application/json'}
        };

        var req = httpReq.request(options, function (res) {
            var data = '';
            res.on('data', function (chunk) {
                data += chunk;
            });
            res.on('end', function () {
                errorSentences = JSON.parse(data).errors;
                // only one sentence contains error
                assert.equal(errorSentences.length, 1);
                firstErrorSentence = errorSentences[0];
                assert.equal(firstErrorSentence.sentence, 'This sentence contains toolongword.');
                // there is one too word exceeds 10 chalacteres long
                assert.equal(1, firstErrorSentence.errors.length);
                assert.equal('[validator.js] word [toolongword.] is too long. length: 12', firstErrorSentence.errors[0].message);
                done();
            });
        });
        req.write(JSON.stringify({
            "document": "This sentence contains toolongword. This sentence doesn't contain too long word.",
            "format": "json2",
            "documentParser": "PLAIN",
            "config": {
                "lang": "en",
                "validators": {
                    "JavaScript": {}

                }
            }
        }));
        req.end();

    });
});

