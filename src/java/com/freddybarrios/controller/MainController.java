/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freddybarrios.controller;

import com.freddybarrios.validator.Analizador;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

/**
 *
 * @author freddy
 */
public class MainController extends SelectorComposer {

    @Wire
    private Textbox tbxEditor;
    @Wire
    private Textbox declaradasUsadas;
    @Wire
    private Textbox declaradasNoUsadas;
    @Wire
    private Textbox noDeclaradasUsadas;
    @Wire
    private Textbox malDeclaradas;
    @Wire
    private Vbox logger;

    private Analizador analizador;

    private Map<String, Object> varMap;
    private final StringBuilder output = new StringBuilder();

    public MainController() {
    }

    @Listen("onClick = #btnValidar")
    public void testing() {
        clearBox(new Textbox[]{declaradasUsadas, declaradasNoUsadas, noDeclaradasUsadas, malDeclaradas});
              
        logger.getChildren().clear();

        if (!tbxEditor.getText().isEmpty()) {
            analizador = new Analizador();
            varMap = analizador.getResultado(tbxEditor.getText());

            output.append(varMap.get("output"));
            showLog(output.toString());
            if (!varMap.isEmpty() && (Boolean) varMap.get("validaParentesis") && (Boolean) varMap.get("validaPuntoComa")) {
                print(declaradasUsadas, (ArrayList<String>) varMap.get("declaradasUsadas"));
                print(declaradasNoUsadas, (ArrayList<String>) varMap.get("declaradasNoUsadas"));
                print(noDeclaradasUsadas, (ArrayList<String>) varMap.get("noDeclaradasUsadas"));
                print(malDeclaradas, (ArrayList<String>) varMap.get("malDeclaradas"));
            }
//            output.delete(0, output.toString().length());
        } else {
            output.append(Calendar.getInstance().getTime());
            output.append("- Nothing to analize|");
            showLog(output.toString());
//            output.delete(0, output.toString().length());
        }
    }

    private void clearBox(Textbox... boxes) {
        for (Textbox box : boxes) {
            box.setValue("");
        }
        
    }

    public void showLog(String opt) {
        StringTokenizer outputs = new StringTokenizer(opt, "|");
        while (outputs.hasMoreTokens()) {
            logger.appendChild(new Label("-"  + outputs.nextToken()));
        }
    }

    public void print(Textbox box, ArrayList<String> varGroup) {
        StringBuilder outputPrint = new StringBuilder();
        if (varGroup != null) {
            for (String var : varGroup) {
                if (var.contains("_")) {
                  String[] vec = var.split("_");
                  outputPrint.append(vec[0]);   
                }else{
                outputPrint.append(var);
                }
                outputPrint.append("\n");
            }
            box.setRows(varGroup.size() <= 0 ? 1 : varGroup.size());
            box.setValue(outputPrint.toString());
            outputPrint.delete(0, outputPrint.toString().length());
        }
    }

    @Override
    public void doAfterCompose(Component t) throws Exception {
        super.doAfterCompose(t);
    }

}
