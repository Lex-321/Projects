import pygame
import os
import sys
import pyautogui

import Enviroment
import views.Squad_Builder as sb_module
import views.MainMenuView as menu_module
import views.Sim_View as sim_module
import views.Mission_View as mission_module
from View_Manager import View_Manager

# Pygame initialization
pygame.init()
# ------------ SCREEN CONTROLS --------------------------
WIDTH, HEIGHT = pyautogui.size()
screen=pygame.display.set_mode((WIDTH, HEIGHT), pygame.FULLSCREEN)
font=pygame.font.SysFont("Arial", 16)
pygame.display.set_caption("Hivemind")
# ----------------------------------------------------------
#                  GLOBAL STATE
# ----------------------------------------------------------
drones=[]
wings ={}   # {"Red": [drone,drone],...}
squadrons = {}  # {"Alpha": [drone1, wing1], ...}
checkpoints=[]
env=Enviroment.Enviroment(0.1)
env.enable_wind=False
env.enable_birds=True
# -------------------- Inicjalizacja widoków -------------------
view_manager=View_Manager()
sim_view=sim_module.Sim_View(drones,env,view_manager)
create_view=sb_module.SquadBuilder(drones,wings,squadrons,sim_view,view_manager)
mission_view=mission_module.Mission_View(drones,checkpoints,view_manager)
view_manager.set_current_view(menu_module.MainMenuView(view_manager,env))
view_manager.register("Main_Menu",menu_module)
view_manager.register("Squad_Builder",create_view)
view_manager.register("Mission_View", mission_view)
view_manager.register("Sim_View", sim_view)
current_view = menu_module  # aktualnie wyświetlany widok
# -------------------- MAIN LOOP ---------------------------
clock = pygame.time.Clock()
running = True
while running:
    dt=clock.tick(60)/1000
    env.world_tick()
    for squadron in squadrons.values():
        if not sim_view.paused:
            squadron.update(env,dt)
    for event in pygame.event.get():
        view_manager.current_view.handle_event(event)
        if event.type==pygame.QUIT:
            running=False
        if event.type==pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                # tryb okienkowy
                WIDTH, HEIGHT = 1200, 1000
                screen_x = (pyautogui.size().width - WIDTH) // 2
                screen_y = (pyautogui.size().height - HEIGHT) // 2
                os.environ['SDL_VIDEO_WINDOW_POS'] = f"{screen_x},{screen_y}"
                screen = pygame.display.set_mode((WIDTH, HEIGHT))
    view_manager.current_view.update(dt)
    view_manager.current_view.draw(screen)
    pygame.display.flip()
    clock.tick(60)
pygame.quit()
sys.exit()