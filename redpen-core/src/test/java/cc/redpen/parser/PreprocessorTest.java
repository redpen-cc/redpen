package cc.redpen.parser;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.validator.ValidationError;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class PreprocessorTest {
    private static final Logger LOG = LoggerFactory.getLogger(PreprocessorTest.class);

    private String sampleAsiiDocText = "Instances Overview\n==================\n" + "Author's Name <person@email.address>\nv1.2, 2015-08\n" +
        "\nThis is the optional preamble (an untitled section body). Useful for " +
        "writing simple sectionless documents consisting only of a preamble.\n\n" +

        "NOTE: The abstract, preface, appendix, bibliography, glossary and index section titles are significant ('specialsections').\n" +

        "\n\n:numbered!:\n[abstract]\n" +
        "[suppress]\n" +
        "Instances\n" +
        "---------\n" +
        "In this article, we'll call a computer server that works as a member of a cluster an _instan3ce_. " +
        "for example, as shown in this http://redpen.ignored.url/[mishpelled link], each instance in distributed search engines stores the the fractions of data.\n" +
        "\nSuch distriubuted systems need a component to merge the preliminary results from member instnaces.\n\n\n" +
        ".Instance image\n" +
        "image::images/tiger.png[Instance image]\n\n" +
        "[suppress='SuppressRuleParameter']\n" +
        "Instances\n" +
        "---------\n" +
        "In this article, we'll call a computer server that works as a member of a cluster an _instan3ce_. " +
        "for example, as shown in this http://redpen.ignored.url/[mishpelled link], each instance in distributed search engines stores the the fractions of data.\n" +
        "\nSuch distriubuted systems need a component to merge the preliminary results from member instnaces.\n\n\n" +
        ".Instance image\n" +
        "image::images/tiger.png[Instance image]\n\n" +
        "A sample table:\n\n" +
        ".A sample table\n" +
        "[width=\"60%\",options=\"header\"]\n" +
        "|==============================================\n" +
        "| Option     | Description\n" +
        "| GROUP      | The instance group.\n" +
        "|==============================================\n\n" +
        ".example list\n" +
        "===============================================\n" +
        "Lorum ipum...\n" +
        "===============================================\n\n\n" +
        "[bibliography]\n" +
        "- [[[taoup]]] Eric Steven Raymond. 'The Art of Unix\n" +
        "  Programming'. Addison-Wesley. ISBN 0-13-142901-9.\n" +
        "- [[[walsh-muellner]]] Norman Walsh & Leonard Muellner.\n" +
        "  'DocBook - The Definitive Guide'. O'Reilly & Associates. 1999.\n" +
        "  ISBN 1-56592-580-7.\n\n\n" +
        "[glossary]\n" +
        "Example Glossary\n" +
        "----------------\n" +
        "Glossaries are optional. Glossaries enries are an example of a style\n" +
        "of AsciiDoc labeled lists.\n" +
        "[suppress='SuccessiveWord InvalidExpression Spelling']\n" +
        "The following is an example of a glosssary.\n\n" +
        "[glossary]\n" +
        "A glossary term::\n" +
        "  The corresponding (indented) defnition.\n\n" +
        "A second glossary term::\n" +
        "  The corresponding (indented) definition.\n\n\n" +
        "ifdef::backend-docbook[]\n" +
        "[suppress=without using any quotes]\n" +
        "[index]\n" +
        "Example Index\n" +
        "-------------\n" +
        "////////////////////////////////////////////////////////////////\n" +
        "The index is normally left completely empty, it's contents being\n" +
        "generated automatically by the DocBook toolchain.\n" +
        "////////////////////////////////////////////////////////////////\n" +
        "endif::backend-docbook[]";

    private String sampleMarkdownText =
            "<!-- @suppress -->\n" +
            "# Instances \n" +
            "Some software tools work in more than one machine, and such _distributed_ (cluster) " +
            "systems can handle huge data or tasks.\n\n" +
            "In this article, we'll call a computer server that works as a member of a cluster " +
            "an _instance_. for example, each instance in distributed search engines stores the the" +
            "fractions of data. Such distriubuted systems need a component to merge the preliminary" +
            "results from member instances.";

    @Test
    public void testSuppressErrorsInAsciiDoc() throws UnsupportedEncodingException, RedPenException {

        Document doc = createFileContent(sampleAsiiDocText, DocumentParser.ASCIIDOC);
        assertEquals(4, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleAsiiDocText, DocumentParser.PLAIN);
        assertEquals(0, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleAsiiDocText, DocumentParser.LATEX);
        assertEquals(0, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleAsiiDocText, DocumentParser.MARKDOWN);
        assertEquals(0, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleAsiiDocText, DocumentParser.REVIEW);
        assertEquals(0, doc.getPreprocessorRules().size());
    }

    @Test
    public void testSuppressErrorsInMarkdown() throws UnsupportedEncodingException, RedPenException {

        Document doc = createFileContent(sampleMarkdownText, DocumentParser.ASCIIDOC);
        assertEquals(0, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleMarkdownText, DocumentParser.PLAIN);
        assertEquals(0, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleMarkdownText, DocumentParser.LATEX);
        assertEquals(0, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleMarkdownText, DocumentParser.MARKDOWN);
        assertEquals(1, doc.getPreprocessorRules().size());

        doc = createFileContent(sampleMarkdownText, DocumentParser.REVIEW);
        assertEquals(0, doc.getPreprocessorRules().size());
    }

    @Test
    public void testTriggeredBy() throws UnsupportedEncodingException, RedPenException {

        Document doc = createFileContent(sampleAsiiDocText, DocumentParser.ASCIIDOC);

        assertEquals(doc.getPreprocessorRules().size(), 4);

        List<PreprocessorRule> rules = new ArrayList<>(doc.getPreprocessorRules());
        // sort the rules by line number
        rules.sort((a, b) -> a.getLineNumber() - b.getLineNumber());

        PreprocessorRule rule = rules.get(0);
        // this rule should start on line 9
        assertEquals(rule.getLineNumber(), 14);
        // this rule should end on line 14
        assertEquals(rule.getLineNumberLimit(), 24);
        // line 18 should trigger this rule. The parameter is irrelevant since the rule has no parameters
        assertEquals(true, rule.isTriggeredBy(doc, 18, "this parameter ignored by this rule"));
        // line 26 should not trigger this rule since it is outside the line number range for this rule
        assertEquals(false, rule.isTriggeredBy(doc, 26, "this parameter ignored by this rule"));

        rule = rules.get(2);
        // this rule should start on line 64
        assertEquals(rule.getLineNumber(), 64);
        // this rule should end on line 75
        assertEquals(rule.getLineNumberLimit(), 75);
        // line 18 should not trigger this rule even though the parameter is valid for this rule
        assertEquals(false, rule.isTriggeredBy(doc, 18, "spelling"));
        // line 68 should trigger this rule - the parameter is valid for this rule
        assertEquals(true, rule.isTriggeredBy(doc, 68, "spelling"));
        // line 68 should trigger this rule - the parameter is valid for this rule
        assertEquals(true, rule.isTriggeredBy(doc, 68, "SuccessiveWord"));
        // line 68 should trigger this rule - the parameter is valid for this rule
        assertEquals(true, rule.isTriggeredBy(doc, 68, "invalidexpression"));
        // line 68 should not trigger this rule - the parameter is not valid for this rule
        assertEquals(false, rule.isTriggeredBy(doc, 68, "weakexpression"));
    }

    @Test
    public void testAsciiDocErrorSuppressionSpecificValidator() throws Exception {
        String sampleAsciiDocShortText =
                "[suppress='SuccessiveWord']\n" +
                 "The following is is an example of a glosssary.\n";

        Document doc = createFileContent(sampleAsciiDocShortText, DocumentParser.ASCIIDOC);
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .build();
        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(doc);
        assertEquals(1, errors.size()); //NOTE: Spelling is not specified
    }

    @Test
    public void testAsciiDocErrorSuppression() throws Exception {
        String sampleAsciiDocShortText =
                "[suppress]\n" +
                "= Section 1\n" +
                "The following is is an example of a glosssary.\n";

        Document doc = createFileContent(sampleAsciiDocShortText, DocumentParser.ASCIIDOC);
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .build();
        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(doc);
        assertEquals(0, errors.size());
    }

    @Test
    public void testMarkdownErrorSuppressionSpecificValidator() throws Exception {
        String sampleAsciiDocShortText =
                "<!-- @suppress SuccessiveWord -->\n" +
                "# Section 1\n" +
                "The following is is an example of a glosssary.\n";

        Document doc = createFileContent(sampleAsciiDocShortText, DocumentParser.MARKDOWN);
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .build();
        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(doc);
        assertEquals(1, errors.size()); //NOTE: Spelling is not specified
    }

    @Test
    public void testMarkdownErrorSuppression() throws Exception {
        String sampleAsciiDocShortText =
                "<!-- @suppress -->\n" +
                "# Section 1\n" +
                "The following is is an example of a glosssary.\n";

        Document doc = createFileContent(sampleAsciiDocShortText, DocumentParser.MARKDOWN);
        Configuration configuration = Configuration.builder()
                .addValidatorConfig(new ValidatorConfiguration("Spelling"))
                .addValidatorConfig(new ValidatorConfiguration("SuccessiveWord"))
                .build();
        RedPen redPen = new RedPen(configuration);
        List<ValidationError> errors = redPen.validate(doc);
        assertEquals(0, errors.size());
    }

    private Document createFileContent(String inputDocumentString, DocumentParser parser) {
        Document doc = null;
        try {
            Configuration configuration = Configuration.builder().build();
            doc = parser.parse(
                    inputDocumentString,
                    new SentenceExtractor(configuration.getSymbolTable()),
                    configuration.getTokenizer());
        } catch (RedPenException e) {
            e.printStackTrace();
            fail();
        }
        return doc;
    }
}
