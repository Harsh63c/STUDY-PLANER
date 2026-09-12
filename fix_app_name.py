import os
import xml.etree.ElementTree as ET

folders = ["values", "values-hi", "values-fr", "values-it", "values-es", "values-pt"]

app_names = {
    "values": "Study Planner",
    "values-hi": "Study Planner",
    "values-fr": "Study Planner",
    "values-it": "Study Planner",
    "values-es": "Study Planner",
    "values-pt": "Study Planner"
}

for folder, app_name in app_names.items():
    path = f"app/src/main/res/{folder}/strings.xml"
    if os.path.exists(path):
        tree = ET.parse(path)
        root = tree.getroot()
        node = ET.SubElement(root, "string", name="app_name")
        node.text = app_name
        tree.write(path, encoding="utf-8", xml_declaration=True)

