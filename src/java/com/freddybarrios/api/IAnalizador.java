/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.freddybarrios.api;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author freddy
 */
public interface IAnalizador {

    ArrayList<String> getVariables(String texto);
    
    Map<String, ArrayList<String>> getResultado(String texto);
}