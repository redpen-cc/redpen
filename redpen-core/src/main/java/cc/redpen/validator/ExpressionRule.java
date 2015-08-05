package cc.redpen.validator;

import cc.redpen.tokenizer.TokenElement;

import java.util.ArrayList;
import java.util.List;

public class ExpressionRule {
    private List<TokenElement> elements;

    public ExpressionRule() {
        this.elements = new ArrayList<>();
    }

    public void addElement(TokenElement element) {
        this.elements.add(element);
    }
}
