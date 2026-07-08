import os.path

import torch
from PIL import Image

from iris_auth.database.embed_store import EmbeddingStore
from iris_auth.match.cos_match import Matcher
from iris_auth.models.resnet18_embedding import IrisEmbedModel
from iris_auth.preprocessing.transform import IrisTransforms


class Register:
    def __init__(self):
        self.device=torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model=IrisEmbedModel().to(self.device)
        self.transform=IrisTransforms()
        self.model.eval()
        self.db=EmbeddingStore()
        self.checkpoint_path = os.path.normpath(os.path.join(os.path.dirname(__file__),"..","..","best_iridion_model.pth"))
        self.checkpoint=torch.load(self.checkpoint_path,map_location=torch.device("cuda" if torch.cuda.is_available() else "cpu"))
        self.model.load_state_dict(self.checkpoint["model"])

    def register(self,uid,image_path):
        image=Image.open(image_path).convert("L")
        image=self.transform.val_transform(image)
        image=image.unsqueeze(0)
        image=image.to(self.device)

        with torch.no_grad():
            embedding=self.model(image)
        self.db.add_user(uid,embedding)

class Login:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model = IrisEmbedModel().to(self.device)
        self.transform=IrisTransforms()
        self.model.eval()
        self.db=EmbeddingStore()
        self.checkpoint_path = os.path.normpath(os.path.join(os.path.dirname(__file__),"..","..","best_iridion_model.pth"))
        self.checkpoint = torch.load(self.checkpoint_path,map_location=torch.device("cuda" if torch.cuda.is_available() else "cpu"))
        self.model.load_state_dict(self.checkpoint["model"])

    def login(self,L_image_path):
        image=Image.open(L_image_path).convert("L")
        image=self.transform.val_transform(image)
        image=image.unsqueeze(0)
        image=image.to(self.device)
        Auid=None
        Ascore=-1
        with torch.no_grad():
            p_embedding=self.model(image).to(self.device)
        for uid,embeddings in self.db.get_all().items():
            user_sc=-1
            for embedding in embeddings:
                embedding=embedding.to(self.device)
                score=Matcher.match(p_embedding,embedding)
                user_sc=max(user_sc,score)
                #print(uid,round(score,3))
            if user_sc>Ascore:
                Ascore=user_sc
                Auid=uid
        if Ascore>=0.93:
            print("USER:",Auid,"ACCESS GRANTED")
            return True,Auid,Ascore
        print("ACCESS DENIED")
        return False,None,Ascore


