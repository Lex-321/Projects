import cv2
import matplotlib.pyplot as plt

from config.paths import WYNIKI


# zapisuje obraz danego etapu do folderu output/
def zapisz_krok(nr, nazwa_pliku, img):
    WYNIKI.mkdir(exist_ok=True)
    sciezka = WYNIKI / f"{nr:02d}_{nazwa_pliku}.jpg"
    cv2.imwrite(str(sciezka), img)
    print(f"  zapisano: {sciezka.name}")
    return sciezka


# skleja wszystkie etapy w jeden plik podglad_etapow.png
def polacz_etapy_na_jednym_obrazie(lista_plikow):
    miniaturki = []
    for p in lista_plikow:
        img = cv2.imread(str(p))
        if img is None:
            continue
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        skala = 280 / img.shape[0]
        miniaturki.append(cv2.resize(img, (int(img.shape[1] * skala), 280)))

    if not miniaturki:
        return

    fig, axs = plt.subplots(len(miniaturki), 1, figsize=(9, 2.8 * len(miniaturki)))
    if len(miniaturki) == 1:
        axs = [axs]
    for ax, m in zip(axs, miniaturki):
        ax.imshow(m)
        ax.axis("off")
    plt.tight_layout()
    plt.savefig(WYNIKI / "podglad_etapow.png", dpi=110)
    plt.close()
