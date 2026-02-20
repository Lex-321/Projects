import pygame
import pyautogui
from pygame import font
from controls import Button, Popup,view_swap

# rozmiary
WIDTH, HEIGHT = pyautogui.size()
# Colors
background=(151, 187, 230)
drone_creation_panel=(180, 199, 222)
drone_list_panel=(195, 209, 227)
panel_border=(161, 171, 171)
create_drone=(202, 225, 252)
WHITE=(255, 255, 255)
BLACK=(0, 0, 0)
BLUE=(60, 120, 255)
RED=(255, 70, 70)

class MainMenuView:
    def __init__(self,view_manager,env):
        self.title_font=pygame.font.Font(None, 64)
        self.background_color=background
        self.view_manager=view_manager
        self.env=env

        #Kontrolki
        self.squad_builder=Button(100,HEIGHT-300,200,50,"Squad Builder",lambda: self.view_manager.switch_views("Squad_Builder"))
        self.mission = Button(100, HEIGHT - 230, 200, 50, "Mission",lambda: self.view_manager.switch_views("Mission_View"))
        self.simulation = Button(100, HEIGHT - 150, 200, 50, "Simulation",lambda: self.view_manager.switch_views("Sim_View"))
        self.wind=Button(100, HEIGHT - 600, 200, 50, "Wind",lambda: self.wind_controler)
        self.Wind_Popup = Popup(WIDTH / 2 - 100, HEIGHT / 2 - 20, 250, 50, "Wind On",self.wind)
        self.birds = Button(100, HEIGHT - 500, 200, 50, "Angry birds", lambda: self.angry_birds)
        self.Birds_Popup = Popup(WIDTH / 2 - 100, HEIGHT / 2 - 20, 250, 50, "Birds Off", self.birds)

    def wind_controler(self):
        if self.env.enable_wind:
            self.env.enable_wind = False
            self.Wind_Popup.text = "Wind Off"
            self.Wind_Popup.show("Wind Off")
            print("Wind Off")
        else:
            self.env.enable_wind = True
            self.Wind_Popup.text = "Wind On"
            self.Wind_Popup.show("Wind On")
            print("Wind On")
    def angry_birds(self):
        if self.env.enable_birds:
            self.env.enable_birds = False
            self.Birds_Popup.text = "Birds Off"
            self.Birds_Popup.show("Birds Off")
            print("Birds Off")
        else:
            self.env.enable_birds = True
            self.Birds_Popup.text = "Birds On"
            self.Birds_Popup.show("Birds On")
            print("Birds On")
    def handle_event(self, event):
        self.squad_builder.handle_event(event)
        self.mission.handle_event(event)
        self.simulation.handle_event(event)
        self.wind.handle_event(event)
        self.birds.handle_event(event)
        self.Birds_Popup.handle_event(event)
        self.Wind_Popup.handle_event(event)
    def update(self,dt):
        pass
    def draw(self, surface):
        surface.fill(self.background_color)
        title_surface = self.title_font.render("Main menu", True, (255, 255, 255))
        surface.blit(title_surface, (100, 100))
        self.squad_builder.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.mission.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.simulation.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.wind.draw(surface,font=pygame.font.SysFont("Arial", 16))
        self.birds.draw(surface,font=pygame.font.SysFont("Arial", 16))
        self.Wind_Popup.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Birds_Popup.draw(surface, font=pygame.font.SysFont("Arial", 16))
