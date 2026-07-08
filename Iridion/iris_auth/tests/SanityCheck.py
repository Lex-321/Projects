import torch
import os

from iris_auth.training.dataset import IrisDataset
from iris_auth.preprocessing.transform import IrisTransforms
from iris_auth.models.resnet18_embedding import IrisEmbedModel


class Iridion_Sanity_Check:
    def __init__(self):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        print("[INFO] Device: ",self.device)
        self.transforms=IrisTransforms()
        self.root=os.path.join("iris_auth","data","CASIA-Iris-Thousand")
        self.model=IrisEmbedModel().to(self.device)
        self.model.eval()

    # -------------------------
    # RUN TEST
    # -------------------------
    def run(self):
        self._test_dataset()
        self._test_sample()
        self._test_forward()
        print("\n[OK] PIPELINE TEST PASSED")

    # -------------------------
    # DATASET TEST
    # -------------------------
    def _test_dataset(self):
        self.dataset=IrisDataset(self.root,self.transforms.train_transform)
        len_dataset=len(self.dataset)
        if len_dataset >0:
            print("[DATASET] size: ",len(self.dataset))
        else:
            print("[DATASET] size: empty dataset")

    # -------------------------
    # SAMPLE TEST
    # -------------------------
    def _test_sample(self):
        self.image, self.label = self.dataset[0]
        print("expected: torch.Size([1, 224, 224])")
        print("[SAMPLE] label: ",self.label)
        print("[SAMPLE] shape: ",self.image.shape)
        print("[SAMPLE] dtype: ",self.image.dtype)

    # -------------------------
    # FORWARD TEST
    # -------------------------
    def _test_forward(self):
        image = self.image.unsqueeze(0).to(self.device)
        print("[BATCH] expected: torch.Size([1, 1, 224, 224])")
        print("[BATCH] shape: ",image.shape)
        with torch.no_grad():embedding = self.model(image)
        print("[EMEDNIG] expected: torch.Size([1, 128])")
        print("[EMBEDDING] shape: ,",embedding.shape)
        norm=torch.norm(embedding,dim=1)
        print("[EMBEDING NORM] expected: tensor([1.0000])")
        print("[EMBEDING NORM]: ",norm)
        print("[OK] forward pass")
        print(embedding[0][:10])
