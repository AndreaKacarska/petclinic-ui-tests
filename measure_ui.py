from codecarbon import EmissionsTracker
import subprocess

tracker = EmissionsTracker(
    project_name="ui-tests",
    output_file="ui_emissions.csv"
)
tracker.start()
subprocess.run("mvn test", shell=True)
tracker.stop()