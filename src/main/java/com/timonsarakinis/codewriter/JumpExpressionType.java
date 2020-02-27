package com.timonsarakinis.codewriter;

public enum JumpExpressionType {
    EQUALS("D;JEQ"),
    LESS_THEN("D;JLT"),
    GREATER_THEN("D;JGT"),
    NOT_EQUAL("D;JNE"),
    JUMP("0;JMP");

    private String logicExpression;

    JumpExpressionType(String logicExpression) {
        this.logicExpression = logicExpression;
    }

    public String getLogicExpression() {
        return logicExpression;
    }
}
