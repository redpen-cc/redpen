function httpGet(theUrl) {
  var xmlHttp = new XMLHttpRequest();
  xmlHttp.open("GET", theUrl, false);
  xmlHttp.send(null);

  return xmlHttp;
}

function getElement(id) {
  return document.getElementById(id);
}
function validateDocument() {
  var doc = getElement('textarea').value;
  var xmlHttp = httpGet('rest/document/validate?doc=' + doc);
  var result = eval("(" + xmlHttp.responseText + ")");
  var errors = result['errors'];

  var div = getElement("result");

  if (result['document'].length > 0) {
    div.innerHTML = "<h2>Result from document validation</h2>";

    if (errors.length > 0) {
      var html = "<h3>The following errors were found:</h3>";

      for (var i = 0; i < errors.length; i++) {
        html += '<div class="bg-warning">' +
          '<h4 class="alert-warning">' +
          errors[i]['message'] +
          '</h4>' +
          '<p>' +
          errors[i]['sentence'] +
          '</p>' +
          '</div>';
      }

      div.innerHTML += html;
    } else {
      div.innerHTML = "<em>No errors found in this document!</em>";
    }
  } else {
    div.innerHTML = "<em>No document provided.</em>"
  }
}

function clearResult() {
  getElement('textarea').value = '';
  getElement('result').innerHTML = '';
}
