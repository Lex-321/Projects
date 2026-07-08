import torchvision.transforms as T

class IrisTransforms:

    def __init__(self, img_size=224):
        self.train_transform=T.Compose([
            T.Resize((img_size, img_size)),
            T.Grayscale(num_output_channels=1),
            T.ToTensor(),
            T.Normalize(mean=[0.5],std=[0.5])])
        self.val_transform=T.Compose([
            T.Resize((img_size, img_size)),
            T.Grayscale(num_output_channels=1),
            T.ToTensor(),
            T.Normalize(mean=[0.5], std=[0.5])
        ])