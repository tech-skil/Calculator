import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Calculator {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Basic Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); 
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4));

        // Create input field
        JTextField inputField = new JTextField();
        inputField.setHorizontalAlignment(JTextField.RIGHT);
        inputField.setFont(new Font("Arial", Font.PLAIN, 20)); 
        inputField.setPreferredSize(new Dimension(0, 40)); 
        mainPanel.add(inputField, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.add(mainPanel);

        String[] buttons = {
            "C", "<-", "Delete", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "%","0", ".", "=",
        };

        for (String button : buttons) {
            JButton b = new JButton(button);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(e.getActionCommand(), inputField);
                }
            });
            buttonPanel.add(b);
        }

        frame.setVisible(true);
    }

    private static void handleButtonClick(String button, JTextField inputField) {
        switch (button) {
            case "=":
                evaluateExpression(inputField);
                break;
            case "C":
                inputField.setText("");
                break;
            case "<-":
                String currentText = inputField.getText();
                if (currentText.length() > 0) {
                    inputField.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case "Delete":
                inputField.setText("");
                break;
            case "%":
                inputField.setText(inputField.getText() + "%");
                break;
            default:
                inputField.setText(inputField.getText() + button);
                break;
        }
    }

    private static void evaluateExpression(JTextField inputField) {
        String expression = inputField.getText();
        try {
            double result = evaluateArithmeticExpression(expression);
            inputField.setText(Double.toString(result));
        } catch (ArithmeticException | NumberFormatException ex) {
            inputField.setText("Error");
        }
    }

    private static double evaluateArithmeticExpression(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean isDigitChar() {
                return Character.isDigit(ch);
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else if (eat('%')) x %= parseFactor(); // remainder
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if (isDigitChar() || ch == '.') { 
                    while (isDigitChar() || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }

            boolean eat(int charToEat) {
                while (Character.isWhitespace(ch)) nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
        }.parse();
    }
}
