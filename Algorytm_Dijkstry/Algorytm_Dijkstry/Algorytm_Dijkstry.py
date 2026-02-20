import heapq
import random
def create_graph():
    graph={}
    n=int(input("Podaj liczbę wierzchołków: "))
    for i in range(n):
        v=input(f"Podaj nazwę wierzchołka {i+1}: ")
        graph[v] = []
    m=int(input("Podaj liczbę krawędzi: "))
    for i in range(m):
        u=input("Początek krawędzi: ")
        v=input("Koniec krawędzi: ")
        w=int(input("Waga krawędzi: "))
        if u in graph and v in graph:
            graph[u].append((v,w))
            graph[v].append((u,w))
        else:
            print("Podany wieszchołek nie istnieje!")
    print("Utworzono graf:")
    return graph
#def display(g):
    print("Utworzony graf:")
    for v, sasiedzi in graph.items():
        print(v, ":", sasiedzi)
def generate_graph(n):
    graphNodes=[]
    graphEdges=[]
    vert_names=[]
    while len(vert_names) < n:
        name=chr(random.randint(97,122))  # litery a-z
        if name not in vert_names:
            vert_names.append(name)
    # wierzchołki
    for i, name in enumerate(vert_names):
        node={
            "id": name,
            "x": random.randint(50,550),
            "y": random.randint(50,350)
        }
        graphNodes.append(node)
    # krawędzie
    m = n
    for _ in range(m):
        u=random.choice(vert_names)
        v=random.choice(vert_names)
        if u!=v:
            w=random.randint(1, 10)
            graphEdges.append({"u": u, "v": v, "weight": w})
    connected_set=set()
    for node in vert_names: # szukanie wierzchołków w przestrzeni
        if any(e["u"] == node or e["v"] == node for e in graphEdges):
            connected_set.add(node)
        if not connected_set:
            connected_set.add(vert_names[0])
    for node in vert_names:
        if node not in connected_set:
            other=random.choice(list(connected_set))
            w=random.randint(1,10)
            graphEdges.append({"u": node, "v": other, "weight": w})
    return {"nodes": graphNodes, "edges": graphEdges}
def dijkstra(g,s):
    distances={vertex:float('inf') for vertex in g}
    distances[s]=0
    previous={vertex: None for vertex in g}
    priority_queue=[(0,s)]
    while priority_queue:
        current_distance,current_vertex=heapq.heappop(priority_queue)
        if current_distance>distances[current_vertex]:
            continue
        for neighbor,weight in g[current_vertex]:
            distance=current_distance + weight
            if distance < distances[neighbor]:
                distances[neighbor] = distance
                previous[neighbor] = current_vertex
                heapq.heappush(priority_queue, (distance, neighbor))
    return distances,previous
######
#graph=create_graph()
#graph=graph_generator(5)
#display(graph)
#start=input("Podaj wierzchołek początkowy: ")
#if start in graph:
    #result=dijkstra(graph,start)
    #print("Najkrótsze odległości od wierzchołka", start)
    #for vertex, distance in result.items():
        #print(f" -> {vertex}: {distance}")
#else:
    #print("Podany wierzchołek nie istnieje.")