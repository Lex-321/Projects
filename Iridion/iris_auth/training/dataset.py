from torch.utils.data import Dataset
from PIL import Image
import os

class IrisDataset(Dataset):
    def __init__(self,root_dir,transform=None):
        self.root_dir=root_dir
        self.transform=transform
        self.samples=[]
        self.label_map={}
        self._build_index()

    def _build_index(self):
        label_id=0
        for subset in os.listdir(self.root_dir):
            subset_path=os.path.join(self.root_dir, subset)
            if not os.path.isdir(subset_path):
                continue
            for subject in os.listdir(subset_path):
                subject_path=os.path.join(subset_path,subject)
                if not os.path.isdir(subject_path):
                    continue
                key = f"{subset}_{subject}"
                if key not in self.label_map:
                    self.label_map[key] = label_id
                    label_id+=1
                # L/R
                for eye_side in os.listdir(subject_path):
                    eye_path = os.path.join(subject_path,eye_side)
                    if not os.path.isdir(eye_path):
                        continue
                    for file in os.listdir(eye_path):
                        if file.lower().endswith((".jpg",".jpeg",".png",".bmp")):
                            path = os.path.join(eye_path,file)
                            self.samples.append((path,self.label_map[key]))

        print(f"Total samples: {len(self.samples)}")
        print(f"Total classes: {len(self.label_map)}")
        if len(self.samples) > 0:
            print("Example sample:", self.samples[0])
    def __len__(self):
        return len(self.samples)

    def __getitem__(self, idx):
        path,label=self.samples[idx]
        image=Image.open(path).convert("L")  #grayscale
        if self.transform:
            image=self.transform(image)
        return image,label
