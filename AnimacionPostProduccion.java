
public class AnimacionPostProduccion extends Animacion {

    // Cargo fijo por la composición final
    public static final double Cargo_composicion = 80.0;
    // Recargo del 5 % sobre el valor declarado de los recursos licenciados
    public static final double Recargo_recursos = 0.05;

    // Valor declarado de los recursos licenciados
    private double valorRecursosLicenciados;

    // Constructor por defecto,delega en Animacion y deja el valor de recursos en 0
    public AnimacionPostProduccion() {
        super();
        valorRecursosLicenciados = 0;
    }

    // Constructor completo. los datos de Trabajo y Animacion se pasan con super y el valor de recursos se guarda aquí
    public AnimacionPostProduccion(int codigo, String nombreEstudio, String nombreProyecto, double horasComputoEstimada, boolean horarioEjecucion, int cantidadFotogramas, double valorRecursosLicenciados) {
        super(codigo, nombreEstudio, nombreProyecto, horasComputoEstimada, horarioEjecucion, cantidadFotogramas);
        this.valorRecursosLicenciados = valorRecursosLicenciados;
    }

    public void setValorRecursosLicenciados(double newvalorRecursosLicenciados) {
        // Valida que el valor sea mayor que 0 
        if (!(newvalorRecursosLicenciados > 0)) {
            throw new IllegalArgumentException("El valor de los recursos licenciados debe ser mayor que 0.");
        }
        this.valorRecursosLicenciados = newvalorRecursosLicenciados;
    }

    public double getValorRecursosLicenciados() {
        return valorRecursosLicenciados;
    }

    // Devuelve el nombre de la categoría
    public String categoria() {
        return "Animación con postproducción";
    }

    // Calcula el costo, costo de una animación + Q80 de composición + 5 % de los recursos licenciados
    public double calcularCosto() {

        // super.calcularCosto() es el costo de Animacio, se le suman los cargos propios
        return super.calcularCosto() + Cargo_composicion + Recargo_recursos * valorRecursosLicenciados;
    }

    // Devuelve el tiempo de entrega, 12 horas más que una animación normal con el mismo horario
    public int tiempoEntrega() {
        return super.tiempoEntrega() + 12;
    }

    // Con postproducción solo se permiten 2 intentos
    public int intentosPermitidos() {
        return 2;
    }

    // Valida lo de Animacion y además que el valor de los recursos licenciados sea positivo
    public boolean validacionDeAceptacion() {
        return super.validacionDeAceptacion() && valorRecursosLicenciados > 0;
    }

    // Devuelve el texto de la regla que incumple una animación con postproducción rechazada
    public String descripcionRegla() {
        return "Una animación con postproducción requiere fotogramas positivos y un valor de recursos licenciados mayor que 0";
    }

    // Devuelve la descripción de Animacion agregando el valor de los recursos licenciados
    public String toString() {
        return super.toString() + " | Recursos licenciados: Q" + valorRecursosLicenciados;
    }

}
