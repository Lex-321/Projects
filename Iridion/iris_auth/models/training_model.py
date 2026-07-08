import torch.nn as nn

class IrisTrainingModel(nn.Module):
    def __init__(self,embed_model,classifier):
        super().__init__()
        self.embedding_model=embed_model
        self.classifier=classifier

    def forward(self,x):
        embed=self.embedding_model(x)
        logits=self.classifier(embed)
        return logits