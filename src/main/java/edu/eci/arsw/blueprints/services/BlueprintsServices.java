/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import java.util.stream.Collectors;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing blueprint operations.
 * Handles business logic related to blueprint storage and retrieval.
 * @author hcadavid
 */
@Service
public class BlueprintsServices {

    @Autowired
    BlueprintsPersistence bpp=null;

    @Autowired
    @Qualifier("subsamplingFilter")  // Values: "subsamplingFilter" and "redundancyFilter"
    private BlueprintFilter blueprintFilter;

    /**
     * Adds a new blueprint to the system.
     * @param blueprint the blueprint to be added
     * @throws BlueprintPersistenceException if the blueprint already exists
     */
    public void addNewBlueprint(Blueprint blueprint) throws BlueprintPersistenceException {
        try {
            Blueprint existing = bpp.getBlueprint(blueprint.getAuthor(), blueprint.getName());
            if (existing != null) {
                throw new BlueprintPersistenceException("El plano ya existe: " + blueprint.getName());
            }
        } catch (BlueprintNotFoundException e) {
            bpp.saveBlueprint(blueprint);
        }
    }

    /**
     * Retrieves all stored blueprints, applying the selected filter.
     * @return a set of filtered blueprints
     * @throws BlueprintNotFoundException if no blueprints are found
     */
    public Set<Blueprint> getAllBlueprints() throws BlueprintNotFoundException{
        return bpp.getAllBlueprints();
        //return bpp.getAllBlueprints().stream()        retorno con filtro
                //.map(blueprintFilter::filter)
                //.collect(Collectors.toSet());
    }

    /**
     *
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{
        Blueprint bp = bpp.getBlueprint(author, name);
        if (bp == null) {
            throw new BlueprintNotFoundException("Blueprint not found.");
        }
        return bp;
        // return blueprintFilter.filter(bp);      retorno con filtro
    }

    /**
     *
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException{
        Set<Blueprint> blueprints = bpp.getBlueprintsByAuthor(author);
        if (blueprints == null || blueprints.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }
        return blueprints;
        // return blueprints.stream()                   retorno con filtro
                //.map(blueprintFilter::filter)
                //.collect(Collectors.toSet());
    }

    /**
     * Updates an existing blueprint.
     * @param author blueprint's author
     * @param name blueprint's name
     * @param updatedBlueprint updated blueprint data
     * @throws BlueprintNotFoundException if the blueprint does not exist
     */
    public void updateBlueprint(String author, String name, Blueprint updatedBlueprint) throws BlueprintNotFoundException {
        Blueprint existing = bpp.getBlueprint(author, name);
        if (existing == null) {
            throw new BlueprintNotFoundException("El plano no existe: " + name);
        }
        bpp.updateBlueprint(author, name, updatedBlueprint);
    }

    /**
     * Deletes a blueprint by its author and name.
     * Calls the persistence layer to remove the blueprint.
     *
     * @param author The name of the blueprint's author.
     * @param name The name of the blueprint.
     * @throws BlueprintNotFoundException If the blueprint does not exist.
     */
    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        bpp.deleteBlueprint(author, name);
    }
}