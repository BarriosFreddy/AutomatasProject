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
    private final String GET_TIME = calendar.get(Calendar.DATE)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR)+" "+
            calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+ "- ";

    
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
                        } else {
                            declaradas.add(variable);
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
                output.append(GET_TIME);
                output.append(String.format("Falta el simbolo ; en la linea numero %d |", counter));
                validaPuntoComa = false;
            } else {

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


        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("declare") && line.endsWith(";")) {

                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String derNoUsu = matcher.group().trim();
                    if (derNoUsu != null && !declaradas.contains(derNoUsu) && !noDeclaradasUsadas.contains(derNoUsu)) {
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
    
    public boolean validaParentesis(String line){
        Stack<String> pila = new Stack();
        int lineLength = line.length();
        
        for (int i = 0; i < lineLength; i++) {
            if (line.charAt(i) == '(') {
                pila.push("(");
            }else if (line.charAt(i) == ')') {
                if (!pila.isEmpty()) {
                    pila.pop();
                }else{
                    pila.push(")");
                    break;
                }
            }
        }
        
        return !pila.isEmpty();
    }
}
