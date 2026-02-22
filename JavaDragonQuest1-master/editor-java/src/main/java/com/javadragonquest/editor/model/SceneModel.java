package com.javadragonquest.editor.model;

import java.util.ArrayList;
import java.util.List;

public class SceneModel {
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
