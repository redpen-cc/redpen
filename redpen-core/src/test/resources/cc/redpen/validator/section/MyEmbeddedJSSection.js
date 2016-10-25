function validateSection(section) {
    addError('MyEmbeddedJSSection ' + getString("property3"), section.getParagraph(0).getSentence(0));
}