from urllib.request import urlretrieve

from config.paths import LINKI_MODELI, MODEL_OCZY, MODEL_TWARZ


# pobiera jeden plik wag z internetu jesli go nie ma lokalnie
def sciagnij_model(sciezka):
    if sciezka.exists():
        return
    sciezka.parent.mkdir(exist_ok=True)
    print(f"pobieram {sciezka.name}...")
    urlretrieve(LINKI_MODELI[sciezka], sciezka)


# pobiera oba modele - twarz i oczy
def sciagnij_modele():
    sciagnij_model(MODEL_TWARZ)
    sciagnij_model(MODEL_OCZY)
