-- SQL table for monster visual scripting graphs
CREATE TABLE monster_scripts (
    monster_id INTEGER,
    script_file VARCHAR(128),
    PRIMARY KEY (monster_id),
    FOREIGN KEY (monster_id) REFERENCES monsters(id)
);

-- SQL table for monster combat events (example for extensibility)
CREATE TABLE monster_combat_events (
    event_id INTEGER PRIMARY KEY,
    monster_id INTEGER,
    event_type VARCHAR(32),
    trigger_condition VARCHAR(128),
    action VARCHAR(128),
    FOREIGN KEY (monster_id) REFERENCES monsters(id)
);

-- SQL table for monster attributes history (for balancing/telemetry)
CREATE TABLE monster_attribute_history (
    history_id INTEGER PRIMARY KEY,
    monster_id INTEGER,
    attribute VARCHAR(32),
    old_value INTEGER,
    new_value INTEGER,
    change_date DATETIME,
    FOREIGN KEY (monster_id) REFERENCES monsters(id)
);

-- SQL table for monster loot tables
CREATE TABLE monster_loot (
    loot_id INTEGER PRIMARY KEY,
    monster_id INTEGER,
    item_name VARCHAR(64),
    drop_rate FLOAT,
    FOREIGN KEY (monster_id) REFERENCES monsters(id)
);

-- SQL table for monster AI roles
CREATE TABLE monster_ai_roles (
    monster_id INTEGER,
    ai_role VARCHAR(32),
    PRIMARY KEY (monster_id, ai_role),
    FOREIGN KEY (monster_id) REFERENCES monsters(id)
);
