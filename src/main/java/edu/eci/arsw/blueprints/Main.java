package edu.eci.arsw.blueprints;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        BlueprintsServices blueprintService = context.getBean(BlueprintsServices.class);

        try {
            // Agregar un nuevo blueprint
            Point[] points = new Point[]{new Point(10, 10), new Point(20, 20)};
            Blueprint bp = new Blueprint("john", "mypaint", points);
            blueprintService.addNewBlueprint(bp);

            // Consultar un blueprint
            Blueprint retrievedBp = blueprintService.getBlueprint("john", "mypaint");
            System.out.println("Blueprint retrieved: " + retrievedBp.getName());

            // Consultar blueprints por autor
            System.out.println("Blueprints by author 'john': " + blueprintService.getBlueprintsByAuthor("john"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

