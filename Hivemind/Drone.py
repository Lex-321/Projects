import math
from collections import deque

from pygame.math import clamp
from Enviroment import Enviroment
from Squadron import Squadron
from Mission import Mission
from Wing import Wing

env=Enviroment()
class Drone:
    def __init__(
        self,
        drone_id: str,
        role: str,
        status: str = "operative",
        active: bool = False,
        squadron: Squadron = None,
        wing: Wing = None,
        mission: Mission = None,
        max_speed: float = None,
        gps_position: tuple | None=None,
        heading: float = 0.0,
        formation_angle: float | None=None,
        formation_distance: float | None=None,
        altitude: float | None=None,
        velocity: tuple | None=None,
        neighbours: list | None=None,
        proximity_memory: dict | None=None,
    ):
        self.id=drone_id
        self.role=role
        self.status=status
        self.active=active
        self.squadron=squadron
        self.wing=wing
        self.heading=heading
        self.velocity=velocity or (0, 0, 0)
        self.neighbours=neighbours or []
        self.proximity_memory=proximity_memory or {}
        # Strefy bezpieczeństwa
        self.control_zone_radius=3.0
        self.safety_zone_radius=1.5
        self.danger_zone_radius=1.0
        self.stand_by_mess=deque(maxlen=5)
        # Pozycjonowanie w formacji
        if role =="Squad Leader":
            # Dowódcy mają GPS
            self.gps_position = gps_position
            self.formation_angle = None
            self.formation_distance = None
            self.altitude = altitude
            self.mission=mission
            self.max_speed=max_speed
            self.distance_to_target=None
        elif role == "Wing Leader":
            self.gps_position = gps_position
            # FORMACJA
            self.formation_angle = formation_angle if formation_angle is not None else 0.0
            self.formation_distance = formation_distance if formation_distance is not None else 1.0
            self.formation_x = 0.0
            self.formation_y = 0.0
            # STAN
            self.real_x = self.formation_distance * math.cos(self.formation_angle)
            self.real_y = self.formation_distance * math.sin(self.formation_angle)
            self.altitude = altitude
            self.last_heading=0.0
            self.mission = mission
            self.max_speed = max_speed
        elif role=="Generic":
            # Genryki nie mają GPS, obliczją swoją pozycję w formacji na podstawie położenia dowódców
            self.gps_position = None
            # FORMACJA
            self.formation_angle = formation_angle if formation_angle is not None else 0.0
            self.formation_distance = formation_distance if formation_distance is not None else 1.0
            self.formation_x=0.0
            self.formation_y=0.0
            # STAN
            self.real_x = self.formation_distance * math.cos(self.formation_angle)
            self.real_y = self.formation_distance * math.sin(self.formation_angle)
            self.last_heading=0.0
            self.altitude = altitude
        else:
            raise ValueError(f"Unknown role:{role}")

    def __repr__(self):
        return f"Drone(id={self.id}, role={self.role}, status={self.status})"

    def __str__(self):
        return f"Drone(id={self.id}, role={self.role}, status={self.status})"

    # --- setery ---
    def set_operative(self):
        self.status="operative"

    def set_offline(self):
        self.status="offline"

    # ===== FUNKCJE =====
    # === Helpery
    def env_2_drone(self,dx,dy,dz):
        yaw=self.heading
        lx=math.cos(-yaw)*dx-math.sin(-yaw)*dy
        ly=-math.sin(-yaw)*dx+math.cos(-yaw)*dy
        lz=dz
        return lx,ly,lz
    def vec_2_ang(self,vx,vy,vz):
        azimuth=math.atan2(vy,vx)
        elevation=math.atan2(vz,math.sqrt(vx**2+vy**2))
        return azimuth,elevation
    def Stand_By(self,message):
        if len(self.stand_by_mess)>0:
            self.stand_by_mess=[]
        self.stand_by_mess.append(message)
    def mission_extender(self):
        if self.role != "Wing Leader":
            return
        if self.squadron is None:
            return
        commander = self.squadron.get_commander()
        if commander is None:
            return
        if self.max_speed is None:
            self.max_speed = self.squadron.get_commander().max_speed
        if self.mission is None:
            self.mission=self.squadron.get_commander().mission
        if self.gps_position is None:
            self.gps_position=self.squadron.get_commander().gps_position
    def get_direct_commander(self):
        if self.wing is not None:
            if self.role == "Generic":
                return self.wing.get_Wing_Leader()
            elif self.role=='Wing Leader':
                return self.squadron.get_commander()
        elif self.squadron is not None:
            return self.squadron.get_commander()
        return None
    # === Sensory
    def neighbours_sensor(self,neighbour_data):
        neighbour_contacts=[]
        for neighbour in neighbour_data:
            neighbour_id=neighbour["id"]
            distance=neighbour["distance"]
            az=neighbour["azimuth"]
            el=neighbour["elevation"]
            last_zone=self.proximity_memory.get(neighbour_id,{}).get("last_zone")
            zone=None
            if distance < self.danger_zone_radius:
                zone = "danger"
            elif self.safety_zone_radius > distance > self.danger_zone_radius:
                zone = "safety"
            elif self.safety_zone_radius < distance < self.control_zone_radius:
                zone = "control"
            if last_zone is None:
                zone="control"
                self.proximity_memory[neighbour_id]={"last_zone":zone}
                continue
            if last_zone != zone:
                self.proximity_memory[neighbour_id]["last_zone"] = zone
                neighbour_contacts.append({"id":neighbour["id"],"zone":zone,"azimuth":az,"elevation":el})
        return neighbour_contacts
    def world_sensor(self,env):
        if not env.enable_wind:
            return 0.0,0.0,0.0
        else:
            wind_vector = env.wind_generator()
            return wind_vector
    def proximity_sensor(self,env):
        dt = env.dt
        proximity_contacts = []
        if not env.enable_birds:
            return proximity_contacts
        birds=env.get_active_birds()
        for bird in birds:
            bird_id=bird["id"]
            bird_vx, bird_vy, bird_vz = bird["vector"]
            bird_v = math.sqrt(bird["vector"][0] ** 2 + bird["vector"][1] ** 2 + bird["vector"][2] ** 2)
            t_c_s = (self.control_zone_radius - self.safety_zone_radius) / bird_v
            t_s_d = (self.safety_zone_radius - self.danger_zone_radius) / bird_v
            last_zone=self.proximity_memory.get(bird_id,{}).get("last_zone")
            zone=None
            if bird_id not in self.proximity_memory:
                zone="control"
                tiz=0
                az, el = self.vec_2_ang(bird_vx, bird_vy, bird_vz)
                self.proximity_memory[bird_id]={"t":0.0,"last_zone":zone}
                proximity_contacts.append({"id": bird_id, "type": "bird", "zone": zone, "time": tiz, "azimuth":az,"elevation": el})
            else:
                tiz=self.proximity_memory[bird_id]["t"]
                last_zone=self.proximity_memory[bird_id]["last_zone"]
                tiz+=dt
                if tiz<t_c_s:
                    zone="control"
                elif t_c_s <= tiz < (t_s_d + t_c_s):
                    zone = "safety"
                elif tiz > t_s_d + t_c_s:
                    zone = "danger"
            if last_zone != zone:
                az,el=self.vec_2_ang(bird_vx,bird_vy,bird_vz)
                proximity_contacts.append({"id": bird_id, "type": "bird", "zone": zone, "time": tiz, "azimuth": az,
                                 "elevation": el})
                self.proximity_memory[bird_id]["last_zone"] = zone
        return proximity_contacts
    # === Reakcje
    def avoid_collision(self,env):
        if not self.active or self.squadron is None:
            return None
        proximity_contacts=self.proximity_sensor(env)
        neighbour_contacts=self.neighbours_sensor(self.neighbour_data_computer())
        ivasive_manouvers=[]
        contacts=proximity_contacts+neighbour_contacts
        p=0
        for contact in contacts:
            threat_direction=(contact["azimuth"], contact["elevation"])
            vx = math.cos(threat_direction[1]) * math.cos(threat_direction[0])
            vy = math.cos(threat_direction[1]) * math.sin(threat_direction[0])
            vz = math.sin(threat_direction[1])
            threat_sign=0
            R=(0,0,0)
            threat_vector=(vx,vy,vz)
            threat_axis=max(range(3),key=lambda x:abs(threat_vector[x]))
            if threat_axis==0:
                R=(0,0,1)
                threat_sign=math.copysign(1.0,vx)
            elif threat_axis==1:
                R=(0,0,1)
                threat_sign=math.copysign(1.0,vy)
            elif threat_axis==2:
                R=(1,0,0)
                threat_sign=math.copysign(1.0,vz)
            iv_X=threat_vector[1]*R[2]-threat_vector[2]*R[1]
            iv_Y=threat_vector[2]*R[0]-threat_vector[0]*R[2]
            iv_Z=threat_vector[0]*R[1]-threat_vector[1]*R[0]
            iv_len=math.sqrt(iv_X**2+iv_Y**2+iv_Z**2)
            if iv_len>0:
                ivade_norm=(iv_X/iv_len, iv_Y/iv_len, iv_Z/iv_len)
            else:
                ivade_norm=(0,0,0)
            if threat_sign<0:
                ivade_vector=(-ivade_norm[0],-ivade_norm[1],-ivade_norm[2])
            else:
                ivade_vector=ivade_norm
            if contact["zone"]=="danger":
                p=1.5
            elif contact["zone"]=="safety":
                p=0.5
            elif contact["zone"]=="control":
                p=0.25
            ivade=(p*ivade_vector[0],p*ivade_vector[1],p*ivade_vector[2])
            ivasive_manouvers.append(ivade)
        manouver_x = sum(ivade[0] for ivade in ivasive_manouvers)
        manouver_y = sum(ivade[1] for ivade in ivasive_manouvers)
        manouver_z = sum(ivade[2] for ivade in ivasive_manouvers)
        ivade=(manouver_x,manouver_y,manouver_z)
        ivade_len=math.sqrt(ivade[0]**2+ivade[1]**2+ivade[2]**2)
        if ivade_len==0:
            return (0.0,0.0,0.0),0.0
        ivade_norm=(ivade[0]/ivade_len,ivade[1]/ivade_len,ivade[2]/ivade_len)
        return ivade_norm,p
    def maintain_altitude(self, altitude):
        error_z=altitude-self.altitude
        kp=0
        if abs(error_z)<0.2:
            delta_vz=0.0
            return 0.0,0.0,delta_vz
        elif error_z<1.0:
                kp=0.5
        elif error_z>1.0:
                kp=1.0
        delta_vz=kp*error_z
        delta_vz=clamp(delta_vz,0.0,10.0)
        delta_v=(0.0,0.0,delta_vz)
        return delta_v
    def hold_the_line(self):
        if not self.active:
            return 0.0,0.0,0.0
        if self.role=="Squad Leader":
            return 0.0, 0.0, 0.0
        else:
            leader=self.get_direct_commander()
            if leader is None:
                return 0.0, 0.0, 0.0
            x_cur = self.real_x
            y_cur = self.real_y
            x_target = self.formation_x
            y_target = self.formation_y
            error_x = x_target - x_cur
            error_y = y_target - y_cur
            k_p = 1.2
            vx = k_p * error_x
            vy = k_p * error_y
            delta_v=vx,vy,0.0
            return delta_v
    def neighbour_velocity_tracker(self, neighbours):
        neighbour_velocities = []
        sum_vx = 0
        sum_vy = 0
        sum_vz = 0
        delta_v = 0.0, 0.0, 0.0
        if self.role != "Squad Leader":
            return 0.0, 0.0, 0.0
        for neighbour in neighbours:
            if neighbour["distance"] > self.danger_zone_radius:
                neighbour_velocities.append(neighbour["velocity"])
            else:
                return 0.0, 0.0, 0.0
            for velocity in neighbour_velocities:
                sum_vx += velocity[0]
                sum_vy += velocity[1]
                sum_vz+=velocity[2]
            n=len(neighbour_velocities)
            avg_velocity=(sum_vx/n,sum_vy/n,sum_vz/n)
            error_v=(avg_velocity[0]-self.velocity[0],avg_velocity[1]-self.velocity[1],avg_velocity[2]-self.velocity[2])
            delta_v=(0.1*error_v[0],0.1*error_v[1],0.1*error_v[2])
            delta_vx=clamp(delta_v[0],-10,10)
            delta_vy=clamp(delta_v[1],-10,10)
            delta_v=(delta_vx,delta_vy,0.0)
        return delta_v

    # === Ruch i dynamika
    def velocity_computer(self,env):
        self.mission_extender()
        damp_factor=0.12
        leader= self.get_direct_commander() if self.squadron is not None else None
        leader_v=leader.velocity if leader else (0.0,0.0,0.0)
        leader_heading_x=math.cos(leader.heading)
        leader_heading_y=math.sin(leader.heading)
        if self.role == "Squad Leader":
            arrival_radius=max(self.max_speed*1.5,3.0)
            stop_radius=0.5
            if not self.mission.started:
                starting_point = self.mission.get_current_target()
                self.gps_position=starting_point
                self.velocity=0.0,0.0,0.0
                self.heading=0.0
                self.mission.started = True
                return
            target=self.mission.get_current_target()
            if target:
                dx=target[0]-self.gps_position[0]
                dy=target[1]-self.gps_position[1]
                distance=math.hypot(dx,dy)
                target_heading=math.atan2(dy,dx)
                error_heading=math.atan2(math.sin(target_heading-self.heading),math.cos(target_heading-self.heading))
                if distance<arrival_radius:
                    speed=self.max_speed*(distance/arrival_radius)
                else:
                    speed=self.max_speed
                self.distance_to_target=distance
                print("distance: ", distance)
                k_heading = 1.5
                self.heading += k_heading * error_heading*env.dt
                vhx=speed*math.cos(self.heading)
                vhy=speed*math.sin(self.heading)
                v_goal=vhx,vhy,0.0
            else:
                v_goal=(0.0,0.0,0.0)
        else:
            v_goal=(0.0,0.0,0.0)
        ivade_vec, ivade_p = self.avoid_collision(env)
        v_form = self.hold_the_line()
        leader_speed=math.hypot(leader_v[0],leader_v[1])
        speed_scale=min(1.0,leader_speed/leader.max_speed)
        v_form_scaled = v_form[0] * speed_scale, v_form[1] * speed_scale, v_form[2]
        v_neigh = self.neighbour_velocity_tracker(self.neighbour_list_computer())
        v_wind=self.world_sensor(env)
        v_alt=self.maintain_altitude(self.altitude)
        if ivade_p >= 1.0:
            self.Stand_By("Ivade")
            v_target = (ivade_vec[0] * 6.0, ivade_vec[1] * 6.0, ivade_vec[2] * 6.0)
        elif ivade_p > 0.0:
            self.Stand_By("Adjustment")
            v_target = (ivade_vec[0] * 2.5 + v_form_scaled[0], ivade_vec[1] * 2.5 + v_form_scaled[1], v_form[2])
        else: # formacja
            if self.role == "Squad Leader":
                v_target=(v_goal[0]+v_wind[0],v_goal[1]+v_wind[1],v_alt[2])
            else:
                if leader_speed>0.4 * leader.max_speed: # Podążanie
                    v_target = (leader_v[0] + v_goal[0] + v_form[0] + v_neigh[0] + v_wind[0],
                                leader_v[1] + v_goal[1] + v_form[1] + v_neigh[1] + v_wind[1],
                                v_alt[2])
                elif leader_speed<0.1 * leader.max_speed: # Podążanie + trzymanie formacji
                    v_target = (leader_v[0]+v_goal[0]+v_form_scaled[0]+v_neigh[0]+v_wind[0],
                                leader_v[1]+v_goal[1]+v_form_scaled[1]+v_neigh[1]+v_wind[1],
                                v_alt[2])
                else:
                    v_target = (v_goal[0] + v_form_scaled[0] + v_neigh[0] + v_wind[0],
                                v_goal[1] + v_form_scaled[1] + v_neigh[1] + v_wind[1],
                                v_alt[2])
                v_forward=v_target[0]*leader_heading_x+v_target[1]*leader_heading_y
                leader_forward=leader_v[0]*leader_heading_x+leader_v[1]*leader_heading_y
                if self.role in ("Wing Leader","Generic"):
                    max_forward=leader_forward
                    if v_forward>max_forward:
                        over_v=v_forward-max_forward
                        v_target=(v_target[0]-over_v*leader_heading_x,v_target[1]-over_v*leader_heading_y,v_target[2])
        # - - - - Tłumnienie/wygładzanie ruchu - - - -
        Vx = self.velocity[0] + damp_factor * (v_target[0] - self.velocity[0])
        Vy = self.velocity[1] + damp_factor * (v_target[1] - self.velocity[1])
        Vz = self.velocity[2] + damp_factor * (v_target[2] - self.velocity[2])
        v = (Vx,Vy,Vz)
        v_len=math.sqrt(v[0]**2+v[1]**2+v[2]**2)
        max_v=leader.max_speed if self.role=="Generic" else self.max_speed
        if v_len>max_v:
            v=(max_v*v[0]/v_len,max_v*v[1]/v_len,max_v*v[2]/v_len)
        delta_v=(v[0]-self.velocity[0],v[1]-self.velocity[1],v[2]-self.velocity[2])
        delta_v_len=math.sqrt(delta_v[0]**2+delta_v[1]**2+delta_v[2]**2)
        if delta_v_len>10:
            dvn=(3*delta_v[0]/delta_v_len,3*delta_v[1]/delta_v_len,3*delta_v[2]/delta_v_len)
        else:
            dvn=delta_v
        v=(self.velocity[0]+dvn[0],self.velocity[1]+dvn[1],self.velocity[2]+dvn[2])
        self.velocity=v
        return

    def update_position(self,dt):
        vx=self.velocity[0]
        vy=self.velocity[1]
        vz=self.velocity[2]
        # DOWÓDCY
        if self.role == "Squad Leader" and self.mission:
            if self.gps_position is None:
                return
            if len(self.gps_position) != 3:
                raise ValueError("gps_position must be 3D")
            x=self.gps_position[0]
            y=self.gps_position[1]
            z=self.gps_position[2]
            self.gps_position=(x+vx*dt,y+vy*dt,z+vz*dt)
            self.mission.check_target(self.gps_position,0.5)
        # GENERYKI I SKRZYDŁA
        else:
            leader=self.get_direct_commander()
            if self.altitude is None:
                self.altitude=10.0
            dtang = leader.heading - leader.heading
            self.last_heading=leader.heading
            if abs(dtang)>0.00001:
                coshead=math.cos(-dtang)
                sinhead=math.sin(-dtang)
                x=self.real_x
                y=self.real_y
                self.real_x=x*coshead - y*sinhead
                self.real_y=x*sinhead + y*coshead
            self.real_x+=vx*dt
            self.real_y+=vy*dt
            altitude=self.altitude
            altitude+=vz*dt
            self.altitude=altitude
            # Mission
            if leader.mission.completed:
                self.Stand_By("MISSION COMPLETED")
                return
        #Heading
        vx,vy,vz=self.velocity
        speed_xy=math.hypot(vx,vy)
        if speed_xy>0.05:
            self.heading=math.atan2(vy,vx)
    #=== Funkcję dowódców
    def neighbour_data_computer(self):  #<- <- <-
        if not self.squadron:
            return []
        neighbour_data=[]
        if self.wing is not None:
            generics=self.wing.generic_list()
        else:
            generics=self.squadron.generic_list()
        for d in range(len(generics)-1): #ogarnąć co tu jest z typami zmiennych
            d_x_i = generics[d].formation_distance*math.cos(generics[d].formation_angle)
            d_y_i = generics[d].formation_distance * math.sin(generics[d].formation_angle)
            d_z_i = generics[d].altitude
            d_x_j = generics[d+1].formation_distance * math.cos(generics[d+1].formation_angle)
            d_y_j=generics[d+1].formation_distance * math.sin(generics[d+1].formation_angle)
            d_z_j=generics[d+1].altitude
            distance=math.sqrt(pow(d_x_i-d_x_j,2)+pow(d_y_i-d_y_j,2)+pow(d_z_i-d_z_j,2))
            az,el=self.vec_2_ang(d_x_i,d_y_i,d_z_i)
            neighbour_data.append({"id":generics[d].id,"distance":distance,"azimuth":az,"elevation":el})
        return neighbour_data
    def neighbour_list_computer(self):  #<- <- <-
        if self.squadron is None or self.role == "Squad Leader":
            return []
        neighbours=[]
        generics=self.squadron.generic_list()
        for drone in generics:
            if drone.id==self.id:
                continue
            Dx=(drone.formation_distance*math.cos(drone.formation_angle)-self.formation_distance*math.cos(self.formation_angle))
            Dy=(drone.formation_distance*math.sin(drone.formation_angle)-self.formation_distance*math.sin(self.formation_angle))
            Dist=math.sqrt(Dx**2+Dy**2)
            Dv=drone.velocity
            neighbours.append({"id":drone.id,"distance":Dist,"velocity":Dv})
            neighbours.sort(key=lambda x:x["distance"])
        return neighbours[:2]