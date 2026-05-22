package cn.handyplus.lib.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public final class FormulaUtil {
   private static final String TEMPLATE_EXPRESSION_VALUE = "TEMPLATE_EXPRESSION_VALUE";

   private FormulaUtil() {
   }

   public static Long evaluateFormulaToLong(String formula, Map<String, String> map) {
      return NumberUtil.isNumericToLong(evaluateFormula(formula, map), 0L);
   }

   public static Double evaluateFormulaToDouble(String formula, Map<String, String> map) {
      return NumberUtil.isNumericToDouble(evaluateFormula(formula, map), 0.0);
   }

   public static Integer evaluateFormulaToInt(String formula, Map<String, String> map) {
      return evaluateFormulaToDouble(formula, map).intValue();
   }

   public static String evaluateFormula(String formula, Map<String, String> map) {
      Matcher matcher = PatternUtil.TEMPLATE_EXPRESSION_REGEX.matcher(formula);

      while (matcher.find()) {
         String variable = matcher.group(1);
         formula = StrUtil.replace(formula, variable, "TEMPLATE_EXPRESSION_VALUE");

         for (String key : map.keySet()) {
            variable = variable.replace(key, map.get(key));
         }

         String result = calculateExpression(variable);
         formula = formula.replace("TEMPLATE_EXPRESSION_VALUE", result);
      }

      return formula;
   }

   private static String calculateExpression(String expression) {
      List<Object> rpnTokens = toRPN(expression);
      return String.valueOf(calculateRPN(rpnTokens));
   }

   private static List<Object> toRPN(String expression) {
      List<Object> rpn = new ArrayList<>();
      Deque<Character> operatorStack = new ArrayDeque<>();
      int i = 0;

      while (i < expression.length()) {
         char c = expression.charAt(i);
         if (c == ' ') {
            i++;
         } else if (!Character.isDigit(c) && c != '.') {
            if (isOperator(c)) {
               while (!operatorStack.isEmpty() && isOperator(operatorStack.peek()) && getPriority(operatorStack.peek()) >= getPriority(c)) {
                  rpn.add(operatorStack.pop());
               }

               operatorStack.push(c);
               i++;
            } else if (c == '(') {
               operatorStack.push(c);
               i++;
            } else if (c != ')') {
               i++;
            } else {
               while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                  rpn.add(operatorStack.pop());
               }

               if (!operatorStack.isEmpty() && operatorStack.peek() == '(') {
                  operatorStack.pop();
               }

               i++;
            }
         } else {
            int start = i;

            while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
               i++;
            }

            rpn.add(Double.parseDouble(expression.substring(start, i)));
         }
      }

      while (!operatorStack.isEmpty()) {
         char operator = operatorStack.pop();
         if (isOperator(operator)) {
            rpn.add(operator);
         }
      }

      return rpn;
   }

   private static boolean isOperator(char c) {
      return c == '+' || c == '-' || c == '*' || c == '/';
   }

   private static int getPriority(char operator) {
      switch (operator) {
         case '*':
         case '/':
            return 2;
         case '+':
         case '-':
            return 1;
         case ',':
         case '.':
         default:
            return 0;
      }
   }

   private static double calculateRPN(List<Object> rpnTokens) {
      Deque<Double> valueStack = new ArrayDeque<>();

      for (Object token : rpnTokens) {
         if (token instanceof Double) {
            valueStack.push((Double)token);
         } else {
            char operator = (Character)token;
            double operand2 = valueStack.pop();
            double operand1 = valueStack.pop();
            double result = performOperation(operand1, operand2, operator);
            valueStack.push(result);
         }
      }

      return valueStack.pop();
   }

   private static double performOperation(double operand1, double operand2, char operator) {
      BigDecimal bigDecimalOperand1 = BigDecimal.valueOf(operand1);
      BigDecimal bigDecimalOperand2 = BigDecimal.valueOf(operand2);
      switch (operator) {
         case '*':
            return bigDecimalOperand1.multiply(bigDecimalOperand2).doubleValue();
         case '+':
            return bigDecimalOperand1.add(bigDecimalOperand2).doubleValue();
         case ',':
         case '.':
         default:
            throw new RuntimeException("不支持的运算符: " + operator);
         case '-':
            return bigDecimalOperand1.subtract(bigDecimalOperand2).doubleValue();
         case '/':
            if (operand2 == 0.0) {
               throw new RuntimeException("除数不能为0");
            } else {
               return bigDecimalOperand1.divide(bigDecimalOperand2, 2, RoundingMode.HALF_UP).doubleValue();
            }
      }
   }
}
