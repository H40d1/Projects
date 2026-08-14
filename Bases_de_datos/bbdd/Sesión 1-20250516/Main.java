package series;

public class Main {
    public static void main(String[] args) {
        // Crea el gestor de conexiones
        ConnectionManager cm = new ConnectionManager("estudiante","bbdd-upm");

        // Crear las tareas:
        DataBaseTask[] tasks = { new CreateTableValora(), new CreateTableComenta() };
        String[] data = { "", "" };

        // Llamar a run:
        String ok = cm.runTask(tasks, data);
        System.out.println(ok);
    }
}
