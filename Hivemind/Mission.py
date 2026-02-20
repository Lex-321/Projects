import math

class Mission:
    def __init__(self,checkpoints,mission_altitude=10.0):
        self.checkpoints=checkpoints
        self.progress=0
        self.started=False
        self.completed=False
        self.mission_altitude=mission_altitude
    def get_current_target(self):
        if self.progress>=len(self.checkpoints):
            if not self.completed:
                print("Mission completed")
                self.completed=True
            return None
        cp=self.checkpoints[self.progress]
        if len(cp)==2:
            return cp[0],cp[1],self.mission_altitude
        return cp
    def check_target(self,position,threshold):
        target=self.get_current_target()
        if target is None:
            return None
        dx=target[0]-position[0]
        dy=target[1]-position[1]
        if math.hypot(dx,dy) < threshold:
            if self.progress==0:
                print("Mission started")
                self.started=True
            self.progress+=1
            print("Target reached")
            print("Mission progress: ",(self.progress/len(self.checkpoints))*100,"%")
        return None
    def set_checkpoints(self, checkpoints):
        self.checkpoints=list(checkpoints)
        self.progress=0

