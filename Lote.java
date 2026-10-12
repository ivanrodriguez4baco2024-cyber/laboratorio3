import java.util.ArrayList;


public class Lote {

    // Estados posibles de un lote
    public static final String En_preparacion = "En preparación";
    public static final String En_ejecucion = "En ejecución";
    public static final String Cerrado = "Cerrado";

    // Código que identifica al lote
    private int codigoLote;
    // Nodo en el que se ejecuta el lote
    private Nodo nodo;
    // Trabajos asignados al lote
    private ArrayList<Trabajo> trabajos;
    // Trabajos del lote que ya tienen resultado registrado (sirve para saber cuándo cerrar el lote)
    private ArrayList<Trabajo> conResultado;
    // Estado actual del lote (uno de los tres estados definidos arriba)
    private String estadoLote;

    // Constructor por defecto, lote sin nodo, en preparación y sin trabajos
    public Lote() {
        codigoLote = 0;
        nodo = null;
        trabajos = new ArrayList<Trabajo>();
        conResultado = new ArrayList<Trabajo>();
        estadoLote = En_preparacion;
    }

    // Constructor completp, el lote nace en preparación y sin trabajos
    public Lote(int codigoLote, Nodo nodo) {
        this.codigoLote = codigoLote;
        this.nodo = nodo;
        this.trabajos = new ArrayList<Trabajo>();
        this.conResultado = new ArrayList<Trabajo>();
        this.estadoLote = En_preparacion;
    }

    public int getCodigoLote() {
        return codigoLote;
    }

    public Nodo getNodo() {
        return nodo;
    }

    // Devuelve una copia de la lista para que nadie la modifique sin pasar por asignar o retirar
    public ArrayList<Trabajo> getTrabajos() {
        return new ArrayList<Trabajo>(trabajos);
    }

    public String getEstadoLote() {
        return estadoLote;
    }

    // Asigna un trabajo al lote. Valida: lote en preparación, trabajo en cola, compatible con el nodo y dentro de la capacidad.
    // Si alguna validación falla, lanza una excepción y no modifica nada.
    public void asignar(Trabajo trabajo) {

        // Solo se puede asignar mientras el lote está en preparación
        if (!estadoLote.equals(En_preparacion)) {
            throw new IllegalStateException("El lote " + codigoLote + " no está en preparación.");
        }
        // El trabajo debe estar en cola (si está asignado a otro lote, en ejecución, completado o cancelado se rechaza)
        if (!trabajo.getEstadoTrabajo().equals(Trabajo.En_cola)) {
            throw new IllegalStateException("El trabajo " + trabajo.getCodigo() + " no está en cola (estado actual: " + trabajo.getEstadoTrabajo() + ").");
        }

        // El trabajo debe ser compatible con el nodo (la categoría decide si requiere GPU)
        if (!trabajo.esCompatible(nodo)) {
            throw new IllegalArgumentException("El trabajo " + trabajo.getCodigo()
                    + " requiere GPU y el nodo " + nodo.getCodigoNodo() + " no cuenta con ella.");
        }
        // La suma de horas estimadas no puede superar la capacidad del nodo. 11e-9 evita errores de decimales
        if (horasAsignadas() + trabajo.getHorasComputoEstimada() > nodo.getCapacidadNodo() + 1e-9) {
            throw new IllegalArgumentException("El trabajo " + trabajo.getCodigo() + " excede la capacidad del nodo: " + "disponible " + String.format(java.util.Locale.US, "%.2f", capacidadDisponible()) + " h y el trabajo requiere " + String.format(java.util.Locale.US, "%.2f", trabajo.getHorasComputoEstimada()) + " h.");
        }

        // Todo es válido, agrega el trabajo al lote
        trabajos.add(trabajo);
        // El trabajo pasa al estado asignado
        trabajo.setEstadoTrabajo(Trabajo.Asignado);
    }

