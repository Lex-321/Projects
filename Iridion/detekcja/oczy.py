from ultralytics import YOLO

from config.paths import MODEL_OCZY, PKT_LEWE, PKT_PRAWE, ROZMIAR_OCZU


# liczy bbox wokol wybranych punktow kluczowych oka
def prostokat_z_punktow(punkty, idx, margines=12):
    pkt = punkty[idx]
    widoczne = pkt[(pkt[:, 0] > 1) & (pkt[:, 1] > 1)]
    if len(widoczne) == 0:
        return None

    x1 = int(widoczne[:, 0].min()) - margines
    y1 = int(widoczne[:, 1].min()) - margines
    x2 = int(widoczne[:, 0].max()) + margines
    y2 = int(widoczne[:, 1].max()) + margines

    w, h = x2 - x1, y2 - y1
    min_h = max(18, int(w * 0.35))
    if h < min_h:
        cy = (y1 + y2) // 2
        y1, y2 = cy - min_h // 2, cy + min_h // 2

    return x1, y1, x2, y2


# szuka oczu na wycinku twarzy, zwraca boxy i keypointy
def szukaj_oczu(model, fragment, przesuniecie_x, przesuniecie_y, conf):
    wynik = model(fragment, conf=conf, imgsz=ROZMIAR_OCZU, verbose=False)[0]
    boxy, opisy, pkt = [], [], None

    for det in wynik:
        if det.keypoints is None or len(det.keypoints) == 0:
            continue

        pkt = det.keypoints.xy[0].cpu().numpy()
        pewnost = float(det.boxes.conf[0]) if len(det.boxes) else 0

        for nazwa, idx in [("lewe", PKT_LEWE), ("prawe", PKT_PRAWE)]:
            box = prostokat_z_punktow(pkt, idx)
            if box is None:
                continue
            bx1, by1, bx2, by2 = box
            boxy.append((
                bx1 + przesuniecie_x,
                by1 + przesuniecie_y,
                bx2 + przesuniecie_x,
                by2 + przesuniecie_y,
            ))
            opisy.append(f"{nazwa} {pewnost:.0%}")

    return boxy, opisy, pkt


# laduje model YOLO-pose do detekcji oczu
def zaladuj_detektor_oczu():
    return YOLO(str(MODEL_OCZY))
