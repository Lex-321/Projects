import math

import pygame
import pyautogui

from Camera import Camera
from Orders.ChangeFormation import ChangeFormationOrder
from controls import Listbox, TextInput, Button, Droplist, view_swap

#dane globalne
global drones
global squadrons
# rozmiary
WIDTH, HEIGHT = pyautogui.size()
# Colors
stand_by_panel=(180, 199, 222)
drone_list_panel=(195, 209, 227)
panel_border=(161, 171, 171)
WHITE=(255, 255, 255)
BLACK=(0, 0, 0)
BLUE=(60, 120, 255)
RED=(255, 70, 70)
GREEN=(100, 255, 100)
YELLOW=(235, 207, 0)
BACKGROUND = (151, 187, 230)
Commander=(135, 3, 7)
Wing=(3, 60, 135)

def drone_draw(surface, color, cx, cy, heading, size=6):
    nose_x = cx + size * math.cos(heading)
    nose_y = cy - size * math.sin(heading)
    back_x = cx - size * 0.6 * math.cos(heading)
    back_y = cy + size * 0.6 * math.sin(heading)
    width = size * 0.6
    perp_x = math.cos(heading + math.pi / 2)
    perp_y = -math.sin(heading + math.pi / 2)
    left_x = back_x + width * perp_x
    left_y = back_y + width * perp_y
    right_x = back_x - width * perp_x
    right_y = back_y - width * perp_y
    points = [(nose_x, nose_y), (left_x, left_y), (right_x, right_y)]
    pygame.draw.polygon(surface, color, points)

