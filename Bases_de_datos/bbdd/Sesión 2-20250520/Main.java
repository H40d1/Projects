package series;

public class Main {

    public static void testInsertaTemporadasConAC() {
        // Crea el gestor de conexiones
        ConnectionManager cm = new ConnectionManager("alumno","bbdd-upm");

        // Crear las tareas
        // Cambia los archivos para hacer mas pruebas
        DataBaseTask[] tasks = { new InsertaTemporadas(), new InsertaTemporadas() };
        String[] data = { "data/temporadas.csv", "data/temporadas_duplicada.csv" };

        // Llamar a run:
        String ok = cm.runTask(tasks, data);
        System.out.println(ok);
    }

    public static void testInsertaTemporadasSinAC() {
        // Crea el gestor de conexiones
        ConnectionManager cm = new ConnectionManagerWithoutAC("estudiante","bbdd-upm");

        // Crear las tareas
        // Cambia los archivos para hacer mas pruebas
        DataBaseTask[] tasks = { new InsertaTemporadas(), new InsertaTemporadas() };
        String[] data = { "data/temporadas.csv", "data/temporadas_duplicada.csv" };

        // Llamar a run:
        String ok = cm.runTask(tasks, data);
        System.out.println(ok);
    }

    public static void main(String[] args) {
        // Comenta o descomenta para probar de distintas maneras
        testInsertaTemporadasConAC();
        // testInsertaTemporadasSinAC();
    }
}
