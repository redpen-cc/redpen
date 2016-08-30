function validateDocument(document) {
    addError('MyEmbeddedJS ' + getString("property1"), document.getLastSection().getParagraph(0).getSentence(0));
}