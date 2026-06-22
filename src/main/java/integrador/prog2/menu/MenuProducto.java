package integrador.prog2.menu;

import integrador.prog2.entities.Categoria;
import integrador.prog2.entities.Producto;
import integrador.prog2.exception.ErrorAplicacion;
import integrador.prog2.exception.ErrorBaseDatos;
import integrador.prog2.service.ServicioProducto;

import java.util.List;
import java.util.Scanner;

public class MenuProducto {

    private final Scanner scanner;
    private final ServicioProducto servicioProducto;

    public MenuProducto(Scanner scanner) {
        this.scanner = scanner;
        this.servicioProducto = new ServicioProducto();
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== PRODUCTOS ===");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            opcion = leerEntero();

            try {
                switch (opcion) {
                    case 1 -> listar();
                    case 2 -> crear();
                    case 3 -> editar();
                    case 4 -> eliminar();
                    case 0 -> System.out.println("Volviendo al menú principal...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (ErrorAplicacion | ErrorBaseDatos e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void listar() {
        List<Producto> productos = servicioProducto.listar();
        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        productos.forEach(System.out::println);
    }

    private void crear() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Precio: ");
        double precio = Double.parseDouble(scanner.nextLine());
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Imagen: ");
        String imagen = scanner.nextLine();
        System.out.print("ID de categoría: ");
        long categoriaId = Long.parseLong(scanner.nextLine());

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);

        Producto producto = new Producto(nombre, precio, descripcion, stock, imagen, categoria);
        servicioProducto.crear(producto);
        System.out.println("Producto creado con ID: " + producto.getId());
    }

    private void editar() {
        System.out.print("ID del producto a editar: ");
        long id = Long.parseLong(scanner.nextLine());
        Producto producto = servicioProducto.listar().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ErrorAplicacion("Producto no encontrado."));

        System.out.print("Nuevo nombre (actual: " + producto.getNombre() + "): ");
        producto.setNombre(scanner.nextLine());
        System.out.print("Nuevo precio (actual: " + producto.getPrecio() + "): ");
        producto.setPrecio(Double.parseDouble(scanner.nextLine()));
        System.out.print("Nuevo stock (actual: " + producto.getStock() + "): ");
        producto.setStock(Integer.parseInt(scanner.nextLine()));

        servicioProducto.editar(producto);
        System.out.println("Producto actualizado.");
    }

    private void eliminar() {
        System.out.print("ID del producto a eliminar: ");
        long id = Long.parseLong(scanner.nextLine());
        if (servicioProducto.eliminar(id)) {
            System.out.println("Producto eliminado.");
        } else {
            System.out.println("No se pudo eliminar el producto.");
        }
    }

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
