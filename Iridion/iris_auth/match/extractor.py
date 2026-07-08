import torch

class Extractor:

    def __init__(self,model):
        self.model=model
        self.model.eval()

    @torch.no_grad()
    def extract(self,image_tensor):
        embedding=self.model(image_tensor)
        return embedding