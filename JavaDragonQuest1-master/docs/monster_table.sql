-- SQL table for monsters with all editor/game logic fields
CREATE TABLE monsters (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    str INTEGER,
    agi INTEGER,
    hp INTEGER,
    pat INTEGER,
    sr INTEGER,
    dr INTEGER,
    xp INTEGER,
    gp INTEGER,
    group_id INTEGER,
    final_boss BOOLEAN,
    type VARCHAR(32),
    class VARCHAR(32),
    attribute VARCHAR(32),
    subtype VARCHAR(32),
    substat VARCHAR(32),
    script_file VARCHAR(128)
);

-- Example insert
INSERT INTO monsters (id, name, str, agi, hp, pat, sr, dr, xp, gp, group_id, final_boss, type, class, attribute, subtype, substat, script_file)
VALUES (1, 'Slime', 5, 3, 20, 1, 0, 0, 10, 2, 1, false, 'Beast', 'Normal', 'None', 'None', 'None', 'monster_1.vsgraph');

INSERT INTO monsters (id, name, str, agi, hp, pat, sr, dr, xp, gp, group_id, final_boss, type, class, attribute, subtype, substat, script_file)
VALUES (2, 'Dragon', 50, 20, 500, 10, 5, 5, 1000, 500, 2, true, 'Dragon', 'Boss', 'Fire', 'None', 'None', 'monster_2.vsgraph');
