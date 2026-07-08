from pathlib import Path

KATALOG = Path(__file__).resolve().parent.parent
MODELE = KATALOG / "models"
WYNIKI = KATALOG / "output"
ZDJECIA = KATALOG / "images"

MODEL_TWARZ = MODELE / "yolov8n-face-lindevs.pt"
MODEL_OCZY = MODELE / "eyes_best_256_v2.pt"

LINKI_MODELI = {
    MODEL_TWARZ: "https://github.com/lindevs/yolov8-face/releases/download/1.0.1/yolov8n-face-lindevs.pt",
    MODEL_OCZY: "https://raw.githubusercontent.com/KT313/eye_tracking/main/models/eyes_best_256_v2.pt",
}

# grupowanie keypointow z modelu KT313
PKT_LEWE = [0, 1, 2, 6, 7]
PKT_PRAWE = [3, 4, 5, 8, 9]
ROZMIAR_OCZU = 256
ROZMIAR_YOLO = 640

ROZSZERZENIA = (".jpg", ".jpeg", ".png", ".webp", ".bmp")
