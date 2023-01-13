package com.tfg.mifeed.controlador.utilidades;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class ValidacionesTest {
    private Validaciones validaciones;
    @Before
    public void incio(){
        validaciones = new Validaciones();
    }

    @Test
    public void validacionEmail() {
        assertEquals("vacio", Validaciones.validacionEmail(""));
    }

    @Test
    public void validacionUser() {
        assertEquals("noValido",Validaciones.validacionUser("`+´+´+"));
        assertEquals("vacio",Validaciones.validacionUser(""));
        assertEquals("ok",Validaciones.validacionUser("Miguel"));
    }

    @Test
    public void validacionContraseña() {
        assertEquals("vacia",Validaciones.validacionContraseña("",""));
        assertEquals("noSegura",Validaciones.validacionContraseña("a","a"));
        assertEquals("distintas",Validaciones.validacionContraseña("123asdfc","134wswaDD"));
        assertEquals("ok",Validaciones.validacionContraseña("123MGG","123MGG"));
    }

    @Test
    public void testValidacionContraseña() {
        assertEquals("noSegura",Validaciones.validacionContraseña("1"));
        assertEquals("noSegura",Validaciones.validacionContraseña(""));
    }

    @Test
    public void hashearMD5(){
        assertEquals("ccee5504c9d889922b101124e9e43b71",Validaciones.hashearMD5("hola1234"));
    }
}