import os
import datetime
import torch
import torch.nn as nn
import torch.optim as opt
import sys
from pathlib import Path
import cv2
from torch.utils.data import DataLoader

from iris_auth.database.users import Register, Login
from iris_auth.models.resnet18_embedding import IrisEmbedModel
from iris_auth.models.training_model import IrisTrainingModel
from iris_auth.training.dataset import IrisDataset
from iris_auth.preprocessing.transform import IrisTransforms
from iris_auth.training.trainer import one_epoch_training, evaluate
from iris_auth.utils.random_spliter import RandomSplitter
from iris_auth.tests.SanityCheck import Iridion_Sanity_Check
from iris_auth.tests.EmbedTest import Iridion_Embeding_Test
from iris_auth.tests.IdentificationTest import Iridion_Identification_Test
from config.paths import ROZMIAR_YOLO, ZDJECIA
from detekcja.modele import sciagnij_modele
from detekcja.oczy import szukaj_oczu, zaladuj_detektor_oczu
from detekcja.twarz import szukaj_twarzy
from ui.wejscie import wczytaj_zdjecie_od_uzytkownika
from utils.obraz import dopasuj_do_kwadratu, narysuj_boxy, narysuj_keypointy, obetnij
from utils.zapis import polacz_etapy_na_jednym_obrazie, zapisz_krok


# wczytuje zdjecie z argumentu lub pyta uzytkownika
def wczytaj_obraz():
    if len(sys.argv) > 1:
        sciezka = Path(sys.argv[1])
        if not sciezka.is_absolute():
            sciezka = ZDJECIA / sciezka
        if sciezka.exists():
            return sciezka
        print(f"nie znaleziono pliku: {sciezka}")
        sys.exit(1)
    return wczytaj_zdjecie_od_uzytkownika()

# liczy bbox regionu oczu na podstawie keypointow
def region_oczu_z_punktow(punkty, margines=25):
    widoczne = punkty[(punkty[:, 0] > 1) & (punkty[:, 1] > 1)]
    if len(widoczne) == 0:
        return None
    x1 = int(widoczne[:, 0].min()) - margines
    y1 = int(widoczne[:, 1].min()) - margines
    x2 = int(widoczne[:, 0].max()) + margines
    y2 = int(widoczne[:, 1].max()) + margines
    return x1, y1, x2, y2

# powieksza wycinek regionu oczu
def powieksz_region(img, skala=2.0):
    h, w = img.shape[:2]
    nw, nh = int(w * skala), int(h * skala)
    if nw < 1 or nh < 1:
        return img
    return cv2.resize(img, (nw, nh), interpolation=cv2.INTER_CUBIC)

# wycina fragment wzgledem wspolrzednych lokalnych (na twarzy)
def obetnij_lokalnie(fragment, box):
    h, w = fragment.shape[:2]
    x1, y1, x2, y2 = box
    x1, y1 = max(0, x1), max(0, y1)
    x2, y2 = min(w, x2), min(h, y2)
    if x2 <= x1 or y2 <= y1:
        return None
    return fragment[y1:y2, x1:x2].copy()

# laczy kilka boxow w jeden prostokat (oba oczy naraz)
def box_z_boxow(boxy, margines=15):
    x1 = min(b[0] for b in boxy) - margines
    y1 = min(b[1] for b in boxy) - margines
    x2 = max(b[2] for b in boxy) + margines
    y2 = max(b[3] for b in boxy) + margines
    return x1, y1, x2, y2

