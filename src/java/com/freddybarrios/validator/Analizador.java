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

    private final String EXP_REG = "declare\\s[a-zA-Z][a-zA-Z0-9]{0,7}(\\s?,\\s?[a-zA-Z][a-zA-Z0-9]{0,7})?\\s";
    private final String EXP_REG_USO = "\\s?[a-zA-Z][a-zA-Z0-9]{0,7}\\s?";
    private ArrayList<String> declaradas, declaradasUsadas, declaradasNoUsadas, noDeclaradasUsadas;

    public Analizador() {
        this.declaradas = new ArrayList<String>();
        this.declaradasUsadas = new ArrayList<String>();
        this.declaradasNoUsadas = new ArrayList<String>();
        this.noDeclaradasUsadas = new ArrayList<String>();
    }

    @Override
    public ArrayList<String> getVariables(String texto) {
        Pattern pattern = Pattern.compile(EXP_REG);
        String[] lines = texto.split("\n");
        for (String line : lines) {
            if (line.startsWith("declare")) {
                Matcher matcher = pattern.matcher(line);
                String[] dividedline = line.split("\\s");
                try{
                String variable = dividedline[1];
                if (matcher.find()) {
                    if (variable.contains(",")) {
                        StringTokenizer tokenizer = new StringTokenizer(variable, ",");
                        declaradas.add(tokenizer.nextToken());
                        declaradas.add(tokenizer.nextToken());
                    } else {
                        declaradas.add(variable);
                    }
                }
                }catch(Exception e){
                    Messagebox.show("No puede haber espacios despues de la coma al declarar las variables", "Info", Messagebox.OK, Messagebox.EXCLAMATION);
                }
            }
        }
        return declaradas;
    }

    public Map<String, ArrayList<String>> getResultado(String texto) {
        Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        ArrayList<String> varDeclaradas = getVariables(texto);

        Pattern pattern = Pattern.compile(EXP_REG_USO);
        String[] lines = texto.split("\n");

        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("declare")) {
                for (String var : varDeclaradas) {
                    if (line.contains(var) && !declaradasUsadas.contains(var)) {
                        declaradasUsadas.add(var);
                    }
                }
            }
        }
//
        
        
        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("declare")) {

                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                        String derNoUsu = matcher.group().trim();
                        if (derNoUsu != null && !declaradas.contains(derNoUsu) && !noDeclaradasUsadas.contains(derNoUsu)) {
                            noDeclaradasUsadas.add(derNoUsu);
                        }

                }
            }
        }

        for (String varDer : varDeclaradas) {
            if (!declaradasUsadas.contains(varDer)) {
                declaradasNoUsadas.add(varDer);
            }
        }

        map.put("declaradasUsadas", declaradasUsadas);
        map.put("declaradasNoUsadas", declaradasNoUsadas);
        map.put("noDeclaradasUsadas", noDeclaradasUsadas);

        return map;
    }
}
