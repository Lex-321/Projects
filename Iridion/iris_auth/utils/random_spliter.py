from torch.utils.data import random_split

def RandomSplitter(dataset,ratio=0.8):
    train_size=int(ratio*len(dataset))
    val_size=len(dataset)-train_size
    train_dataset,val_dataset=random_split(dataset,[train_size,val_size])
    return train_dataset,val_dataset

def UserCaseSplitter(dataset,ratio=0.8):
    train_size=int(ratio*len(dataset))