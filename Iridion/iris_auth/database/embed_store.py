import os
import torch

class EmbeddingStore:
    def __init__(self,db_path="iris_auth/database/users_db.pt"):
        self.db_path=db_path
        self.embeddings = {}
        self.load()

    def load(self): #Load database
        if os.path.exists(self.db_path):
            self.embeddings=torch.load(self.db_path,map_location="cpu")
            print("[INFO] Loaded ",len(self.embeddings)," users")
        else:
            self.embeddings={}
            print("[INFO] New embedding database created")

    def save(self): #Save database
        torch.save(self.embeddings,self.db_path)

    def add_user(self, user_id, embedding):
        embedding=(embedding.detach().cpu().squeeze(0))
        if user_id not in self.embeddings:
            self.embeddings[user_id]=[]
        self.embeddings[user_id].append(embedding)
        self.save()
        print("[INFO] User ",user_id," saved")

    def rm_user(self,user_id):
        if user_id in self.embeddings:
            del self.embeddings[user_id]
            self.save()
            print("[INFO] User ",user_id," removed")

    def get_embedding(self,user_id):
        return self.embeddings.get(user_id,None)

    def list_users(self):
        return list(self.embeddings.keys())

    def get_all(self):
        return self.embeddings

    def exist(self,user_id):
        return user_id in self.embeddings

    def size(self):
        return len(self.embeddings)