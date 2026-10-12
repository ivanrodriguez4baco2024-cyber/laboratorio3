
public abstract class Trabajo {

    // Cargo fijo que se cobra por cada intento adicional al primero
    public static final double Cargo_reintento = 25.0;

    // Estados posibles de un trabajo
    public static final String En_cola = "En cola";
    public static final String Asignado = "Asignado";
    public static final String En_ejecucion = "En ejecución";
    public static final String Completado = "Completado";
    public static final String Cancelado = "Cancelado";
    // Arreglo con todos los estados válidos
    public static final String[] Estados = {En_cola, Asignado, En_ejecucion, Completado, Cancelado};

    // Código que identifica al trabajo
    private int codigo;
    // Nombre del estudio que envía el trabajo
    private String nombreEstudio;
    // Nombre del proyecto
    private String nombreProyecto;
    // Horas de cómputo estimadas
    private double horasComputoEstimada;
    // Horario de ejecución, true = diurno, false = nocturno
    private boolean horarioEjecucion;
    // Estado actual del trabajo
    private String estadoTrabajo;
    // Cantidad de veces que se ha intentado ejecutar
    private int vecesEjecutadas;

    // Constructor por defecto, valores vacíos, el trabajo empieza en cola y con 0 intentos
    public Trabajo() {
        this.codigo = 0;
        this.nombreEstudio = "";
        this.nombreProyecto = "";
        this.horasComputoEstimada = 0;
        this.horarioEjecucion = true;
        this.estadoTrabajo = En_cola;
        this.vecesEjecutadas = 0;
    }

    // Constructor con los datos comunestodo trabajo nuevo empieza en cola y con 0 intentos
    public Trabajo(int codigo, String nombreEstudio, String nombreProyecto, double horasComputoEstimada, boolean horarioEjecucion) {
        this.codigo = codigo;
        this.nombreEstudio = nombreEstudio;
        this.nombreProyecto = nombreProyecto;
        this.horasComputoEstimada = horasComputoEstimada;
        this.horarioEjecucion = horarioEjecucion;
        this.estadoTrabajo = En_cola;
        this.vecesEjecutadas = 0;
    }

