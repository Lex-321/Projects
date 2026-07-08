import os
import random


def path_builder(dataset):
    persons = [folder for folder in os.listdir(dataset) if os.path.isdir(os.path.join(dataset,folder))]
    return persons

def sample_same_pair(dataset):
    persons=path_builder(dataset)
    person=random.choice(persons)
    side = random.choice(["L","R"])
    eye_dir = os.path.join(dataset,person,side)
    files = os.listdir(str(eye_dir))
    img1, img2 = random.sample(files, 2)
    return os.path.join(str(eye_dir), str(img1)),os.path.join(str(eye_dir),str(img2))

def sample_different_pair(dataset):
    persons = path_builder(dataset)
    person1, person2 = random.sample(persons,2)
    side1 = random.choice(["L", "R"])
    side2 = random.choice(["L", "R"])
    dir1 = os.path.join(dataset,person1,side1)
    dir2 = os.path.join(dataset,person2,side2)
    img1 = random.choice(os.listdir(str(dir1)))
    img2 = random.choice(os.listdir(str(dir2)))
    return os.path.join(str(dir1),img1), os.path.join(str(dir2),img2)

def generate_same_pairs(dataset,n=100):
    pairs = []
    for i in range(n):
        pairs.append(sample_same_pair(dataset))
    return pairs

def generate_different_pairs(dataset,n=100):
    pairs = []
    for i in range(n):
        pairs.append(sample_different_pair(dataset))
    return pairs
