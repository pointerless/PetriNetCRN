import pandas as pd
import tempfile
import subprocess
import os
from timeit import default_timer as timer


class SimulationOptions:
    def __init__(self):
        self._net_data = None
        self._repeats = 1
        self._java_path = "java"
        self._jar_path = "./PetriNetCRN.jar"
        self._max_time = None
        self._volumes = None
        self._use_file = False
        self.file_path = None
        self._format = "json"
        self._file_overwrite = False
        self._dtype = None

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
        self._use_file = use_temp_file
        self.file_path = tempfile.NamedTemporaryFile().name

    def use_file(self, use_file, file_path, overwrite=False):
        self._use_file = use_file
        self.file_path = file_path
        self._file_overwrite = overwrite

    def check_for_overwrite(self):
        if self._use_file and not self._file_overwrite and os.path.exists(self.file_path):
            return True
        with open(self.file_path, "w") as f:
            f.write("")
        return False

    def set_format(self, new_format):
        if new_format not in ["json", "csv"]:
            raise AttributeError("Not a format: "+new_format)
        self._format = new_format

    def auto_read(self, to_read):
        if self._format == "json":
            if self._dtype is not None:
                return pd.read_json(to_read, encoding="utf-8", dtype=self._dtype)
            return pd.read_json(to_read, encoding="utf-8")
        else:
            if self._dtype is not None:
                return pd.read_csv(to_read, encoding="utf-8", dtype=self._dtype, engine="pyarrow")
            return pd.read_csv(to_read, encoding="utf-8", engine="pyarrow")

    def set_dtype(self, dtype_dict):
        self._dtype = dtype_dict

    def build_command(self):
        if self._net_data is None:
            raise AttributeError("net_data not set")
        basic = [self._java_path, "-jar", self._jar_path, "--crnData", self._net_data, "--repeats", str(self._repeats)]
        if self._max_time is not None:
            basic += ["--tMax", str(self._max_time)]
        if self._volumes is not None:
            basic += ["--volumes", ",".join([str(volume) for volume in self._volumes])]
        if self._use_file:
            if self._format == "json":
                basic += ["--JSON", "--out", self.file_path]
            else:
                basic += ["--CSV", "--out", self.file_path]
        return basic


def run_simulation(simulation_options):
    """
    Run the simulation with the given options
    :type simulation_options: SimulationOptions
    """
    start = timer()
    if simulation_options.check_for_overwrite():
        result = simulation_options.auto_read(simulation_options.file_path)
    elif not simulation_options._use_file:
        net_output = subprocess.Popen(simulation_options.build_command(), stdout=subprocess.PIPE, encoding="utf-8")
        result = simulation_options.auto_read(net_output.stdout)
    else:
        subprocess.run(simulation_options.build_command())
        result = simulation_options.auto_read(simulation_options.file_path)
    end = timer()
    seconds_elapsed = end - start
    print("Execution and deserialization took: {}s".format(str(int(seconds_elapsed))))
    print("Rows/second: {}".format(str(len(result.index) / seconds_elapsed)))
    print("Memory info:")
    print(result.info(memory_usage="deep"))
    return result


if __name__ == "__main__":
    options = SimulationOptions()
    options.set_java_path("/home/harry/.jdks/openjdk-19.0.1/bin/java")
    options.net_from_file("../src/main/resources/examplenet.json")
    options.use_file(True, "{}/crn_test.csv".format(os.getcwd()), overwrite=True)
    options.set_format("csv")
    options.set_max_time(5000)
    options.set_repeats(100)
    options.set_volumes([200, 300])
    output = run_simulation(options)
    print(output)

