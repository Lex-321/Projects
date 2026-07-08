import os
import random

import torch
from PIL import Image

from iris_auth.match.cos_match import Matcher
from iris_auth.models.resnet18_embedding import IrisEmbedModel
from iris_auth.preprocessing.transform import IrisTransforms

class Iridion_Identification_Test:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        print("[INFO] Device: ", self.device)
        self.model = IrisEmbedModel().to(self.device)
        self.transforms = IrisTransforms()
        self.checkpoint_path = os.path.normpath(os.path.join(os.path.dirname(__file__), "..", "..", "best_iridion_model.pth"))
        self.checkpoint = torch.load(self.checkpoint_path,map_location=torch.device("cuda" if torch.cuda.is_available() else "cpu"))
        self.dataset = os.path.join("iris_auth", "data", "CASIA-Iris-Thousand", "CASIA-Iris-Thousand")
        self.model.load_state_dict(self.checkpoint["model"])
        self.model.eval()

    def run(self):
        self.ID_Stats()
        print("\n[OK] PIPELINE TEST PASSED\n")

    def get_embedding(self, image_path):
        image=Image.open(image_path).convert("L")
        image=self.transforms.val_transform(image)
        image=image.unsqueeze(0).to(self.device)
        with torch.no_grad():
            embedding=self.model(image)
        return embedding

    def ID_test(self):
        hun=random.randint(0,9)
        dec=random.randint(0,9)
        uni=random.randint(0,9)
        TpID=str(hun)+str(dec)+str(uni)
        print("Target:",TpID)
        person=os.path.join(self.dataset, TpID)
        side=random.choice(["L", "R"])
        probe=os.path.join(person,side)
        files=os.listdir(str(probe))
        f_probe=random.choice(files)
        probe_path=os.path.join(probe, f_probe)
        #print("[INFO] Probe:\n",probe_path)
        e_probe=self.get_embedding(probe_path)
        id_rank=[]
        tested=[]
        full_rank=[]
        for i in range(1000):
            CpID=f"{i:03d}"
            if CpID not in tested:
                tested.append(CpID)
                person=os.path.join(self.dataset,CpID)
                photo=os.path.join(person,side)
                files=os.listdir(str(photo))
                f_photo=random.choice(files)
                photo_path=os.path.join(photo,f_photo)
                if probe_path==photo_path:
                    print("[INFO] probe_path == photo_path\nChoosing another photo")
                    f_photo = random.choice(files)
                    photo_path = os.path.join(photo, f_photo)
                    print(photo_path)
                if str(photo_path).endswith(".jpg"):
                    e_photo=self.get_embedding(photo_path)
                    match_score=Matcher.match(e_probe,e_photo)
                    match=(CpID,round(match_score,3))
                    match_and_path=(CpID,round(match_score,3),photo_path)
                    id_rank.append(match)
                    full_rank.append(match_and_path)
                else:
                    continue
            else:
                continue
        id_rank.sort(key=lambda x: x[1],reverse=True)
        full_rank.sort(key=lambda x: x[1], reverse=True)
        print(id_rank[:10])
        #print("Best match:\n",full_rank[0])
        Trank=0
        for rank,(pid,score) in enumerate(id_rank):
            if pid==TpID:
                print("Target rank:",rank+1,"score:",round(score,3))
                Trank=rank+1
                break
        return Trank

    def ID_Stats(self,test_q=100):
        top1=0
        top5=0
        top10=0
        for i in range(0,test_q):
            rank=self.ID_test()
            if rank==1:
                top1+=1
            elif 6>rank>1:
                top5+=1
            elif 11>rank>5:
                top10+=1
        tp1percent=round((top1/test_q)*100,2)
        tp5percent=round((top5/test_q)*100,2)
        tp10percent=round((top10/test_q)*100,2)
        print("[INFO] Top 1:",tp1percent,'%')
        print("[INFO] Top 5:",tp5percent+tp1percent,"%")
        print("[INFO] Top 10:",tp10percent+tp5percent+tp1percent,"%")
        return




