
public class Nodo {

    // Capacidad máxima de horas de cómputo por lote
    private int capacidadNodo;
    // Indica si el nodo cuenta con GPU
    private boolean cuentaGPU;
    // Código que identifica al nodo
    private int codigoNodo;
    // Estado del nodo, true = ocupado, false = libre
    private boolean estadoNodo;

    // Constructor por defecto, valores vacíos y nodo libre
    public Nodo() {
        capacidadNodo = 0;
        cuentaGPU = false;
        codigoNodo = 0;
        estadoNodo = false;
    }

    // Constructor completo, el nodo nuevo siempre empieza libre
    public Nodo(int capacidadNodo, boolean cuentaGPU, int codigoNodo) {
        this.capacidadNodo = capacidadNodo;
        this.cuentaGPU = cuentaGPU;
        this.codigoNodo = codigoNodo;
        this.estadoNodo = false;
    }

    public void setCapacidadNodo(int newcapacidadNodo) {
        // Valida que la capacidad sea un entero positivo
        if (newcapacidadNodo <= 0) {
            throw new IllegalArgumentException("La capacidad del nodo debe ser un entero positivo.");
        }
        this.capacidadNodo = newcapacidadNodo;
    }

    public int getCapacidadNodo() {
        return capacidadNodo;
    }

    public void setCuentaGPU(boolean newcuentaGPU) {
        this.cuentaGPU = newcuentaGPU;
    }

    public boolean getCuentaGPU() {
        return cuentaGPU;
    }

    public void setCodigoNodo(int newcodigoNodo) {
        // Valida que el código sea un entero positivo
        if (newcodigoNodo <= 0) {
            throw new IllegalArgumentException("El código del nodo debe ser un entero positivo.");
        }
        this.codigoNodo = newcodigoNodo;
    }

    public int getCodigoNodo() {
        return codigoNodo;
    }

    public void setEstadoNodo(boolean newestadoNodo) {
        this.estadoNodo = newestadoNodo;
    }

    public boolean getEstadoNodo() {
        return estadoNodo;
    }

    // Marca el nodo como ocupado por un lote sin cerrar, falla si ya estaba ocupado
    public void ocupar() {

        // Un nodo no puede tener más de un lote sin cerrar
        if (estadoNodo) {
            throw new IllegalStateException("El nodo " + codigoNodo + " ya tiene un lote sin cerrar.");
        }

        // Lo marca como ocupado
        estadoNodo = true;
    }

    // Libera el nodo cuando su lote se cierra
    public void liberar() {

        // Lo marca como libre
        estadoNodo = false;
    }

    // Devuelve una descripción de una línea del nodo, código, capacidad, GPU y disponibilidad
    public String toString() {

        // Convierte el boolean de la GPU a texto
        String gpu;
        if (cuentaGPU) {
            gpu = "Sí";
        } else {
            gpu = "No";
        }

        // Convierte el estado del nodo a texto
        String estado;
        if (estadoNodo) {
            estado = "Ocupado";
        } else {
            estado = "Libre";
        }

        // Arma la línea final con todos los datos
        return "Nodo " + codigoNodo + " | Capacidad: " + capacidadNodo + " h | GPU: " + gpu + " | " + estado;
    }

}