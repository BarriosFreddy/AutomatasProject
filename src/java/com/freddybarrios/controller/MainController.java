/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freddybarrios.controller;

import com.freddybarrios.validator.Analizador;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
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
    private Vbox declaradasUsadas;
    @Wire
    private Vbox declaradasNoUsadas;
    @Wire
    private Vbox noDeclaradasUsadas;

    private Analizador analizador;

    @Listen("onClick = #btnValidar")
    public void testing() {
        String text = tbxEditor.getText();
        analizador = new Analizador();
        Map<String, ArrayList<String>> varMap = analizador.getResultado(text);
        if (!varMap.isEmpty()) {

            print(declaradasUsadas, varMap.get("declaradasUsadas"));
            print(declaradasNoUsadas, varMap.get("declaradasNoUsadas"));
            print(noDeclaradasUsadas, varMap.get("noDeclaradasUsadas"));

        }
    }

    private void clearVbox(Vbox box) {
            if (box.getChildren().size() > 0) {
                Iterator<Component> it = box.getChildren().iterator();
                while (it.hasNext()) {
                    box.removeChild(it.next());
                }
            }
    }

    public void print(Vbox vbox, ArrayList<String> varGroup) {
        try {
            clearVbox(vbox);

            if (varGroup != null) {
                for (String var : varGroup) {
                    Label label = new Label(var);
                    vbox.appendChild(label);
                }
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
