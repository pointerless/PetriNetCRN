# Petri Net CRN Simulator

This runs a simulation of a chemical reaction network as specified in a JSON
file formatted as following:

```json
{
  "volume": [100],
  "places": [
    {"element": "A", "volumeRatio": 0.5},
    {"element": "B", "volumeRatio": 0.5},
    {"element": "C", "volumeRatio": 0.0}
  ],
  "transitions": [{
      "inputPlaces": [
        {
          "element": "A",
          "amount": 1
        },
        {
          "element": "B",
          "amount": 1
        } ],
      "outputPlaces": [
        {
          "element": "C",
          "amount": 1
        } ],
      "k": 1.0
    }]
}
```

> Schema can be found at [./src/main/resources/net-schema.json](./src/main/resources/net-schema.json)

## CLI

The command line tool can be used with the following command:

```commandline
java -jar PetriNetCRN.jar -p "net-spec.json" -oJ "./output.json" -r 10
```

For more information use the `--help` flag.

## REST API

WIP
