from matplotlib import pyplot as plt
import numpy as np


def data_by_correctness(data, zero_element, volume_element):
    """Returns a tuple of the volume and correctness for each volume in data"""
    temp_data = data[data["isFinal"]][["volume", zero_element, volume_element]]
    temp_data["isCorrect"] = temp_data[zero_element].eq(0) & temp_data[volume_element].eq(temp_data["volume"])
    temp_data = temp_data[["volume", "isCorrect"]]
    v_counts = temp_data.groupby(["volume"]).value_counts(normalize=True)
    v_counts = v_counts[v_counts.index.get_level_values(1)]
    return v_counts.index.get_level_values(0).values.tolist(), v_counts.values.tolist()


def final_time_graph(data, dataset_name, xscale='log'):
    """Plots all the final times (t_end) for data provided"""
    temp_data = data[data["isFinal"]][["volume", "time"]].groupby(["volume"])
    time_av = temp_data.mean().reset_index()
    y_data = time_av["time"]
    x_data = time_av["volume"]
    err = temp_data.std().reset_index()["time"]

    plt.xscale(xscale)
    plt.xlabel("$n$")
    plt.ylabel("time (s)")
    plt.scatter(x_data, y_data)
    plt.errorbar(x_data, y_data, err)
    plt.title("Final time for {}".format(dataset_name))
    plt.show()


def correctness_graph(data, zero_element, volume_element, dataset_name, xscale='log'):
    """Plots the correctness for data provided"""
    x_data, y_data = data_by_correctness(data, zero_element, volume_element)
    plt.plot(x_data, y_data)
    plt.xscale(xscale)
    plt.xlabel("$n$")
    plt.ylabel("correctness")
    plt.title("Correctness for {}".format(dataset_name))
    plt.show()


def lowest_correctness_trajectory(data, zero_element, volume_element, dataset_name):
    """Plots the trajectory for the lowest correctness volume in data"""
    volume, correctness = data_by_correctness(data, zero_element, volume_element)
    lowest_volume = volume[correctness.index(min(correctness))]
    all_trajectories(data[data["volume"] == lowest_volume], "{} (lowest correctness)".format(dataset_name))


def all_trajectories(data, dataset_name):
    """Plots all trajectories in given data by element name"""
    places = data.columns.difference(["repeatNum", "time", "volume", "isFinal"])
    fig, axs = plt.subplots(len(places), sharex=True)
    fig.suptitle("Trajectories for {}".format(dataset_name))
    fig.tight_layout(h_pad=3, w_pad=5)
    row = 0
    for place in places:
        for repeat in data["repeatNum"].unique():
            axs[row].step(data[data["repeatNum"] == repeat]["time"], data[data["repeatNum"] == repeat][place],
                          where='post', label="{}-{}".format(place, repeat))
            if row+1 == len(places):
                axs[row].set_xlabel("time (s)")
            axs[row].set_ylabel("$n_{Ra}$")
            axs[row].set_title(place, loc="right")
        row += 1


def firing_gamma_graph(data, dataset_name):
    """Plots a graph of gamma (#firings against size multiplied by log of size)"""
    firings_by_volume = data.groupby(["volume"]).size()
    firings = firings_by_volume.values
    ntot_logntot = firings_by_volume.index.values*np.log(firings_by_volume.index.values)
    calc_gamma = np.mean(np.ediff1d(firings)/np.ediff1d(ntot_logntot))
    calc_c = np.mean(firings)/(calc_gamma*np.mean(ntot_logntot))
    calc_gamma_std = np.std(np.ediff1d(firings)/np.ediff1d(ntot_logntot))
    gamma_low = calc_gamma - calc_gamma_std
    gamma_high = calc_gamma + calc_gamma_std
    plt.plot(ntot_logntot, firings, label="$\\gamma \\simeq {} \\pm {}$".format(round(calc_gamma, 2), round(calc_gamma_std, 2)))
    plt.plot(ntot_logntot, gamma_low*ntot_logntot+calc_c, "g--", label="$\\gamma = {}$".format(round(gamma_low, 2)))
    plt.plot(ntot_logntot, gamma_high*ntot_logntot+calc_c, "r--", label="$\\gamma = {}$".format(round(gamma_high, 2)))
    plt.title("$firings \\simeq \\gamma \\cdot n_{{tot}} \\cdot \\ln n_{{tot}}$ for {}".format(dataset_name))
    plt.xlabel("$n_{tot} \\cdot \\ln n_{tot}$")
    plt.ylabel("$firings$")
    plt.legend()
