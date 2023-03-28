package core;

/**
 * Base class representing a player with a name and a number of points
 */
public abstract class Player {
    private String name;
    private int points = 0;

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
