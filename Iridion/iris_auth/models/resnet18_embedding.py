import torch.nn as nn
import torch.nn.functional as F
from torchvision import models

class IrisEmbedModel(nn.Module):
    def __init__(self):
        super().__init__()
        backbone=models.resnet18(weights=models.ResNet18_Weights.DEFAULT)
        backbone.conv1=nn.Conv2d(
            1,
            64,
            kernel_size=7,
            stride=2,
            padding=3,
            bias=False)
        backbone.fc=nn.Identity()
        self.backbone = backbone
        self.embed=nn.Sequential(
            nn.Linear(512, 256),
            nn.AvgPool1d(2, 2),
            nn.ReLU(),
            nn.Dropout(0.3))

    def forward(self, x):
        features=self.backbone(x)
        embedding=self.embed(features)
        embedding=F.normalize(embedding,p=2,dim=1)
        return embedding