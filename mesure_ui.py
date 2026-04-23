from codecarbon import EmissionsTracker
import subprocess

tracker = EmissionsTracker(project_name="PetClinic-UI-Tests", output_file="ui_with_AI_emissions.csv")

tracker.start()

# Run the UI tests
# subprocess.run(
#     '"C:\\Program Files\\JetBrains\\IntelliJ IDEA 2025.3\\plugins\\maven\\lib\\maven3\\bin\\mvn" test -Dtest=AddOwnerTest',
#     shell=True
# )

# Run the AI-assisted UI tests
subprocess.run(
    '"C:\\Program Files\\JetBrains\\IntelliJ IDEA 2025.3\\plugins\\maven\\lib\\maven3\\bin\\mvn" test -Dtest=AddOwnerUITestsWithAI',
    shell=True
)

tracker.stop()

print("UI Test measurement complete. Results saved to ui_emissions.csv")