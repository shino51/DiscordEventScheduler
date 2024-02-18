# DiscordEventScheduler
Applicaiotn to 

## Environment
Java 18
Docker 20.10.10

# Preparation
## properties
Create **discord.properties** under resources and add the followings:

guild.id={guild id of your discord server}
auth.token={auth token of your discord}

## dokcer
### local DB
```shell
docker-compose up -d
```

Do the following when you want to stop container (The entry of the DB will gets removed as well)
```shell
docker-compose down
```

# How to run
run
```shell
mvn clean install -DskipTests
```

Then
```shell
mvn spring-boot:run
```

## Add the event into the DB
sample sql:
```sqlite-sql
USE discord;

INSERT INTO EVENT (NAME, GUILD_ID, CHANNEL_ID, FREQUENCY, DAY_OF_WEEK, START_TIME, TIME_ZONE, DESCRIPTION) VALUES
('test event', '1111111', '222222', 'WEEKLY', 'SUNDAY', 21, 'JST', 'event details');
```

### TIME_ZONE
please check [here](https://cr.openjdk.org/~sherman/threeten/old/javax/time/calendar/ZoneId.html) at section **OLD_IDS_PRE_2005**

- EST - UTC-05:00
- HST - UTC-10:00
- MST - UTC-07:00
- ACT - Australia/Darwin
- AET - Australia/Sydney
- AGT - America/Argentina/Buenos_Aires
- ART - Africa/Cairo
- AST - America/Anchorage
- BET - America/Sao_Paulo
- BST - Asia/Dhaka
- CAT - Africa/Harare
- CNT - America/St_Johns
- CST - America/Chicago
- CTT - Asia/Shanghai
- EAT - Africa/Addis_Ababa
- ECT - Europe/Paris
- IET - America/Indiana/Indianapolis
- IST - Asia/Kolkata
- JST - Asia/Tokyo
- MIT - Pacific/Apia
- NET - Asia/Yerevan
- NST - Pacific/Auckland
- PLT - Asia/Karachi
- PNT - America/Phoenix
- PRT - America/Puerto_Rico
- PST - America/Los_Angeles
- SST - Pacific/Guadalcanal
- VST - Asia/Ho_Chi_Minh

### Frequency
- WEEKLY
- MONTHLY_EVERY_FIRST
- MONTHLY_EVERY_SECOND
- MONTHLY_EVERY_THIRD
- MONTHLY_EVERY_FOURTH


## API details
### GET - list of event
`http://localhost:9898/discord/scheduled-event/list` \
get a list of event in the guild which is specified at discord.properties.

### POST - create weekly/monthly events
`http://localhost:9898/discord/scheduled-event/create` \
create events based on your db entry
#### body - json sample
```json
{
    "first_day_of_month": "2024-03-01"
}
```

### DELETE - delete a event on that day
`http://localhost:9898/discord/scheduled-event` \
delete all the event(s) on the specified day. From 0:00 to 23:59 on that day.
```json
{
    "time_zone": "ECT",
    "cancelled_date": "2024-02-19"
}
```