package integrador.prog2.menu;

import integrador.prog2.service.ServicioPedido;

import java.util.Scanner;

public class AppMenu {

    private final Scanner scanner;
    private final MenuCategoria menuCategoria;
    private final MenuProducto menuProducto;
    private final MenuUsuario menuUsuario;
    private final MenuPedido menuPedido;

    public AppMenu() {
        this.scanner = new Scanner(System.in);
        ServicioPedido servicioPedido = new ServicioPedido();
        this.menuCategoria = new MenuCategoria(scanner);
        this.menuProducto = new MenuProducto(scanner);
        this.menuUsuario = new MenuUsuario(scanner);
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
                case 1 -> menuCategoria.mostrar();
                case 2 -> menuProducto.mostrar();
                case 3 -> menuUsuario.mostrar();
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
