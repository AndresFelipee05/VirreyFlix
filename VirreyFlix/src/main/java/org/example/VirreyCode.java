package org.example;

import org.example.model.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static org.example.menuUsuario.menuUsuario;
import static org.example.menuPerfil.menuPerfil;
import static org.example.menuSerie.menuSerie;
import static org.example.menuEpisodio.menuEpisodio;
import static org.example.menuHistorial.menuHistorial;

public class VirreyCode {
    private static final Scanner sc = new Scanner(System.in);

    public static void menuPrincipal() {
        int opcion;
        do {
            System.out.println("\n --- VirreyFlix ---");
            System.out.println("1. Menú de Usuario");
            System.out.println("2. Menú de Perfil");
            System.out.println("3. Menú de Serie");
            System.out.println("4. Menú de Episodio");
            System.out.println("5. Menú de Historial");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            opcion = introduceEntero();

            switch (opcion) {
                case 1 -> menuUsuario();
                case 2 -> menuPerfil();
                case 3 -> menuSerie();
                case 4 -> menuEpisodio();
                case 5 -> menuHistorial();
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción no válida. Por favor, elige una opción del menú.");
            }
        } while (opcion != 0);
    }

    private static int introduceEntero() {
        int numero;
        try {
            numero = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
        return numero;
    }
}
