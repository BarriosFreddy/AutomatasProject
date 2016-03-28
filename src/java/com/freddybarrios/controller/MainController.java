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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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

    private Analizador analizador;

    @Listen("onClick = #btnValidar")
    public void testing() {
        if (!tbxEditor.getText().isEmpty()) {
            String text = tbxEditor.getText();
            analizador = new Analizador();
            Map<String, ArrayList<String>> varMap = analizador.getResultado(text);
            clearBox(new Textbox[]{declaradasUsadas, declaradasNoUsadas, noDeclaradasUsadas});

            if (!varMap.isEmpty()) {
                print(declaradasUsadas, varMap.get("declaradasUsadas"));
                print(declaradasNoUsadas, varMap.get("declaradasNoUsadas"));
                print(noDeclaradasUsadas, varMap.get("noDeclaradasUsadas"));
            }
        }
    }

    private void clearBox(Textbox... boxes) {
        for (Textbox box : boxes) {
            box.setText("");
        }
    }

    public void print(Textbox box, ArrayList<String> varGroup) {
        try {
            StringBuilder output = new StringBuilder();
            if (varGroup != null) {
                for (String var : varGroup) {
                    output.append(var);
                    output.append("\n");
                }
                box.setRows(varGroup.size() <= 0 ? 1 : varGroup.size());
                box.setText(output.toString());
            }
        } catch (ConcurrentModificationException exception) {
            System.out.println("ERROR DE MODIFICACIÓN PROGRESIVA: " + exception);
        }
    }

    @Override
    public void doAfterCompose(Component t) throws Exception {
        super.doAfterCompose(t);
    }

}
