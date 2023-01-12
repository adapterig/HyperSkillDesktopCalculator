package calculator;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.*;

import static javax.swing.SwingConstants.*;

public class Calculator extends JFrame {
    private JLabel equation;
    private JLabel result;

    public Calculator() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 450);
        setName("Calculator");
        setTitle("Calculator");
        initComponents();
        setLayout(null);
        setVisible(true);
    }

    private JButton createButton(int x, int y, int width, int height, String name, String text, ActionListener actionListener) {
        JButton button = new JButton();
        button.setName(name);
        button.setText(text);
        button.setBounds(x, y, width, height);
        button.addActionListener(actionListener);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        return button;
    }

    private JButton createButton(int row, int column, String name, String text, ActionListener actionListener) {
        int leftPadding = 20;
        int topPadding = 100;
        int horizontalSpace = 10;
        int verticalSpace = 10;
        int buttonWidth = 65;
        int buttonHeight = 40;
        return createButton(leftPadding + column * (buttonWidth + horizontalSpace),
                topPadding + row * (buttonHeight + verticalSpace), buttonWidth, buttonHeight, name, text,
                actionListener);
    }

    private JButton createButton(int row, int column, String name, String text) {
        return createButton(row, column, name, text, e -> {
            this.equation.setText(this.equation.getText() + text);
        });
    }

    private void initComponents() {
        this.equation = new JLabel();
        equation.setBounds(30, 65, 270, 30);
        equation.setName("EquationLabel");
        equation.setHorizontalAlignment(RIGHT);
        equation.setForeground(Color.green.darker());
        add(this.equation);

        this.result = new JLabel();
        result.setBounds(30, 40, 270, 30);
        result.setName("ResultLabel");
        result.setHorizontalAlignment(RIGHT);
        result.setText("0");
        result.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        add(this.result);
        add(createButton(0, 0, "Parentheses", "()", e -> {
            insertParenthesis();
        }));
        add(createButton(0, 1, "ClearError", "CE", e -> {
            equation.setForeground(Color.green.darker());
        }));
        add(createButton(0, 2, "Clear", "C", e -> {
            clear();
        }));
        add(createButton(0, 3, "Delete", "Del", e -> {
            delete();
        }));
        add(createButton(1, 0, "PowerTwo", "x²", e -> {
            equation.setText(equation.getText() + "^(2)");
        }));
        add(createButton(1, 1, "PowerY", "^", e -> {
            equation.setText(equation.getText() + "^(");
        }));
        add(createButton(1, 2, "SquareRoot", "\u221A", e -> {
            this.equation.setText(this.equation.getText() + "\u221A(");
        }));
        add(createButton(1, 3, "Divide", "÷", e -> {
            parseAndCheckInput();
            addOperand("÷");

        }));
        add(createButton(2, 0, "Seven", "7"));
        add(createButton(2, 1, "Eight", "8"));
        add(createButton(2, 2, "Nine", "9"));
        add(createButton(2, 3, "Multiply", "×", e -> {
            parseAndCheckInput();
            addOperand("×");

        }));

        add(createButton(3, 0, "Four", "4"));
        add(createButton(3, 1, "Five", "5"));
        add(createButton(3, 2, "Six", "6"));
        add(createButton(3, 3, "Add", "+", e -> {
            parseAndCheckInput();
            addOperand("+");
        }));
        add(createButton(4, 0, "One", "1"));
        add(createButton(4, 1, "Two", "2"));
        add(createButton(4, 2, "Three", "3"));
        add(createButton(4, 3, "Subtract", "-", e -> {
            parseAndCheckInput();
            addOperand("-");

        }));
        add(createButton(5, 0, "PlusMinus", "±", e -> {
            String expression = equation.getText();
            if (expression.isEmpty()) {
                equation.setText(expression + "(-");
            } else if (expression.endsWith("(-")) {
                equation.setText(expression.substring(0, expression.length() - 2));
            } else if (String.valueOf(expression.charAt(expression.length() - 1)).matches("\\d")) {
                int start = 0;
                for (int i = expression.length() - 1; i >= 0; ) {
                    if (String.valueOf(expression.charAt(i)).matches("\\d")) {
                        i--;
                    } else {
                        start = i + 1;
                        break;
                    }
                }
                if (start - 2 >= 0 && expression.substring(start - 2, start).equals("(-")) {
                    equation.setText(expression.substring(0, start - 2) + expression.substring(start));
                } else {
                    equation.setText(expression.substring(0, start) + "(-" + expression.substring(start));
                }

            }
        }));
        add(createButton(5, 1, "Zero", "0"));
        add(createButton(5, 2, "Dot", "."));

        add(createButton(5, 3, "Equals", "=", e -> {
            if (getPrecedence(equation.getText().charAt(equation.getText().length() - 1)) > 0) {
                equation.setForeground(Color.red.darker());
            } else {
                calculate();
            }
        }));


    }

    private void calculate() {
        calculatePostfix(infixToPostFix());
    }

    private void clear() {
        equation.setText("");
        result.setText("0");
    }

    private void delete() {
        int length = equation.getText().length();
        if (length > 0) {
            equation.setText(equation.getText().substring(0, length - 1));
        }
    }

    private List<String> infixToPostFix() {
        // initializing empty String for result
        String result = new String("");
        List<String> resultList = new LinkedList<>();
        StringBuilder operand = new StringBuilder("");

// initializing empty stack
        Deque<Character> stack = new ArrayDeque<>();
        String exp = equation.getText();

        for (int i = 0; i < exp.length(); ++i) {
            char c = exp.charAt(i);

// If the scanned character is an
// operand, add it to output.
            if (Character.isLetterOrDigit(c) || c == '.') {
                result += c;
                operand.append(c);
            }

// If the scanned character is an '(',
// push it to the stack.
            else if (c == '(') {
                stack.push(c);
            }

//  If the scanned character is an ')',
// pop and output from the stack
// until an '(' is encountered.
            else if (c == ')') {
                if (!operand.isEmpty()) {
                    resultList.add(operand.toString());
                    operand.delete(0, operand.length());
                }
                while (!stack.isEmpty()
                        && stack.peek() != '(') {
                    result += stack.peek();
                    resultList.add(stack.peek().toString());
                    stack.pop();
                }

                stack.pop();
            } else // an operator is encountered
            {
                if (!operand.isEmpty()) {
                    resultList.add(operand.toString());
                    operand.delete(0, operand.length());
                }

                while (!stack.isEmpty() && getPrecedence(c) <= getPrecedence(stack.peek())) {

                    result += stack.peek();

                    resultList.add(stack.peek().toString());
                    stack.pop();
                }
                stack.push(c);
            }
        }
        if (!operand.isEmpty()) {
            resultList.add(operand.toString());
        }

// pop all the operators from the stack
        while (!stack.isEmpty()) {
            if (stack.peek() == '(')
                return null;
            result += stack.peek();
            resultList.add(stack.peek().toString());
            stack.pop();
        }
        System.out.println(resultList);
        System.out.println(result);
        return resultList;
        /*String expression = this.equation.getText();
        Stack<Character> stack = new Stack<>();
        StringBuilder operand = new StringBuilder("");
        List<String> result = new LinkedList<>();
        for (int i = 0; i < expression.length(); i++) {
            if (getPrecedence(expression.charAt(i)) == -1) {
                operand.append(expression.charAt(i));
            } else if (expression.charAt(i) == '(') {
                stack.push(expression.charAt(i));
            } else if (expression.charAt(i) == ')') {
                while (!stack.isEmpty()
                        && stack.peek() != '(') {
                    result.add(stack.peek().toString());
                    stack.pop();
                }
                stack.pop();
            } else {
                char currentCh = expression.charAt(i);
                if (stack.empty() || getPrecedence(stack.peek()) < getPrecedence(currentCh)) {
                    stack.push(currentCh);
                    result.add(operand.toString());
                    operand.delete(0, operand.length());

                } else {
                    result.add(operand.toString());
                    operand.delete(0, operand.length());
                    while (!stack.empty() && getPrecedence(stack.peek()) >= getPrecedence(currentCh)) {
                        result.add(stack.pop().toString());
                    }
                    stack.push(currentCh);
                }
            }
        }
        result.add(operand.toString());
        while (!stack.isEmpty()) {
            if (stack.peek() == '(') {
                throw new IllegalArgumentException();
            }
            result.add(stack.pop().toString());
        }
        System.out.println(result);
        return result;*/
    }

    private int getPrecedence(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;

            case '×':
            case '÷':
                return 2;
            case '\u221A':
            case '^':
                return 3;
        }
        return -1;
    }

    private void calculatePostfix(List<String> operandsAndOperators) {
        Stack<String> st = new Stack<>();
        try {
            for (String s : operandsAndOperators) {
                if (getOperator(s.charAt(0)) == null) {
                    st.push(s);
                } else if ("squareRoot".equals(getOperator(s.charAt(0)))) {
                    double num = Double.parseDouble(st.pop());
                    st.push(String.valueOf(Math.sqrt(num)));
                } else {
                    String operator = getOperator(s.charAt(0));
                    double num1 = Double.parseDouble(st.pop());
                    double num2 = st.isEmpty() ? 0 : Double.parseDouble(st.pop());
                    double ans = 0;
                    if ("add".equals(operator)) {
                        ans = num2 + num1;
                    } else if ("subtract".equals(operator)) {
                        ans = num2 - num1;
                    } else if ("multiply".equals(operator)) {
                        ans = num2 * num1;
                    } else if ("divide".equals(operator)) {
                        if (num1 == 0) {
                            equation.setForeground(Color.red.darker());
                        }
                        ans = num2 / num1;
                    } else if ("squareRoot".equals(operator)) {
                        ans = Math.sqrt(num1);
                    } else if ("powerTo".equals(operator)) {
                        ans = Math.pow(num2, num1);
                    }
                    st.push(String.valueOf(ans));
                }
            }
            double result = Double.parseDouble(st.peek());
            if ((int) result == result) {
                this.result.setText(Integer.toString((int) result));
            } else {
                this.result.setText(Double.toString(result));
            }
        } catch (Exception e) {
            equation.setForeground(Color.red.darker());
        }
    }

    private String getOperator(char ch) {
        if (ch == '÷') {
            return "divide";
        } else if (ch == '×') {
            return "multiply";
        } else if (ch == '+') {
            return "add";
        } else if (ch == '-') {
            return "subtract";
        } else if (ch == '^') {
            return "powerTo";
        } else if (ch == '\u221A') {
            return "squareRoot";
        } else if (ch == '(') {
            return "(";
        }
        return null;
    }


    private void parseAndCheckInput() {
        String expression = this.equation.getText();
        if (expression.length() > 0 && expression.charAt(0) == '.') {
            this.equation.setText("0" + expression);
        }
        expression = this.equation.getText();
        if (expression.length() > 0 && expression.charAt(expression.length() - 1) == '.') {
            this.equation.setText(expression + "0");
        }
        if (expression.length() > 0 && getPrecedence(this.equation.getText().charAt(0)) > 0 &&
                this.equation.getText().charAt(0) != '\u221A') {
            this.equation.setText(this.equation.getText().substring(1));
        }
    }

    private void addOperand(String operand) {
        String expression = this.equation.getText();
        if (!(expression.length() == 0)) {
            if (getPrecedence(expression.charAt(expression.length() - 1)) > 0) {
                this.equation.setText(expression.substring(0, expression.length() - 1) + operand);
            } else {
                this.equation.setText(this.equation.getText() + operand);
            }
        }
    }

    private void insertParenthesis() {
        int right = 0;
        int left = 0;
        String expression = this.equation.getText();
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                left++;
            } else if (expression.charAt(i) == ')') {
                right++;
            }
        }
        if (left == right || expression.charAt(expression.length() - 1) == '('
                || getPrecedence(expression.charAt(expression.length() - 1)) > 0) {
            this.equation.setText(expression + "(");
        } else {
            this.equation.setText(expression + ")");
        }
    }
}
