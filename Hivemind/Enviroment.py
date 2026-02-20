import math
import time
import random
class Enviroment:
    def __init__(self,dt=0.1):
        self.dt = dt
        self.time=0.0
        self.event_id=0
        self.angry_birds=[]
    def wind_generator(self):
        wind_direction_x=math.radians(random.randint(0,360))
        wind_direction_z=math.radians(random.randint(0,180))
        wind_force=random.randint(1,10)
        wind_vx=wind_force*math.sin(wind_direction_z)*math.cos(wind_direction_x)
        wind_vy=wind_force*math.sin(wind_direction_x)*math.cos(wind_direction_z)
        wind_vz=wind_force*math.cos(wind_direction_z)
        wind_vector=(wind_vx,wind_vy,wind_vz)
        return wind_vector
    def bird_spawner(self):
        bird_starting_direction_x=math.radians(random.randint(0,360))
        bird_starting_direction_z=math.radians(random.randint(0,180))
        bird_speed=random.randint(1,5)
        bird_vx=bird_speed*math.sin(bird_starting_direction_z)*math.cos(bird_starting_direction_x)
        bird_vy=bird_speed*math.sin(bird_starting_direction_x)*math.cos(bird_starting_direction_z)
        bird_vz=bird_speed*math.cos(bird_starting_direction_z)
        bird=(bird_vx,bird_vy,bird_vz)
        bird_id=self.event_id
        self.event_id+=1
        bird_stime=time.time()
        bird_duration=random.randint(1,10)
        bird_etime=bird_stime+bird_duration
        self.angry_birds.append({"id":bird_id,"type":"bird","vector":bird,"start_time":bird_stime,"end_time":bird_etime})
        return
    def get_active_birds(self):
        return self.angry_birds
    def world_tick(self):
        self.time+=self.dt
        birds_filtered=[]
        for bird in self.angry_birds:
            if bird["start_time"] <= self.time <= bird["end_time"]:
                birds_filtered.append(bird)
        self.angry_birds=birds_filtered
        if random.random()<0.1*self.dt:
            self.bird_spawner()
        return