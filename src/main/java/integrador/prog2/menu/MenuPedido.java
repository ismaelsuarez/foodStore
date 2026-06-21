package integrador.prog2.menu;

import integrador.prog2.service.ServicioPedido;

import java.util.Scanner;
import integrador.prog2.enums.Estado;
import integrador.prog2.enums.FormaPago;

public class MenuPedido {

    private final Scanner scanner;
    private final ServicioPedido servicioPedido;

    public MenuPedido(Scanner scanner, ServicioPedido servicioPedido) {
        this.scanner = scanner;
        this.servicioPedido = servicioPedido;
    }

    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println("\n=== MENÚ PEDIDOS ===");
            System.out.println("1. Listar pedidos");
            System.out.println("2. Crear pedido");
            System.out.println("3. Actualizar estado");
            System.out.println("4. Actualizar forma de pago");
            System.out.println("5. Eliminar pedido");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1 -> listarPedidos();
                case 2 -> crearPedido();
                case 3 -> actualizarEstado();
                case 4 -> actualizarFormaPago();
                case 5 -> eliminarPedido();
                case 0 -> System.out.println("Volviendo al menú principal...");
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

    private void listarPedidos() {
        var pedidos = servicioPedido.listar();

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }

        for (var pedido : pedidos) {
            System.out.println(pedido);
        }
    }

    private void crearPedido() {
        System.out.println("Crear pedido: pendiente de integrar con Usuario y Producto.");
    }

    private void actualizarEstado() {
        System.out.print("Ingrese ID del pedido: ");
        Long id = leerLong();

        System.out.println("Seleccione nuevo estado:");
        Estado[] estados = Estado.values();

        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }

        int opcion = leerEntero();

        if (opcion < 1 || opcion > estados.length) {
            System.out.println("Estado inválido.");
            return;
        }

        boolean actualizado = servicioPedido.actualizarEstado(id, estados[opcion - 1]);

        if (actualizado) {
            System.out.println("Estado actualizado correctamente.");
        } else {
            System.out.println("No se encontró el pedido.");
        }
    }

    private void actualizarFormaPago() {
        System.out.print("Ingrese ID del pedido: ");
        Long id = leerLong();

        System.out.println("Seleccione nueva forma de pago:");
        FormaPago[] formasPago = FormaPago.values();

        for (int i = 0; i < formasPago.length; i++) {
            System.out.println((i + 1) + ". " + formasPago[i]);
        }

        int opcion = leerEntero();

        if (opcion < 1 || opcion > formasPago.length) {
            System.out.println("Forma de pago inválida.");
            return;
        }

        boolean actualizado = servicioPedido.actualizarFormaPago(id, formasPago[opcion - 1]);

        if (actualizado) {
            System.out.println("Forma de pago actualizada correctamente.");
        } else {
            System.out.println("No se encontró el pedido.");
        }
    }

    private void eliminarPedido() {
        System.out.print("Ingrese ID del pedido a eliminar: ");
        Long id = leerLong();

        if (id == null) {
            System.out.println("ID inválido.");
            return;
        }

        boolean eliminado = servicioPedido.eliminar(id);

        if (eliminado) {
            System.out.println("Pedido eliminado correctamente.");
        } else {
            System.out.println("No se encontró el pedido.");
        }
    }

    private Long leerLong() {
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}