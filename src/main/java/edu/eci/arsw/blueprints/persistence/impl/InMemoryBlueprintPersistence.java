/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BlueprintsPersistence.
 * Stores blueprints in a thread-safe ConcurrentHashMap.
 * @author hcadavid
 */
@Service
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{

    private final Map<Tuple<String, String>, Blueprint> blueprints = new ConcurrentHashMap<>();

    /**
     * Initializes the persistence layer with sample blueprints.
     */
    public InMemoryBlueprintPersistence() {
        Point[] pts1 = new Point[]{new Point(140, 140), new Point(115, 115)};
        Point[] pts2 = new Point[]{new Point(200, 200), new Point(250, 250)};
        Point[] pts3 = new Point[]{new Point(50, 60), new Point(70, 80)};
        Point[] pts4 = new Point[]{new Point(10, 20), new Point(30, 40)};

        Blueprint bp1 = new Blueprint("Autor1", "Plano1", pts1);
        Blueprint bp2 = new Blueprint("Autor1", "Plano2", pts2);
        Blueprint bp3 = new Blueprint("Autor2", "Plano3", pts3);
        Blueprint bp4 = new Blueprint("Autor3", "Plano4", pts4);

        blueprints.put(new Tuple<>(bp1.getAuthor(), bp1.getName()), bp1);
        blueprints.put(new Tuple<>(bp2.getAuthor(), bp2.getName()), bp2);
        blueprints.put(new Tuple<>(bp3.getAuthor(), bp3.getName()), bp3);
        blueprints.put(new Tuple<>(bp4.getAuthor(), bp4.getName()), bp4);
    }

    /**
     * Saves a new blueprint to the in-memory storage.
     * If a blueprint with the same name already exists, it throws an exception.
     *
     * @param bp the blueprint to save
     * @throws BlueprintPersistenceException if the blueprint already exists
     */
    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (blueprints.putIfAbsent(new Tuple<>(bp.getAuthor(),bp.getName()), bp) != null){
            throw new BlueprintPersistenceException("The given blueprint already exists: "+bp);
        }
        else{
            blueprints.put(new Tuple<>(bp.getAuthor(),bp.getName()), bp);
            System.out.println("Plano guardado correctamente.");
        }
    }

    /**
     * Retrieves a blueprint by author and name.
     *
     * @param author the author of the blueprint
     * @param name the name of the blueprint
     * @return the requested blueprint
     * @throws BlueprintNotFoundException if the blueprint does not exist
     */
    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        Blueprint bp = blueprints.get(new Tuple<>(author, name));
        if (bp == null) {
            throw new BlueprintNotFoundException("No existe el plano: " + name);
        }
        return bp;
    }

    /**
     * Retrieves all blueprints created by a specific author.
     *
     * @param author the author's name
     * @return a set of blueprints created by the author
     * @throws BlueprintNotFoundException if no blueprints are found for the author
     */
    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> result = blueprints.values().stream()
                .filter(bp -> bp.getAuthor().equals(author))
                .collect(Collectors.toSet());

        if (result.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }
        return result;
    }

    /**
     * Retrieves all stored blueprints.
     *
     * @return a set containing all blueprints
     */
    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(blueprints.values());
    }

    /**
     * Updates an existing blueprint.
     *
     * @param author the author of the blueprint
     * @param name the name of the blueprint
     * @param updatedBlueprint the updated blueprint object
     * @throws BlueprintNotFoundException if the blueprint does not exist
     */
    @Override
    public void updateBlueprint(String author, String name, Blueprint updatedBlueprint) throws BlueprintNotFoundException {
        Tuple<String, String> key = new Tuple<>(author, name);

        if (!blueprints.containsKey(key)) {
            throw new BlueprintNotFoundException("El plano no existe: " + name);
        }

        blueprints.put(key, updatedBlueprint);
        System.out.println("Plano actualizado: " + name + " de " + author);
    }

}