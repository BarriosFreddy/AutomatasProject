/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freddybarrios.validator;

import com.freddybarrios.api.IAnalizador;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.zkoss.zul.Messagebox;

/**
 *
 * @author freddy
 */
public class Analizador implements IAnalizador {

    private final String EXP_REG = "declare\\s[a-zA-Z][a-zA-Z0-9]{0,7}(\\s?,\\s?[a-zA-Z][a-zA-Z0-9]{0,7})?\\s(.+);";
    private final String EXP_REG_USO = "\\s?[a-zA-Z][a-zA-Z0-9]{0,7}\\s?";
    private final ArrayList<String> declaradas, declaradasUsadas, declaradasNoUsadas, noDeclaradasUsadas, malDeclaradas;
    boolean validaParentesis, validaPuntoComa;
    StringBuilder output = new StringBuilder();

    public Analizador() {
        this.declaradas = new ArrayList<String>();
        this.declaradasUsadas = new ArrayList<String>();
        this.declaradasNoUsadas = new ArrayList<String>();
        this.noDeclaradasUsadas = new ArrayList<String>();
        this.malDeclaradas = new ArrayList<String>();
        this.validaParentesis = true;
        this.validaPuntoComa = true;
    }

    @Override
    public void getVariables(String texto) {
        Pattern pattern = Pattern.compile(EXP_REG);
        String[] lines = texto.split("\n");
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
                    }
                } catch (Exception e) {
                    output.append("No puede haber espacios despues de la coma al declarar las variables|");
                }
            }
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
                output.append(String.format("Falta el simbolo ; en la linea numero %d |", counter));
                validaPuntoComa = false;
            } else {

                if (!line.isEmpty() && !line.startsWith("declare")) {
                    for (String var : declaradas) {
                        if (line.contains(var) && !declaradasUsadas.contains(var)) {
                            declaradasUsadas.add(var);
                        }
                    }
                }

                if (line.contains("(") || line.contains(")")) {
                    int counterOpened = 0, counterClosed = 0;
                    String[] vector = line.split("");

                    for (String caracter : vector) {
                        if (caracter.equals("(")) {
                            counterOpened++;
                        }
                        if (caracter.equals(")")) {
                            counterClosed++;
                        }
                    }

                    if (counterOpened != counterClosed) {
                        validaParentesis = false;
                        output.append(String.format("El numero de parentesis abierto no es igual al numero de cerrados, en la línea %d |", counter));
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
}
