
public class Animacion extends Trabajo {

    // Tarifa de Q15 por cada hora facturable o fracción
    public static final double Tarifa_hora = 15.0;

    // Cantidad de fotogramas de la animación
    private int cantidadFotogramas;

    // Constructor por defecto, delega en Trabajo y deja los fotogramas en 0
    public Animacion() {
        super();
        cantidadFotogramas = 0;
    }

    // Constructor completo. los datos comunes se pasan a Trabajo y los fotogramas se guardan aquí
    public Animacion(int codigo, String nombreEstudio, String nombreProyecto, double horasComputoEstimada, boolean horarioEjecucion, int cantidadFotogramas) {
        super(codigo, nombreEstudio, nombreProyecto, horasComputoEstimada, horarioEjecucion);
        this.cantidadFotogramas = cantidadFotogramas;
    }

    public void setCantidadFotogramas(int newcantidadFotogramas) {
        // Valida que los fotogramas sean un entero positivo
        if (newcantidadFotogramas <= 0) {
            throw new IllegalArgumentException("La cantidad de fotogramas debe ser un entero positivo.");
        }
        this.cantidadFotogramas = newcantidadFotogramas;
    }

    public int getCantidadFotogramas() {
        return cantidadFotogramas;
    }

    // Calcula las horas por fotogramas, cantidad de fotogramas x 0.02
    protected double horasPorFotogramas() {
        // Se calcula como fotogramas * 2 / 100
        return cantidadFotogramas * 2 / 100.0;
    }

    // Calcula las horas facturables, el mayor entre las horas estimadas y las horas por fotogramas, redondeado hacia arriba
    protected int horasFacturables() {

        // Toma el mayor de los dos valores
        double mayor = Math.max(getHorasComputoEstimada(), horasPorFotogramas());
        // Obtiene la parte entera del valor (descarta los decimales)
        int horasEnteras = (int) mayor;
        // Si había parte decimal, se cobra la hora completa (hora o fracción)
        if (mayor > horasEnteras) {
            horasEnteras++;
        }
        // Devuelve las horas que se facturan
        return horasEnteras;

    }

    // Devuelve el nombre de la categoría
    public String categoria() {
        return "Animación";
    }

    // Calcula el costo, tarifa base según el horario + Q15 por cada hora facturable
    public double calcularCosto() {
        return tarifaBase() + Tarifa_hora * horasFacturables();
    }

    // Devuelve el tiempo estimado de entrega, 24 horas en horario diurno y 36 en nocturno
    public int tiempoEntrega() {
        // Horario diurno
        if (getHorarioEjecucion()) {
            return 24;
        } else {
            // Horario nocturno
            return 36;
        }
    }

    // Las animaciones permiten hasta 3 intentos de ejecución
    public int intentosPermitidos() {
        return 3;
    }

    // Valida la regla común y además que haya al menos un fotograma
    public boolean validacionDeAceptacion() {
        // Reutiliza la validación de Trabajo y agrega la de los fotogramas
        return super.validacionDeAceptacion() && cantidadFotogramas > 0;
    }

    // Devuelve el texto de la regla que incumple una animación rechazada
    public String descripcionRegla() {
        return "Una animación requiere una cantidad de fotogramas entera y positiva.";
    }

    // Devuelve la descripción del trabajo agregando fotogramas, horas por fotogramas y horas facturables
    public String toString() {

        // Parte de la descripción común de Trabajo y le suma los datos propios de la animación
        return super.toString()
            + " | Fotogramas: " + cantidadFotogramas
            + " (" + horasPorFotogramas() + " h por fotogramas"
            + ", " + horasFacturables() + " h facturables)";
    }




    
}