    // Retira un trabajo del lote (solo si está en preparación), el trabajo vuelve a quedar en cola
    public void retirar(Trabajo trabajo) {

        // Solo se pueden retirar trabajos mientras el lote está en preparación
        if (!estadoLote.equals(En_preparacion)) {
            throw new IllegalStateException("Solo se pueden retirar trabajos de un lote en preparación.");
        }

        // El trabajo debe pertenecer a este lote
        if (!trabajos.contains(trabajo)) {
            throw new IllegalArgumentException("El trabajo " + trabajo.getCodigo() + " no pertenece al lote " + codigoLote + ".");
        }

        // Lo saca de la lista del lote
        trabajos.remove(trabajo);
        // Vuelve a quedar en cola
        trabajo.setEstadoTrabajo(Trabajo.En_cola);
    }

    // Inicia el lote, todos sus trabajos pasan a ejecución y cada uno suma un intento
    public void iniciar() {

        // Solo un lote en preparación puede iniciar
        if (!estadoLote.equals(En_preparacion)) {
            throw new IllegalStateException("El lote " + codigoLote + " no está en preparación.");
        }

        // No se puede iniciar un lote vacío
        if (trabajos.isEmpty()) {
            throw new IllegalStateException("No se puede iniciar un lote vacío.");
        }

        // Cada trabajo comienza un nuevo intento y pasa a "en ejecución"
        for (Trabajo trabajo : trabajos) {
            trabajo.iniciarEjecucion();
        }

        // El lote pasa a estar en ejecución
        estadoLote = En_ejecucion;
    }

    // Registra el resultado de un trabajo del lote. Cuando todos tienen resultado, el lote se cierra y el nodo queda libre
    public void registrarResultado(Trabajo trabajo, boolean completado) {

        // Solo se registran resultados cuando el lote está en ejecución
        if (!estadoLote.equals(En_ejecucion)) {
            throw new IllegalStateException("El lote " + codigoLote + " no está en ejecución.");
        }

        // El trabajo debe pertenecer a este lote
        if (!trabajos.contains(trabajo)) {
            throw new IllegalArgumentException("El trabajo " + trabajo.getCodigo() + " no pertenece al lote " + codigoLote + ".");
        }

        // No se puede registrar dos veces el resultado del mismo trabajo en este lote
        if (conResultado.contains(trabajo)) {
            throw new IllegalStateException("El trabajo " + trabajo.getCodigo() + " ya tiene resultado en este lote.");
        }

        // El trabajo actualiza su propio estado según el resultado y su política de intentos
        trabajo.registrarResultado(completado);

        // Anota que este trabajo ya tiene resultado
        conResultado.add(trabajo);
        // Si todos los trabajos del lote ya tienen resultado, el lote se cierra
        if (conResultado.size() == trabajos.size()) {
            estadoLote = Cerrado;
            // El nodo queda libre para otro lote
            nodo.liberar();
        }
    }

    // Calcula la suma de las horas estimadas de los trabajos del lote
    public double horasAsignadas() {

        // Acumulador de horas
        double total = 0;
        // Suma las horas estimadas de cada trabajo (no las facturables)
        for (Trabajo trabajo : trabajos) {
            total += trabajo.getHorasComputoEstimada();
        }
        // Devuelve el total
        return total;
    }

    // Calcula la capacidad del nodo que todavía no está asignada
    public double capacidadDisponible() {
        // Capacidad total del nodo menos las horas ya asignadas
        return nodo.getCapacidadNodo() - horasAsignadas();
    }

    // Indica si el lote está cerrado
    public boolean estaCerrado() {
        // Compara el estado actual con "Cerrado"
        return estadoLote.equals(Cerrado);
    }

    // Devuelve una descripción de una línea del lote, código, nodo, estado, horas, capacidad y trabajos
    public String toString() {
        // Texto con los códigos de los trabajos del lote
        String codigos = "";

        // Recorre los trabajos del lote
        for (Trabajo trabajo : trabajos) {
            // Si ya hay códigos anotados, agrega una coma antes del siguiente
            if (codigos.length() > 0) {
                codigos += ", ";
            }
            // Agrega el código del trabajo actual
            codigos += trabajo.getCodigo();
        }

        // Arma la línea final con todos los datos
        return "Lote " + codigoLote + " | Nodo " + nodo.getCodigoNodo() + " | " + estadoLote + " | Horas asignadas: " + horasAsignadas() + " | Capacidad disponible: " + capacidadDisponible() + " | Trabajos: [" + codigos + "]";
    }

}
