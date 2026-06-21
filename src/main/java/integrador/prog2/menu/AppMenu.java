package integrador.prog2.menu;

import integrador.prog2.service.ServicioPedido;

import java.util.Scanner;

public class AppMenu {

    private final Scanner scanner;
    private final MenuPedido menuPedido;

    public AppMenu() {
        this.scanner = new Scanner(System.in);
        ServicioPedido servicioPedido = new ServicioPedido();
        this.menuPedido = new MenuPedido(scanner, servicioPedido);
    }

    public void iniciar() {
        int opcion;

        do {
            System.out.println("\n=== SISTEMA FOOD STORE ===");
            System.out.println("1. Categorías");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> System.out.println("Menú Categorías pendiente de integrar.");
                case 2 -> System.out.println("Menú Productos pendiente de integrar.");
                case 3 -> System.out.println("Menú Usuarios pendiente de integrar.");
                case 4 -> menuPedido.mostrarMenu();
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}