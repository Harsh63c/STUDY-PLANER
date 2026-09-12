import xml.etree.ElementTree as ET
import glob

nav_strings_en = {
    "nav_calendar": "Calendar",
    "nav_study": "Study Planner",
    "nav_settings": "Settings"
}
nav_strings_hi = {
    "nav_calendar": "कैलेंडर",
    "nav_study": "अध्ययन योजनाकार",
    "nav_settings": "सेटिंग्स"
}
nav_strings_fr = {
    "nav_calendar": "Calendrier",
    "nav_study": "Planificateur",
    "nav_settings": "Paramètres"
}
nav_strings_it = {
    "nav_calendar": "Calendario",
    "nav_study": "Piano di Studi",
    "nav_settings": "Impostazioni"
}
nav_strings_es = {
    "nav_calendar": "Calendario",
    "nav_study": "Planificador",
    "nav_settings": "Ajustes"
}
nav_strings_pt = {
    "nav_calendar": "Calendário",
    "nav_study": "Planejador",
    "nav_settings": "Configurações"
}

maps = {
    "values": nav_strings_en,
    "values-hi": nav_strings_hi,
    "values-fr": nav_strings_fr,
    "values-it": nav_strings_it,
    "values-es": nav_strings_es,
    "values-pt": nav_strings_pt
}

for folder, dic in maps.items():
    file = f"app/src/main/res/{folder}/strings.xml"
    tree = ET.parse(file)
    root = tree.getroot()
    for k, v in dic.items():
        node = ET.SubElement(root, "string", name=k)
        node.text = v
    tree.write(file, encoding="utf-8", xml_declaration=True)

