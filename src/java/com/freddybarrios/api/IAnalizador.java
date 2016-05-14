/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.freddybarrios.api;

import java.util.Map;

/**
 *
 * @author freddy
 */
public interface IAnalizador {

    void getVariables(String texto);
    
    Map<String, Object> getResultado(String texto);
}
