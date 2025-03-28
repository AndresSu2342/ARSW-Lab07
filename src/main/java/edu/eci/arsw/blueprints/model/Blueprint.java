
package edu.eci.arsw.blueprints.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a blueprint, which consists of an author, a name, and a list of points.
 */
public class Blueprint {

    private String author=null;

    private List<Point> points=null;

    private String name=null;

    /**
     * Constructor for creating a blueprint with a set of points.
     *
     * @param author The author of the blueprint.
     * @param name The name of the blueprint.
     * @param pnts An array of points defining the blueprint.
     */
    public Blueprint(String author,String name,Point[] pnts){
        this.author=author;
        this.name=name;
        points=Arrays.asList(pnts);
    }

    /**
     * Constructor for creating an empty blueprint.
     *
     * @param author The author of the blueprint.
     * @param name The name of the blueprint.
     */
    public Blueprint(String author, String name){
        this.name=name;
        points=new ArrayList<>();
    }

    /**
     * Default constructor.
     */
    public Blueprint() {
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public List<Point> getPoints() {
        return points;
    }

    /**
     * Adds a point to the blueprint.
     *
     * @param p The point to be added.
     */
    public void addPoint(Point p){
        this.points.add(p);
    }

    @Override
    public String toString() {
        return "Blueprint{" + "author=" + author + ", name=" + name + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Blueprint other = (Blueprint) obj;
        if (!Objects.equals(this.author, other.author)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.points.size()!=other.points.size()){
            return false;
        }
        for (int i=0;i<this.points.size();i++){
            if (this.points.get(i)!=other.points.get(i)){
                return false;
            }
        }

        return true;
    }



}