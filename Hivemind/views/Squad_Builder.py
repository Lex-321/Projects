import pygame
import pyautogui
from pygame import font
from Drone import Drone
from Formations import formation_wedge, formation_line, formation_column, formation_circle
from Squadron import Squadron
from Wing import Wing
from controls import Button, TextInput, Droplist, Popup, Listbox, view_swap

# rozmiary
WIDTH, HEIGHT = pyautogui.size()
DRONE_SIZE = 20
SPACING = 30
# Colors
background=(151, 187, 230)
drone_creation_panel=(180, 199, 222)
drone_list_panel=(195, 209, 227)
squadron_canvas=(252, 245, 245)
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

class SquadBuilder:
    def __init__(self,drones,wings,squadrons,sim_view,view_manager):
        self.drones=drones
        self.wings=wings
        self.squadrons=squadrons
        self.background_color=background
        self.sim_view=sim_view
        self.view_manager=view_manager
        # stan widoku
        self.selected_drone = None
        self.Wing_men = []
        #panele
        self.drone_creation_panel=pygame.Rect(10,10,WIDTH/4,HEIGHT-20,)
        self.drone_list_panel=pygame.Rect(int(WIDTH/4),10,WIDTH/6,HEIGHT-20)
        self.squadron_canvas=pygame.Rect(int(WIDTH/3+WIDTH/11),10,WIDTH/2,HEIGHT-20)
        #Napisy
        font=pygame.font.SysFont('Arial', 16)
        self.Id_label="ID"
        self.Id_label_surface=font.render(self.Id_label, True, BLACK)
        self.Id_label_rect=self.Id_label_surface.get_rect(center=(60, 190))
        self.Roles_label="Role"
        self.Roles_label_surface=font.render(self.Roles_label, True, BLACK)
        self.Roles_label_rect=self.Roles_label_surface.get_rect(center=(70, 270))
        self.Altitude_label="Altitude [m]"
        self.Altitude_label_surface=font.render(self.Altitude_label, True, BLACK)
        self.Altitude_label_rect=self.Altitude_label_surface.get_rect(center=(85, 350))

        self.Squad_label = "Squad name"
        self.Squad_label_surface = font.render(self.Squad_label, True, BLACK)
        self.Squad_label_rect = self.Squad_label_surface.get_rect(center=(295, 190))
        self.Squad_Formation_label = "Formation"
        self.Squad_Formation_label_surface = font.render(self.Squad_Formation_label, True, BLACK)
        self.Squad_Formation_rect = self.Squad_Formation_label_surface.get_rect(center=(290, 270))
        self.Spacing_label = "Spacing [m]"
        self.Spacing_label_surface = font.render(self.Spacing_label, True, BLACK)
        self.Spacing_label_rect = self.Spacing_label_surface.get_rect(center=(295, 350))

        font = pygame.font.SysFont('Arial', 16)
        self.Wing_name_label = "Wing name"
        self.Wing_label_surface = font.render(self.Wing_name_label, True, BLACK)
        self.Wing_label_rect = self.Wing_label_surface.get_rect(center=(85, 450))
        self.Wing_Formation_label = "Formation"
        self.Wing_Formation_surface = font.render(self.Wing_Formation_label, True, BLACK)
        self.Wing_Formation_rect = self.Wing_Formation_surface.get_rect(center=(80, 550))

        #Kontrolki
        self.Id=TextInput(50,200,200,50)
        self.Roles=Droplist(50,280,200,50,["Squad Leader","Wing Leader","Generic"])
        self.Altitude = TextInput(50, 360, 200, 50)
        self.Add_2_Squad=Button(50,HEIGHT-150,200,50,"Add to Squadron",(lambda:self.Add_2_Squad_click()))
        self.Add_2_Squad_Popup=Popup(WIDTH/2-100,HEIGHT/2-20,250,50,"Drone added successfully",self.Add_2_Squad)
        self.Squad_name=TextInput(260,200,200,50)
        self.Formation=Droplist(260,280,200,50,["wedge","line","column","circle","custom"])
        self.Create_Squadron=Button(260,HEIGHT - 80, 200, 50, "Create Squadron", lambda: self.Create_squad_click())
        self.Create_Squad_Popup=Popup(WIDTH/2-100,HEIGHT/2-20,250,50,"Squad created successfully",self.Create_Squadron)
        self.Drone_list=Listbox(490,20,300,500,self.drones)
        self.Formation_spacing=TextInput(260,360,200,50)
        self.Wing_name=TextInput(50,460,200,50)
        self.Wing_Formation = Droplist(50, 560, 200, 50, ["wedge", "line", "column", "circle", "custom"])
        self.Wingmen = Listbox(260, 460, 200, 500, self.Wing_men)
        self.Wing_list = Listbox(490, 540, 300, 500, self.wings)
        self.Add_2_Wing = Button(50, HEIGHT - 80, 200, 50, "Add to Wing", (lambda: self.Add_2_Wing_click()))
        self.Create_Wing = Button(260, HEIGHT - 150, 200, 50, "Create Wing", (lambda: self.Create_Wing_click()))
        self.Create_Wing_Popup = Popup(WIDTH / 2 - 100, HEIGHT / 2 - 20, 250, 50, "Squad created successfully",self.Create_Wing)

        self.mission = Button(50, 100, 200, 50, "Mission",lambda: self.view_manager.switch_views("Mission_View"))
        self.simulation = Button(260, 100, 200, 50, "Simulation",lambda: self.view_manager.switch_views("Sim_View"))
    def Add_2_Squad_click(self):
        try:
            id = self.Id.text
            alt=self.Altitude.text
            role= self.Roles.selected
            if id=='' or any(drone.id == id for drone in self.drones):
                raise ValueError("Drone id mustn't be empty or redundant")
            elif role=="Squad Leader":
                if any(drone.role=="Squad Leader" for drone in self.drones):
                    raise ValueError("Squad Leader already selected")
                else:
                    drone=Drone(id,role,"operative")
                    drone.altitude = float(alt)
                    print("Utworzono Drona: ",drone.id," ",drone.role," ",drone.status)
                    self.drones.append(drone)
            elif role=="Wing Leader":
                drone=Drone(id, role, "operative")
                drone.altitude = float(alt)
                print("Utworzono Drona: ",drone.id," ",drone.role," ",drone.status)
                self.drones.append(drone)
            elif role=="Generic":
                drone=Drone(id,role,"operative")
                drone.altitude=float(alt)
                print("Utworzono Drona: ", drone.id, " ", drone.role, " ", drone.status)
                print(drone.formation_distance,drone.formation_angle)
                self.drones.append(drone)
        except ValueError as er:
            print(er)
            self.Add_2_Squad_Popup.show(str(er))
    def Add_2_Wing_click(self):
        try:
            if not self.drones:
                raise ValueError("Cannot create empty wing")
            if self.selected_drone:#najpierw stworzyć skrzydło potem wypełnić dronami na końcu dodać do eskadry
                self.Wing_men.append(self.selected_drone)
            else:
                raise ValueError("Choose a drone")
        except ValueError as er:
            print(er)
    def Create_Wing_click(self):
        try:
            if not self.drones:
                raise ValueError("Cannot create empty wing")
            Name=self.Wing_name.text
            Drones=self.Wing_men.copy()
            Formation=self.Wing_Formation.selected
            wing=Wing(Name,Drones,Formation)
            print("Utworzono Skrzydło", self.Wing_name, " ", self.Wing_Formation)
            for drone in self.Wing_men:
                drone.wing = wing
            self.Wing_men.clear()
            self.wings[Name]=wing
        except ValueError as er:
            print(er)
            self.Create_Wing_Popup.show(str(er))
    def Create_squad_click(self):
        try:
            if not self.drones:
                raise ValueError("Cannot create empty squadron")
            elif self.Squad_name.text == '':
                raise ValueError("No name set")
            squadron = Squadron(self.Squad_name.text, self.drones.copy(), self.Formation.selected)
            print("Utworzono Eskadrę", self.Squad_name, " ", self.Formation)
            for wing in self.wings.values():
                wing.squadron=squadron
            for drone in self.drones:
                drone.squadron=squadron
            self.squadrons[self.Squad_name.text]=squadron
            self.sim_view.attach_squadron(squadron)
        except ValueError as er:
            print(er)
            self.Create_Squad_Popup.show(str(er))
    def preview(self):
        formation = self.Formation.selected
        spacing = float(self.Formation_spacing.text) if self.Formation_spacing.text else SPACING
        drones = self.drones
        positions = []
        cx = self.squadron_canvas.centerx
        cy = self.squadron_canvas.centery
        index = 0
        for drone in drones:
            if drone.role == "Squad Leader":
                positions.append((drone, cx, cy))
                continue
            if formation == "wedge":
                x,y = formation_wedge(index, spacing)
            elif formation == "line":
                x,y = formation_line(index, spacing)
            elif formation == "column":
                x,y = formation_column(index, spacing)
            elif formation == "circle":
                x,y = formation_circle(index, spacing, len(drones))
            else:
                x,y = 0,0
            positions.append((drone,cx+x,cy+y))
            index+=1

        return positions
    def draw_drone(self,surface,x,y,color):
        h=DRONE_SIZE
        points=[(x,y-h),(x-h*0.866,y+h/2),(x+h*0.866,y+h/2)]
        pygame.draw.polygon(surface, color, points)
    def handle_event(self, event):
        self.Add_2_Squad.handle_event(event)
        self.Id.handle_event(event)
        self.Roles.handle_event(event)
        self.Altitude.handle_event(event)
        self.Drone_list.handle_event(event)
        self.Add_2_Squad_Popup.handle_event(event)
        self.Squad_name.handle_event(event)
        self.Formation.handle_event(event)
        self.Create_Squadron.handle_event(event)
        self.Create_Squad_Popup.handle_event(event)
        self.Formation_spacing.handle_event(event)
        self.Wing_name.handle_event(event)
        self.Create_Wing.handle_event(event)
        self.Create_Wing_Popup.handle_event(event)
        self.Add_2_Wing.handle_event(event)
        self.Wing_Formation.handle_event(event)
        self.Wingmen.handle_event(event)
        self.mission.handle_event(event)
        self.simulation.handle_event(event)

        selected = self.Drone_list.get_selected()
        if selected and selected != self.selected_drone:
            self.selected_drone = selected
    def update(self, dt):
        self.Drone_list.items = [drone for drone in self.drones]
        if self.selected_drone:
            try:
                pass
            except ValueError:
                pass
    def draw(self, surface):
        surface.fill(self.background_color)
        # Panel tworzenia
        pygame.draw.rect(surface, drone_creation_panel,self.drone_creation_panel)
        pygame.draw.rect(surface,panel_border,self.drone_creation_panel,2)
        #Napisy
        surface.blit(self.Id_label_surface,self.Id_label_rect)
        surface.blit(self.Roles_label_surface,self.Roles_label_rect)
        surface.blit(self.Altitude_label_surface,self.Altitude_label_rect)
        surface.blit(self.Squad_label_surface,self.Squad_label_rect)
        surface.blit(self.Squad_Formation_label_surface,self.Squad_Formation_rect)
        surface.blit(self.Spacing_label_surface,self.Spacing_label_rect)
        surface.blit(self.Wing_label_surface,self.Wing_label_rect)
        surface.blit(self.Wing_Formation_surface,self.Wing_Formation_rect)

        self.Id.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Roles.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Altitude.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Squad_name.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Formation.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Wing_name.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Add_2_Squad.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Create_Wing.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Add_2_Wing.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Wing_Formation.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Wingmen.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Create_Squadron.draw(surface,font=pygame.font.SysFont("Arial",16))
        self.Formation_spacing.draw(surface,font=pygame.font.SysFont("Arial",16))
        #Panel lista dronów
        pygame.draw.rect(surface,drone_list_panel,self.drone_list_panel)
        pygame.draw.rect(surface,panel_border,self.drone_list_panel,2)
        self.Drone_list.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Wing_list.draw(surface, font=pygame.font.SysFont("Arial", 16))
        #Panel canvas eskadry
        pygame.draw.rect(surface,squadron_canvas,self.squadron_canvas)
        pygame.draw.rect(surface,panel_border,self.squadron_canvas,2)

        self.mission.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.simulation.draw(surface, font=pygame.font.SysFont("Arial", 16))

        #Pop-up
        self.Add_2_Squad_Popup.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Create_Squad_Popup.draw(surface, font=pygame.font.SysFont("Arial", 16))
        self.Create_Wing_Popup.draw(surface, font=pygame.font.SysFont("Arial", 16))

        #Rysowanie preview eskadry
        if self.drones:
            positions = self.preview()
            for drone,x,y in positions:
                color=DRONE_COLORS.get(drone.role,WHITE)
                self.draw_drone(surface,x,y,color)


