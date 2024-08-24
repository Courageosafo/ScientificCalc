package com.java.scientific;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;


public class ScientificCalcController {

    @FXML
    private Label label;

    @FXML
    private Button fnButton;


    private static BigDecimal count, temp;
    private static char operation;
    private static boolean hasCount, hasTemp;
    private static StringBuilder builder = new StringBuilder();
    private static String onLabel;
    private static boolean isOnSimple = true;
    private String id;

    private static final int maxDigits = 11;
    private static final String errorString = "err";

    /**
     * Currently performed operations:
     * Addition,
     * Subtraction,
     * Multiplication,
     * Division,
     * Exponentiation of N power,
     * Root Extraction of N index,
     * Logarithm of N base.
     */
    private void performOperation() {
        switch (operation) {
            case '+' -> count = addition(count, temp);
            case '-' -> count = subtraction(count, temp);
            case '*' -> count = multiplication(count, temp);
            case '/' ->  {
                if (temp.doubleValue() == 0.0) {
                    System.out.println("Cannot divide by 0");
                    displayErr();
                    return;
                }
                count = division(count, temp);
            }
            case 'p' ->  {
                if (count.doubleValue() <= 0) {
                    System.out.println("Cannot raise power of a negative number");
                    displayErr();
                    return;
                }
                count = exponentiation(count, temp);
            }
            case 'r' -> {
                if (temp.doubleValue() == 0.0 || !String.valueOf(temp.doubleValue()).endsWith(".0") || count.doubleValue() < 0.0) {
                    System.out.println("Cannot extract root of index 0");
                    displayErr();
                    return;
                }
                count = rootExtraction(count, temp);
            }
            case 'l' -> {
                if (temp.doubleValue() == 1.0 || temp.doubleValue() <= 0.0 || count.doubleValue() < 0.0) {
                    System.out.println("invalid argument or base for this logarithm");
                    displayErr();
                    return;
                }
                count = logBaseN(count, temp);
            }
        }
    }

