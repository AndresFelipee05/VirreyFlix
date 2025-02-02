package org.example;

import java.util.Scanner;

import static org.example.VirreyCode.menuPrincipal;

public class Main {

    public static void main(String[] args) {
        HibernateUtil.getSessionFactory();
        menuPrincipal();
    }
}