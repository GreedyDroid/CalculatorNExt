package com.example.sayed.calculatornext;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.renderscript.Script;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button calButton;
    private TextView editTV, resultTV;
    private static String currentText="";
    private static String editTVSave = "";
    private static String resultTVSave = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTV = (TextView) findViewById(R.id.editTextView);
        resultTV = (TextView) findViewById(R.id.resultTV);

        if(savedInstanceState!=null){
            currentText=savedInstanceState.getString(editTVSave);
            editTV.setText(savedInstanceState.getString(editTVSave));
        //    resultTV.setText(savedInstanceState.getString(resultTVSave).toString());
            resultTVSave=savedInstanceState.getString(resultTVSave);
        }

        resultTV.setText("0");
    }


    // If app is crashed or rotated >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        //Save use current State>>>>>>>>>>>>>>>>>>>>>>>>>
        savedInstanceState.putString(editTVSave, editTV.getText().toString());
     //   savedInstanceState.putString(resultTVSave, resultTV.getText().toString());
        // Always call the superclass so it can save the view hierarchy state>>>>>>>>>>>>>>>
        super.onSaveInstanceState(savedInstanceState);
    }


//get text>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void getButtonFunction(View view) {
        calButton = (Button) view;

        // When user press two logic button at a time>>>>>>>>>>>>>>>>>>>>>>>>>
        if(currentText.length()>0&&(currentText.substring(currentText.length()-1).equals("-")||currentText.substring(currentText.length()-1).equals("+")||
                currentText.substring(currentText.length()-1).equals("÷")||currentText.substring(currentText.length()-1).equals("×"))) {
            if (calButton.getText().toString().equals("-") || calButton.getText().toString().equals("+") ||
                    calButton.getText().toString().equals("÷") || calButton.getText().toString().equals("×")) {
                currentText = currentText.substring(0, currentText.length() - 1);
            }
        }
        // set new text view>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        currentText += calButton.getText().toString();
        editTV.setText(currentText);

        //animate text size while setting numbers>>>>>>>>>>>>>>>>>>
        resultTV.setTextSize(22);
        editTV.setTextSize(44);

        // for long input animation ??????????????????????????
        changeEditTVsize();
    }


    //Change editTV size>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void changeEditTVsize(){
        if (currentText.length()>16){
            editTV.setTextSize(22);
        }else{
            editTV.setTextSize(44);
        }
        resultTV.setTextSize(22);
    }
    //removeOne Character >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void removeOneChar(View view){
        changeEditTVsize();
        if(currentText.equals("")){
            editTV.setText("0");
        }else {
            currentText = currentText.substring(0,currentText.length()-1);
            editTV.setText(currentText);
        }
    }
    // clear all>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void clear(View view) {
        currentText="";
        editTV.setText("0");
        resultTV.setText("0");
    }
    // get result equal button>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void equal(View view) {
        currentText =editTV.getText().toString().replace("×","*");
        currentText=currentText.replace("÷","/");
        try {
            String result = String.valueOf((eval(currentText)));
            resultTV.setText(result);
        } catch (Exception e) {
            resultTV.setText("Syntax Error");
        }
        currentText="";
        if (resultTV.length()>16){
            resultTV.setTextSize(22);
        }else{
            resultTV.setTextSize(44);
        }
        editTV.setTextSize(22);
    }

    //Take number from resultView>>>>>>>>>>>>>>>
    public void ans(View view) {
        if (!resultTV.getText().equals("0")){
            currentText=resultTV.getText().toString();
            editTV.setText(currentText);
            resultTV.setText("0");
        }
    }








//Mehod to calculate number from String>>>>>>>>>>>>>>>>>>>>>>>>
//For Calculating from String>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
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
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}
