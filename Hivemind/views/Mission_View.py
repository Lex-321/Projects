import pygame
import pyautogui

from Mission import Mission
from controls import Listbox, TextInput, Button, view_swap

#dane globalne
global drones
global squadrons
# rozmiary
WIDTH, HEIGHT = pyautogui.size()
# Colors
background=(151, 187, 230)
commander_settings_panel=(180, 199, 222)
commander_list_panel=(195, 209, 227)
mission_map=(252, 245, 245)
panel_border=(161, 171, 171)
create_drone=(202, 225, 252)
WHITE=(255, 255, 255)
BLACK=(0, 0, 0)
BLUE=(60, 120, 255)
RED=(255, 70, 70)
DRONE_COLORS = {
    "Squad Leader": (54, 181, 139),
    "Wing Leader": (54, 166, 181),
    "Generic": (214, 209, 58)
}

def kmh_to_ms(v_kmh):
    return v_kmh / 3.6

def ms_to_kmh(v_ms):
    return v_ms * 3.6

class Mission_View:
    def __init__(self,drones,checkpoints,view_manager):
        self.checkpoints = checkpoints
        self.drones = drones
        self.title_font = pygame.font.Font(None, 48)
        self.background_color = background
        self.leaders=[]
        self.view_manager=view_manager

        #stan widoku
        self.selected_commander = None
        self.active_checkpoint= None

        #Napisy
        font = pygame.font.SysFont('Arial', 16)
        self.MS_label = "Max Speed [km/h]"
        self.MS_label_surface = font.render(self.MS_label, True, BLACK)
        self.MS_label_rect = self.MS_label_surface.get_rect(center=(175, 100))

        #panele
        self.commander_settings_panel = pygame.Rect(10, 10, WIDTH / 4, HEIGHT - 20)
        self.commander_list_panel = pygame.Rect(int(WIDTH / 4), 10, WIDTH / 6, HEIGHT - 20)
        self.mission_map = pygame.Rect(int(WIDTH / 3 + WIDTH / 11), 10, WIDTH / 2, HEIGHT - 20)

        #kontrolki
        self.Commander_list = Listbox(490, 20, 300, 500, self.leaders)
        self.Commander_params = Listbox(490, 530, 300, 500, self.leaders)
        self.Commander_id=TextInput(20, 20, 200, 50)
        self.Commander_max_speed=TextInput(20, 80, 100, 50)

        self.squad_builder = Button(20, 220, 200, 50, "Squad Builder", lambda: self.view_manager.switch_views("Squad_Builder"))
        self.simulation = Button(230, 220, 200, 50, "Simulation", lambda: self.view_manager.switch_views("Sim_View"))
    def handle_event(self,event):
        #textfieldy
        self.Commander_list.handle_event(event)
        self.Commander_id.handle_event(event)
        self.Commander_max_speed.handle_event(event)
        self.simulation.handle_event(event)
        self.squad_builder.handle_event(event)
        selected=self.Commander_list.get_selected()
        if selected and selected!=self.selected_commander:
            self.selected_commander=selected
            self.Commander_id.text=selected.id
            self.Commander_max_speed.text=str(selected.max_speed)
        # chceckpointy
        if event.type==pygame.MOUSEBUTTONDOWN:
            Mx, My = event.pos
            if self.mission_map.collidepoint(Mx, My):
                world_x=(Mx-self.mission_map.centerx)/50
                world_y=-(My-self.mission_map.centery)/50
                self.checkpoints.append([world_x,world_y])
                if self.selected_commander:
                    if self.selected_commander.mission is None:
                        self.selected_commander.mission=Mission(self.checkpoints)
                    self.selected_commander.mission.set_checkpoints(self.checkpoints)
    def update(self,dt):
        self.Commander_list.items=[drone for drone in self.drones if drone.role in ("Squad Leader", "Wing Leader")]
        if self.selected_commander:
            try:
                value=float(self.Commander_max_speed.text)
                self.selected_commander.max_speed=value
            except ValueError:
                pass
    def draw(self,surface):
        surface.fill(self.background_color)
        # commander_settings_panel
        pygame.draw.rect(surface, commander_settings_panel, self.commander_settings_panel)
        pygame.draw.rect(surface, panel_border, self.commander_settings_panel, 2)
        self.Commander_id.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Commander_max_speed.draw(surface,font=pygame.font.SysFont("Arial",16))
        surface.blit(self.MS_label_surface,self.MS_label_rect)
        # commander_list_panel
        pygame.draw.rect(surface, commander_list_panel, self.commander_list_panel)
        pygame.draw.rect(surface, panel_border, self.commander_list_panel, 2)
        self.Commander_list.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Commander_params.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.squad_builder.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.simulation.draw(surface, font=pygame.font.SysFont("Arial", 16))
        #mission_map
        pygame.draw.rect(surface, mission_map, self.mission_map)
        pygame.draw.rect(surface, panel_border, self.mission_map, 2)

        # checkpointy
        for i,(x,y) in enumerate(self.checkpoints):
            sX=int(self.mission_map.centerx+x*50)
            sY=int(self.mission_map.centery - y * 50)
            if i==0:
                color = RED
            else:
                color=BLUE
            pygame.draw.circle(surface, color, (sX,sY), 4)
            if i>0:
                pX,pY=self.checkpoints[i-1]
                psX=int(self.mission_map.centerx+pX*50)
                psY=int(self.mission_map.centery-pY*50)
                pygame.draw.line(surface,BLACK,(psX,psY),(sX,sY),2)