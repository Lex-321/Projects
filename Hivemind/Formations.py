import math
def formation_wedge(i, spacing, spread=0.7):
    rank=(i+1)//2
    side=-1 if i % 2 else 1
    x=-rank*spacing
    y=side*rank*spacing*spread
    return x, y
def formation_line(i,spacing):
    return -i*spacing, 0.0
def formation_column(i,spacing):
    return -i*spacing,90.0
def formation_circle(i, spacing, total):
    angle=2*math.pi*i/total
    x=math.cos(angle)*spacing
    y=math.sin(angle)*spacing
    return x, y

