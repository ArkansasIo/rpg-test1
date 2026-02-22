-- SQL table for player characters
CREATE TABLE player_characters (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    level INTEGER,
    class VARCHAR(32),
    race VARCHAR(32),
    str INTEGER,
    dex INTEGER,
    con INTEGER,
    int INTEGER,
    wis INTEGER,
    vit INTEGER,
    spi INTEGER,
    lck INTEGER,
    wil INTEGER,
    cha INTEGER,
    hp INTEGER,
    mp INTEGER,
    stamina INTEGER,
    gear_score INTEGER,
    pvp_rank INTEGER,
    pve_rank INTEGER,
    status_effects VARCHAR(128),
    buffs VARCHAR(128),
    debuffs VARCHAR(128),
    resources VARCHAR(128),
    resistances VARCHAR(128),
    inventory VARCHAR(256),
    equipped_items VARCHAR(256),
    achievements VARCHAR(256),
    titles VARCHAR(128),
    mount_id INTEGER,
    pet_id INTEGER
);

-- SQL table for items
CREATE TABLE items (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    type VARCHAR(32),
    rarity VARCHAR(16),
    stats VARCHAR(128),
    set_id INTEGER,
    owner_id INTEGER,
    equipped BOOLEAN,
    FOREIGN KEY (owner_id) REFERENCES player_characters(id)
);

-- SQL table for abilities/skills
CREATE TABLE abilities (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    class VARCHAR(32),
    type VARCHAR(32),
    cooldown INTEGER,
    cost INTEGER,
    effect VARCHAR(128),
    scaling VARCHAR(64),
    unlock_level INTEGER
);

-- SQL table for quests
CREATE TABLE quests (
    id INTEGER PRIMARY KEY,
    name VARCHAR(128),
    description TEXT,
    type VARCHAR(32),
    status VARCHAR(32),
    reward VARCHAR(128),
    required_level INTEGER,
    required_items VARCHAR(128),
    required_kills INTEGER,
    progress INTEGER
);

-- SQL table for parties/groups
CREATE TABLE parties (
    id INTEGER PRIMARY KEY,
    leader_id INTEGER,
    member_ids VARCHAR(128),
    party_size INTEGER,
    raid BOOLEAN,
    FOREIGN KEY (leader_id) REFERENCES player_characters(id)
);

-- SQL table for guilds
CREATE TABLE guilds (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    leader_id INTEGER,
    member_ids VARCHAR(256),
    rank INTEGER,
    achievements VARCHAR(128),
    FOREIGN KEY (leader_id) REFERENCES player_characters(id)
);

-- SQL table for PvP matches
CREATE TABLE pvp_matches (
    id INTEGER PRIMARY KEY,
    team1_ids VARCHAR(128),
    team2_ids VARCHAR(128),
    winner_team INTEGER,
    match_type VARCHAR(32),
    duration INTEGER,
    score VARCHAR(64)
);

-- SQL table for boss encounters
CREATE TABLE boss_encounters (
    id INTEGER PRIMARY KEY,
    boss_id INTEGER,
    party_id INTEGER,
    phase INTEGER,
    start_time DATETIME,
    end_time DATETIME,
    outcome VARCHAR(32),
    loot VARCHAR(128),
    FOREIGN KEY (boss_id) REFERENCES monsters(id),
    FOREIGN KEY (party_id) REFERENCES parties(id)
);

-- SQL table for achievements
CREATE TABLE achievements (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    description TEXT,
    unlock_condition VARCHAR(128),
    reward VARCHAR(64)
);

-- SQL table for mounts and pets
CREATE TABLE mounts (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    type VARCHAR(32),
    speed INTEGER,
    owner_id INTEGER,
    FOREIGN KEY (owner_id) REFERENCES player_characters(id)
);

CREATE TABLE pets (
    id INTEGER PRIMARY KEY,
    name VARCHAR(64),
    type VARCHAR(32),
    abilities VARCHAR(128),
    owner_id INTEGER,
    FOREIGN KEY (owner_id) REFERENCES player_characters(id)
);
