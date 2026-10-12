
public class SimulacionFisica extends Trabajo {

    // Tarifa de Q22 por cada hora estimada
    public static final double Tarifa_hora = 22.0;
    // Cargo fijo de Q65 por el uso de GPU
    public static final double Cargo_gpu = 65.0;

    // Resolución de la malla en millones de celdas
    private int resolucionMallas;

    // Constructor por defecto, delega en Trabajo y deja la resolución en 0
    public SimulacionFisica() {
        super();
        resolucionMallas = 0;
    }

    // Constructor completo, los datos comunes se pasan a Trabajo y la resolución se guarda aquí
    public SimulacionFisica(int codigo, String nombreEstudio, String nombreProyecto, double horasComputoEstimada, boolean horarioEjecucion, int resolucionMallas) {
        super(codigo, nombreEstudio, nombreProyecto, horasComputoEstimada, horarioEjecucion);
        this.resolucionMallas = resolucionMallas;
    }

    public void setResolucionMallas(int newresolucionMallas) {
        // Valida que la resolución esté entre 1 y 50 millones de celdas
        if (newresolucionMallas < 1 || newresolucionMallas > 50) {
            throw new IllegalArgumentException("La resolución de malla debe estar entre 1 y 50 millones de celdas.");
        }
        this.resolucionMallas = newresolucionMallas;
    }

    public int getResolucionMallas() {
        return resolucionMallas;
    }

    // Devuelve el nombre de la categoría
    public String categoria() {
        return "Simulación física";
    }

    // Calcula el costo, tarifa base según el horario + Q22 por cada hora estimada + Q65 fijos por uso de GPU
    public double calcularCosto() {
        return tarifaBase() + Tarifa_hora * getHorasComputoEstimada() + Cargo_gpu;
    }

    // Devuelve el tiempo estimado de entrega, siempre 18 horas
    public int tiempoEntrega() {
        return 18;
    }

    // Solo se permite 1 intento, la caché de simulación se descarta si falla
    public int intentosPermitidos() {
        return 1;
    }

    // Las simulaciones físicas exigen un nodo con GPU
    public boolean requiereGPU() {
        return true;
    }

    // Valida la regla común y además, horario nocturno y resolución de malla entre 1 y 50
    public boolean validacionDeAceptacion() {

        // Reutiliza la validación de Trabajo, !getHorarioEjecucion() significa que el horario es nocturno
        return super.validacionDeAceptacion() && !getHorarioEjecucion() && resolucionMallas >= 1 && resolucionMallas <= 50;
    }

    // Devuelve el texto de la regla que incumple una simulación física rechazada
    public String descripcionRegla() {
        return "Una simulación física solo se acepta en horario nocturno y con malla entre 1 y 50 millones de celdas.";
    }

    // Devuelve la descripción del trabajo agregando la resolución de la malla
    public String toString() {

        // Parte de la descripción común de Trabajo y le suma la malla
        return super.toString() + " | Malla: " + resolucionMallas + " M celdas";
    }



    
}
