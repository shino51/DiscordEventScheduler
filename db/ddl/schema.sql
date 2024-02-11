USE discord;

CREATE TABLE EVENT (
    ID INTEGER IDENTITY NOT NULL PRIMARY KEY,
    NAME VARCHAR(255),
    GUILD_ID VARCHAR(20) NOT NULL,
    CHANNEL_ID VARCHAR(20) NOT NULL,
    FREQUENCY VARCHAR(30) NOT NULL,
    START_TIME INTEGER NOT NULL,
    TIME_ZONE VARCHAR(3) NOT NULL,
    DESCRIPTION CLOB NOT NULL
);