class Sim_View:
    def __init__(self,drones,env,view_manager):
        self.env = env
        self.drones = drones
        self.squadron = None
        self.title_font = pygame.font.Font(None, 48)
        self.background_color = BACKGROUND
        self.scale = 50  # Piksele na jednostkę przestrzeni
        self.offset_x = 0
        self.offset_y = 0
        self.paused=True
        self.zones=False
        self.view_manager=view_manager

        # stan widoku
        self.selected_drone = None
        self.view_point="TOP"

        #PANEL STAND-BY
        self.stand_by_panel = pygame.Rect(10, 10, WIDTH / 3, HEIGHT - 20)
        self.mission_tracker = TextInput(340, 20, 300, 50)

        # kontrolki
        self.Drone_list=Listbox(20, 20, 300, 500, self.drones)
        self.Drone_ID=TextInput(20,540,100,50)
        self.Drone_status=TextInput(140,540,100,50)
        self.Drone_Stand_By=TextInput(260,540,380,50)
        self.Change_Formation_list = Droplist(240, 600, 100, 50, ["wedge", "line", "column", "circle"])
        self.Change_Formation_Button=Button(20, 600, 200, 50,"Change Formation",(lambda: self.change_formation()))

        self.squad_builder = Button(20, HEIGHT - 300, 200, 50, "Squad Builder",lambda: self.view_manager.switch_views("Squad_Builder"))
        self.mission = Button(20, HEIGHT - 230, 200, 50, "Mission",lambda: self.view_manager.switch_views("Mission_View"))

        self.world_rect = pygame.Rect(self.stand_by_panel.right + 10,10,WIDTH - self.stand_by_panel.width - 20,HEIGHT - 20 )
        self.camera = None
    def attach_squadron(self,squadron):
        self.squadron=squadron
        self.camera=Camera(squadron,self.world_rect)
    def change_formation(self):
        formation=self.Change_Formation_list.selected
        if formation is None:
            return
        if self.squadron is None:
            return
        order=ChangeFormationOrder(formation)
        self.squadron.issue_order(order)
    def mission_tracker_message(self):
        mess=""
        if self.squadron is None:
            return mess
        commander = self.squadron.get_commander()
        if commander is None:
            return mess
        if commander.mission.completed:
            return "Mission completed"
        dist = commander.distance_to_target
        if dist is None:
            return "No target"
        mission = commander.mission
        mess=("Distance: "+str(dist))
        return mess
    def handle_event(self, event):
        self.Drone_ID.handle_event(event)
        self.Drone_status.handle_event(event)
        self.Drone_list.handle_event(event)
        self.Drone_status.handle_event(event)
        self.Drone_Stand_By.handle_event(event)
        self.Change_Formation_list.handle_event(event)
        self.Change_Formation_Button.handle_event(event)
        self.squad_builder.handle_event(event)
        self.mission.handle_event(event)

        selected = self.Drone_list.get_selected()
        if selected and selected != self.selected_drone:
            self.selected_drone = selected
            self.Drone_ID.text = selected.id
            self.Drone_status.text = selected.status

        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_UP:
                self.offset_y -= 20
            elif event.key == pygame.K_DOWN:
                self.offset_y += 20
            elif event.key == pygame.K_LEFT:
                self.offset_x -= 20
            elif event.key == pygame.K_RIGHT:
                self.offset_x += 20
            elif event.key == pygame.K_EQUALS:
                self.scale *= 1.1
            elif event.key == pygame.K_MINUS:
                self.scale /= 1.1
        if event.type==pygame.KEYDOWN:
            if event.key==pygame.K_SPACE:
                self.paused = not self.paused
        if event.type==pygame.KEYDOWN:
            if event.key==pygame.K_z:
                self.zones=not self.zones
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_p:
                if self.view_point == "TOP":
                    self.view_point = "SIDE"
                else:
                    self.view_point = "TOP"
    def update(self, dt):
        self.Drone_list.items = self.drones
        if self.selected_drone:
            try:
                value = float(self.Drone_status.text)
                self.Drone_status.max_speed = value
            except ValueError:
                pass
        if self.selected_drone:
            self.Drone_Stand_By.text="|".join(self.selected_drone.stand_by_mess)

        if self.mission_tracker:
            self.mission_tracker.text = self.mission_tracker_message()

        # kamera
        if self.camera:
            self.camera.view_point = self.view_point
            self.camera.scale = self.scale
            self.camera.CAM_movement()
    def draw(self, surface):
        surface.fill(self.background_color)

        # panel STAND-BY
        pygame.draw.rect(surface, stand_by_panel, self.stand_by_panel)
        pygame.draw.rect(surface, panel_border, self.stand_by_panel, 2)
        self.mission_tracker.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Drone_list.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Drone_ID.draw(surface,font=pygame.font.SysFont("Arial", 16))
        self.Drone_status.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Drone_Stand_By.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Change_Formation_Button.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Change_Formation_list.draw(surface, font=pygame.font.SysFont("Arial", 16))

        self.squad_builder.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.mission.draw(surface, font=pygame.font.SysFont("Arial", 16))

        # Rysowanie dronów
        for drone in self.drones:
            if drone.role == "Squad Leader":
                pos = drone.gps_position
                if pos is None:
                    continue
                x, y, z = pos
            else:
                leader=drone.squadron.get_commander()
                lx, ly, lz = leader.gps_position
                x=lx+drone.real_x
                y=ly+drone.real_y
                z=drone.altitude

            screen_x, screen_y = self.camera.world_to_screen(x, y, z)

            # Kolor wg roli
            if drone.role == "Squad Leader":
                color = Commander
            elif drone.role == "Wing Leader":
                color = Wing
            else:
                color = GREEN
            if self.zones:
                danger_radius = drone.danger_zone_radius
                safe_radius = drone.safety_zone_radius  # np. 1.0
                control_radius = drone.control_zone_radius
                pygame.draw.circle(surface,RED,(screen_x, screen_y),int(danger_radius * self.scale),1)
                pygame.draw.circle(surface, YELLOW, (screen_x, screen_y), int(safe_radius * self.scale), 1)
                pygame.draw.circle(surface, GREEN, (screen_x, screen_y), int(control_radius * self.scale), 1)

            if self.view_point=="TOP":
                drone_draw(surface,color, screen_x, screen_y, drone.heading,6)
            else:
                pygame.draw.circle(surface, color, (screen_x, screen_y), 8)

            # Wektor prędkości
            vx, vy, vz = drone.velocity
            if self.view_point == "TOP":
                end_x = screen_x + int(vx * self.scale * 0.5)
                end_y = screen_y - int(vy * self.scale * 0.5)
            else:
                end_x = screen_x + int(vx * self.scale * 0.5)
                end_y = screen_y - int(vz * self.scale * 0.5)
            pygame.draw.line(surface, RED, (screen_x, screen_y), (end_x, end_y), 2)

            # ID
            font = pygame.font.Font(None, 20)
            text_surf = font.render(drone.id, True, BLACK)
            surface.blit(text_surf, (screen_x + 10, screen_y + 10))