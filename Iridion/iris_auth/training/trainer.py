import torch

def one_epoch_training(model,loader,optimizer,criterion,device):
    model.train()
    total_loss=0
    for images,labels in loader:
        images=images.to(device)
        labels=labels.to(device)
        logits=model(images)
        loss=criterion(logits, labels)
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()
        total_loss+=loss.item()
    return total_loss/len(loader)

@torch.no_grad()

def evaluate(model, loader, criterion, device):
    model.eval()
    total_loss=0
    cor=0
    total=0
    for images,labels in loader:
        images=images.to(device)
        labels=labels.to(device)
        logits=model(images)
        loss=criterion(logits,labels)
        total_loss+=loss.item()
        preds=logits.argmax(dim=1)
        cor+=(preds==labels).sum().item()
        total+=labels.size(0)
    accuracy=cor/total
    avg_loss=total_loss/len(loader)
    return avg_loss,accuracy