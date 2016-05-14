/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freddybarrios.controller;

import com.freddybarrios.validator.Analizador;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.StringTokenizer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

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
    private Hbox logger;
    
    private final Analizador analizador;

    private Map<String, Object> varMap;
    private final StringBuilder output = new StringBuilder();
    
    public MainController() {
        analizador = new Analizador();
    }

    @Listen("onClick = #btnValidar")
    public void testing() {

        if (!tbxEditor.getText().isEmpty()) {
            clearBox(new Textbox[]{declaradasUsadas, declaradasNoUsadas, noDeclaradasUsadas, malDeclaradas});
            varMap = analizador.getResultado(tbxEditor.getText());
                
            output.append(varMap.get("output"));
            if (!varMap.isEmpty() && (Boolean) varMap.get("validaParentesis") && (Boolean)varMap.get("validaPuntoComa")) {
                print(declaradasUsadas, (ArrayList<String>) varMap.get("declaradasUsadas"));
                print(declaradasNoUsadas, (ArrayList<String>) varMap.get("declaradasNoUsadas"));
                print(noDeclaradasUsadas, (ArrayList<String>) varMap.get("noDeclaradasUsadas"));
                print(malDeclaradas, (ArrayList<String>) varMap.get("malDeclaradas"));
            }
            
        } else {
            output.append("Nothing to analize|");
            showLog(output.toString());
//            Messagebox.show(, "Info", Messagebox.OK, Messagebox.EXCLAMATION);
        }
        
        

    }

    private void clearBox(Textbox... boxes) {
        for (Textbox box : boxes) {
            box.setText("");
        }
    }
    
    public void showLog(String opt){
        StringTokenizer outputs = new StringTokenizer(opt, "|");
        while (outputs.hasMoreTokens()) {
            logger.appendChild(new Label("#"+outputs.nextToken()));
        }
    }

    public void print(Textbox box, ArrayList<String> varGroup) {
            StringBuilder output = new StringBuilder();
            if (varGroup != null) {
                for (String var : varGroup) {
                    output.append(var);
                    output.append("\n");
                }
                box.setRows(varGroup.size() <= 0 ? 1 : varGroup.size());
                box.setText(output.toString());
            }
    }

    @Override
    public void doAfterCompose(Component t) throws Exception {
        super.doAfterCompose(t);
    }

}
