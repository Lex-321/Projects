from django.urls import path

from . import views

urlpatterns = [
    path("", views.index, name="index"),
    path("run_dijkstra/", views.run_dijkstra, name="run_dijkstra"),
    path("generate_graph/", views.generate_graph_view, name="generate_graph"),
]