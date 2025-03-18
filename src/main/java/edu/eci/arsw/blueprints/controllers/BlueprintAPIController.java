/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.controllers;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing blueprints.
 * Provides endpoints to retrieve, create, and update blueprints.
 * @author hcadavid
 */
@RestController
@RequestMapping(value = "/blueprints")
public class BlueprintAPIController {

    @Autowired
    private BlueprintsServices blueprintsServices;

    /**
     * Retrieves all available blueprints.
     * @return a ResponseEntity containing the list of blueprints or an error message.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> GetAllBlueprints(){
        try {
            Set<Blueprint> response = blueprintsServices.getAllBlueprints();
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error al obtener los planos", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all blueprints created by a specific author.
     * @param author the author's name
     * @return a ResponseEntity containing the list of blueprints or an error message.
     */

    @RequestMapping(value = "/{author}", method = RequestMethod.GET)
    public ResponseEntity<?> getBlueprintsByAuthor(@PathVariable String author) {
        try {
            Set<Blueprint> blueprintsByAuthor = blueprintsServices.getBlueprintsByAuthor(author);
            return new ResponseEntity<>(blueprintsByAuthor, HttpStatus.OK);
        } catch (BlueprintNotFoundException e) {
            return new ResponseEntity<>("No se encontraron planos para el autor: " + author, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener los planos del autor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a specific blueprint by author and name.
     * @param author the author's name
     * @param bpname the blueprint's name
     * @return a ResponseEntity containing the blueprint or an error message.
     */
    @RequestMapping(value = "/{author}/{bpname}", method = RequestMethod.GET)
    public ResponseEntity<?> getBlueprintByAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            Blueprint blueprint = blueprintsServices.getBlueprint(author, bpname);
            return new ResponseEntity<>(blueprint, HttpStatus.OK);
        } catch (BlueprintNotFoundException e) {
            return new ResponseEntity<>("No se encontró el plano '" + bpname + "' del autor: " + author, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el plano", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a new blueprint.
     * @param blueprint the blueprint to be added
     * @return a ResponseEntity with the status of the operation.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addBlueprint(@RequestBody Blueprint blueprint) {
        try {
            blueprintsServices.addNewBlueprint(blueprint);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (BlueprintPersistenceException e) {
            return new ResponseEntity<>("El plano ya existe o no se pudo registrar", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar el plano", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing blueprint.
     * @param author the author's name
     * @param bpname the blueprint's name
     * @param blueprint the updated blueprint data
     * @return a ResponseEntity with the status of the update operation.
     */
    @RequestMapping(value = "/{author}/{bpname}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateBlueprint(@PathVariable("author") String author, @PathVariable("bpname") String bpname, @RequestBody Blueprint blueprint) {
        try {
            blueprintsServices.updateBlueprint(author, bpname, blueprint);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BlueprintNotFoundException e) {
            return new ResponseEntity<>("El plano no existe", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar el plano", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}