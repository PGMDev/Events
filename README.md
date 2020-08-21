# Events

Plugin used to manage tournament-level matches. Upon joining or cycling, all players on the match's registered teams will be forced onto their respective teams. Events also manages cycling & starting matches as well as readying teams. Maps played in a match can be customised using a format file. Customisable vetos are also supported.

## Setup

* For an offline tournament, make sure `api.enabled` is set to `false` in `config.yml`.
* Put each team that is playing in its own yml file in `plugins/Events/teams`.
* Put each format in its own file in `plugins/Events/format`.

### Sample Team file

```yml
name: A
players:
- 30401f63-f5bb-4f24-9a5a-0ecd5f706115
- 2318a6ef-937d-41ad-aac0-59070815de92
- 77d845ce-c88a-467d-9880-1db671a18933
- 9b4fbb38-ed55-44ad-b510-9f0197433f93
- 02407912-8bbf-4b7b-a34c-a45339841436
```

## Downloading & Building

* To compile run: `mvn clean install`
    * Pull requests are welcome.

## Running

* Ensure that `plugins/Events/teams/` contains all the teams playing in this tournament and `plugins/Events/formats/` contains the formats for this tournament.
* Register each team playing with `/tm register <team>`
* Start the format with `/tm create <format>` where `<format>.xml` is a valid file in `plugins/Events/formats`

### Permissions

* `events.staff` - allows users to run `/tm`
* `events.spectate` - allows users to watch the match
* `events.spectate.vanish` - allows users to watch the match, but they will be vanished and therefore cannot interact with the game

## Implementing your own API

* Extend the `TournamentTeam` and `TournamentPlayer` interfaces
* Edit `TournamentTeam#create` so that it instantiates your implementation. Do the same with `TournamentPlayer`.
* Modify `RankedManager` and `APIManager` to include your new endpoints.

## XML structure

| Tag | Definition | Attributes | Expects child |
| --- | ---------- | ---------- | ------------- |
| `<format>` | Represents a map format. Should be the root element in your format.xml file. It is also a round and therefore can be used to create a nested best of 3. | `best-of="3"` - what this match should be out of | Round(s) |
| `<match>` | Represents a single match. | `id` - defaults to map name | The name of the map to be played on this round |
| `<result-from />` | Uses the result from a round with a matching id - useful to stop repeating veto deciders | `id="map-name"` | N/A |
| `<veto>` | Represents a veto round | `id="veto"` | **Required:** `<decider>`, `<options>`, `<order>` |
| `<decider>` | Which team vetoes first | N/A | A round |
| `<options>` | What vetos should there be | `name` on the child element - what name should be displayed in the veto | Round(s) |
| `<order>` | The veto order (ban, pick, etc.). The last element is enacted by the system (not by a team and so should not have the who attribute). | `ban-until="3"` (ban until 3 maps are left), `starting-team="2"` (the team who lost the decider starts), `time="30"` - the time in seconds that each team has to veto | If no attributes are present, `<pick />` and `<ban />` |
| `<pick />` | A pick in the veto | `team="0"` - the team that gets to pick a map (zero is random map picked), `insert="back"` - whether the map should be added to the front or back of the maps to be played | N/A |
| `<ban />` | A ban in the veto | `team="0"` - the team that gets to ban a map (zero is random map banned), `insert="back"` - whether the map should be added to the front or back of the maps to be played | N/A |

## Examples

### Simple Bo3

```xml
<format best-of="3">
    <match>Facility TE</match>
    <match>Ascendance</match>
    <match>Limbo II</match>
</format>
```

### Bo3 with vetos

```xml
<format best-of="3">
    <veto>
        <decider>
            <match id="veto-decider">No Return</match>
        </decider>
        <options>
            <match>Ascendance 02</match>
            <match>Facility TE</match>
            <match>Nartica</match>
            <match>Desert Sanctuary</match>
            <match>Smoke</match>
        </options>
        <order ban-until="3" starting-team="2" />
    </veto>
</format>
```

### More complicated sample format file

```xml
<format best-of="1">
    <veto>
        <decider>
            <match id="veto-decider">No Return</match>
        </decider>
        <options>
            <format name="CTW" best-of="3">
                <veto>
                    <decider>
                        <result-from id="veto-decider" />
                    </decider>
                    <options>
                        <match>Race for Victory 2</match>
                        <match>Fairy Tales 2: A Tale or Two</match>
                        <match>Golden Drought III</match>
                        <match>Deepwind Jungle TE</match>
                        <match>NextGen TE</match>
                    </options>
                    <order ban-until="1" starting-team="2" />
                </veto>
            </format>
            <format name="DTM" best-of="3">
                <veto>
                    <decider>
                        <result-from id="veto-decider" />
                    </decider>
                    <options>
                        <match>Warlock</match>
                        <match>Spaceship Battles TE</match>
                        <match>The Fenland</match>
                        <match>Ender Blast</match>
                        <match>BoomBox</match>
                    </options>
                    <order ban-until="1" starting-team="2" />
                </veto>
            </format>
            <format name="Conquest" best-of="3">
                <veto>
                    <decider>
                        <result-from id="veto-decider" />
                    </decider>
                    <options>
                        <match>Limbo II</match>
                        <match>Facility TE</match>
                        <match>Dwyer Hill</match>
                        <match>Nartica</match>
                        <match>Desert Sanctuary</match>
                    </options>
                    <order ban-until="3" starting-team="2" />
                </veto>
            </format>
        </options>
        <order ban-until="1" starting-team="2" />
    </veto>
</format>
```

### License
> AGPL-3.0