    /**
     * Appends to the label the number of the corresponding button
     * respecting the limit of maximum digits allowed.
     */
    @FXML
    protected void OnNumberPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals("0"))
            builder = new StringBuilder();

        switch (id) {
            case "zeroButton" ->  {
                if (onLabel.startsWith("0") && (!onLabel.contains("."))) {
                    System.out.println("Cannot append 0");
                    return;
                }
                builder.append(0);
            }
            case "oneButton" -> builder.append(1);
            case "twoButton" -> builder.append(2);
            case "threeButton" -> builder.append(3);
            case "fourButton" -> builder.append(4);
            case "fiveButton" -> builder.append(5);
            case "sixButton" -> builder.append(6);
            case "sevenButton" -> builder.append(7);
            case "eightButton" -> builder.append(8);
            case "nineButton" -> builder.append(9);
            case "piButton" -> {
                builder = new StringBuilder();
                builder.append(round(BigDecimal.valueOf(Math.PI)));
            }
            case "eButton" -> {
                builder = new StringBuilder();
                builder.append(round(BigDecimal.valueOf(Math.E)));
            }
        }

        onLabel = builder.toString();
        if (hasTooManyDigits(onLabel.length())) {
            System.out.println("Maximum limit of digits reached");
            onLabel = onLabel.substring(0, maxDigits);
        }

        label.setText(onLabel);
    }


    @FXML
    protected void onMemoryOperationPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals(""))
            return;

        if (!hasCount) {
            count = new BigDecimal(label.getText());
            hasCount = true;
        } else {
            temp = new BigDecimal(label.getText());
            hasTemp = true;
        }

        builder = new StringBuilder();

        if (hasTemp && operation != 0) {
            performOperation();
            hasTemp = false;
            temp = new BigDecimal("0");
        } else {
            System.out.println("No value to perform the operation");
        }

        onLabel = count.toString();

        if (hasTooManyDigits(onLabel.length())) {
            System.out.println("Result had too many digits");
            displayErr();
            return;
        }

        label.setText(onLabel);

        switch (id) {
            case "plusButton" -> operation = '+';
            case "minusButton" -> operation = '-';
            case "timesButton" -> operation = '*';
            case "divideButton" -> operation = '/';
            case "toThePowerOfNButton" -> operation = 'p';
            case "radicalNButton" -> operation = 'r';
            case "logBaseNButton" -> operation = 'l';
        }
    }


    @FXML
    protected void onEqualsPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (operation != 0) {
            if (label.getText().equals("")) return;
            temp = new BigDecimal(label.getText());
            performOperation();
            operation = 0;
            temp = new BigDecimal("0");
            hasTemp = false;
        } else {
            return;
        }

        builder = new StringBuilder();

        onLabel = String.valueOf(count);
        if (hasTooManyDigits(onLabel.length())) {
            System.out.println("Result had too many digits");
            displayErr();
            return;
        }

        label.setText(onLabel);
        builder.append(onLabel);

        count = new BigDecimal("0");
        hasCount = false;
    }


    @FXML
    protected void onPointPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.contains(".")) return;

        builder = new StringBuilder();
        builder.append(onLabel);
        builder.append(".");
        onLabel = builder.toString();
        label.setText(onLabel);
    }


    @FXML
    protected void onDelPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals("0")) return;

        builder = new StringBuilder();
        onLabel = onLabel.substring(0, onLabel.length() - 1);
        if (onLabel.equals(""))
            onLabel = "0";
        builder.append(onLabel);
        label.setText(onLabel);
    }


    @FXML
    protected void onAcPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        setDefaults();
    }


    @FXML
    protected void onPlusMinusPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        if (onLabel.equals("0"))
            return;

        BigDecimal value = new BigDecimal(onLabel);
        value = value.multiply(new BigDecimal("-1"));
        value = stripDecimalTrailingZeros(value);

        onLabel = String.valueOf(value);
        builder = new StringBuilder();
        builder.append(onLabel);

        if (hasTooManyDigits(onLabel.length())) {
            System.out.println("Result had too many digits");
            displayErr();
            return;
        }

        label.setText(onLabel);
    }


    @FXML
    protected void onFnPressed(ActionEvent e) throws IOException {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        FXMLLoader fxmlLoader;
        onLabel = label.getText();

        if (isOnSimple) {
            isOnSimple = false;
            fxmlLoader = new FXMLLoader(ScientificCalc.class.getResource("complexOperations.fxml"));
        } else {
            isOnSimple = true;
            fxmlLoader = new FXMLLoader(ScientificCalc.class.getResource("simpleOperations.fxml"));
        }

        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ScientificCalc.class.getResource("darcula.css")).toExternalForm());
        ScientificCalcController controller = fxmlLoader.getController();

        controller.label.setText(onLabel);
        controller.fnButton.requestFocus();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    protected void onInstantOperationPressed(ActionEvent e) {
        id = updateId(e);
        System.out.println("Pressed: " + id);

        //error checking
        onLabel = label.getText();
        if (status(onLabel) == -1) return;

        BigDecimal value = new BigDecimal(onLabel);

        switch (id) {
            case "toThePowerOfTwoButton" -> value = value.pow(2);
            case "radicalTwoButton" ->  {
                if (value.doubleValue() < 0.0) {
                    System.out.println("Cannot extract square root of a negative number");
                    displayErr();
                    return;
                }
                value = value.sqrt(new MathContext(15));
            }
            case "percentButton" ->  {
                if (value.doubleValue() < 0.0) {
                    System.out.println("Cannot calculate percentage off a negative number");
                    displayErr();
                    return;
                }
                value = division(value, BigDecimal.valueOf(100)); //divide by 100
            }
            case "logButton" ->  {
                if (value.doubleValue() <= 0.0 || value.doubleValue() == 1.0) {
                    System.out.println("invalid argument for this logarithm");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.log10(value.doubleValue()));
            }
            case "lnButton" ->  {
                if (value.doubleValue() <= 0.0 || value.doubleValue() == 1.0) {
                    System.out.println("invalid argument for this logarithm");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.log(value.doubleValue()));
            }
            case "sinButton" -> value = BigDecimal.valueOf(Math.sin(value.doubleValue()));
            case "cosButton" -> value = BigDecimal.valueOf(Math.cos(value.doubleValue()));
            case "tanButton" -> value = BigDecimal.valueOf(Math.tan(value.doubleValue()));
            case "asinButton" -> {
                if (value.doubleValue() < -1.0 || value.doubleValue() > 1.0) {
                    System.out.println("Out of arc-sine domain");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.asin(value.doubleValue()));
            }
            case "acosButton" -> {
                if (value.doubleValue() < -1.0 || value.doubleValue() > 1.0) {
                    System.out.println("Out of arc-cosine domain");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.acos(value.doubleValue()));
            }
            case "atanButton" -> value = BigDecimal.valueOf(Math.atan(value.doubleValue()));
            case "radicalThreeButton" -> value = BigDecimal.valueOf(Math.cbrt(value.doubleValue()));
            case "reciprocalButton" -> {
                if (value.doubleValue() == 0.0) {
                    System.out.println("Reciprocal of 0 does not exist");
                    displayErr();
                    return;
                }
                value = division(new BigDecimal("1"), value);
            }
            case "factorialButton" -> {
                if (value.toString().contains(".") || value.doubleValue() < 0.0) {
                    System.out.println("Factorial must a be a positive integer");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(factorial(value.doubleValue()));
            }
        }

        if (value.toString().contains(".")) {
            value = round(value);
        }

        onLabel = String.valueOf(value);
        if (hasTooManyDigits(onLabel.length())) {
            System.out.println("Result had too many digits");
            displayErr();
            return;
        }

        builder = new StringBuilder();
        builder.append(onLabel);
        label.setText(onLabel);
    }

    /**
     * Resets all the variables of the program to default values.
     */
    private void setDefaults() {
        count = new BigDecimal("0");
        temp = new BigDecimal("0");
        operation = 0;
        hasCount = false;
        hasTemp = false;
        onLabel = "0";
        label.setText(onLabel);
        builder = new StringBuilder();
    }

    /**
     * Checks whether the application needs to be reset or not.
     */
    private int status(String onLabel) {
        if (onLabel.equals("err")) {
            System.out.println("Resetting all fields to default");
            setDefaults();
            return -1;
        }
        return 0;
    }


    private String updateId(ActionEvent e) {
        return ((Node) e.getSource()).getId();
    }


    private BigDecimal round(BigDecimal d) {
        if (!d.toString().contains("."))
            throw new IllegalArgumentException();
        int leftDigits = d.toString().substring(0, d.toString().indexOf('.')).length();
        d = d.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
        d = stripDecimalTrailingZeros(d);
        return d;
    }


    private BigDecimal stripDecimalTrailingZeros(BigDecimal d) {
        d = d.stripTrailingZeros();
        if (d.scale() < 0)
            d = d.setScale(0, RoundingMode.HALF_UP);
        return d;
    }


    private void displayErr() {
        onLabel = errorString;
        label.setText(onLabel);
    }


    private boolean hasTooManyDigits(int i) {
        return i > maxDigits;
    }


    private BigDecimal addition(BigDecimal a, BigDecimal b) {
        a = a.add(b);
        a = stripDecimalTrailingZeros(a);
        System.out.println("Result = " + a);
        return a;
    }


    private BigDecimal subtraction(BigDecimal a, BigDecimal b) {
        a = a.subtract(b);
        a = stripDecimalTrailingZeros(a);
        System.out.println("Result = " + a);
        return a;
    }


    private BigDecimal multiplication(BigDecimal a, BigDecimal b) {
        a = a.multiply(b);
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result = " + a);
        return a;
    }

    /**
     * Updates a with the quotient of a and b.
     * @return a
     */
    private BigDecimal division(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = a.divide(b, RoundingMode.HALF_UP);
        if (a.toString().contains("."))
            a = round(a);
        //dividing 0 by something is a special case that keeps the scale of 15 because
        //it does not contain a point so round is not run on it
        if (a.toString().contains("E") && a.toString().contains("0"))
            a = new BigDecimal("0");
        System.out.println("Result = " + a);
        return a;
    }


    private BigDecimal exponentiation(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.pow(a.doubleValue(), b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result = " + a);
        return a;
    }


    private BigDecimal rootExtraction(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.pow(a.doubleValue(), 1 / b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result = " + a);
        return a;
    }


    private BigDecimal logBaseN(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.log(a.doubleValue()) / Math.log(b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("Result = " + a);
        return a;
    }


    private double factorial(double a) {
        if (a == 0.0)
            return 1.0;
        else
            return (a * factorial(a - 1));
    }
}
