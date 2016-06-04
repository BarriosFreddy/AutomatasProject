/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freddybarrios.validator;

import com.freddybarrios.api.IAnalizador;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author freddy
 */
public class Analizador implements IAnalizador {

    private final String EXP_REG = "declare\\s[a-zA-Z][a-zA-Z0-9]{0,7}(\\s?,\\s?[a-zA-Z][a-zA-Z0-9]{0,7})?\\s(.+);";
    private final String EXP_REG_USO = "\\s?[a-zA-Z][a-zA-Z0-9]{0,7}\\s?";
    private final ArrayList<String> declaradas,
            declaradasUsadas,
            declaradasNoUsadas,
            noDeclaradasUsadas,
            malDeclaradas;
    boolean validaParentesis, validaPuntoComa;
    StringBuilder output = new StringBuilder();
    Calendar calendar = Calendar.getInstance();
    Stack<String> pilaIf = new Stack();
    Stack<String> pilaFor = new Stack();
    private final String GET_TIME = "[" + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + " "
            + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) + "] ";

    private int countSINO = 0;

    public Analizador() {
        declaradas = new ArrayList<String>();
        declaradasUsadas = new ArrayList<String>();
        declaradasNoUsadas = new ArrayList<String>();
        noDeclaradasUsadas = new ArrayList<String>();
        malDeclaradas = new ArrayList<String>();
        validaParentesis = true;
        validaPuntoComa = true;
    }

    @Override
    public void getVariables(String texto) {
        Pattern pattern = Pattern.compile(EXP_REG);
        String[] lines = texto.split("\n");

        int count = 1;
        for (String line : lines) {
            if (line.startsWith("declare")) {
                Matcher matcher = pattern.matcher(line);
                String[] dividedline = line.split("\\s");
                try {
                    String variable = dividedline[1];
                    if (matcher.find()) {
                        if (variable.contains(",")) {
                            StringTokenizer tokenizer = new StringTokenizer(variable, ",");
                            declaradas.add(tokenizer.nextToken());
                            declaradas.add(tokenizer.nextToken());
                        } else if (!isReserved(variable)) {
                            declaradas.add(variable);
                        } else if (isReserved(variable)) {
                            output.append(GET_TIME);
                            output.append(String.format("Esta variable '%s' es una palabra reservada|", variable));
                        }
                    } else {
                        malDeclaradas.add(variable);
                        output.append(GET_TIME);
                        String aux = String.format("Variable mal declarada, en la linea %d |", count);
                        output.append(aux);
                    }
                } catch (Exception e) {
                    output.append(GET_TIME);
                    output.append("No puede haber espacios despues de la coma al declarar las variables|");
                }
            }
            count++;
        }
    }

    public Map<String, Object> getResultado(String texto) {
        Map<String, Object> map = new HashMap<String, Object>();
        getVariables(texto);
        Pattern pattern = Pattern.compile(EXP_REG_USO);
        String[] lines = texto.split("\n");

        int counter = 1;
        for (String line : lines) {
            if (!line.isEmpty() && !line.endsWith(";")) {
                if ((line.startsWith("si ") && line.endsWith("entonces")) || line.startsWith("para")
                        || line.startsWith("sino")) {
                    emparejarIF(line, counter);
                    emparejarFOR(line, counter);
                } else {
                    output.append(GET_TIME);
                    output.append(String.format("Falta el simbolo ; en la linea numero %d |", counter));
                    validaPuntoComa = false;
                }

            } else {

                if (line.startsWith("finsi") || line.startsWith("finpara")) {
                    emparejarIF(line, counter);
                    emparejarFOR(line, counter);
                }

                if (!line.isEmpty() && !line.startsWith("declare")) {
                    for (String var : declaradas) {
                        if (line.contains(var) && !declaradasUsadas.contains(var)) {
                            declaradasUsadas.add(var);
                        }
                    }

                    if (validaParentesis(line)) {
                        validaParentesis = false;
                        output.append(GET_TIME);
                        output.append(String.format("La sintaxis de los parentesis no es correcta, en la línea %d |", counter));
                    }

                }
                counter++;
            }
        }

        if (!pilaIf.isEmpty()) {
            output.append(GET_TIME);
            String[] aux = pilaIf.peek().split("#");
            output.append(String.format("La sintaxis de las 'si' no es correcta, en la linea %s|", aux[1]));
        }

        if (!pilaFor.isEmpty()) {
            output.append(GET_TIME);
            String[] aux = pilaFor.peek().split("#");
            output.append(String.format("La sintaxis de los 'para' no es correcta, en la linea %s|", aux[1]));
        }
        if (countSINO > 1) {
            output.append(GET_TIME);
            output.append("No puede haber mas de un 'sino'|");
        }

        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("declare") && line.endsWith(";")) {

                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String derNoUsu = matcher.group().trim();
                    if (derNoUsu != null && !declaradas.contains(derNoUsu)
                            && !noDeclaradasUsadas.contains(derNoUsu) && !isReserved(derNoUsu)) {
                        noDeclaradasUsadas.add(derNoUsu);
                    }

                }
            }
        }

        for (String varDer : declaradas) {
            if (!declaradasUsadas.contains(varDer)) {
                declaradasNoUsadas.add(varDer);
            }
        }

        map.put("declaradasUsadas", declaradasUsadas);
        map.put("declaradasNoUsadas", declaradasNoUsadas);
        map.put("noDeclaradasUsadas", noDeclaradasUsadas);
        map.put("malDeclaradas", malDeclaradas);
        map.put("validaParentesis", validaParentesis);
        map.put("validaPuntoComa", validaPuntoComa);
        map.put("output", output.toString());

        return map;

    }

    public boolean validaParentesis(String line) {
        Stack<String> pila = new Stack();
        int lineLength = line.length();

        for (int i = 0; i < lineLength; i++) {
            if (line.charAt(i) == '(') {
                pila.push("(");
            } else if (line.charAt(i) == ')') {
                if (!pila.isEmpty()) {
                    pila.pop();
                } else {
                    pila.push(")");
                    break;
                }
            }
        }

        return !pila.isEmpty();
    }

    public void emparejarIF(String line, int counter) {

        Pattern pattern = Pattern.compile(EXP_REG_USO);

        if (line.startsWith("si ") && line.endsWith("entonces")) {
            int index = line.lastIndexOf("entonces");
            String condition = line.substring(3, --index);

            String[] vars = condition.split("(>|=|<)");
            if (vars.length == 2) {

                for (String variable : vars) {
                    Matcher matcher = pattern.matcher(variable);
                    if (matcher.find()) {
                        String var = matcher.group().trim();
                        if (var != null && !isReserved(var) && !declaradas.contains(var)
                                && !noDeclaradasUsadas.contains(var)) {
                            noDeclaradasUsadas.add(var);
                        }
                    } else {
                        malDeclaradas.add(variable);
                        output.append(GET_TIME);
                        String aux = String.format("Variable mal declarada, en la linea %d |", counter);
                        output.append(aux);
                    }
                }
            } else {
                output.append(GET_TIME);
                output.append(String.format("La condición del si esta mal planteada, en la linea %d |", counter));
            }

            pilaIf.push("si" + "#" + counter);
        } else if (line.startsWith("sino")) {
            countSINO++;
        } else if (line.equals("finsi;")) {
            if (!pilaIf.isEmpty()) {
                pilaIf.pop();
            } else {
                pilaIf.push("finsi;" + "#" + ++counter);
            }
        }
    }

    public void emparejarFOR(String line, int counter) {
        Pattern pattern = Pattern.compile(EXP_REG_USO);

        if (line.startsWith("para") && line.contains("hasta") && line.endsWith("haga")) {

            int index = line.lastIndexOf("haga");
            
            String[] vars = (line.substring(5, --index)).split("\\s");
            
                for (String variable : vars) {
                    
                    Matcher matcher = pattern.matcher(variable);
                    if (matcher.find()) {
                        String var = matcher.group().trim();
                        if (var != null && !isReserved(var) && !declaradas.contains(var)
                                && !noDeclaradasUsadas.contains(var)) {
                            noDeclaradasUsadas.add(var);
                        }
                    } 
                }
           

            pilaFor.push("para" + "#" + counter);
        } else if (line.equals("finpara;")) {
            if (!pilaFor.isEmpty()) {
                pilaFor.pop();
            } else {
                pilaFor.push("finpara" + "#" + counter);
            }
        }
    }

    public boolean isReserved(String word) {
        boolean toggle = false;
        for (String w : RESERVED_WORDS) {
            if (w.equalsIgnoreCase(word)) {
                toggle = true;
            }
        }
        return toggle;
    }

}
