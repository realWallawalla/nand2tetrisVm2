package com.timonsarakinis.codewriter;

public enum JumpExpressionType {
    EQUALS("D;JEQ"),
    LESS_THEN("D;JLT"),
    GREATER_THEN("D;JGT");

    private String logicExpression;

    JumpExpressionType(String logicExpression) {
        this.logicExpression = logicExpression;
    }

    public String getLogicExpression() {
        return logicExpression;
    }
}
