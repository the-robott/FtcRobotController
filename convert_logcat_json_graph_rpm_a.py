import json
import csv
from datetime import datetime
import re
import matplotlib.pyplot as plt

input_file = "logcat.logcat"
output_file = "logcat_ftc.csv"

# Lists for plotting
rpm_list = []
A_list = []

# Load JSON
with open(input_file, "r") as f:
    data = json.load(f)

# Prepare CSV
with open(output_file, "w", newline="") as csvfile:
    writer = csv.writer(csvfile)
    
    # Write CSV header
    writer.writerow(["date", "time", "pid", "tid", "Target", "Vel", "V", "A", "Error"])
    
    # Regex to extract motor data
    pattern = re.compile(r"Target=([\d\.]+)\s+Vel=([\d\.]+)\s+V=([\d\.]+)\s+A=([\d\.]+)\s+Error=([\d\.]+)")
    
    for entry in data.get("logcatMessages", []):
        header = entry["header"]
        if header.get("tag") != "FTC":
            continue  # skip non-FTC entries
        
        ts_sec = header["timestamp"]["seconds"]
        ts_nano = header["timestamp"]["nanos"]
        dt = datetime.fromtimestamp(ts_sec + ts_nano / 1e9)
        date_str = dt.date().isoformat()
        time_str = dt.strftime("%H:%M:%S.%f")  # Keep microseconds
        
        message = entry.get("message", "")
        match = pattern.search(message)
        if match:
            Target, Vel, V, A, Error = match.groups()
            writer.writerow([date_str, time_str, header["pid"], header["tid"], Target, Vel, V, A, Error])
            
            # Store data for graph
            rpm_list.append(Vel)
            A_list.append(float(A))

print(f"Filtered FTC log saved to {output_file}")

# Plot Error vs Time
if rpm_list and A_list:
    plt.figure(figsize=(10, 5))
    plt.plot(rpm_list, A_list, label="miliamps", linewidth=1)
    plt.xlabel("rpm")
    plt.ylabel("miliamps")
    plt.title("velocity and amps")
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig("logcat_rmp_A_plot.png", dpi=300)
    plt.show()
    print("Graph saved as logcat_rmp_A_plot.png")
else:
    print("No FTC Error data found to plot.")
