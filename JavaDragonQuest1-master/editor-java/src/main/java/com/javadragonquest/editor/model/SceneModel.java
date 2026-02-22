package com.javadragonquest.editor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SceneModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name = "Untitled";
    private final List<Actor> actors = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void addActor(Actor a) {
        actors.add(a);
    }

    public void removeActor(Actor a) {
        actors.remove(a);
    }
}