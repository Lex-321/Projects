from pathlib import Path

from config.paths import ROZSZERZENIA, ZDJECIA


# zwraca posortowana liste zdjec z folderu images/
def lista_zdjec():
    if not ZDJECIA.exists():
        return []
    return sorted(p for p in ZDJECIA.iterdir() if p.suffix.lower() in ROZSZERZENIA)


# pyta uzytkownika o sciezke lub numer z listy
def wczytaj_zdjecie_od_uzytkownika():
    print("=" * 50)
    print("  DETEKCJA OCZU - YOLOv8")
    print("=" * 50)
    print("\nWgraj zdjecie do folderu images/ albo podaj pelna sciezke.")
    print(f"Folder na zdjecia: {ZDJECIA.resolve()}\n")

    pliki = lista_zdjec()
    if pliki:
        print("Zdjecia w folderze images/:")
        for i, p in enumerate(pliki, 1):
            print(f"  {i}. {p.name}")
        print()

    while True:
        wpis = input("Podaj sciezke do zdjecia (lub sam numer z listy): ").strip().strip('"').strip("'")
        if not wpis:
            print("  wpisz sciezke albo numer - nie mozna zostawic pustego\n")
            continue

        if wpis.isdigit():
            nr = int(wpis)
            if 1 <= nr <= len(pliki):
                return pliki[nr - 1]

        sciezka = Path(wpis)
        if not sciezka.is_absolute():
            sciezka = ZDJECIA / sciezka

        if sciezka.exists() and sciezka.is_file():
            return sciezka

        print(f"  nie znaleziono pliku: {sciezka}\n")
