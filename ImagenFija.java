
public class ImagenFija extends Trabajo {

    // Límite de horas de cómputo estimadas que acepta una imagen fija
    public static final double Max_horas = 2.0;

    // Constructor por defecto, delega en el constructor vacío de Trabajo
    public ImagenFija() {
        super();
    }

    // Constructor completo, los datos comunes se pasan al constructor de Trabajo
    public ImagenFija(int codigo, String nombreEstudio, String nombreProyecto, double horasComputoEstimada, boolean horarioEjecucion) {
        super(codigo, nombreEstudio, nombreProyecto, horasComputoEstimada, horarioEjecucion);
    }

    // Devuelve el nombre de la categoría
    public String categoria() {
        return "Imagen fija";
    }

    // Calcula el costo, Q45 si el horario es diurno y Q30 si es nocturno, sin importar las horas
    public double calcularCosto() {
        // Horario diurno
        if (getHorarioEjecucion()) {
            return 45.0;
        } else {
            // Horario nocturno
            return 30.0;
        }
    }

    // Devuelve el tiempo estimado de entrega, 4 horas en horario diurno y 10 en nocturno
    public int tiempoEntrega() {
        // Horario diurno
        if (getHorarioEjecucion()) {
            return 4;
        } else {
            // Horario nocturno
            return 10;
        }
    }

    // Las imágenes fijas permiten hasta 3 intentos de ejecución
    public int intentosPermitidos() {
        return 3;
    }

    // Valida la regla común y además que las horas estimadas no superen el máximo
    public boolean validacionDeAceptacion() {

        // Reutiliza la validación de Trabajo y agrega el límite de horas
        return super.validacionDeAceptacion() && getHorasComputoEstimada() <= Max_horas;
    }

    // Devuelve el texto de la regla que incumple una imagen fija rechazada
    public String descripcionRegla() {
        return "Una imagen fija no puede superar 2 horas de cómputo estimadas.";
    }



    
}
