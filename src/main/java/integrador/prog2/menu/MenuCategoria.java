package integrador.prog2.menu;

import integrador.prog2.entities.Categoria;
import integrador.prog2.exception.ErrorAplicacion;
import integrador.prog2.exception.ErrorBaseDatos;
import integrador.prog2.service.ServicioCategoria;

import java.util.List;
import java.util.Scanner;

public class MenuCategoria {

    private final Scanner scanner;
    private final ServicioCategoria servicioCategoria;

    public MenuCategoria(Scanner scanner) {
        this(scanner, new ServicioCategoria());
    }

    public MenuCategoria(Scanner scanner, ServicioCategoria servicioCategoria) {
        this.scanner = scanner;
        this.servicioCategoria = servicioCategoria;
    }

    public void mostrar() {
        int opcion;
        do {
            imprimirMenu();
            opcion = leerEntero("Seleccione: ");
            ejecutarOpcion(opcion);
        } while (opcion != 0);
    }

    private void imprimirMenu() {
        System.out.println();
        System.out.println("=== CATEGORIAS ===");
        System.out.println("1. Listar");
        System.out.println("2. Crear");
        System.out.println("3. Editar");
        System.out.println("4. Eliminar");
        System.out.println("0. Volver");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    listar();
                    break;
                case 2:
                    crear();
                    break;
                case 3:
                    editar();
                    break;
                case 4:
                    eliminar();
                    break;
                case 0:
                    System.out.println("Volviendo al menu principal...");
                    break;
                default:
                    System.out.println("Opcion invalida.");
                    break;
            }
        } catch (ErrorAplicacion e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ErrorBaseDatos e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private void listar() {
        List<Categoria> categorias = servicioCategoria.listar();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorias cargadas.");
            return;
        }

        System.out.println();
        System.out.println("Categorias registradas:");
        for (Categoria categoria : categorias) {
            System.out.printf(
                    "ID: %d | Nombre: %s | Descripcion: %s%n",
                    categoria.getId(),
                    categoria.getNombre(),
                    categoria.getDescripcion() != null ? categoria.getDescripcion() : "-"
            );
        }
    }

    private void crear() {
        String nombre = leerTexto("Nombre: ");
        String descripcion = leerTexto("Descripcion: ");

        Categoria categoria = servicioCategoria.crear(nombre, descripcion);
        System.out.println("Categoria creada correctamente. ID generado: " + categoria.getId());
    }

    private void editar() {
        listar();
        Long id = leerLong("Ingrese el id de la categoria a editar: ");
        Categoria actual = servicioCategoria.buscarPorId(id);

        System.out.println("Deje el campo vacio para mantener el valor actual.");
        System.out.println("Nombre actual: " + actual.getNombre());
        String nombre = leerTexto("Nuevo nombre: ");
        System.out.println("Descripcion actual: " + (actual.getDescripcion() != null ? actual.getDescripcion() : "-"));
        String descripcion = leerTexto("Nueva descripcion: ");

        servicioCategoria.actualizar(id, nombre, descripcion);
        System.out.println("Categoria actualizada correctamente.");
    }

    private void eliminar() {
        listar();
        Long id = leerLong("Ingrese el id de la categoria a eliminar: ");
        if (!confirmar("Confirma la eliminacion? (S/N): ")) {
            System.out.println("Operacion cancelada.");
            return;
        }

        servicioCategoria.eliminar(id);
        System.out.println("Categoria eliminada correctamente.");
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private Long leerLong(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            try {
                return Long.parseLong(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un id valido.");
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private boolean confirmar(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (texto.equalsIgnoreCase("S")) {
                return true;
            }
            if (texto.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Debe ingresar S o N.");
        }
    }
}
