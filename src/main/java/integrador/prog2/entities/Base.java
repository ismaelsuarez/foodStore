package integrador.prog2.entities;

import java.time.LocalDateTime;

public abstract class Base {
    private static long contadorId = 1;

    private Long id;
    private boolean eliminado;
    private LocalDateTime createAt;

    protected Base() {
        this.id = generarId();
        this.eliminado = false;
        this.createAt = LocalDateTime.now();
    }

    public static synchronized Long generarId() {
        return contadorId++;
    }

    public Long getId() {
        return id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
