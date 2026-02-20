from django.shortcuts import render
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
import ast
from Algorytm_Dijkstry.Algorytm_Dijkstry import dijkstra
from Algorytm_Dijkstry.Algorytm_Dijkstry import generate_graph
def index(request):
    return render(request, "D_App/GUI.html")
@csrf_exempt  # tymczasowo, do testów
def run_dijkstra(request):
    if request.method == "POST":
        data = json.loads(request.body)
        graph_data = data.get("graph", {})
        start = data.get("start")
        graph = {node: [] for node in graph_data["nodes"]}
        for u, v, w in graph_data["edges"]:
            graph[u].append((v, w))
            graph[v].append((u, w))
        distances,previous = dijkstra(graph, start)
        return JsonResponse({"distances": distances, "previous": previous})
    return JsonResponse({"error": "Only POST allowed"}, status=400)
@csrf_exempt
def generate_graph_view(request):
    if request.method == "POST":
        try:
            data_str=request.body.decode('utf8')
            data=ast.literal_eval(data_str)
            #data=json.loads(data_str)
            n = int(data.get("n", 5))
            graph = generate_graph(n)
            return JsonResponse(graph)
        except Exception as e:
            print("==== Exception:", e)
            return JsonResponse({"error": str(e)}, status=400)
    return JsonResponse({"error": "POST required"}, status=400)