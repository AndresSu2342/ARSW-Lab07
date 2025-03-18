/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;

import java.util.Set;

/**
 * Interface that defines the blueprint persistence layer.
 * It provides methods for storing, retrieving, updating, and listing blueprints.
 * @author hcadavid
 */
public interface BlueprintsPersistence {

    /**
     * Saves a new blueprint.
     *
     * @param bp the new blueprint
     * @throws BlueprintPersistenceException if a blueprint with the same name already exists,
     *         or any other low-level persistence error occurs.
     */
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException;

    /**
     * Retrieves a blueprint by its author and name.
     *
     * @param author blueprint's author
     * @param bprintname blueprint's name
     * @return the blueprint of the given name and author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Blueprint getBlueprint(String author,String bprintname) throws BlueprintNotFoundException;

    /**
     * Retrieves all blueprints associated with a specific author.
     *
     * @param author the author's name
     * @return a set of blueprints created by the author
     * @throws BlueprintNotFoundException if no blueprints are found for the author
     */
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException;

    /**
     * Retrieves all stored blueprints.
     *
     * @return a set containing all blueprints
     */
    public Set<Blueprint> getAllBlueprints();

    /**
     * Updates an existing blueprint.
     *
     * @param author the author's name
     * @param name the name of the blueprint
     * @param updatedBlueprint the updated blueprint object
     * @throws BlueprintNotFoundException if the blueprint does not exist
     */
    public void updateBlueprint(String author, String name, Blueprint updatedBlueprint) throws BlueprintNotFoundException;

}