class Camera:
    def __init__(self,squadron,screen_rect):
        self.squadron = squadron
        self.offset_x = 0.0
        self.offset_y = 0.0
        self.scale = 50
        self.view_point="TOP"
        self.screen_rect=screen_rect
    def CAM_movement(self):
        if not self.squadron:
            return
        x,y,z=self.squadron.center_point()
        centx=self.screen_rect.centerx
        centy=self.screen_rect.centery
        if self.view_point == "TOP":
            self.offset_x = centx - x * self.scale
            self.offset_y = centy + y * self.scale
        else:  # SIDE
            self.offset_x = centx - x * self.scale
            self.offset_y = centy + z * self.scale
    def world_to_screen(self,x,y,z):
        if self.view_point == "TOP":
            sx=int(x*self.scale+self.offset_x)
            sy=int(-y*self.scale+self.offset_y)
        else:
            sx=int(x*self.scale+self.offset_x)
            sy=int(-z*self.scale+self.offset_y)
        return sx,sy