    public void setCodigo(int newcodigo) {
        // Valida que el código sea un entero positivo
        if (newcodigo <= 0) {
            throw new IllegalArgumentException("El código del trabajo debe ser un entero positivo.");
        }
        this.codigo = newcodigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setNombreEstudio(String newnombreEstudio) {
        // Valida que el nombre no sea nulo ni esté vacío
        if (newnombreEstudio == null || newnombreEstudio.isBlank()) {
            throw new IllegalArgumentException("El nombre del estudio no puede estar vacío.");
        }
        this.nombreEstudio = newnombreEstudio;
    }

    public String getNombreEstudio() {
        return nombreEstudio;
    }

    public void setNombreProyecto(String newnombreProyecto) {
        // Valida que el nombre no sea nulo ni esté vacío
        if (newnombreProyecto == null || newnombreProyecto.isBlank()) {
            throw new IllegalArgumentException("El nombre del proyecto no puede estar vacío.");
        }
        this.nombreProyecto = newnombreProyecto;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setHorasComputoEstimada(double newhorasComputoEstimada) {
        // Valida que las horas sean mayores que 0 (también rechaza NaN)
        if (!(newhorasComputoEstimada > 0)) {
            throw new IllegalArgumentException("Las horas de cómputo estimadas deben ser mayores que 0.");
        }
        this.horasComputoEstimada = newhorasComputoEstimada;
    }

    public double getHorasComputoEstimada() {
        return horasComputoEstimada;
    }

    public void setHorarioEjecucion(boolean newhorarioEjecucion) {
        this.horarioEjecucion = newhorarioEjecucion;
    }

    public boolean getHorarioEjecucion() {
        return horarioEjecucion;
    }

    public void setEstadoTrabajo(String newestadoTrabajo) {
        // Recorre los estados válidos
        for (String estado : Estados) {
            // Si el nuevo estado coincide con alguno válido, lo guarda y termina
            if (estado.equals(newestadoTrabajo)) {
                this.estadoTrabajo = newestadoTrabajo;
                return;
            }
        }

        // Si no coincidió con ninguno, el estado es inválido
        throw new IllegalArgumentException("Estado de trabajo inválido: " + newestadoTrabajo);
    }

    public String getEstadoTrabajo() {
        return estadoTrabajo;
    }

    public void setVecesEjecutadas(int newvecesEjecutadas) {
        // Valida que las veces ejecutadas no sean negativas
        if (newvecesEjecutadas < 0) {
            throw new IllegalArgumentException("Las veces ejecutadas no pueden ser negativas.");
        }
        this.vecesEjecutadas = newvecesEjecutadas;
    }

    public int getVecesEjecutadas() {
        return vecesEjecutadas;
    }

    // Método abstracto, cada categoría devuelve su nombre (se usa en reportes y mensajes)
    public abstract String categoria();

    // Método abstracto, cada categoría calcula cuánto cuesta el trabajo al registrarse
    public abstract double calcularCosto();

    // Método abstracto, cada categoría indica en cuántas horas se estima entregar el resultado
    public abstract int tiempoEntrega();

    // Método abstracto, cada categoría indica cuántos intentos de ejecución permite
    public abstract int intentosPermitidos();

    // Indica si el trabajo exige un nodo con GPU. Por defecto no; SimulacionFisica lo sobrescribe
    public boolean requiereGPU() {
        // La mayoría de categorías puede ejecutarse en cualquier nodo
        return false;
    }

    // Verifica que los datos comunes sean válidos para aceptar el trabajo; las subclases agregan sus reglas
    public boolean validacionDeAceptacion() {

        // Devuelve true solo si el código es positivo, los nombres tienen contenido y las horas son mayores que 0
        return codigo > 0 && !nombreEstudio.isBlank() && !nombreProyecto.isBlank() && horasComputoEstimada > 0;
    }

    // Devuelve el texto de la regla de aceptación, se muestra cuando validacionDeAceptacion() es false
    public String descripcionRegla() {
        // Mensaje de la regla común a todas las categorías
        return "Todo trabajo requiere código positivo, nombres no vacíos y horas de cómputo mayores que 0.";
    }

    // Devuelve la tarifa base según el horario
    protected double tarifaBase() {
        // Si el horario es diurno, la tarifa base es Q60
        if (horarioEjecucion) {
            return 60.0;

        } else {
            // Si es nocturno, la tarifa base es Q40
            return 40.0;
        }
    }

    // Indica si el trabajo puede ejecutarse en el nodo recibido
    public boolean esCompatible(Nodo nodo) {

        // Es compatible si no necesita GPU o si el nodo cuenta con GPU
        return !requiereGPU() || nodo.getCuentaGPU();
    }

    // Devuelve el cargo que genera iniciar una nueva ejecución, Q25 si ya se había ejecutado antes, Q0 si es el primer intento
    public double cargoReintento() {
        // Si ya hubo al menos un intento, el siguiente es un reintento con cargo
        if (vecesEjecutadas >= 1) {
            return Cargo_reintento;
        } else {

            // Si es el primer intento, no hay cargo
            return 0.0;
        }

    }

    // Marca el inicio de una ejecución, suma un intento y pasa el trabajo a "en ejecución"
    public void iniciarEjecucion() {
        // Cuenta el intento que comienza
        vecesEjecutadas++;
        // El trabajo pasa a estar en ejecución
        estadoTrabajo = En_ejecucion;
    }

    // Registra el resultado de la ejecución, completado, vuelve a la cola o se cancela según los intentos permitidos
    public void registrarResultado(boolean completado) {
        // Si terminó bien, queda completado
        if (completado) {
            estadoTrabajo = Completado;
        // Si falló y ya agotó los intentos de su categoría, se cancela
        } else if (vecesEjecutadas >= intentosPermitidos()) {
            estadoTrabajo = Cancelado;
        // Si falló pero aún tiene intentos, vuelve a la cola
        } else {
            estadoTrabajo = En_cola;
        }
    }

    // Convierte el horario a texto legible
    public String textoHorario() {
        // true = diurno
        if (horarioEjecucion) {
            return "Diurno";
        } else {
            // false = nocturno
            return "Nocturno";
        }
    }

    // Devuelve una descripción de una línea del trabajo
    public String toString() {

        // Une código, estudio, proyecto, horas, horario, estado, intentos, costo y tiempo de entrega
        return "[" + categoria() + "] Código " + codigo + " | " + nombreEstudio + " - " + nombreProyecto + " | " + horasComputoEstimada + " h" + " | " + textoHorario() + " | " + estadoTrabajo + " | Intentos: " + vecesEjecutadas + "/" + intentosPermitidos() + " | Costo: Q" + calcularCosto() + " | Entrega: " + tiempoEntrega() + " h";

    }

}