import pygame
import pyautogui
WIDTH, HEIGHT = pyautogui.size()
# = = = = = = = Przycisk = = = = = = =
class Button:
    def __init__(self,x,y,w,h,text,callback):
        self.rect=pygame.Rect(x,y,w,h)
        self.text=text
        self.callback=callback
    def handle_event(self,event):
        if event.type==pygame.MOUSEBUTTONDOWN:
            if self.rect.collidepoint(event.pos):
                self.callback()    # -> wywołanie funkcji
    def draw(self,screen,font):
        pygame.draw.rect(screen,(150, 180, 255),self.rect)
        screen.blit(font.render(self.text,True,(0,0,0)),(self.rect.x+5, self.rect.y+5))
# = = = = = = = Pole tekstowe = = = = = = =
class TextInput:
    def __init__(self,x,y,w,h):
        self.rect=pygame.Rect(x,y,w,h)
        self.text=""
        self.active=False
    def handle_event(self,event):
        if event.type==pygame.MOUSEBUTTONDOWN:
            self.active=self.rect.collidepoint(event.pos)
        if event.type==pygame.KEYDOWN and self.active:
            if event.key==pygame.K_BACKSPACE:
                self.text=self.text[:-1]
            else:
                self.text+=event.unicode
    def draw(self,screen,font):
        pygame.draw.rect(screen,(255,255,255),self.rect)
        txt=font.render(self.text,True,(0,0,0))
        screen.blit(txt,(self.rect.x+5,self.rect.y+5))
# = = = = = = = DropList = = = = = = =
class Droplist:
    def __init__(self,x,y,w,h,options):
        self.rect=pygame.Rect(x,y,w,h)
        self.options=options
        self.selected=options[0]
        self.open=False
    def handle_event(self,event):
        if event.type==pygame.MOUSEBUTTONDOWN:
            if self.rect.collidepoint(event.pos):
                self.open=not self.open
            elif self.open:
                for i,opt in enumerate(self.options):
                    opt_rect=pygame.Rect(self.rect.x, self.rect.y+(i+1)*self.rect.height,
                                           self.rect.width, self.rect.height)
                    if opt_rect.collidepoint(event.pos):
                        self.selected=opt
                        self.open=False

    def draw(self,screen,font):
        pygame.draw.rect(screen,(200,200,200),self.rect)
        txt=font.render(self.selected,True,(0,0,0))
        screen.blit(txt,(self.rect.x+5,self.rect.y+5))
        if self.open:
            for i,opt in enumerate(self.options):
                r=pygame.Rect(self.rect.x, self.rect.y+(i+1)*self.rect.height,
                                self.rect.width, self.rect.height)
                pygame.draw.rect(screen,(220,220,220),r)
                screen.blit(font.render(opt,True,(0,0,0)),(r.x+5, r.y+5))
# = = = = = = = Pop up = = = = = = =
class Popup:
    def __init__(self,x,y,w,h,text,button):
        self.rect=pygame.Rect(x, y, w, h)
        self.text=text
        self.button=button
        self.show_popup=False
    def show(self,text):
        self.show_popup=True
        self.text=text
    def handle_event(self,event):
        if event.type==pygame.MOUSEBUTTONDOWN:
            if self.button.rect.collidepoint(event.pos):
                self.show_popup=True
            else:
                self.show_popup=False
    def draw(self,screen,font):
        if self.show_popup:
            pygame.draw.rect(screen,(200,200,200),self.rect)
            txt=font.render(self.text,True,(0,0,0))
            txt_rect=txt.get_rect(center=(WIDTH/2,HEIGHT/2))
            screen.blit(txt,txt_rect)
# = = = = = = = List items = = = = = = =
class Listbox:
    def __init__(self,x,y,w,h,items):
        item_height=30
        self.rect=pygame.Rect(x,y,w,h)
        self.items=items
        self.item_height=item_height
        self.scroll_offset=0
        self.selected_index=None
        # Kolory
        self.bg_color=(230,230,230)
        self.border_color=(0,0,0)
        self.item_color=(255,255,255)
        self.item_selected=(180,200,255)
    def get_selected(self):
        if self.selected_index is None:
            return None
        if 0<=self.selected_index<len(self.items):
            return self.items[self.selected_index]
        return None
    def handle_event(self,event):
        if event.type==pygame.MOUSEBUTTONDOWN:
            # kliknięcie
            if self.rect.collidepoint(event.pos):
                mx,my=event.pos
                index=(my-self.rect.y+self.scroll_offset)//self.item_height
                if 0<=index<len(self.items):
                    self.selected_index=index
            # scroll
            if event.button==4:  # scroll up
                self.scroll_offset=max(self.scroll_offset-20,0)
            elif event.button==5:  # scroll down
                max_scroll=max(0,len(self.items)*self.item_height-self.rect.height)
                self.scroll_offset=min(self.scroll_offset+20,max_scroll)
    def draw(self,screen,font):
        # tło panelu
        pygame.draw.rect(screen,self.bg_color,self.rect)
        pygame.draw.rect(screen,self.border_color,self.rect, 2)
        # obszar wyświetlania
        clip=screen.get_clip()
        screen.set_clip(self.rect)
        y_start=self.rect.y-self.scroll_offset
        for i,item in enumerate(self.items):
            item_rect=pygame.Rect(self.rect.x,y_start+i*self.item_height,self.rect.width,self.item_height)
            # tło
            if i==self.selected_index:
                pygame.draw.rect(screen,self.item_selected,item_rect)
            else:
                pygame.draw.rect(screen,self.item_color,item_rect)
            # tekst
            txt=font.render(str(item),True,(0,0,0))
            screen.blit(txt,(item_rect.x+5,item_rect.y+5))
            # obramowanie
            pygame.draw.rect(screen,self.border_color,item_rect,1)
        screen.set_clip(clip)
# = = = = = = = View swapper = = = = = = =
def view_swap(view_manager,view_swapper):
    def callback():
        view_manager.switch_view(view_swapper)
    return callback