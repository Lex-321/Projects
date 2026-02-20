class View_Manager:
    def __init__(self):
        self.current_view = None
        self.views={}
    def set_current_view(self, view):
        if callable(view):
            raise TypeError("set_current_view expects VIEW")
        self.current_view = view
    def register(self, name, view):
        self.views[name] = view
    def switch_views(self, name):
        if name not in self.views:
            raise ValueError(f"View '{name}' not registered")
        self.current_view = self.views[name]