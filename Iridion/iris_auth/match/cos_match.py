import torch.nn.functional as F

class Matcher:
    @staticmethod
    def match(p1,p2):
        return F.cosine_similarity(p1,p2,dim=1).item()
    @staticmethod
    def verify(p1,p2,threshold=0.8):
        score=Matcher.match(p1,p2)
        return score >= threshold,score
