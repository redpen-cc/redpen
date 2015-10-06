httpReq = require('http');

exports.callRedPen = function (request, assertion) {
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
            assertion(errorSentences);
        });
    });
    req.write(JSON.stringify(request));
    req.end();

}