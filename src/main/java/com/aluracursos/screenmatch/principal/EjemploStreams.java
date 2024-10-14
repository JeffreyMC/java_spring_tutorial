package com.aluracursos.screenmatch.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EjemploStreams {
    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("Brenda", "Rebeca", "Luis", "Diego");
        nombres.stream()
                .sorted()
                .forEach(System.out::println);

    }
}
