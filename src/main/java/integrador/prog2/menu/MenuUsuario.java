package integrador.prog2.menu;

import integrador.prog2.entities.Usuario;
import integrador.prog2.enums.Rol;
import integrador.prog2.exception.ErrorAplicacion;
import integrador.prog2.exception.ErrorBaseDatos;
import integrador.prog2.service.ServicioUsuario;

import java.util.List;
import java.util.Scanner;

public class MenuUsuario {

    private final Scanner scanner;
    private final ServicioUsuario servicioUsuario;

    public MenuUsuario(Scanner scanner) {
        this.scanner = scanner;
        this.servicioUsuario = new ServicioUsuario();
    }

    public void mostrar() {
        int opcion;
        do {
            System.out.println("\n=== USUARIOS ===");
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
        List<Usuario> usuarios = servicioUsuario.listar();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
            return;
        }
        usuarios.forEach(System.out::println);
    }

    private void crear() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Mail: ");
        String mail = scanner.nextLine();
        System.out.print("Celular: ");
        String celular = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        System.out.print("Rol (ADMIN/USUARIO): ");
        Rol rol = Rol.valueOf(scanner.nextLine().toUpperCase());

        Usuario usuario = new Usuario(nombre, apellido, mail, celular, contrasena, rol);
        servicioUsuario.crear(usuario);
        System.out.println("Usuario creado con ID: " + usuario.getId());
    }

    private void editar() {
        System.out.print("ID del usuario a editar: ");
        long id = Long.parseLong(scanner.nextLine());
        Usuario usuario = servicioUsuario.listar().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ErrorAplicacion("Usuario no encontrado."));

        System.out.print("Nuevo nombre (actual: " + usuario.getNombre() + "): ");
        usuario.setNombre(scanner.nextLine());
        System.out.print("Nuevo apellido (actual: " + usuario.getApellido() + "): ");
        usuario.setApellido(scanner.nextLine());
        System.out.print("Nuevo mail (actual: " + usuario.getMail() + "): ");
        usuario.setMail(scanner.nextLine());

        servicioUsuario.editar(usuario);
        System.out.println("Usuario actualizado.");
    }

    private void eliminar() {
        System.out.print("ID del usuario a eliminar: ");
        long id = Long.parseLong(scanner.nextLine());
        if (servicioUsuario.eliminar(id)) {
            System.out.println("Usuario eliminado.");
        } else {
            System.out.println("No se pudo eliminar el usuario.");
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
