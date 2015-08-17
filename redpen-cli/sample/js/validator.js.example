function validateSentence(sentence) {
    var content = sentence.getContent().split(" ");
    for(var i = 0; i<content.length;i++){
        if(content[i].length >= 10){
            addError("word [" + content[i] +"] is too long. length: " + content[i].length, sentence);
        }
    }
}

// optionally, you can implement validation logic for document, section
/*
 function preValidateSentence(sentence) {
 }
 function preValidateSection(section) {
 }
 function validateDocument(document) {
 }
 function validateSection(section) {
 }
*/
