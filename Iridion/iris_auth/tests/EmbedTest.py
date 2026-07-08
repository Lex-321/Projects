import os
import numpy as np
import  torch
from PIL import Image
from iris_auth.match.cos_match import Matcher
from iris_auth.models.resnet18_embedding import IrisEmbedModel
from iris_auth.preprocessing.transform import IrisTransforms
from iris_auth.utils import pair_sampler


class Iridion_Embeding_Test:
    def __init__(self):
        self.device=torch.device("cuda" if torch.cuda.is_available() else "cpu")
        print("[INFO] Device: ", self.device)
        self.model=IrisEmbedModel().to(self.device)
        self.transforms=IrisTransforms()
        self.checkpoint_path = os.path.normpath(os.path.join(os.path.dirname(__file__), "..", "..", "best_iridion_model.pth"))
        self.checkpoint=torch.load(self.checkpoint_path, map_location=torch.device("cuda" if torch.cuda.is_available() else "cpu"))
        test_img_1=os.path.join("iris_auth","data","CASIA-Iris-Thousand","CASIA-Iris-Thousand","000","L","S5000L00.jpg")
        test_img_2=os.path.join("iris_auth","data","CASIA-Iris-Thousand","CASIA-Iris-Thousand","000","L","S5000L01.jpg")
        test_img_3=os.path.join("iris_auth","data","CASIA-Iris-Thousand","CASIA-Iris-Thousand","025","R","S5025R00.jpg")
        self.dataset = os.path.join("iris_auth", "data", "CASIA-Iris-Thousand","CASIA-Iris-Thousand")
        self.images=[test_img_1,test_img_2,test_img_3]
        self.model.load_state_dict(self.checkpoint["model"])
        self.model.eval()

    def run(self):
        self.embed_test()
        self.embed_stats_test()
        print("\n[OK] PIPELINE TEST PASSED")

    def get_embedding(self, image_path):
        image=Image.open(image_path).convert("L")
        image=self.transforms.val_transform(image)
        image=image.unsqueeze(0).to(self.device)
        with torch.no_grad():
            embedding=self.model(image)
        return embedding

    def embed_test(self):
        with torch.no_grad():
            output1=self.get_embedding(self.images[0])
            print(output1.shape)
            output2=self.get_embedding(self.images[1])
            print(output2.shape)
            output3=self.get_embedding(self.images[2])
            print(output3.shape)
        match_score_1=Matcher.match(output1,output2)
        match_score_2=Matcher.match(output1,output3)
        print("Same person: ",match_score_1)
        print("Differnt person: ",match_score_2)
        return match_score_1,match_score_2

    def embed_stats_test(self):
        same_pairs=pair_sampler.generate_same_pairs(dataset=self.dataset)
        diff_pairs=pair_sampler.generate_different_pairs(dataset=self.dataset)
        same_scores=[]
        diff_scores=[]
        for img1, img2 in same_pairs:
            emb1=self.get_embedding(img1)
            emb2=self.get_embedding(img2)
            score=Matcher.match(emb1,emb2)
            same_scores.append(score)
        for img1, img2 in diff_pairs:
            emb1=self.get_embedding(img1)
            emb2=self.get_embedding(img2)
            score=Matcher.match(emb1,emb2)
            diff_scores.append(score)
        same_stats = [np.mean(same_scores),np.min(same_scores),np.max(same_scores),np.std(same_scores),np.median(same_scores)]
        diff_stats=[np.mean(diff_scores),np.min(diff_scores),np.max(diff_scores),np.std(diff_scores),np.median(diff_scores)]
        space=same_stats[0]-diff_stats[0]
        print("\nSame person:\nśrednia:", round(same_stats[0],2),"\nmin:", round(same_stats[1],2),"\nmax:", round(same_stats[2],2),"\nstd:",round(same_stats[3],2),"\nmediana:", round(same_stats[4],2))
        print("\nDiffernt person:\nśrednia:", round(diff_stats[0],2),"\nmin:", round(diff_stats[1],2),"\nmax:", round(diff_stats[2],2),"\nstd:",round(diff_stats[3],2),"\nmediana:", round(diff_stats[4],2))
        print("\nSpace: ",round(space,2))
        return same_stats,diff_stats,space

