import pandas as pd
import tempfile
import subprocess


class SimulationOptions:
    def __init__(self):
        self._net_data = None
        self._repeats = 1
        self._java_path = "java"
        self._jar_path = "./PetriNetCRN.jar"
        self._max_time = None
        self._volumes = None
        self._use_temp = False
        self.file_path = None

    def net_from_json(self, json_net):
        self._net_data = json_net

    def net_from_file(self, json_path):
        with open(json_path, "r") as json_file:
            self._net_data = "".join(json_file.readlines())

    def set_repeats(self, repeats):
        self._repeats = repeats

    def set_java_path(self, path):
        self._java_path = path

    def set_jar_path(self, path):
        self._jar_path = path

    def set_max_time(self, time):
        self._max_time = time

    def set_volumes(self, volumes):
        self._volumes = volumes

    def use_temp_file(self, use_temp_file):
        self._use_temp = use_temp_file

    def build_command(self):
        if self._net_data is None:
            raise AttributeError("net_data not set")
        basic = [self._java_path, "-jar", self._jar_path, "--crnData", self._net_data, "--repeats", str(self._repeats)]
        if self._max_time is not None:
            basic += ["--tMax", str(self._max_time)]
        if self._volumes is not None:
            basic += ["--volumes", ",".join([str(volume) for volume in self._volumes])]
        if self._use_temp:
            self.file_path = tempfile.NamedTemporaryFile()
            basic += ["--outJSON", self.file_path.name]
        return basic


def run_simulation(simulation_options):
    """
    Run the simulation with the given options
    :type simulation_options: SimulationOptions
    """
    if not simulation_options._use_temp:
        net_output = subprocess.Popen(simulation_options.build_command(), stdout=subprocess.PIPE, encoding="utf-8")
        return pd.read_json(net_output.stdout, encoding="utf-8")
    else:
        subprocess.run(simulation_options.build_command())
        return pd.read_json(simulation_options.file_path.name, encoding="utf-8")


if __name__ == "__main__":
    options = SimulationOptions()
    options.set_java_path("/home/harry/.jdks/openjdk-19.0.1/bin/java")
    options.net_from_file("../src/main/resources/net2.json")
    options.set_max_time(5000)
    options.set_volumes([110, 220])
    output = run_simulation(options)
    print(output)

