from ultralytics import YOLO

from config.paths import MODEL_TWARZ


# uruchamia YOLOv8-Face i zwraca liste prostokatow twarzy
def szukaj_twarzy(img, conf=0.25):
    model = YOLO(str(MODEL_TWARZ))
    wynik = model(img, conf=conf, verbose=False)[0]

    twarze = []
    for b in wynik.boxes:
        x1, y1, x2, y2 = map(int, b.xyxy[0])
        twarze.append((x1, y1, x2, y2))
        print(f"  twarz: ({x1},{y1})-({x2},{y2}), conf={float(b.conf[0]):.2f}")

    return twarze
