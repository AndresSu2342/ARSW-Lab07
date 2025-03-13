package edu.eci.arsw.blueprints.services.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.services.BlueprintFilter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedundancyFilter implements BlueprintFilter {

    @Override
    public Blueprint filter(Blueprint blueprint) {
        List<Point> points = blueprint.getPoints();
        List<Point> filteredPoints = new ArrayList<>();

        if (!points.isEmpty()) {
            filteredPoints.add(points.get(0)); // Agregar el primer punto
            for (int i = 1; i < points.size(); i++) {
                if (points.get(i).getX() != points.get(i - 1).getX() ||
                    points.get(i).getY() != points.get(i - 1).getY()) {
                    filteredPoints.add(points.get(i));
                }
            }
        }

        return new Blueprint(blueprint.getAuthor(), blueprint.getName(), filteredPoints.toArray(new Point[0]));
    }
}
