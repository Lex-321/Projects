import math
import Formations

class Squadron:
    def __init__(self,name,drones,formation):
        self.spacing = 0.0
        self.name = name
        self.drones = drones
        self.formation = formation
        self.wings = []
        self.stand_by()
        self.apply_formation()
        self.execution=False
        for drone in drones:
            if drone.role == "Squad Leader":
                drone.gps_position = (0.0, 0.0, 10.0)
                drone.altitude = 10.0
                drone.velocity = (0.0, 0.0, 0.0)
    def __str__(self):
        return f"Squadron(id={self.name}, formation={self.formation})"
    def stand_by(self):
        for drone in self.drones:
            drone.squadron=self
            drone.active=True
    def update(self,env,dt):
        for drone in self.drones:
            drone.velocity_computer(env)
            drone.update_position(dt)
    def get_commander(self):
        for drone in self.drones:
            if drone.role == "Squad Leader":
                return drone
        return None
    def get_squadron(self):
        return self.drones
    def generic_list(self):
        return [drone for drone in self.drones if drone.role=='Generic']
    def set_formation(self,formation):
        if formation == self.formation:
            return
        self.formation = formation
        self.apply_formation()
    def wings_list(self):
        wings=[]
        for drone in self.drones:
            if drone.role=='Wing Leader':
                wings.append(drone)
        return wings
    def apply_formation(self):
        drones=self.get_squadron()
        formation=FORMATIONS[self.formation]
        #spacing=self.spacing
        spacing=1.5
        index=1
        for drone in drones:
            if drone.wing is not None and drone.role == "Generic":
                continue
            print("Drone",drone.id)
            print("Squad index",index)
            print("formation_distance before formation ",drone.formation_distance)
            if drone.role=='Squad Leader':
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
            print("formation_distance after formation ",drone.formation_distance)
            print("formation_angle after formation ",math.degrees(drone.formation_angle))
    def issue_order(self,order):
        if self.execution:
            return
        self.execution=True
        order.execute(self)
        self.execution=False
    def center_point(self):
        xs,ys,zs=[],[],[]
        for drone in self.drones:
            if drone.role == "Squad Leader":
                x,y,z=drone.gps_position
            else:
                leader=self.get_commander()
                lx,ly,lz=leader.gps_position
                x=lx+drone.real_x
                y=ly+drone.real_y
                z=drone.altitude
            xs.append(x)
            ys.append(y)
            zs.append(z)
            return sum(xs)/len(xs), sum(ys)/len(ys), sum(zs)/len(zs)
        return None

FORMATIONS = {
    "wedge": Formations.formation_wedge,
    "line": Formations.formation_line,
    "column": Formations.formation_column,
    "circle": Formations.formation_circle,
}
