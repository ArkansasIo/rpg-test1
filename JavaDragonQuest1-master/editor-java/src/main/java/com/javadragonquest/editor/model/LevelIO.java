package com.javadragonquest.editor.model;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class LevelIO {
    private static final Gson gson = new Gson();

    public static void save(SceneModel model, File file) throws Exception {
        try (FileWriter fw = new FileWriter(file)) {
            gson.toJson(model, fw);
        }
    }

    public static SceneModel load(File file) throws Exception {
        try (FileReader fr = new FileReader(file)) {
            return gson.fromJson(fr, SceneModel.class);
        }
    }
}