# glowny pipeline: detekcja twarzy i oczu, zapis etapow
def main():
    sciagnij_modele()

    sciezka_zdjecia = wczytaj_obraz()
    img = cv2.imread(str(sciezka_zdjecia))
    if img is None:
        print(f"nie mozna wczytac obrazu: {sciezka_zdjecia}")
        return

    etapy = []
    etapy.append(zapisz_krok(1, "oryginal", img))

    szary = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    etapy.append(zapisz_krok(2, "szary", cv2.cvtColor(szary, cv2.COLOR_GRAY2BGR)))

    yolo_input = dopasuj_do_kwadratu(img, ROZMIAR_YOLO)
    etapy.append(zapisz_krok(3, "yolo_input", yolo_input))

    twarze = szukaj_twarzy(img)
    if not twarze:
        print("nie wykryto twarzy na zdjeciu")
        polacz_etapy_na_jednym_obrazie(etapy)
        return

    box_twarz = twarze[0]
    twarz = obetnij(img, box_twarz)
    if twarz.size == 0:
        print("nie udalo sie wyciac twarzy")
        polacz_etapy_na_jednym_obrazie(etapy)
        return
    etapy.append(zapisz_krok(4, "twarz", twarz))

    model_oczu = zaladuj_detektor_oczu()
    x0, y0, _, _ = box_twarz
    boxy_oczu, opisy_oczu, punkty = szukaj_oczu(model_oczu, twarz, x0, y0, conf=0.25)

    region = region_oczu_z_punktow(punkty) if punkty is not None else None
    if region is None:
        h, w = twarz.shape[:2]
        region = (0, 0, w, int(h * 0.55))

    img_region = obetnij_lokalnie(twarz, region)
    if img_region is not None:
        etapy.append(zapisz_krok(5, "region_oczu", img_region))
        etapy.append(zapisz_krok(5, "region_zoom", powieksz_region(img_region)))

    if punkty is not None:
        etapy.append(zapisz_krok(6, "keypointy", narysuj_keypointy(twarz, punkty)))

    if boxy_oczu:
        lokalne = [
            (bx - x0, by - y0, bx2 - x0, by2 - y0)
            for bx, by, bx2, by2 in boxy_oczu
        ]
        etapy.append(
            zapisz_krok(
                6,
                "oczy_boxy",
                narysuj_boxy(twarz, lokalne, opisy_oczu, (255, 0, 0)),
            )
        )

    for i, (bx, by, bx2, by2) in enumerate(boxy_oczu):
        oko = obetnij(img, (bx, by, bx2, by2))
        if oko.size == 0:
            continue
        nazwa = "lewe_oko" if "lewe" in opisy_oczu[i].lower() else "prawe_oko"
        etapy.append(zapisz_krok(7, nazwa, oko))

    if len(boxy_oczu) >= 2:
        oboje = obetnij(img, box_z_boxow(boxy_oczu))
        if oboje.size > 0:
            etapy.append(zapisz_krok(7, "oboje_oczy", oboje))

    img_twarze = narysuj_boxy(
        img,
        twarze,
        [f"twarz {i + 1}" for i in range(len(twarze))],
        (0, 255, 0),
    )
    if boxy_oczu:
        img_twarze = narysuj_boxy(img_twarze, boxy_oczu, opisy_oczu, (255, 0, 0))

    polacz_etapy_na_jednym_obrazie(etapy)
    print("gotowe: sprawdz folder output/")
    
    #identyfikacja użytkownika
    log=Login()
    LogIMG1=os.path.join("output","07_lewe_oko.jpg")
    LogIMG2=os.path.join("output","07_prawe_oko.jpg")
    LogIMG3=os.path.join("iris_auth", "data", "CASIA-Iris-Thousand", "CASIA-Iris-Thousand", "015", "L", "S5015L02.jpg")
    log.login(LogIMG1)
    log.login(LogIMG2)
    log.login(LogIMG3)

if __name__ == "__main__":
    main()

'''
test=Iridion_Sanity_Check()
test.run()
'''

'''
test=Iridion_Embeding_Test()
test.run()
'''

'''
test=Iridion_Identification_Test()
test.run()
'''

'''
def main():
    #zmienne kontrolne
    device=torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(device)
    epochs=100
    transforms=IrisTransforms()
    dataset=IrisDataset(os.path.join("iris_auth","data","CASIA-Iris-Thousand"),transforms.train_transform)

    # modele i loadery
    train_dataset,val_dataset=RandomSplitter(dataset,0.8)
    train_loader=DataLoader(train_dataset,batch_size=32,shuffle=True,num_workers=2)
    val_loader=DataLoader(val_dataset,batch_size=32,shuffle=False,num_workers=2)
    model=IrisEmbedModel()

    # proxy classifier
    classifier=nn.Linear(128,len(train_dataset.dataset.label_map))
    crit=nn.CrossEntropyLoss()
    training_model=IrisTrainingModel(model,classifier).to(device)
    optimizer=opt.Adam(training_model.parameters(),lr=1e-3)
    best_val=float("inf")
    start_time=datetime.datetime.now()
    print("Starting training\nStart time: ",start_time)
    for epoch in range(1, epochs+1):
        train_loss=one_epoch_training(training_model,train_loader,optimizer,crit,device)
        val_loss,val_acc=evaluate(training_model,val_loader,crit,device)
        print("[Epoch",epoch,"] train=",round(train_loss,2)," loss=",round(val_loss,2)," accuracy= ",round(val_acc*100,2),'%')
        # checkpointing
        if val_loss<best_val:
            best_val=val_loss
            torch.save({"model": model.state_dict(),"\nclassifier": classifier.state_dict()},"best_iridion_model.pth")
            print("Model saved")
    fin_time=datetime.datetime.now()
    train_time=fin_time-start_time
    print("Training finished\nTraining time: ",train_time)
if __name__=="__main__":
    main()
'''

