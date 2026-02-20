import math
import Formations

class Wing:
    def __init__(self,name,drones,formation):
        self.name = name
        self.drones=drones
        self.formation=formation
        self.squadron=None
        self.stand_by()
        self.apply_formation()
    def __str__(self):
        return f"Wing(formation={self.formation})"
    def stand_by(self):
        for drone in self.drones:
            drone.squadron=self
            drone.active=True
    def update(self,env,dt):
        for drone in self.drones:
            drone.velocity_computer(env)
            drone.update_position(dt)
    def get_Wing_Leader(self):
        for drone in self.drones:
            if drone.role == "Wing Leader":
                return drone
        return None
    def get_wing(self):
        return self.drones
    def generic_list(self):
        return [drone for drone in self.drones if drone.role=='Generic']
    def apply_formation(self):
        drones=self.get_wing()
        formation=FORMATIONS[self.formation]
        spacing=1.5
        index=1
        for drone in drones:
            print("Drone",drone.id)
            print("Wing index",index)
            print("WING formation_distance before formation ",drone.formation_distance)
            if drone.role=='Wing Leader':
                drone.formation_x=0.0
                drone.formation_y=0.0
                continue
            if self.formation=="circle":
                x,y=formation(index,spacing,len(drones))
            else:
                x,y=formation(index,spacing)
            drone.formation_x=x
            drone.formation_y=y
            drone.formation_distance=math.hypot(x,y)
            drone.formation_angle=math.atan2(y,x)
            index+=1
            print("WING formation_distance after formation ",drone.formation_distance)
            print("WING formation_angle after formation ",math.degrees(drone.formation_angle))
FORMATIONS = {
    "wedge": Formations.formation_wedge,
    "line": Formations.formation_line,
    "column": Formations.formation_column,
    "circle": Formations.formation_circle,
}