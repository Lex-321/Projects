import cv2
import numpy as np

from config.paths import PKT_LEWE, PKT_PRAWE


# skaluje obraz do kwadratu z czarnymi paskami (letterbox)
def dopasuj_do_kwadratu(img, bok=640):
    h, w = img.shape[:2]
    skala = min(bok / h, bok / w)
    nw, nh = int(w * skala), int(h * skala)
    pomniejszony = cv2.resize(img, (nw, nh))
    ramka = np.zeros((bok, bok, 3), dtype=np.uint8)
    ox, oy = (bok - nw) // 2, (bok - nh) // 2
    ramka[oy : oy + nh, ox : ox + nw] = pomniejszony
    return ramka


# wycina prostokatny fragment obrazu
def obetnij(img, box):
    h, w = img.shape[:2]
    x1, y1, x2, y2 = box
    x1, y1 = max(0, x1), max(0, y1)
    x2, y2 = min(w, x2), min(h, y2)
    if x2 <= x1 or y2 <= y1:
        return np.array([])
    return img[y1:y2, x1:x2].copy()


# rysuje prostokaty detekcji z etykietami
def narysuj_boxy(img, boxy, napisy, kolor):
    out = img.copy()
    for (x1, y1, x2, y2), txt in zip(boxy, napisy):
        cv2.rectangle(out, (x1, y1), (x2, y2), kolor, 2)
        cv2.putText(out, txt, (x1, y1 - 6), cv2.FONT_HERSHEY_SIMPLEX, 0.55, kolor, 2)
    return out


# rysuje punkty kluczowe i linie na oczach
def narysuj_keypointy(img, punkty):
    out = img.copy()
    grupy = [
        (PKT_LEWE, (0, 0, 255)),
        (PKT_PRAWE, (0, 255, 255)),
    ]
    for idx, kolor in grupy:
        pts = []
        for i in idx:
            x, y = int(punkty[i, 0]), int(punkty[i, 1])
            if x > 0 and y > 0:
                pts.append((x, y))
                cv2.circle(out, (x, y), 3, kolor, -1)
        for j in range(len(pts) - 1):
            cv2.line(out, pts[j], pts[j + 1], kolor, 1)
    return out
