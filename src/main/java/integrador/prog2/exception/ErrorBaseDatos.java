package integrador.prog2.exception;

public class ErrorBaseDatos extends RuntimeException {

    public ErrorBaseDatos(String mensaje) {
        super(mensaje);
    }

    public ErrorBaseDatos(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
